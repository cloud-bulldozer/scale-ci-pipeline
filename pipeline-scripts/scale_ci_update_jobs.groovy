#!/usr/bin/env groovy

def contact = "nelluri@redhat.com"
def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def job_name = "SCALE-CI-UPDATE-JOBS"
def create_jobs = SCALE_CI_UPDATE_JOBS.toString().toUpperCase()
def property_file_name = "jobs.properties"


println "Current pipeline job build id is '${pipeline_id}'"

// run jobs scale test
stage ('Create/Update scale-ci-pipeline jobs') {
	if (create_jobs == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${JOBS_PROPERTY_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def jobs_properties = readProperties file: property_file_name
			def jump_host = jobs_properties['JUMP_HOST']
			def user = jobs_properties['USER']
			def use_proxy = jobs_properties['USE_PROXY']
			def proxy_user = jobs_properties['PROXY_USER']
			def proxy_host = jobs_properties['PROXY_HOST']
			def token = jobs_properties['GITHUB_TOKEN']
			def config = jobs_properties['JJB_CONFIG_PATH']
			def repo = jobs_properties['SCALE_CI_WATCHER_REPO']
			def branch = jobs_properties['SCALE_CI_WATCHER_REPO_BRANCH']
			def path = jobs_properties['SCALE_CI_WATCHER_REPO_PATH']
			def workdir = jobs_properties['WORKDIR']
			def update_scale_ci_jobs = jobs_properties['UPDATE_SCALE_CI_JOBS']
			def add_public_key = jobs_properties['ADD_PUBLIC_KEY']
			def public_key = jobs_properties['PUBLIC_KEY']
			def keys_path = jobs_properties['KEYS_PATH']
	
			// Run the alderaan-create-jobs job
			try {
				jobs_build = build job: job_name,
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'JUMP_HOST', value: jump_host ],
						[$class: 'StringParameterValue', name: 'USER', value: user ],
						[$class: 'BooleanParameterValue', name: 'USE_PROXY', value: Boolean.valueOf(use_proxy) ],
						[$class: 'StringParameterValue', name: 'PROXY_USER', value: proxy_user ],
						[$class: 'StringParameterValue', name: 'PROXY_HOST', value: proxy_host ],
						[$class: 'StringParameterValue', name: 'GITHUB_TOKEN', value: token ],
						[$class: 'StringParameterValue', name: 'JJB_CONFIG_PATH', value: config ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_WATCHER_REPO', value: repo ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_WATCHER_REPO_BRANCH', value: branch ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_WATCHER_REPO_PATH', value: path ],
                                                [$class: 'StringParameterValue', name: 'WORKDIR', value: workdir ],
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
