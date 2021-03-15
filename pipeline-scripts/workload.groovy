#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def property_file_name = "workload.properties"
def contact = "msheth@redhat.com, nelluri@redhat.com"
def workload = env.WORKLOAD
def workload_properties_file = env.WORKLOAD_PROPERTIES_FILE
def pipeline_stage = env.PIPELINE_STAGE
def pipeline = env.PIPELINE.toString().toUpperCase()

println "Current pipeline job build id is '${pipeline_id}'"

// run workload
stage (workload) {
	currentBuild.result = "SUCCESS"
	node(node_label) {
	// get properties file
	if (fileExists(property_file_name)) {
		println "Looks like the property file already exists, erasing it"
		sh "rm ${property_file_name}"
	}
	// get properties file
	sh "wget ${workload_properties_file} -O ${property_file_name}"
	sh "cat ${property_file_name}"
	// Load the properties file.
	def properties = readProperties file: property_file_name
	def job_parameters = []
	job_parameters.add([$class: 'LabelParameterValue', name: 'node', label: node_label ])
	// Convert properties to parameters.
	for (property in properties) {
		job_parameters.add([$class: 'StringParameterValue', name: property.key, value: property.value ])
	}
        
	if (pipeline == "TRUE") {
		if (workload == "ATS-SCALE-CI-SCALE") {
			if (pipeline_stage != "1") {
				job_parameters = job_parameters.minus([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE']])
				if (pipeline_stage == "2") {
					job_parameters.add([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE_V2'] ])
				} else if (pipeline_stage == "3") {
					job_parameters.add([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE_V3'] ])
 				} else if (pipeline_stage == "4") {
					job_parameters.add([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE_V4'] ])
				} else if (pipeline_stage == "5") {
					job_parameters.add([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE_V5'] ])
				} else if (pipeline_stage == "6") {
					job_parameters.add([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE_V6'] ])
				} else if (pipeline_stage == "upgrades") {
					job_parameters.add([$class: 'StringParameterValue', name: 'SCALE', value: properties['SCALE_UPGRADES'] ])
				}
			}
		}
		else if (workload == "RIPSAW-CLUSTER-DENSITY") {
			if (pipeline_stage != "2") {
				job_parameters = job_parameters.minus([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS']])
				if (pipeline_stage == "3") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V2'] ])
				} else if (pipeline_stage == "4") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V3'] ])
				} else if (pipeline_stage == "5") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V4'] ])
				} else if (pipeline_stage == "6") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V5'] ])
				} else if (pipeline_stage == "upgrades") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_FOR_UPGRADES'] ])
				}
			}
		}
		else if (workload == "ATS-SCALE-CI-HTTP") {
			if (pipeline_stage != "1") {
				job_parameters = job_parameters.minus([$class: 'StringParameterValue', name: 'HTTP_TEST_APP_TEMPLATES', value: properties['HTTP_TEST_APP_TEMPLATES']])
				if (pipeline_stage == "2") {
			                job_parameters.add([$class: 'StringParameterValue', name: 'HTTP_TEST_APP_TEMPLATES', value: properties['HTTP_TEST_APP_TEMPLATES_LARGE'] ])
				}
			}
		}
	}

	// OpenShift install specific logic 
	// Install cluster using the payload captured at the build trigger url when scale_ci_build_trigger is set
	if ( workload.toString().trim().equals("ATS-SCALE-CI-OCP-AWS-DEPLOY") || workload.toString().trim().equals("ATS-SCALE-CI-OCP-GCP-DEPLOY") || workload.toString().trim().equals("ATS-SCALE-CI-OCP-AZURE-DEPLOY") || workload.toString().trim().equals("ATS-SCALE-CI-OCP-OSP-DEPLOY") ) {
		def scale_ci_build_trigger = properties['SCALE_CI_BUILD_TRIGGER']
		def scale_ci_build_trigger_url = properties['SCALE_CI_BUILD_TRIGGER_URL']
		if ( scale_ci_build_trigger.toBoolean() ) {
			sh "curl ${scale_ci_build_trigger_url}/payload -o /tmp/payload.${workload}"
			sh "curl ${scale_ci_build_trigger_url}/build.status -o /tmp/status.${workload}"
			sh "curl ${scale_ci_build_trigger_url}/cluster.status -o /tmp/cluster_status.${workload}"
			status = readFile "/tmp/status.${workload}"
			cluster_status = readFile "/tmp/cluster_status.${workload}"
			if ( status.toString().trim().equals("PROCEED") || cluster_status.toString().trim().equals("False") ) {
					println "Build status is set to ${status}, proceeding with the cluster build"
					openshift_install_release_image_override = readFile "/tmp/payload.${workload}"
					job_parameters.add([$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_RELEASE_IMAGE_OVERRIDE', value: openshift_install_release_image_override ])
				} else {
					println "Build status is set to ${status}, exiting without building the cluster"
					return 0
				}
		}
	}

	// run the job with the properties
	try {
		run_workload = build job: workload, parameters: job_parameters
                println "${workload} build ${run_workload.getNumber()} completed successfully"
	} catch ( Exception e) {
		echo "${workload} Job failed with the following error: "
		echo "${e.getMessage()}"
		mail(
			to: contact,
			subject: "${pipeline_id}: ${workload} job failed",
			body: """\
			Encoutered an error while running the ${workload}: ${e.getMessage()}\n\n
			Jenkins job: ${env.BUILD_URL}
			""")
			currentBuild.result = "FAILURE"
			sh "exit 1"
		}
	}
}
