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
			def skip_tls = uperf_properties['SKIP_TLS_VERIFICATION']
			def cluster_user = uperf_properties['CLUSTER_USER']
			def cluster_password = uperf_properties['CLUSTER_PASSWORD']
			def cluster_api_url = uperf_properties['CLUSTER_API_URL']
			def es_server = uperf_properties['ES_SERVER']
			def es_port = uperf_properties['ES_PORT']
			def metadata_collection = uperf_properties['METADATA_COLLECTION']
			def baseline_uperf_uuid = uperf_properties['BASELINE_UPERF_UUID']
			
			try {
				uperf_build = build job: 'RIPSAW-UPERF',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'BooleanParameterValue', name: 'SKIP_TLS_VERIFICATION', value: Boolean.valueOf(skip_tls) ],
						[$class: 'StringParameterValue', name: 'CLUSTER_USER', value: cluster_user ],
						[$class: 'StringParameterValue', name: 'CLUSTER_PASSWORD', value: cluster_password ],
						[$class: 'StringParameterValue', name: 'CLUSTER_API_URL', value: cluster_api_url ],
						[$class: 'StringParameterValue', name: 'ES_SERVER', value: es_server ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'BooleanParameterValue', name: 'METADATA_COLLECTION', value: Boolean.valueOf(metadata_collection) ],
						[$class: 'StringParameterValue', name: 'BASELINE_UPERF_UUID', value: baseline_uperf_uuid ]]
			} catch ( Exception e) {
				echo "UPERF Job failed with the following error: "
				echo "${e.getMessage()}"
				mail(
					to: 'jtaleric@redhat.com', 'msheth@redhat.com',
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
