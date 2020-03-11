#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def run_uperf = UPERF.toString().toUpperCase()
def property_file_name = "uperf.properties"

println "Current pipeline job build id is '${pipeline_id}'"

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
			// get properties file
			sh "wget ${UPERF_PROPERTIES_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def uperf_properties = readProperties file: property_file_name
			def orchestration_user = uperf_properties['ORCHESTRATION_USER']
			def orchestration_host = uperf_properties['ORCHESTRATION_HOST']
			def sshkey_token = uperf_properties['SSHKEY_TOKEN']
			def es_server = uperf_properties['ES_SERVER']
			def es_port = uperf_properties['ES_PORT']
			def metadata_collection = uperf_properties['METADATA_COLLECTION']
			def baseline_uperf_uuid = uperf_properties['BASELINE_UPERF_UUID']
			def hostnetwork_test = uperf_properties['HOSTNETWORK_TEST']
			def pod_test = uperf_properties['POD_TEST']
			def service_test = uperf_properties['SERVICE_TEST']
			
			try {
				uperf_build = build job: 'RIPSAW-UPERF',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'ES_SERVER', value: es_server ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'BooleanParameterValue', name: 'METADATA_COLLECTION', value: Boolean.valueOf(metadata_collection) ],
						[$class: 'StringParameterValue', name: 'BASELINE_UPERF_UUID', value: baseline_uperf_uuid ],
						[$class: 'BooleanParameterValue', name: 'HOSTNETWORK_TEST', value: Boolean.valueOf(hostnetwork_test) ],
						[$class: 'BooleanParameterValue', name: 'POD_TEST', value: Boolean.valueOf(pod_test) ],
						[$class: 'BooleanParameterValue', name: 'SERVICE_TEST', value: Boolean.valueOf(service_test) ]]
						
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
				currentBuild.result = "FAILURE"
				sh "exit 1"
			}
			println "UPERF build ${uperf_build.getNumber()} completed successfully"
		}
	}
}
