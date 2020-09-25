#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def tooling = TOOLING.toString().toUpperCase()
def property_file_name = "tooling.properties"

stage ('tooling') {
	if (tooling == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			echo "Root Workspace: ${env.ROOT_WORKSPACE}"
			echo "Properties Prefix: ${env.PROPERTIES_PREFIX}"
			println "Current pipeline job build id is '${pipeline_id}'"
			sh "cat ${env.PROPERTIES_PREFIX}/${property_file_name}"
			def tooling_properties = readProperties file: "${env.PROPERTIES_PREFIX}/${property_file_name}"
			def sshkey_token = tooling_properties['SSHKEY_TOKEN']
			def orchestration_host = tooling_properties['ORCHESTRATION_HOST']
			def orchestration_user = tooling_properties['ORCHESTRATION_USER']
			def pbench_image = tooling_properties['PBENCH_IMAGE']
			def kubeconfig_file = tooling_properties['KUBECONFIG_FILE']
			def pbench_server = tooling_properties['PBENCH_SERVER']

			try {
				tooling_build = build job: 'ATS-SCALE-CI-TOOLING',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'PBENCH_IMAGE', value: pbench_image ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_FILE', value: kubeconfig_file ],
						[$class: 'StringParameterValue', name: 'PBENCH_SERVER', value: pbench_server ]]
			} catch ( Exception e) {
				echo "TOOLING Job failed with the following error: "
				echo "${e.getMessage()}"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'tooling job failed',
					body: """\
						Encoutered an error while running the setup-tooling job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
				sh "exit 1"
			}
			println "TOOLING build ${tooling_build.getNumber()} completed successfully"
		}
	}
}
