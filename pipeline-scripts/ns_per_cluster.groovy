#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def ns_per_cluster = NS_PER_CLUSTER.toString().toUpperCase()
def property_file_name = "ns_per_cluster.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run ns_per_cluster test
stage('ns_per_cluster_scale_test') {
	if (ns_per_cluster == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${NS_PER_CLUSTER_PROPERTY_FILE} -O ${property_file_name}"
                        sh "cat ${property_file_name}"
			def namespaces_per_cluster_properties = readProperties file: property_file_name
			def skip_tls = namespaces_per_cluster_properties['SKIP_TLS_VERIFICATION']
			def cluster_user = namespaces_per_cluster_properties['CLUSTER_USER']
			def cluster_password = namespaces_per_cluster_properties['CLUSTER_PASSWORD']
			def cluster_api_url = namespaces_per_cluster_properties['CLUSTER_API_URL']
			def sshkey_token = namespaces_per_cluster_properties['SSHKEY_TOKEN']
			def orchestration_host = namespaces_per_cluster_properties['ORCHESTRATION_HOST']
			def orchestration_user = namespaces_per_cluster_properties['ORCHESTRATION_USER']
			def workload_image = namespaces_per_cluster_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = namespaces_per_cluster_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = namespaces_per_cluster_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = namespaces_per_cluster_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = namespaces_per_cluster_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = namespaces_per_cluster_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = namespaces_per_cluster_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = namespaces_per_cluster_properties['ENABLE_PBENCH_COPY']
			def pbench_server = namespaces_per_cluster_properties['PBENCH_SERVER']
			def scale_ci_results_token = namespaces_per_cluster_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = namespaces_per_cluster_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def namespaces_per_cluster_test_prefix = namespaces_per_cluster_properties['NAMESPACES_PER_CLUSTER_TEST_PREFIX']
			def namespaces_per_cluster_cleanup = namespaces_per_cluster_properties['NAMESPACES_PER_CLUSTER_CLEANUP']
			def namespaces_per_cluster_basename = namespaces_per_cluster_properties['NAMESPACES_PER_CLUSTER_BASENAME']
			def namespaces_per_cluster_projects = namespaces_per_cluster_properties['NAMESPACES_PER_CLUSTER_PROJECTS']
			def namespaces_per_cluster_count = namespaces_per_cluster_properties['NAMESPACES_PER_CLUSTER_COUNT']
			def expected_namespaces_per_cluster_duration = namespaces_per_cluster_properties['EXPECTED_NAMESPACES_PER_CLUSTER_DURATION']
	
			// Run namespaces per cluster job
			try {
				ns_per_clusterical_build = build job: 'ATS-SCALE-CI-NAMESPACES-PER-CLUSTER',
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
				[$class: 'StringParameterValue', name: 'NAMESPACES_PER_CLUSTER_TEST_PREFIX', value: namespaces_per_cluster_test_prefix ],
				[$class: 'BooleanParameterValue', name: 'NAMESPACES_PER_CLUSTER_CLEANUP', value: Boolean.valueOf(namespaces_per_cluster_cleanup)  ],
				[$class: 'StringParameterValue', name: 'NAMESPACES_PER_CLUSTER_BASENAME', value: namespaces_per_cluster_basename ],
				[$class: 'StringParameterValue', name: 'NAMESPACES_PER_CLUSTER_COUNT', value: namespaces_per_cluster_count ],
				[$class: 'StringParameterValue', name: 'EXPECTED_NAMESPACES_PER_CLUSTER_DURATION', value: expected_namespaces_per_cluster_duration ]] 
			} catch ( Exception e) {
				echo "NS_PER_CLUSTER Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
      					to: 'nelluri@redhat.com',
      					subject: 'ATS-SCALE-CI-NAMESPACES-PER-CLUSTER job failed',
      					body: """\
						Encoutered an error while running the ATS-SCALE-CI-NAMESPACES-PER-CLUSTER job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
				sh "exit 1"
			}
			println "ATS-SCALE-CI-NAMESPACES-PER-CLUSTER build ${ns_per_clusterical_build.getNumber()} completed successfully"
		}
	}
}
