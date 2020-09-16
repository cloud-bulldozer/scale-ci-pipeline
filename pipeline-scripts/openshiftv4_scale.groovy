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
		def kubeconfig_file = scale_properties['KUBECONFIG_FILE']
		def orchestration_host = scale_properties['ORCHESTRATION_HOST']
		def orchestration_user = scale_properties['ORCHESTRATION_USER']
		def poll_interval = scale_properties['POLL_INTERVAL']
		def post_sleep = scale_properties['POST_SLEEP']
		def timeout = scale_properties['TIMEOUT']
		def runs = scale_properties['RUNS']
		def scale = scale_properties['SCALE']
		if (pipeline == "TRUE" && pipeline_stage == "2") {
			scale = scale_properties['SCALE_V2']
		} else if (pipeline == "TRUE" && pipeline_stage == "3") {
			scale = scale_properties['SCALE_V3']
		}
		def es_user = scale_properties['ES_USER']
		def es_password = scale_properties['ES_PASSWORD']
		def es_server = scale_properties['ES_SERVER']
		def es_port = scale_properties['ES_PORT']
		def metadata_collection = scale_properties['METADATA_COLLECTION']
		def baseline_uuid = scale_properties['BASELINE_UUID']
		def compare = scale_properties['COMPARE']
		def baseline_cloud_name = scale_properties['BASELINE_CLOUD_NAME']
		def es_user_baseline = scale_properties['ES_USER_BASELINE']
		def es_password_baseline = scale_properties['ES_PASSWORD_BASELINE']
		def es_server_baseline = scale_properties['ES_SERVER_BASELINE']
		def es_port_baseline = scale_properties['ES_PORT_BASELINE']
		def cerberus_url = scale_properties['CERBERUS_URL']
		def scale_poll_attempts = scale_properties['SCALE_POLL_ATTEMPTS']
		def expected_scale_duration = scale_properties['EXPECTED_SCALE_DURATION']
		def jenkins_user = scale_properties['JENKINS_USER']
		def jenkins_api_token = scale_properties['JENKINS_API_TOKEN']
		def jenkins_es_server = scale_properties['JENKINS_ES_SERVER']

		try {
			scale_build = build job: 'ATS-SCALE-CI-SCALE',
			parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
					[$class: 'StringParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
					[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
					[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
					[$class: 'StringParameterValue', name: 'JENKINS_USER', value: jenkins_user ],
					[$class: 'StringParameterValue', name: 'JENKINS_API_TOKEN', value: jenkins_api_token ],
					[$class: 'StringParameterValue', name: 'JENKINS_ES_SERVER', value: jenkins_es_server ],
					[$class: 'StringParameterValue', name: 'WORKLOAD_IMAGE', value: workload_image ],
					[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_NODE_SELECTOR', value: Boolean.valueOf(workload_job_node_selector) ],
					[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_TAINT', value: Boolean.valueOf(workload_job_taint)  ],
					[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_PRIVILEGED', value: Boolean.valueOf(workload_job_privileged)  ],
					[$class: 'StringParameterValue', name: 'KUBECONFIG_FILE', value: kubeconfig_file ],
					[$class: 'StringParameterValue', name: 'SCALE', value: scale ],
					[$class: 'StringParameterValue', name: 'POLL_INTERVAL', value: poll_interval ],
					[$class: 'StringParameterValue', name: 'POST_SLEEP', value: post_sleep ],
					[$class: 'StringParameterValue', name: 'TIMEOUT', value: timeout ],
					[$class: 'StringParameterValue', name: 'RUNS', value: runs ],
					[$class: 'StringParameterValue', name: 'ES_USER', value: es_user ],
					[$class: 'StringParameterValue', name: 'ES_PASSWORD', value: es_password ],
					[$class: 'StringParameterValue', name: 'ES_SERVER', value: es_server ],
					[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
					[$class: 'BooleanParameterValue', name: 'METADATA_COLLECTION', value: Boolean.valueOf(metadata_collection) ],
					[$class: 'BooleanParameterValue', name: 'COMPARE', value: Boolean.valueOf(compare) ],
					[$class: 'StringParameterValue', name: 'BASELINE_CLOUD_NAME', value: baseline_cloud_name ],
					[$class: 'StringParameterValue', name: 'ES_USER_BASELINE', value: es_user_baseline ],
					[$class: 'StringParameterValue', name: 'ES_PASSWORD_BASELINE', value: es_password_baseline ],
					[$class: 'StringParameterValue', name: 'ES_SERVER_BASELINE', value: es_server_baseline ],
					[$class: 'StringParameterValue', name: 'ES_PORT_BASELINE', value: es_port_baseline ],
					[$class: 'StringParameterValue', name: 'BASELINE_UUID', value: baseline_uuid ],
					[$class: 'StringParameterValue', name: 'CERBERUS_URL', value: cerberus_url ]]
		} catch ( Exception e) {
			echo "ATS-SCALE-CI-SCALE Job failed with the following error: "
			echo "${e.getMessage()}"
			echo "Sending an email"
			mail(
				to: 'nelluri@redhat.com, msheth@redhat.com',
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
