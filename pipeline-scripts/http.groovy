#!/usr/bin/env groovy
def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def http = HTTP_TEST.toString().toUpperCase()
def pipeline = PIPELINE.toString().toUpperCase()
def property_file_name = "http.properties"
def pipeline_stage = env.PIPELINE_STAGE
println "Current pipeline job stage is '${pipeline_stage}'"
println "Current pipeline job build id is '${pipeline_id}'"

// run http scale test
stage ('http_scale_test') {
	currentBuild.result = "SUCCESS"
	node(node_label) {
		// get properties file
		if (fileExists(property_file_name)) {
			println "Looks like the property file already exists, erasing it"
			sh "rm ${property_file_name}"
		}
		// get properties file
		sh "wget ${HTTP_TEST_PROPERTY_FILE} -O ${property_file_name}"
		sh "cat ${property_file_name}"
		def http_properties = readProperties file: property_file_name
		def sshkey_token = http_properties['SSHKEY_TOKEN']
		def orchestration_host = http_properties['ORCHESTRATION_HOST']
		def orchestration_user = http_properties['ORCHESTRATION_USER']
		def workload_image = http_properties['WORKLOAD_IMAGE']
		def workload_job_node_selector = http_properties['WORKLOAD_JOB_NODE_SELECTOR']
		def workload_job_taint = http_properties['WORKLOAD_JOB_TAINT']
		def workload_job_privileged = http_properties['WORKLOAD_JOB_PRIVILEGED']
		def kubeconfig_file = http_properties['KUBECONFIG_FILE']
		def pbench_instrumentation = http_properties['PBENCH_INSTRUMENTATION']
		def enable_pbench_agents = http_properties['ENABLE_PBENCH_AGENTS']
		def enable_pbench_copy = http_properties['ENABLE_PBENCH_COPY']
		def pbench_server = http_properties['PBENCH_SERVER']
		def scale_ci_results_token = http_properties['SCALE_CI_RESULTS_TOKEN']
		def job_completion_poll_attempts = http_properties['JOB_COMPLETION_POLL_ATTEMPTS']
		def http_test_prefix = http_properties['HTTP_TEST_SUFFIX']
		def http_test_load_generators = http_properties['HTTP_TEST_LOAD_GENERATORS']
		def http_test_load_generator_nodes = http_properties['HTTP_TEST_LOAD_GENERATOR_NODES']
		def http_test_app_projects = http_properties['HTTP_TEST_APP_PROJECTS']
		def http_test_app_templates = http_properties['HTTP_TEST_APP_TEMPLATES']
		if (pipeline == "TRUE" && pipeline_stage == "2") {
			http_test_app_templates = http_properties['HTTP_TEST_APP_TEMPLATES_LARGE']
		}
		def http_test_runtime = http_properties['HTTP_TEST_RUNTIME']
		def http_test_mb_ramp_up = http_properties['HTTP_TEST_MB_RAMP_UP']
		def http_test_mb_delay = http_properties['HTTP_TEST_MB_DELAY']
		def http_test_mb_tls_session_reuse = http_properties['HTTP_TEST_MB_TLS_SESSION_REUSE']
		def http_test_mb_method = http_properties['HTTP_TEST_MB_METHOD']
		def http_test_mb_response_size = http_properties['HTTP_TEST_MB_RESPONSE_SIZE']
		def http_test_mb_request_body_size = http_properties['HTTP_TEST_MB_REQUEST_BODY_SIZE']
		def http_test_route_termination = http_properties['HTTP_TEST_ROUTE_TERMINATION']
		def http_test_smoke_test = http_properties['HTTP_TEST_SMOKE_TEST']
		def http_test_namespace_cleanup = http_properties['HTTP_TEST_NAMESPACE_CLEANUP']
		def http_test_stress_container_image = http_properties['HTTP_TEST_STRESS_CONTAINER_IMAGE']
		def http_test_server_container_image  = http_properties['HTTP_TEST_SERVER_CONTAINER_IMAGE']
		def es_server = http_properties['ES_SERVER']
		def es_port = http_properties['ES_PORT']
		def es_user = http_properties['ES_USER']
		def es_password = http_properties['ES_PASSWORD']
		def compare = http_properties['COMPARE']
		def baseline_cloud_name = http_properties['BASELINE_CLOUD_NAME']
		def es_user_baseline = http_properties['ES_USER_BASELINE']
		def es_password_baseline = http_properties['ES_PASSWORD_BASELINE']
		def es_server_baseline = http_properties['ES_SERVER_BASELINE']
		def es_port_baseline = http_properties['ES_PORT_BASELINE']
		def baseline_router_uuid = http_properties['BASELINE_ROUTER_UUID']
		def throughput_tolerance = http_properties['THROUGHPUT_TOLERANCE']
		def latency_tolerance = http_properties['LATENCY_TOLERANCE']
		def cerberus_url = http_properties['CERBERUS_URL']	
		def email_id_for_results_sheet = http_properties['EMAIL_ID_FOR_RESULTS_SHEET']
		def gsheet_key = http_properties['GSHEET_KEY']
		def gsheet_key_location = http_properties['GSHEET_KEY_LOCATION']

		try {
			http_build = build job: 'ATS-SCALE-CI-HTTP',
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
					[$class: 'StringParameterValue', name: 'HTTP_TEST_SUFFIX', value: http_test_prefix ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_LOAD_GENERATORS', value: http_test_load_generators ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_LOAD_GENERATOR_NODES', value: http_test_load_generator_nodes ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_APP_PROJECTS', value: http_test_app_projects ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_APP_TEMPLATES', value: http_test_app_templates ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_RUNTIME', value: http_test_runtime ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_MB_RAMP_UP', value: http_test_mb_ramp_up ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_MB_DELAY', value: http_test_mb_delay ],
					[$class: 'BooleanParameterValue', name: 'HTTP_TEST_MB_TLS_SESSION_REUSE', value: Boolean.valueOf(http_test_mb_tls_session_reuse) ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_MB_METHOD', value: http_test_mb_method ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_MB_RESPONSE_SIZE', value: http_test_mb_response_size ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_MB_REQUEST_BODY_SIZE', value: http_test_mb_request_body_size ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_ROUTE_TERMINATION', value: http_test_route_termination ],
					[$class: 'BooleanParameterValue', name: 'HTTP_TEST_SMOKE_TEST', value: Boolean.valueOf(http_test_smoke_test) ],
					[$class: 'BooleanParameterValue', name: 'HTTP_TEST_NAMESPACE_CLEANUP', value: Boolean.valueOf(http_test_namespace_cleanup) ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_STRESS_CONTAINER_IMAGE', value: http_test_stress_container_image ],
					[$class: 'StringParameterValue', name: 'HTTP_TEST_SERVER_CONTAINER_IMAGE', value: http_test_server_container_image ],
					[$class: 'StringParameterValue', name: 'ES_SERVER', value: es_server ],
					[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
					[$class: 'StringParameterValue', name: 'ES_USER', value: es_user ],
					[$class: 'StringParameterValue', name: 'ES_PASSWORD', value: es_password ],
					[$class: 'BooleanParameterValue', name: 'COMPARE', value: Boolean.valueOf(compare) ],
					[$class: 'StringParameterValue', name: 'BASELINE_CLOUD_NAME', value: baseline_cloud_name ],
					[$class: 'StringParameterValue', name: 'ES_USER_BASELINE', value: es_user_baseline ],
					[$class: 'StringParameterValue', name: 'ES_PASSWORD_BASELINE', value: es_password_baseline ],
					[$class: 'StringParameterValue', name: 'ES_SERVER_BASELINE', value: es_server_baseline ],
					[$class: 'StringParameterValue', name: 'ES_PORT_BASELINE', value: es_port_baseline ],
					[$class: 'StringParameterValue', name: 'BASELINE_ROUTER_UUID', value: baseline_router_uuid ],
					[$class: 'StringParameterValue', name: 'THROUGHPUT_TOLERANCE', value: throughput_tolerance ],
					[$class: 'StringParameterValue', name: 'LATENCY_TOLERANCE', value: latency_tolerance ],
					[$class: 'StringParameterValue', name: 'CERBERUS_URL', value: cerberus_url ],
					[$class: 'StringParameterValue', name: 'EMAIL_ID_FOR_RESULTS_SHEET', value: email_id_for_results_sheet ],
 					[$class: 'StringParameterValue', name: 'GSHEET_KEY', value: gsheet_key ],
					[$class: 'StringParameterValue', name: 'GSHEET_KEY_LOCATION', value: gsheet_key_location ]]

		} catch ( Exception e) {
			echo "ATS-SCALE-CI-HTTP Job failed with the following error: "
			echo "${e.getMessage()}"
			echo "Sending an email"
			mail(
				to: 'msheth@redhat.com',
				subject: 'ATS-SCALE-CI-HTTP job failed',
				body: """\
					Encoutered an error while running the ats-scale-ci-http job: ${e.getMessage()}\n\n
					Jenkins job: ${env.BUILD_URL}
			""")
			currentBuild.result = "FAILURE"
			sh "exit 1"
		}
		println "ATS-SCALE-CI-HTTP build ${http_build.getNumber()} completed successfully"
	}
}
