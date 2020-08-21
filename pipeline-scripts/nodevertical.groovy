#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def nodevertical = NODEVERTICAL_SCALE_TEST.toString().toUpperCase()
def property_file_name = "nodevertical.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run nodevertical scale test
stage ('nodevertical_scale_test') {
	if (nodevertical == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${NODEVERTICAL_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def nodevertical_properties = readProperties file: property_file_name
			def sshkey_token = nodevertical_properties['SSHKEY_TOKEN']
			def orchestration_host = nodevertical_properties['ORCHESTRATION_HOST']
			def orchestration_user = nodevertical_properties['ORCHESTRATION_USER']
			def workload_image = nodevertical_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = nodevertical_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = nodevertical_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = nodevertical_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = nodevertical_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = nodevertical_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = nodevertical_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = nodevertical_properties['ENABLE_PBENCH_COPY']
			def pbench_server = nodevertical_properties['PBENCH_SERVER']
			def scale_ci_results_token = nodevertical_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = nodevertical_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def nodevertical_node_count = nodevertical_properties['NODEVERTICAL_NODE_COUNT']
			def nodevertical_test_prefix = nodevertical_properties['NODEVERTICAL_TEST_PREFIX']
			def nodevertical_cleanup = nodevertical_properties['NODEVERTICAL_CLEANUP']
			def nodevertical_basename = nodevertical_properties['NODEVERTICAL_BASENAME']
			def nodevertical_maxpods = nodevertical_properties['NODEVERTICAL_MAXPODS']
			def nodevertical_pod_image = nodevertical_properties['NODEVERTICAL_POD_IMAGE']
			def nodevertical_stepsize = nodevertical_properties['NODEVERTICAL_STEPSIZE']
			def nodevertical_pause = nodevertical_properties['NODEVERTICAL_PAUSE']
			def nodevertical_ts_timeout = nodevertical_properties['NODEVERTICAL_TS_TIMEOUT']
			def expected_nodevertical_duration = nodevertical_properties['EXPECTED_NODEVERTICAL_DURATION']
			def snafu_user = nodevertical_properties['SNAFU_USER']
			def snafu_cluster_name = nodevertical_properties['SNAFU_CLUSTER_NAME']
			def es_host = nodevertical_properties['ES_HOST']
			def es_port = nodevertical_properties['ES_PORT']
			def es_index_prefix = nodevertical_properties['ES_INDEX_PREFIX']
			def nodevertical_heavy = nodevertical_properties['NODEVERTICAL_HEAVY']
			def nodevertical_heavy_probe_endpoint = nodevertical_properties['NODEVERTICAL_HEAVY_PROBE_ENDPOINT']
			def nodevertical_heavy_probe_period = nodevertical_properties['NODEVERTICAL_HEAVY_PROBE_PERIOD']


			try {
				nodevertical_build = build job: 'ATS-SCALE-CI-NODEVERTICAL',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
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
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_NODE_COUNT', value: nodevertical_node_count ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_TEST_PREFIX', value: nodevertical_test_prefix ],
						[$class: 'BooleanParameterValue', name: 'NODEVERTICAL_CLEANUP', value: Boolean.valueOf(nodevertical_cleanup)  ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_BASENAME', value: nodevertical_basename ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_MAXPODS', value: nodevertical_maxpods ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_POD_IMAGE', value: nodevertical_pod_image ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_STEPSIZE', value: nodevertical_stepsize ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_PAUSE', value: nodevertical_pause ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_TS_TIMEOUT', value: nodevertical_ts_timeout ],
						[$class: 'StringParameterValue', name: 'SNAFU_USER', value: snafu_user ],
						[$class: 'StringParameterValue', name: 'SNAFU_CLUSTER_NAME', value: snafu_cluster_name ],
						[$class: 'StringParameterValue', name: 'ES_HOST', value: es_host ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'StringParameterValue', name: 'ES_INDEX_PREFIX', value: es_index_prefix ],
						[$class: 'StringParameterValue', name: 'EXPECTED_NODEVERTICAL_DURATION', value: expected_nodevertical_duration ],
						[$class: 'BooleanParameterValue', name: 'NODEVERTICAL_HEAVY', value: Boolean.valueOf(nodevertical_heavy) ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_HEAVY_PROBE_ENDPOINT', value: nodevertical_heavy_probe_endpoint ],
						[$class: 'StringParameterValue', name: 'NODEVERTICAL_HEAVY_PROBE_PERIOD', value: nodevertical_heavy_probe_period ]]

			} catch ( Exception e) {
				echo "ATS-SCALE-CI-NODEVERTICAL Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com, msheth@redhat.com',
					subject: 'ats-scale-ci-nodevertical job failed',
					body: """\
						Encoutered an error while running the ats-scale-ci-nodevertical job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "ATS-SCALE-CI-NODEVERTICAL build ${nodevertical_build.getNumber()} completed successfully"
		}
	}
}
