#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def run_conformance = CONFORMANCE.toString().toUpperCase()
def property_file_name = "conformance.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run conformance
stage ('conformance') {
		if (run_conformance == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${CONFORMANCE_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def conformance_properties = readProperties file: property_file_name
			def skip_tls = conformance_properties['SKIP_TLS_VERIFICATION']
			def cluster_user = conformance_properties['CLUSTER_USER']
			def cluster_password = conformance_properties['CLUSTER_PASSWORD']
			def cluster_api_url = conformance_properties['CLUSTER_API_URL']
			def sshkey_token = conformance_properties['SSHKEY_TOKEN']
			def orchestration_host = conformance_properties['ORCHESTRATION_HOST']
			def orchestration_user = conformance_properties['ORCHESTRATION_USER']
			def workload_image = conformance_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = conformance_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = conformance_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = conformance_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = conformance_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = conformance_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = conformance_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = conformance_properties['ENABLE_PBENCH_COPY']
			def pbench_server = conformance_properties['PBENCH_SERVER']
			def scale_ci_results_token = conformance_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = conformance_properties['JOB_COMPLETION_POLL_ATTEMPTS']	
			def conformance_test_prefix = conformance_properties['CONFORMANCE_TEST_PREFIX']
			
			try {
				conformance_build = build job: 'ATS-SCALE-CI-CONFORMANCE',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'BooleanParameterValue', name: 'SKIP_TLS_VERIFICATION', value: Boolean.valueOf(skip_tls) ],
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
						[$class: 'StringParameterValue', name: 'CONFORMANCE_TEST_PREFIX', value: conformance_test_prefix ]]
			} catch ( Exception e) {
				echo "CONFORMANCE Job failed with the following error: "
				echo "${e.getMessage()}"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'Conformance job failed',
					body: """\
						Encoutered an error while running the conformance job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
			currentBuild.result = "FAILURE"
			sh "exit 1"
			}
			println "CONFORMANCE build ${conformance_build.getNumber()} completed successfully"
		}
	}
}
