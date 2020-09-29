#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def fio = FIO.toString().toUpperCase()
def property_file_name = "fio.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run nodevertical scale test
stage ('fio_scale_test') {
	if (fio == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
//			sh "wget ${FIO_PROPERTY_FILE} -O ${property_file_name}"
			echo "Root Workspace: ${env.ROOT_WORKSPACE}"
			echo "Properties Prefix: ${env.PROPERTIES_PREFIX}"
			println "Current pipeline job build id is '${pipeline_id}'"
			sh "cat ${env.PROPERTIES_PREFIX}/${property_file_name}"
			def fio_properties = readProperties file: "${env.PROPERTIES_PREFIX}/${property_file_name}"
			def orchestration_host = fio_properties['ORCHESTRATION_HOST']
			def orchestration_user = fio_properties['ORCHESTRATION_USER']
			def workload_image = fio_properties['WORKLOAD_IMAGE']
			def workload_job_node_selector = fio_properties['WORKLOAD_JOB_NODE_SELECTOR']
			def workload_job_taint = fio_properties['WORKLOAD_JOB_TAINT']
			def workload_job_privileged = fio_properties['WORKLOAD_JOB_PRIVILEGED']
			def kubeconfig_file = fio_properties['KUBECONFIG_FILE']
			def pbench_instrumentation = fio_properties['PBENCH_INSTRUMENTATION']
			def enable_pbench_agents = fio_properties['ENABLE_PBENCH_AGENTS']
			def enable_pbench_copy = fio_properties['ENABLE_PBENCH_COPY']
			def pbench_server = fio_properties['PBENCH_SERVER']
			def scale_ci_results_token = fio_properties['SCALE_CI_RESULTS_TOKEN']
			def job_completion_poll_attempts = fio_properties['JOB_COMPLETION_POLL_ATTEMPTS']
			def snafu_user = fio_properties['SNAFU_USER']
			def snafu_cluster_name = fio_properties['SNAFU_CLUSTER_NAME']
			def es_host = fio_properties['ES_HOST']
			def es_port = fio_properties['ES_PORT']
			def es_index_prefix = fio_properties['ES_INDEX_PREFIX']
			def pbench_ssh_private_key_file = fio_properties['PBENCH_SSH_PRIVATE_KEY_FILE']
			def pbench_ssh_public_key_file = fio_properties['PBENCH_SSH_PUBLIC_KEY_FILE']
			def fiotest_prefix = fio_properties['FIOTEST_PREFIX']
			def fiotest_cleanup = fio_properties['FIOTEST_CLEANUP']
			def fiotest_basename = fio_properties['FIOTEST_BASENAME']
			def fiotest_maxpods = fio_properties['FIOTEST_MAXPODS']
			def fiotest_pod_image = fio_properties['FIOTEST_POD_IMAGE']
			def fiotest_pause = fio_properties['FIOTEST_PAUSE']
			def fiotest_stepsize = fio_properties['FIOTEST_STEPSIZE']
			def fiotest_storage_size = fio_properties['FIOTEST_STORAGE_SIZE']
			def fiotest_storageclass = fio_properties['FIOTEST_STORAGECLASS']
			def fiotest_access_modes = fio_properties['FIOTEST_ACCESS_MODES']
			def fiotest_bs = fio_properties['FIOTEST_BS']
			def fiotest_filename = fio_properties['FIOTEST_FILENAME']
			def fiotest_filesize = fio_properties['FIOTEST_FILESIZE']
			def fiotest_direct = fio_properties['FIOTEST_DIRECT']
			def fiotest_runtime = fio_properties['FIOTEST_RUNTIME']
			def fiotest_iodepth = fio_properties['FIOTEST_IODEPTH']
			def fiotest_testtype = fio_properties['FIOTEST_TESTTYPE']
			def fiotest_samples = fio_properties['FIOTEST_SAMPLES']
			def fiotest_nodeselector = fio_properties['FIOTEST_NODESELECTOR']
			try {
				fio_build = build job: 'ATS-SCALE-CI-FIO',
				parameters: [   [$class: 'StringParameterValue', name: 'SNAFU_USER', value: snafu_user],
				[$class: 'StringParameterValue', name: 'SNAFU_CLUSTER_NAME', value: snafu_cluster_name],
				[$class: 'StringParameterValue', name: 'ES_HOST', value: es_host],
				[$class: 'StringParameterValue', name: 'ES_PORT', value: es_port],
				[$class: 'StringParameterValue', name: 'ES_INDEX_PREFIX', value: es_index_prefix],
				[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host],
				[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user],
				[$class: 'StringParameterValue', name: 'WORKLOAD_IMAGE', value: workload_image ],
				[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_NODE_SELECTOR', value: Boolean.valueOf(workload_job_node_selector) ],
				[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_TAINT', value: Boolean.valueOf(workload_job_taint)  ],
				[$class: 'BooleanParameterValue', name: 'WORKLOAD_JOB_PRIVILEGED', value: Boolean.valueOf(workload_job_privileged)  ],
				[$class: 'StringParameterValue', name: 'KUBECONFIG_FILE', value: kubeconfig_file ],
				[$class: 'BooleanParameterValue', name: 'PBENCH_INSTRUMENTATION', value: Boolean.valueOf(pbench_instrumentation)  ],
				[$class: 'BooleanParameterValue', name: 'ENABLE_PBENCH_AGENTS', value: Boolean.valueOf(enable_pbench_agents)  ],
				[$class: 'BooleanParameterValue', name: 'ENABLE_PBENCH_COPY', value: Boolean.valueOf(enable_pbench_copy)  ],
				[$class: 'StringParameterValue', name: 'PBENCH_SERVER', value: pbench_server ],
				[$class: 'StringParameterValue', name: 'SCALE_CI_RESULTS_TOKEN', value: scale_ci_results_token ],
				[$class: 'StringParameterValue', name: 'JOB_COMPLETION_POLL_ATTEMPTS', value: job_completion_poll_attempts ],
				[$class: 'StringParameterValue', name: 'PBENCH_SSH_PRIVATE_KEY_FILE', value: pbench_ssh_private_key_file ],
				[$class: 'StringParameterValue', name: 'PBENCH_SSH_PUBLIC_KEY_FILE', value: pbench_ssh_public_key_file ],
				[$class: 'StringParameterValue', name: 'FIOTEST_PREFIX', value: fiotest_prefix ],
				[$class: 'BooleanParameterValue', name: 'FIOTEST_CLEANUP', value: Boolean.valueOf(fiotest_cleanup)],
				[$class: 'StringParameterValue', name: 'FIOTEST_BASENAME', value: fiotest_basename],
				[$class: 'StringParameterValue', name: 'FIOTEST_MAXPODS', value: fiotest_maxpods],
				[$class: 'StringParameterValue', name: 'FIOTEST_POD_IMAGE', value: fiotest_pod_image],
				[$class: 'StringParameterValue', name: 'FIOTEST_PAUSE', value: fiotest_pause],
				[$class: 'StringParameterValue', name: 'FIOTEST_STEPSIZE', value: fiotest_stepsize],
				[$class: 'StringParameterValue', name: 'FIOTEST_STORAGE_SIZE', value: fiotest_storage_size],
				[$class: 'StringParameterValue', name: 'FIOTEST_STORAGECLASS', value: fiotest_storageclass],
				[$class: 'StringParameterValue', name: 'FIOTEST_ACCESS_MODES', value: fiotest_access_modes],
				[$class: 'StringParameterValue', name: 'FIOTEST_BS', value: fiotest_bs],
				[$class: 'StringParameterValue', name: 'FIOTEST_FILENAME', value: fiotest_filename],
				[$class: 'StringParameterValue', name: 'FIOTEST_FILESIZE', value: fiotest_filesize],
				[$class: 'BooleanParameterValue', name: 'FIOTEST_DIRECT', value: Boolean.valueOf(fiotest_direct)],
				[$class: 'StringParameterValue', name: 'FIOTEST_RUNTIME', value: fiotest_runtime],
				[$class: 'StringParameterValue', name: 'FIOTEST_IODEPTH', value: fiotest_iodepth],
				[$class: 'StringParameterValue', name: 'FIOTEST_TESTTYPE', value: fiotest_testtype],
				[$class: 'StringParameterValue', name: 'FIOTEST_SAMPLES', value: fiotest_samples],
				[$class: 'StringParameterValue', name: 'FIOTEST_NODESELECTOR', value: fiotest_nodeselector]]

			} catch ( Exception e) {
				echo "ATS-SCALE-CI-FIO Job failed with the following error: "
				echo "${e.getMessage()}"
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "ATS-SCALE-CI-FIO build ${fio_build.getNumber()} completed successfully"
		}
	}
}
