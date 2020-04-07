#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def baseline = BASELINE_SCALE_TEST.toString().toUpperCase()
def property_file_name = "baseline.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run baseline scale test
stage ('baseline_scale_test') {
	if (baseline == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${BASELINE_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def baseline_properties = readProperties file: property_file_name
			def skip_tls = baseline_properties['SKIP_TLS_VERIFICATION']
			def cluster_user = baseline_properties['CLUSTER_USER']
			def cluster_password = baseline_properties['CLUSTER_PASSWORD']
			def cluster_api_url = baseline_properties['CLUSTER_API_URL']
			def sshkey_token = baseline_properties['SSHKEY_TOKEN']
			def sshkey_repo = baseline_properties['SSHKEY_REPO']
			def sshkey_repopath_pub = baseline_properties['SSHKEY_REPOPATH_PUB']
			def sshkey_repopath_priv = baseline_properties['SSHKEY_REPOPATH_PRIV']   
			def orchestration_host = baseline_properties['ORCHESTRATION_HOST']
			def orchestration_user = baseline_properties['ORCHESTRATION_USER']
			def workload_image = baseline_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = baseline_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = baseline_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = baseline_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = baseline_properties['KUBECONFIG_FILE']
			def enable_pbench_agents = baseline_properties['ENABLE_PBENCH_AGENTS']
			def pbench_server = baseline_properties['PBENCH_SERVER']
			def scale_ci_results_token = baseline_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = baseline_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def baseline_test_prefix = baseline_properties['BASELINE_TEST_PREFIX']
                        def baseline_workload_duration = baseline_properties['BASELINE_WORKLOAD_DURATION']

			// Run baseline job
			try {
				baseline_build = build job: 'BASELINE-SCALE-TEST',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'BooleanParameterValue', name: 'SKIP_TLS_VERIFICATION', value: Boolean.valueOf(skip_tls) ],
						[$class: 'StringParameterValue', name: 'CLUSTER_USER', value: cluster_user ],
						[$class: 'StringParameterValue', name: 'CLUSTER_PASSWORD', value: cluster_password ],
						[$class: 'StringParameterValue', name: 'CLUSTER_API_URL', value: cluster_api_url ],
						[$class: 'StringParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'SSHKEY_REPO', value: sshkey_repo ],
						[$class: 'StringParameterValue', name: 'SSHKEY_REPOPATH_PUB', value: sshkey_repopath_pub ],
						[$class: 'StringParameterValue', name: 'SSHKEY_REPOPATH_PRIV', value: sshkey_repopath_priv ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'WORKLOAD_IMAGE', value: workload_image ],
						[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_NODE_SELECTOR', value: Boolean.valueOf(workload_job_node_selector) ],
						[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_TAINT', value: Boolean.valueOf(workload_job_taint)  ],
						[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_PRIVILEGED', value: Boolean.valueOf(workload_job_privileged)  ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_FILE', value: kubeconfig_file ],
						[$class: 'BooleanParameterValue', name: 'ENABLE_PBENCH_AGENTS', value: Boolean.valueOf(enable_pbench_agents)  ],
						[$class: 'StringParameterValue', name: 'PBENCH_SERVER', value: pbench_server ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_RESULTS_TOKEN', value: scale_ci_results_token ],
						[$class: 'StringParameterValue', name: 'JOB_COMPLETION_POLL_ATTEMPTS', value: job_completion_poll_attempts ],
						[$class: 'StringParameterValue', name: 'BASELINE_TEST_PREFIX', value: baseline_test_prefix ],   
						[$class: 'StringParameterValue', name: 'BASELINE_WORKLOAD_DURATION', value: baseline_workload_duration ]]  
			} catch ( Exception e) {
				echo "BASELINE-SCALE-TEST Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'Baseline-scale-test job failed',
					body: """\
						Encoutered an error while running the baseline-scale-test job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "BASELINE-SCALE-TEST build ${baseline_build.getNumber()} completed successfully"
		}
	}
}
