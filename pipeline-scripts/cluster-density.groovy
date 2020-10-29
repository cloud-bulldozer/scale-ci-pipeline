#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def run_cluster_density = CLUSTER_DENSITY.toString().toUpperCase()
def property_file_name = "cluster-density.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run uperf
stage ('cluster-density') {
		if (run_cluster_density == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${CLUSTER_DENSITY_PROPERTIES_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def cluster_density_properties = readProperties file: property_file_name
			def orchestration_user = cluster_density_properties['ORCHESTRATION_USER']
			def orchestration_host = cluster_density_properties['ORCHESTRATION_HOST']
			def sshkey_token = cluster_density_properties['SSHKEY_TOKEN']
			def qps = cluster_density_properties['QPS']
			def burst = cluster_density_properties['BURST']
			def job_iterations = cluster_density_properties['JOB_ITERATIONS']
			def es_server = cluster_density_properties['ES_SERVER']
			def es_port = cluster_density_properties['ES_PORT']
			def es_index = cluster_density_properties['ES_INDEX']
			def kubeconfig_file = cluster_density_properties['KUBECONFIG_FILE']
			def scale_ci_results_token = cluster_density_properties['SCALE_CI_RESULTS_TOKEN']
			def prom_url = cluster_density_properties['PROM_URL']
			def metadata_collection = cluster_density_properties['METADATA_COLLECTION']
			def job_timeout = cluster_density_properties['JOB_TIMEOUT']
			def step_size = cluster_density_properties['STEP_SIZE']
			def metrics_profile = cluster_density_properties['METRICS_PROFILE']
			def log_streaming = cluster_density_properties['LOG_STREAMING']
			def cleanup = cluster_density_properties['CLEANUP']
			def cleanup_when_finish = cluster_density_properties['CLEANUP_WHEN_FINISH']
			def log_level = cluster_density_properties['LOG_LEVEL']
			def cerberus_url = cluster_density_properties['CERBERUS_URL']

			try {
				cluster_density = build job: 'RIPSAW-CLUSTER-DENSITY',
				parameters: [
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'QPS', value: qps ],
						[$class: 'StringParameterValue', name: 'BURST', value: burst ],
						[$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: job_iterations ],
						[$class: 'StringParameterValue', name: 'ES_SERVER', value: es_server ],
						[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port ],
						[$class: 'StringParameterValue', name: 'ES_INDEX', value: es_index ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_FILE', value: kubeconfig_file ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_RESULTS_TOKEN', value: scale_ci_results_token ],
						[$class: 'StringParameterValue', name: 'PROM_URL', value: prom_url ],
						[$class: 'BooleanParameterValue', name: 'METADATA_COLLECTION', value: Boolean.valueOf(metadata_collection) ],
						[$class: 'StringParameterValue', name: 'JOB_TIMEOUT', value: job_timeout ],
						[$class: 'StringParameterValue', name: 'STEP_SIZE', value: step_size ],
						[$class: 'StringParameterValue', name: 'METRICS_PROFILE', value: metrics_profile ],
						[$class: 'BooleanParameterValue', name: 'LOG_STREAMING', value: Boolean.valueOf(log_streaming) ],
						[$class: 'BooleanParameterValue', name: 'CLEANUP', value: Boolean.valueOf(cleanup) ],
						[$class: 'BooleanParameterValue', name: 'CLEANUP_WHEN_FINISH', value: Boolean.valueOf(cleanup_when_finish) ],
						[$class: 'StringParameterValue', name: 'LOG_LEVEL', value: log_level ],
						[$class: 'StringParameterValue', name: 'CERBERUS_URL', value: cerberus_url ]]
	
			} catch ( Exception e) {
				echo "cluster-density Job failed with the following error: "
				echo "${e.getMessage()}"
				mail(
					to: 'jtaleric@redhat.com, msheth@redhat.com',
					subject: 'Cluster-density job failed',
					body: """\
						Encoutered an error while running the uperf job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
				sh "exit 1"
			}
			println "cluster-density build ${cluster_density.getNumber()} completed successfully"
		}
	}
}
