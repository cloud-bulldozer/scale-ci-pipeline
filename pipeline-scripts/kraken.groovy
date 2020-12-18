#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def kraken = KRAKEN.toString().toUpperCase()
def property_file_name = "kraken.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run kraken
stage ('kraken') {
	if (kraken == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${KRAKEN_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def kraken_properties = readProperties file: property_file_name
			def orchestration_host = kraken_properties['ORCHESTRATION_HOST']
			def orchestration_user = kraken_properties['ORCHESTRATION_USER']
			def sshkey_token = kraken_properties['SSHKEY_TOKEN']
			def kubeconfig_path = kraken_properties['KUBECONFIG_PATH']
			def kraken_dir = kraken_properties['KRAKEN_DIR']
			def kraken_config = kraken_properties['KRAKEN_CONFIG']
			def kraken_repository = kraken_properties['KRAKEN_REPOSITORY']
			def scenarios_folder_path = kraken_properties['SCENARIOS_FOLDER_PATH']
			def scenarios = kraken_properties['SCENARIOS']
			def node_scenarios = kraken_properties['NODE_SCENARIOS']
			def cluster_shut_down_scenario = kraken_properties['CLUSTER_SHUT_DOWN_SCENARIO']
			def exit_on_failure = kraken_properties['EXIT_ON_FAILURE']
			def cerberus_enabled = kraken_properties['CERBERUS_ENABLED']
			def cerberus_url = kraken_properties['CERBERUS_URL']
			def wait_duration = kraken_properties['WAIT_DURATION']
			def iterations = kraken_properties['ITERATIONS']
			def daemon_mode = kraken_properties['DAEMON_MODE']

			// Run kraken job
			try {
				kraken_build = build job: 'kraken',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_PATH', value: kubeconfig_path ],
						[$class: 'StringParameterValue', name: 'KRAKEN_DIR', value: kraken_dir ],
						[$class: 'StringParameterValue', name: 'KRAKEN_CONFIG', value: kraken_config ],
						[$class: 'StringParameterValue', name: 'KRAKEN_REPOSITORY', value: kraken_repository ],
						[$class: 'StringParameterValue', name: 'SCENARIOS_FOLDER_PATH', value: scenarios_folder_path ],
						[$class: 'StringParameterValue', name: 'SCENARIOS', value: scenarios ],
						[$class: 'StringParameterValue', name: 'NODE_SCENARIOS', value: node_scenarios ],
						[$class: 'StringParameterValue', name: 'CLUSTER_SHUT_DOWN_SCENARIO', value: cluster_shut_down_scenario ],
						[$class: 'BooleanParameterValue', name: 'EXIT_ON_FAILURE', value: Boolean.valueOf(exit_on_failure) ],
						[$class: 'BooleanParameterValue', name: 'CERBERUS_ENABLED', value: Boolean.valueOf(cerberus_enabled) ],
						[$class: 'StringParameterValue', name: 'CERBERUS_URL', value: cerberus_url ],
						[$class: 'StringParameterValue', name: 'WAIT_DURATION', value: wait_duration ],
						[$class: 'StringParameterValue', name: 'ITERATIONS', value: iterations ],
						[$class: 'BooleanParameterValue', name: 'DAEMON_MODE', value: Boolean.valueOf(daemon_mode) ]]
			} catch ( Exception e) {
				echo "kraken Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'kraken job failed',
					body: """\
						Encoutered an error while running the kraken job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "kraken build ${kraken_build.getNumber()} completed successfully"
		}
	}
}
