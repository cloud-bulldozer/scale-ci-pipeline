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
				}
			}
		}
		else if (workload == "RIPSAW-CLUSTER-DENSITY") {
			if (pipeline_stage != "1") {
				job_parameters = job_parameters.minus([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS']])
				if (pipeline_stage == "3") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V2'] ])
				} else if (pipeline_stage == "4") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V3'] ])
				} else if (pipeline_stage == "5") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V4'] ])
				} else if (pipeline_stage == "5") {
					job_parameters.add([$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: properties['JOB_ITERATIONS_V5'] ])
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
