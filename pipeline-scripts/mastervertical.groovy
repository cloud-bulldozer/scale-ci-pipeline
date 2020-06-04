#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def mastervertical = MASTERVERTICAL_SCALE_TEST.toString().toUpperCase()
def property_file_name = "mastervert.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run mastervert test
stage('mastervertical_scale_test') {
	if (MASTERVERTICAL_SCALE_TEST) {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${MASTERVERTICAL_PROPERTY_FILE} -O ${property_file_name}"
                        sh "cat ${property_file_name}"
			def mastervertical_properties = readProperties file: property_file_name
			def sshkey_token = mastervertical_properties['SSHKEY_TOKEN']
			def orchestration_host = mastervertical_properties['ORCHESTRATION_HOST']
			def orchestration_user = mastervertical_properties['ORCHESTRATION_USER']
			def workload_image = mastervertical_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = mastervertical_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = mastervertical_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = mastervertical_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = mastervertical_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = mastervertical_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = mastervertical_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = mastervertical_properties['ENABLE_PBENCH_COPY']
			def pbench_server = mastervertical_properties['PBENCH_SERVER']
			def scale_ci_results_token = mastervertical_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = mastervertical_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def mastervertical_test_prefix = mastervertical_properties['MASTERVERTICAL_TEST_PREFIX']
			def mastervertical_cleanup = mastervertical_properties['MASTERVERTICAL_CLEANUP']
			def mastervertical_basename = mastervertical_properties['MASTERVERTICAL_BASENAME']
			def mastervertical_projects = mastervertical_properties['MASTERVERTICAL_PROJECTS']
			def mastervertical_expected_duration = mastervertical_properties['EXPECTED_MASTERVERTICAL_DURATION']
			def snafu_user = mastervertical_properties['SNAFU_USER']
			def snafu_cluster_name = mastervertical_properties['SNAFU_CLUSTER_NAME']
			def es_host = mastervertical_properties['ES_HOST']
			def es_port = mastervertical_properties['ES_PORT']
			def es_index_prefix = mastervertical_properties['ES_INDEX_PREFIX']

			// Run mastervertical job
			try {
				mastervertical_build = build job: 'ATS-SCALE-CI-MASTERVERTICAL',
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
						[$class: 'StringParameterValue', name: 'MASTERVERTICAL_TEST_PREFIX', value: mastervertical_test_prefix ],
						[$class: 'BooleanParameterValue', name: 'MASTERVERTICAL_CLEANUP', value: Boolean.valueOf(mastervertical_cleanup)  ],
						[$class: 'StringParameterValue', name: 'MASTERVERTICAL_BASENAME', value: mastervertical_basename ],
						[$class: 'StringParameterValue', name: 'MASTERVERTICAL_PROJECTS', value: mastervertical_projects ],
						[$class: 'StringParameterValue', name: 'SNAFU_USER', value: snafu_user ],
						[$class: 'StringParameterValue', name: 'SNAFU_CLUSTER_NAME', value: snafu_cluster_name ],
						[$class: 'StringParameterValue', name: 'ES_HOST', value: es_host ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'StringParameterValue', name: 'ES_INDEX_PREFIX', value: es_index_prefix ],
						[$class: 'StringParameterValue', name: 'EXPECTED_MASTERVERTICAL_DURATION', value: mastervertical_expected_duration ]]
			} catch ( Exception e) {
				echo "MASTERVERTICAL SCALE TEST Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
      					to: 'nelluri@redhat.com',
      					subject: 'MasterVertical scale test job failed',
      					body: """\
						Encoutered an error while running the MasterVertical scale test job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
				sh "exit 1"
			}
			println "MASTERVERTICAL SCALE TEST build ${mastervertical_build.getNumber()} completed successfully"
		}
	}
}
