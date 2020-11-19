#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def run_kubelet_density_light = KUBELET_DENSITY_LIGHT.toString().toUpperCase()
def property_file_name = "kubelet-density-light.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run uperf
stage ('kubelet-density-light') {
		if (run_kubelet_density_light == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${KUBELET_DENSITY_LIGHT_PROPERTIES_FILE} -O ${property_file_name}"
			sh "cat ${property_file_name}"
			def kubelet_density_light_properties = readProperties file: property_file_name
			def orchestration_user = kubelet_density_light_properties['ORCHESTRATION_USER']
			def orchestration_host = kubelet_density_light_properties['ORCHESTRATION_HOST']
			def sshkey_token = kubelet_density_light_properties['SSHKEY_TOKEN']
			def qps = kubelet_density_light_properties['QPS']
			def burst = kubelet_density_light_properties['BURST']
			def node_count = kubelet_density_light_properties['NODE_COUNT']
			def pods_per_node = kubelet_density_light_properties['PODS_PER_NODE']
			def es_server = kubelet_density_light_properties['ES_SERVER']
			def es_port = kubelet_density_light_properties['ES_PORT']
			def es_index = kubelet_density_light_properties['ES_INDEX']
			def kubeconfig_file = kubelet_density_light_properties['KUBECONFIG_FILE']
			def scale_ci_results_token = kubelet_density_light_properties['SCALE_CI_RESULTS_TOKEN']
			def prom_url = kubelet_density_light_properties['PROM_URL']
			def metadata_collection = kubelet_density_light_properties['METADATA_COLLECTION']
			def job_timeout = kubelet_density_light_properties['JOB_TIMEOUT']
			def step_size = kubelet_density_light_properties['STEP_SIZE']
			def metrics_profile = kubelet_density_light_properties['METRICS_PROFILE']
			def log_streaming = kubelet_density_light_properties['LOG_STREAMING']
			def cleanup = kubelet_density_light_properties['CLEANUP']
			def cleanup_when_finish = kubelet_density_light_properties['CLEANUP_WHEN_FINISH']
			def log_level = kubelet_density_light_properties['LOG_LEVEL']
			def cerberus_url = kubelet_density_light_properties['CERBERUS_URL']

			try {
				kubelet_density = build job: 'RIPSAW-KUBELET-DENSITY-LIGHT',
				parameters: [
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'StringParameterValue', name: 'QPS', value: qps ],
						[$class: 'StringParameterValue', name: 'BURST', value: burst ],
						[$class: 'StringParameterValue', name: 'NODE_COUNT', value: node_count ],
						[$class: 'StringParameterValue', name: 'PODS_PER_NODE', value: pods_per_node ],
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
				echo "kubelet-density-light Job failed with the following error: "
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
			println "kubelet-density-light build ${kubelet_density.getNumber()} completed successfully"
		}
	}
}
