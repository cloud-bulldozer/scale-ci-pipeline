#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def build_tracker = SCALE_CI_BUILD_TRACKER.toString().toUpperCase()
def property_file_name = "build_tracker.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run scale-ci build tracker
stage ('build_tracker') {
	if (build_tracker == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${SCALE_CI_BUILD_TRACKER_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def build_tracker_properties = readProperties file: property_file_name
			def orchestration_host = build_tracker_properties['ORCHESTRATION_HOST']
			def orchestration_user = build_tracker_properties['ORCHESTRATION_USER']
			def sshkey_token = build_tracker_properties['SSHKEY_TOKEN']
			def build_info_url = build_tracker_properties['BUILD_INFO_URL']
			def build_info_destination = build_tracker_properties['BUILD_INFO_DESTINATION']
			def umb_message_format = build_tracker_properties['UMB_MESSAGE_FORMAT']

			// Run build tracker job
			try {
				build_tracker_build = build job: 'SCALE-CI-BUILD-TRACKER',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'BUILD_INFO_URL', value: build_info_url ],
						[$class: 'StringParameterValue', name: 'BUILD_INFO_DESTINATION', value: build_info_destination ],
						[$class: 'BooleanParameterValue', name: 'UMB_MESSAGE_FORMAT', value: Boolean.valueOf(umb_message_format) ]]
			} catch ( Exception e) {
				echo "SCALE-CI-BUILD-TRACKER Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'jtaleric@redhat.com, wabouham@redhat.com, rzaleski@redhat.com, nelluri@redhat.com',
					subject: 'SCALE-CI-BUILD-TRACKER job failed',
					body: """\
						Encoutered an error while running the baseline-scale-test job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "SCALE-CI-BUILD-TRACKER build ${build_tracker_build.getNumber()} completed successfully"
		}
	}
}
