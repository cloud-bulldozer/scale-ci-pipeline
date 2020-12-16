#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def run_uperf = UPERF.toString().toUpperCase()
def property_file_name = "uperf.properties"
def pipeline = PIPELINE.toString().toUpperCase()

println "Current pipeline job build id is '${pipeline_id}'"
println "Current pipeline run id is ${env.RUN_ID}"

// run uperf
stage ('uperf') {
		if (run_uperf == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			println "Node run id is ${env.RUN_ID}"
			// get properties file
			sh "wget ${UPERF_PROPERTIES_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def uperf_properties = readProperties file: property_file_name
			def orchestration_user = uperf_properties['ORCHESTRATION_USER']
			def orchestration_host = uperf_properties['ORCHESTRATION_HOST']
			def sshkey_token = uperf_properties['SSHKEY_TOKEN']
			def es_user = uperf_properties['ES_USER']
			def es_password = uperf_properties['ES_PASSWORD']
			def es_server = uperf_properties['ES_SERVER']
			def es_port = uperf_properties['ES_PORT']
			def jenkins_user = uperf_properties['JENKINS_USER']
			def jenkins_api_token = uperf_properties['JENKINS_API_TOKEN']
			def jenkins_es_server = uperf_properties['JENKINS_ES_SERVER']
			def metadata_collection = uperf_properties['METADATA_COLLECTION']
			def hostnetwork_test = uperf_properties['HOSTNETWORK_TEST']
			def pod_test = uperf_properties['POD_TEST']
			def service_test = uperf_properties['SERVICE_TEST']
			def smoke_test = uperf_properties['SMOKE_TEST']
			def compare = uperf_properties['COMPARE']
			def baseline_cloud_name = uperf_properties['BASELINE_CLOUD_NAME']
			def es_user_baseline = uperf_properties['ES_USER_BASELINE']
			def es_password_baseline = uperf_properties['ES_PASSWORD_BASELINE']
			def es_server_baseline = uperf_properties['ES_SERVER_BASELINE']
			def es_port_baseline = uperf_properties['ES_PORT_BASELINE']
			def baseline_hostnet_uuid = uperf_properties['BASELINE_HOSTNET_UUID']
			def baseline_pod_1p_uuid = uperf_properties['BASELINE_POD_1P_UUID']
			def baseline_pod_2p_uuid = uperf_properties['BASELINE_POD_2P_UUID']
			def baseline_pod_4p_uuid = uperf_properties['BASELINE_POD_4P_UUID']
			def baseline_svc_1p_uuid = uperf_properties['BASELINE_SVC_1P_UUID']
			def baseline_svc_2p_uuid = uperf_properties['BASELINE_SVC_2P_UUID']
			def baseline_svc_4p_uuid = uperf_properties['BASELINE_SVC_4P_UUID']
			def baseline_multus_uuid = uperf_properties['BASELINE_MULTUS_UUID']
			def throughput_tolerance = uperf_properties['THROUGHPUT_TOLERANCE']
			def latency_tolerance = uperf_properties['LATENCY_TOLERANCE']
			def cerberus_url = uperf_properties['CERBERUS_URL']	
			def email_id_for_results_sheet = uperf_properties['EMAIL_ID_FOR_RESULTS_SHEET']
			def gold_sdn  = uperf_properties['GOLD_SDN']
			def gold_ocp_version  = uperf_properties['GOLD_OCP_VERSION']
			def es_gold = uperf_properties['ES_GOLD']
			def compare_with_gold = uperf_properties['COMPARE_WITH_GOLD']

			try {
				uperf_build = build job: 'RIPSAW-UPERF',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'ES_USER', value: es_user ],
						[$class: 'StringParameterValue', name: 'ES_PASSWORD', value: es_password ],
						[$class: 'StringParameterValue', name: 'ES_SERVER', value: es_server ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'StringParameterValue', name: 'JENKINS_USER', value: jenkins_user ],
						[$class: 'StringParameterValue', name: 'JENKINS_API_TOKEN', value: jenkins_api_token ],
						[$class: 'StringParameterValue', name: 'JENKINS_ES_SERVER', value: jenkins_es_server ],
						[$class: 'BooleanParameterValue', name: 'METADATA_COLLECTION', value: Boolean.valueOf(metadata_collection) ],
						[$class: 'BooleanParameterValue', name: 'HOSTNETWORK_TEST', value: Boolean.valueOf(hostnetwork_test) ],
						[$class: 'BooleanParameterValue', name: 'POD_TEST', value: Boolean.valueOf(pod_test) ],
						[$class: 'BooleanParameterValue', name: 'SERVICE_TEST', value: Boolean.valueOf(service_test) ],
						[$class: 'BooleanParameterValue', name: 'SMOKE_TEST', value: Boolean.valueOf(smoke_test) ],
						[$class: 'BooleanParameterValue', name: 'COMPARE', value: Boolean.valueOf(compare) ],
						[$class: 'StringParameterValue', name: 'BASELINE_CLOUD_NAME', value: baseline_cloud_name ],
						[$class: 'StringParameterValue', name: 'ES_USER_BASELINE', value: es_user_baseline ],
						[$class: 'StringParameterValue', name: 'ES_PASSWORD_BASELINE', value: es_password_baseline ],
						[$class: 'StringParameterValue', name: 'ES_SERVER_BASELINE', value: es_server_baseline ],
						[$class: 'StringParameterValue', name: 'ES_PORT_BASELINE', value: es_port_baseline ],
						[$class: 'StringParameterValue', name: 'BASELINE_HOSTNET_UUID', value: baseline_hostnet_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_POD_1P_UUID', value: baseline_pod_1p_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_POD_2P_UUID', value: baseline_pod_2p_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_POD_4P_UUID', value: baseline_pod_4p_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_SVC_1P_UUID', value: baseline_svc_1p_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_SVC_2P_UUID', value: baseline_svc_2p_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_SVC_4P_UUID', value: baseline_svc_4p_uuid ],
						[$class: 'StringParameterValue', name: 'BASELINE_MULTUS_UUID', value: baseline_multus_uuid ],
						[$class: 'StringParameterValue', name: 'THROUGHPUT_TOLERANCE', value: throughput_tolerance ],
						[$class: 'StringParameterValue', name: 'LATENCY_TOLERANCE', value: latency_tolerance ],
						[$class: 'StringParameterValue', name: 'CERBERUS_URL', value: cerberus_url ],
						[$class: 'StringParameterValue', name: 'EMAIL_ID_FOR_RESULTS_SHEET', value: email_id_for_results_sheet ],
						[$class: 'StringParameterValue', name: 'GOLD_SDN', value: gold_sdn ],
						[$class: 'StringParameterValue', name: 'GOLD_OCP_VERSION', value: gold_ocp_version ],
						[$class: 'StringParameterValue', name: 'ES_GOLD', value: es_gold ],
						[$class: 'BooleanParameterValue', name: 'COMPARE_WITH_GOLD', value: Boolean.valueOf(compare_with_gold) ]]
	
			} catch ( Exception e) {
				echo "UPERF Job failed with the following error: "
				echo "${e.getMessage()}"
				mail(
					to: 'jtaleric@redhat.com, msheth@redhat.com',
					subject: 'Uperf job failed',
					body: """\
						Encoutered an error while running the uperf job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
                                if(pipeline){
                                unstable('RIPSAW-UPERF job FAILED, moving on to next job in the pipeline')
                                } else{
                                currentBuild.result = "FAILURE"
                                }
			}
			println "UPERF build completed with status ${currentBuild.result}"
		}
	}
}
