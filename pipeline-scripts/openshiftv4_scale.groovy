#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def ocpv4_scale = OPENSHIFTv4_SCALE.toString().toUpperCase()
def pipeline = PIPELINE.toString().toUpperCase()
def property_file_name = "openshiftv4_scale.properties"
def pipeline_stage = env.PIPELINE_STAGE
println "Current pipeline job stage is '${pipeline_stage}'"
println "Current pipeline job build id is '${pipeline_id}'"

// 4.x scale cluster
stage ('4.x scale cluster') {
	currentBuild.result = "SUCCESS"
	node(node_label) {
		// get properties file
		if (fileExists(property_file_name)) {
			println "Looks like the property file already exists, erasing it"
			sh "rm ${property_file_name}"
		}
		// get properties file
		sh "wget ${OPENSHIFTv4_SCALE_PROPERTY_FILE} -O ${property_file_name}"
		sh "cat ${property_file_name}"
		def scale_properties = readProperties file: property_file_name
		def sshkey_token = scale_properties['SSHKEY_TOKEN']
		def orchestration_host = scale_properties['ORCHESTRATION_HOST']
		def orchestration_user = scale_properties['ORCHESTRATION_USER']
		def workload_image = scale_properties['WORKLOAD_IMAGE']
		def workload_job_node_selector = scale_properties['WORKLOAD_JOB_NODE_SELECTOR']
		def workload_job_taint = scale_properties['WORKLOAD_JOB_TAINT']
		def workload_job_privileged = scale_properties['WORKLOAD_JOB_PRIVILEGED']
		def kubeconfig_file = scale_properties['KUBECONFIG_FILE']
		def pbench_instrumentation = scale_properties['PBENCH_INSTRUMENTATION']
		def enable_pbench_agents = scale_properties['ENABLE_PBENCH_AGENTS']
		def enable_pbench_copy = scale_properties['ENABLE_PBENCH_COPY']
		def pbench_server = scale_properties['PBENCH_SERVER']
		def scale_ci_results_token = scale_properties['SCALE_CI_RESULTS_TOKEN']
		def job_completion_poll_attempts = scale_properties['JOB_COMPLETION_POLL_ATTEMPTS']
		def scale_test_prefix = scale_properties['SCALE_TEST_PREFIX']
		def scale_metadata_prefix = scale_properties['SCALE_METADATA_PREFIX']
		def scale_worker_count = scale_properties['SCALE_WORKER_COUNT']
		if (pipeline == "TRUE" && pipeline_stage == "2") {
			scale_worker_count = scale_properties['SCALE_WORKER_COUNT_V2']
		} else if (pipeline == "TRUE" && pipeline_stage == "3") {
			scale_worker_count = scale_properties['SCALE_WORKER_COUNT_V3']
		}
		def scale_poll_attempts = scale_properties['SCALE_POLL_ATTEMPTS']
		def expected_scale_duration = scale_properties['EXPECTED_SCALE_DURATION']

		try {
			scale_build = build job: 'ATS-SCALE-CI-SCALE',
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
					[$class: 'StringParameterValue', name: 'SCALE_TEST_PREFIX', value: scale_test_prefix ],
					[$class: 'StringParameterValue', name: 'SCALE_METADATA_PREFIX', value: scale_metadata_prefix  ],
					[$class: 'StringParameterValue', name: 'SCALE_WORKER_COUNT', value: scale_worker_count ],
					[$class: 'StringParameterValue', name: 'SCALE_POLL_ATTEMPTS', value: scale_poll_attempts ],
					[$class: 'StringParameterValue', name: 'EXPECTED_SCALE_DURATION', value: expected_scale_duration ]]
		} catch ( Exception e) {
			echo "ATS-SCALE-CI-SCALE Job failed with the following error: "
			echo "${e.getMessage()}"
			echo "Sending an email"
			mail(
				to: 'nelluri@redhat.com',
				subject: 'ATS-SCALE-CI-SCALE job failed',
				body: """\
					Encoutered an error while running the ats-scale-ci-scale job: ${e.getMessage()}\n\n
					Jenkins job: ${env.BUILD_URL}
			""")
			currentBuild.result = "FAILURE"
			sh "exit 1"
		}
		println "ATS-SCALE-CI-SCALE build ${scale_build.getNumber()} completed successfully"
	}
}
