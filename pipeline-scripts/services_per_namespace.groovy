#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def services_per_namespace = SERVICES_PER_NAMESPACE.toString().toUpperCase()
def property_file_name = "services_per_namespace.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run services_per_namespace scale test
stage ('ATS-SCALE-CI-SERVICES-PER-NAMESPACE') {
	if (services_per_namespace == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the propertyfile already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${SERVICES_PER_NAMESPACE_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def services_per_namespace_properties = readProperties file: property_file_name
			def sshkey_token = services_per_namespace_properties['SSHKEY_TOKEN']
			def orchestration_host = services_per_namespace_properties['ORCHESTRATION_HOST']
			def orchestration_user = services_per_namespace_properties['ORCHESTRATION_USER']
			def workload_image = services_per_namespace_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = services_per_namespace_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = services_per_namespace_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = services_per_namespace_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = services_per_namespace_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = services_per_namespace_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = services_per_namespace_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = services_per_namespace_properties['ENABLE_PBENCH_COPY']
			def pbench_server = services_per_namespace_properties['PBENCH_SERVER']
			def scale_ci_results_token = services_per_namespace_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = services_per_namespace_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def services_per_namespace_test_prefix = services_per_namespace_properties['SERVICES_PER_NAMESPACE_TEST_PREFIX']
			def services_per_namespace_cleanup = services_per_namespace_properties['SERVICES_PER_NAMESPACE_CLEANUP']
			def services_per_namespace_basename = services_per_namespace_properties['SERVICES_PER_NAMESPACE_BASENAME']
			def services_per_namespace_projects = services_per_namespace_properties['SERVICES_PER_NAMESPACE_PROJECTS']
			def services_per_namespace_count = services_per_namespace_properties['SERVICES_PER_NAMESPACE_COUNT']
			def expected_services_per_namespace_duration = services_per_namespace_properties['EXPECTED_SERVICES_PER_NAMESPACE_DURATION']
			def snafu_user = services_per_namespace_properties['SNAFU_USER']
			def snafu_cluster_name = services_per_namespace_properties['SNAFU_CLUSTER_NAME']
			def es_host = services_per_namespace_properties['ES_HOST']
			def es_port = services_per_namespace_properties['ES_PORT']
			def es_index_prefix = services_per_namespace_properties['ES_INDEX_PREFIX']

			// Run services_per_namespace job
			try {
				services_per_namespace_build = build job: 'ATS-SCALE-CI-TEST',
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
						[$class: 'StringParameterValue', name: 'SERVICES_PER_NAMESPACE_TEST_PREFIX', value: services_per_namespace_test_prefix ],
						[$class: 'BooleanParameterValue', name: 'SERVICES_PER_NAMESPACE_CLEANUP', value: Boolean.valueOf(services_per_namespace_cleanup)  ],
						[$class: 'StringParameterValue', name: 'SERVICES_PER_NAMESPACE_BASENAME', value: services_per_namespace_basename ],
						[$class: 'StringParameterValue', name: 'SERVICES_PER_NAMESPACE_PROJECTS', value: services_per_namespace_projects ],
						[$class: 'StringParameterValue', name: 'SERVICES_PER_NAMESPACE_COUNT', value: services_per_namespace_count ],
						[$class: 'StringParameterValue', name: 'SNAFU_USER', value: snafu_user ],
						[$class: 'StringParameterValue', name: 'SNAFU_CLUSTER_NAME', value: snafu_cluster_name ],
						[$class: 'StringParameterValue', name: 'ES_HOST', value: es_host ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'StringParameterValue', name: 'ES_INDEX_PREFIX', value: es_index_prefix ],
						[$class: 'StringParameterValue', name: 'EXPECTED_SERVICES_PER_NAMESPACE_DURATION', value: expected_services_per_namespace_duration ]]
			} catch ( Exception e) {
				echo "ATS-SCALE-CI-SERVICES-PER-NAMESPACE Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'ATS-SCALE-CI-SERVICES-PER-NAMESPACE JOB FAILED',
					body: """\
						Encoutered an error while running the services_per_namespace-scale-test job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
                        	sh "exit 1"
                        }
                        println "ATS-SCALE-CI-SERVICES-PER-NAMESPACE BUILD ${services_per_namespace_build.getNumber()} COMPLETED SUCCESSFULLY"
		}
	}
}
