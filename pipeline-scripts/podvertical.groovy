#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def podvertical = PODVERTICAL.toString().toUpperCase()
def property_file_name = "podvertical.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run podvertical scale test
stage ('podvertical_scale_test') {
	if (podvertical == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the propertyfile already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${PODVERTICAL_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def podvertical_properties = readProperties file: property_file_name
			def cluster_user = podvertical_properties['CLUSTER_USER']
			def cluster_password = podvertical_properties['CLUSTER_PASSWORD']
			def cluster_api_url = podvertical_properties['CLUSTER_API_URL']
			def sshkey_token = podvertical_properties['SSHKEY_TOKEN']
			def orchestration_host = podvertical_properties['ORCHESTRATION_HOST']
			def orchestration_user = podvertical_properties['ORCHESTRATION_USER']
			def workload_image = podvertical_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = podvertical_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = podvertical_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = podvertical_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = podvertical_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = podvertical_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = podvertical_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = podvertical_properties['ENABLE_PBENCH_COPY']
			def pbench_server = podvertical_properties['PBENCH_SERVER']
			def scale_ci_results_token = podvertical_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = podvertical_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def podvertical_test_prefix = podvertical_properties['PODVERTICAL_TEST_PREFIX']
			def podvertical_cleanup = podvertical_properties['PODVERTICAL_CLEANUP']
			def podvertical_basename = podvertical_properties['PODVERTICAL_BASENAME']
			def podvertical_maxpods = podvertical_properties['PODVERTICAL_MAXPODS']
			def podvertical_pod_image = podvertical_properties['PODVERTICAL_POD_IMAGE']
			def podvertical_stepsize = podvertical_properties['PODVERTICAL_STEPSIZE']
			def podvertical_pause = podvertical_properties['PODVERTICAL_PAUSE']
			def podvertical_ts_timeout = podvertical_properties['PODVERTICAL_TS_TIMEOUT']
			def expected_podvertical_duration = podvertical_properties['EXPECTED_PODVERTICAL_DURATION']			

			// debug info
			println "----------USER DEFINED OPTIONS-------------------"
			println "-------------------------------------------------"
			println "PODVERTICAL_MAXPODS: '${podvertical_maxpods}'"
			println "EXPECTED_PODVERTICAL_DURATION: '${expected_podvertical_duration}'"
			println "-------------------------------------------------"
			println "-------------------------------------------------"

			// Run podvertical job
			try {
				podvertical_build = build job: 'ATS-SCALE-CI-PODVERTICAL',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'CLUSTER_USER', value: cluster_user ],
						[$class: 'StringParameterValue', name: 'CLUSTER_PASSWORD', value: cluster_password ],
						[$class: 'StringParameterValue', name: 'CLUSTER_API_URL', value: cluster_api_url ],
						[$class: 'StringParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'WORKLOAD_IMAGE', value: workload_image ],
						[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_NODE_SELECTOR', value: Boolean.valueOf(workload_job_node_selector) ],
						[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_TAINT', value: Boolean.valueOf(workload_job_taint)  ],
						[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_PRIVILEGED', value: Boolean.valueOf(workload_job_privileged)  ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_FILE', value: kubeconfig_file ],
						[$class: 'BooleanParameterValue', name: 'PBENCH_INSTRUMENTATION', value: Boolean.valueOf(pbench_instrumentation)  ],
						[$class: 'BooleanParameterValue', name: 'ENABLE_PBENCH_AGENTS', value: Boolean.valueOf(enable_pbench_agents)  ],
						[$class: 'BooleanParameterValue', name: 'ENABLE_PBENCH_COPY', value: Boolean.valueOf(enable_pbench_copy)  ],
						[$class: 'StringParameterValue', name: 'PBENCH_SERVER', value: pbench_server ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_RESULTS_TOKEN', value: scale_ci_results_token ],
						[$class: 'StringParameterValue', name: 'JOB_COMPLETION_POLL_ATTEMPTS', value: job_completion_poll_attempts ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_TEST_PREFIX', value: podvertical_test_prefix ],
						[$class: 'BooleanParameterValue', name: 'PODVERTICAL_CLEANUP', value: Boolean.valueOf(podvertical_cleanup)  ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_BASENAME', value: podvertical_basename ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_MAXPODS', value: podvertical_maxpods ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_POD_IMAGE', value: podvertical_pod_image ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_STEPSIZE', value: podvertical_stepsize ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_PAUSE', value: podvertical_pause ],
						[$class: 'StringParameterValue', name: 'PODVERTICAL_TS_TIMEOUT', value: podvertical_ts_timeout ],
						[$class: 'StringParameterValue', name: 'EXPECTED_PODVERTICAL_DURATION', value: expected_podvertical_duration ]]	
			} catch ( Exception e) {
				echo "PODVERTICAL Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'PODVERTICAL JOB FAILED',
					body: """\
						Encoutered an error while running the podvertical-scale-test job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
                        	sh "exit 1"
                        }
                        println "PODVERTICAL BUILD ${podvertical_build.getNumber()} COMPLETED SUCCESSFULLY"
		}
	}
}
