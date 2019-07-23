#!/usr/bin/env groovy

def contact = "nelluri@redhat.com"
def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def job_name = "ATS-SCALE-CI-WATCHER"
def create_jobs = SCALE_CI_WATCHER.toString().toUpperCase()
def property_file_name = "jobs.properties"


println "Current pipeline job build id is '${pipeline_id}'"

// run jobs scale test
stage ('SCALE-CI-WATCHER') {
	if (create_jobs == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${SCALE_CI_WATCHER_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def watcher_properties = readProperties file: property_file_name
			def url = watcher_properties['JENKINS_URL']
			def config = watcher_properties['JJB_CONFIG_PATH']
			def repo = watcher_properties['SCALE_CI_WATCHER_REPO']
			def branch = watcher_properties['SCALE_CI_WATCHER_REPO_BRANCH']
			def update_scale_ci_jobs = watcher_properties['UPDATE_SCALE_CI_JOBS']
			def add_public_key = watcher_properties['ADD_PUBLIC_KEY']
			def public_key = watcher_properties['PUBLIC_KEY']
			def keys_path = watcher_properties['KEYS_PATH']
					
			// Run scale-ci-watcher job
			try {
				jobs_build = build job: job_name,
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'JENKINS_URL', value: url ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_WATCHER_REPO', value: repo ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_WATCHER_REPO_BRANCH', value: branch ],
                                                [$class: 'StringParameterValue', name: 'UPDATE_SCALE_CI_JOBS', value: update_scale_ci_jobs ],
                                                [$class: 'StringParameterValue', name: 'ADD_PUBLIC_KEY', value: add_public_key ],
                                                [$class: 'StringParameterValue', name: 'PUBLIC_KEY', value: public_key ],
                                                [$class: 'StringParameterValue', name: 'KEYS_PATH', value: keys_path ]]
				println("${job_name} build ${jobs_build.getNumber()} completed successfully!")
			} catch ( Exception e) {
				echo "{job_name} Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: contact,
					subject: "SCALE-CI-PIPLINE: '${job_name}' failed",
					body: """\
						Encoutered an error while running the  job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
		}
	}
}
