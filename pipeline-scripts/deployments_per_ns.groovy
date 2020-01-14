#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def deployments_per_ns = DEPLOYMENTS_PER_NS.toString().toUpperCase()
def property_file_name = "deployments_per_ns.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run deployments_per_ns scale test
stage ('deployments_per_ns_scale_test') {
	if (deployments_per_ns == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${DEPLOYMENTS_PER_NS_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def deployments_per_ns_properties =readProperties file: property_file_name
			def skip_tls = deployments_per_ns_properties['SKIP_TLS_VERIFICATION']
			def cluster_user = deployments_per_ns_properties['CLUSTER_USER']
			def cluster_password = deployments_per_ns_properties['CLUSTER_PASSWORD']
			def cluster_api_url = deployments_per_ns_properties['CLUSTER_API_URL']
			def sshkey_token = deployments_per_ns_properties['SSHKEY_TOKEN']
			def orchestration_host = deployments_per_ns_properties['ORCHESTRATION_HOST']
			def orchestration_user = deployments_per_ns_properties['ORCHESTRATION_USER']
			def workload_image = deployments_per_ns_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = deployments_per_ns_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = deployments_per_ns_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = deployments_per_ns_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = deployments_per_ns_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = deployments_per_ns_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = deployments_per_ns_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = deployments_per_ns_properties['ENABLE_PBENCH_COPY']
			def pbench_server = deployments_per_ns_properties['PBENCH_SERVER']
			def scale_ci_results_token = deployments_per_ns_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = deployments_per_ns_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def deployments_per_ns_test_prefix = deployments_per_ns_properties['DEPLOYMENTS_PER_NS_TEST_PREFIX']
			def deployments_per_ns_cleanup = deployments_per_ns_properties['DEPLOYMENTS_PER_NS_CLEANUP']
			def deployments_per_ns_basename = deployments_per_ns_properties['DEPLOYMENTS_PER_NS_BASENAME']
			def deployments_per_ns_count = deployments_per_ns_properties['DEPLOYMENTS_PER_NS_COUNT']
			def deployments_per_ns_pod_image = deployments_per_ns_properties['DEPLOYMENTS_PER_NS_POD_IMAGE']
			def snafu_user = deployments_per_ns_properties['SNAFU_USER']
			def snafu_cluster_name = deployments_per_ns_properties['SNAFU_CLUSTER_NAME']
			def es_host = deployments_per_ns_properties['ES_HOST']
			def es_port = deployments_per_ns_properties['ES_PORT']
			def es_index_prefix = deployments_per_ns_properties['ES_INDEX_PREFIX']


			// debug info
			println "----------USER DEFINED OPTIONS-------------------"
			println "-------------------------------------------------"
			println "DEPLOYMENTS_PER_NS_COUNT: '${deployments_per_ns_count}'"
			println "JOB_COMPLETION_POLL_ATTEMPTS: '${job_completion_poll_attempts}'"
			println "-------------------------------------------------"
			println "-------------------------------------------------"

			// Run deployments_per_ns job
			try {
				deployments_per_ns_build = build job: 'ATS-SCALE-CI-DEPLOYMENTS-PER-NS',
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
						[$class: 'StringParameterValue', name: 'DEPLOYMENTS_PER_NS_TEST_PREFIX', value: deployments_per_ns_test_prefix ],
						[$class: 'BooleanParameterValue', name: 'DEPLOYMENTS_PER_NS_CLEANUP', value: Boolean.valueOf(deployments_per_ns_cleanup)  ],
						[$class: 'StringParameterValue', name: 'DEPLOYMENTS_PER_NS_BASENAME', value: deployments_per_ns_basename ],
						[$class: 'StringParameterValue', name: 'DEPLOYMENTS_PER_NS_COUNT', value: deployments_per_ns_count ],
						[$class: 'StringParameterValue', name: 'SNAFU_USER', value: snafu_user ],
						[$class: 'StringParameterValue', name: 'SNAFU_CLUSTER_NAME', value: snafu_cluster_name ],
						[$class: 'StringParameterValue', name: 'ES_HOST', value: es_host ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'StringParameterValue', name: 'ES_INDEX_PREFIX', value: es_index_prefix ],
						[$class: 'StringParameterValue', name: 'DEPLOYMENTS_PER_NS_POD_IMAGE', value: deployments_per_ns_pod_image ]]
			} catch ( Exception e) {
				echo "DEPLOYMENTS_PER_NS Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'Deployments per namespace cluster limits job failed',
					body: """\
						Encoutered an error while running the deployments_per_ns-scale-test job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
                        	sh "exit 1"
                        }
                        println "Deployments per ns build ${deployments_per_ns_build.getNumber()} completed successfully"
		}
	}
}
