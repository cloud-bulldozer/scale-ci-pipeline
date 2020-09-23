#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def ocp_install = OPENSHIFTv4_INSTALL_ON_OSP.toString().toUpperCase()
def property_file_name = "ocp_install.properties"

println "Current pipeline job build id is '${pipeline_id}'"

// run openshift 4.x install
stage ('OCP 4.X INSTALL') {
	if (ocp_install == "TRUE") {
		currentBuild.result = "SUCCESS"
		node(node_label) {
			// get properties file
			if (fileExists(property_file_name)) {
				println "Looks like the property file already exists, erasing it"
				sh "rm ${property_file_name}"
			}
			// get properties file
			sh "wget ${OPENSHIFTv4_ON_OSP_PROPERTY_FILE} -O ${property_file_name}"
			def openshiftv4_properties = readProperties file: property_file_name
			def orchestration_host = openshiftv4_properties['ORCHESTRATION_HOST']
			def orchestration_user = openshiftv4_properties['ORCHESTRATION_USER']
			def sshkey_token = openshiftv4_properties['SSHKEY_TOKEN']
			def openshift_install = openshiftv4_properties['OPENSHIFT_INSTALL']
			def scale_ci_build_trigger = openshiftv4_properties['SCALE_CI_BUILD_TRIGGER']
			def scale_ci_build_trigger_url = openshiftv4_properties['SCALE_CI_BUILD_TRIGGER_URL']
			def enable_dittybopper = openshiftv4_properties['ENABLE_DITTYBOPPER']
			def kubeconfig_path = openshiftv4_properties['KUBECONFIG_PATH']
			def openshift_install_apiversion = openshiftv4_properties['OPENSHIFT_INSTALL_APIVERSION']
			def openshift_install_ssh_pub_key_file = openshiftv4_properties['OPENSHIFT_INSTALL_SSH_PUB_KEY_FILE']
			def openshift_install_pull_secret = openshiftv4_properties['OPENSHIFT_INSTALL_PULL_SECRET']
			def openshift_install_quay_registry_token = openshiftv4_properties['OPENSHIFT_INSTALL_QUAY_REGISTRY_TOKEN']
			def openshift_install_image_registry = openshiftv4_properties['OPENSHIFT_INSTALL_IMAGE_REGISTRY']
			def openshift_install_registry_token = openshiftv4_properties['OPENSHIFT_INSTALL_REGISTRY_TOKEN']
			def openshift_toggle_infra_node = openshiftv4_properties['OPENSHIFT_TOGGLE_INFRA_NODE']
			def scale_lab_cloud_name = openshiftv4_properties['SCALE_LAB_CLOUD_NAME']
   			def ansible_ssh_password = openshiftv4_properties['ANSIBLE_SSH_PASSWORD']
			def openstack_version = openshiftv4_properties['OPENSTACK_VERSION']
			def osp_public_external_interface = openshiftv4_properties['OSP_PUBLIC_EXTERNAL_INTERFACE']
			def osp_public_network_name = openshiftv4_properties['OSP_PUBLIC_NETWORK_NAME']
			def osp_external_gateway = openshiftv4_properties['OSP_EXTERNAL_GATEWAY']
			def osp_external_net_cidr = openshiftv4_properties['OSP_EXTERNAL_NET_CIDR']
			def osp_external_allocation_pools_start = openshiftv4_properties['OSP_EXTERNAL_ALLOCATION_POOLS_START']
			def osp_external_allocation_pools_end = openshiftv4_properties['OSP_EXTERNAL_ALLOCATION_POOLS_END']
			def osp_external_interface_default_route = openshiftv4_properties['OSP_EXTERNAL_INTERFACE_DEFAULT_ROUTE']
			def openshift_base_domain = openshiftv4_properties['OPENSHIFT_BASE_DOMAIN']
			def openshift_cluster_name = openshiftv4_properties['OPENSHIFT_CLUSTER_NAME']
			def openshift_release = openshiftv4_properties['OPENSHIFT_RELEASE']
			def openshift_master_count = openshiftv4_properties['OPENSHIFT_MASTER_COUNT']
			def openshift_worker_count = openshiftv4_properties['OPENSHIFT_WORKER_COUNT']
			def openshift_master_instance_type = openshiftv4_properties['OPENSHIFT_MASTER_INSTANCE_TYPE']
			def openshift_worker_instance_type = openshiftv4_properties['OPENSHIFT_WORKER_INSTANCE_TYPE']
			def openshift_master_root_volume_size = openshiftv4_properties['OPENSHIFT_MASTER_ROOT_VOLUME_SIZE']
			def openshift_master_root_volume_type = openshiftv4_properties['OPENSHIFT_MASTER_ROOT_VOLUME_TYPE']
			def openshift_master_root_volume_iops = openshiftv4_properties['OPENSHIFT_MASTER_ROOT_VOLUME_IOPS']
			def openshift_worker_root_volume_size = openshiftv4_properties['OPENSHIFT_WORKER_ROOT_VOLUME_SIZE']
			def openshift_worker_root_volume_type = openshiftv4_properties['OPENSHIFT_WORKER_ROOT_VOLUME_TYPE']
			def openshift_worker_root_volume_iops = openshiftv4_properties['OPENSHIFT_WORKER_ROOT_VOLUME_IOPS']
			def openshift_cidr = openshiftv4_properties['OPENSHIFT_CIDR']
			def openshift_machine_cidr = openshiftv4_properties['OPENSHIFT_MACHINE_CIDR']
			def openshift_network_type = openshiftv4_properties['OPENSHIFT_NETWORK_TYPE']
			def openshift_service_network = openshiftv4_properties['OPENSHIFT_SERVICE_NETWORK']
			def openshift_host_prefix = openshiftv4_properties['OPENSHIFT_HOST_PREFIX']
			def openshift_post_install = openshiftv4_properties['OPENSHIFT_POST_INSTALL']
			def openshift_post_install_poll_attempts = openshiftv4_properties['OPENSHIFT_POST_INSTALL_POLL_ATTEMPTS']
			def openshift_toggle_workload_node = openshiftv4_properties['OPENSHIFT_TOGGLE_WORKLOAD_NODE']
			def machineset_metadata_label_prefix = openshiftv4_properties['MACHINESET_METADATA_LABEL_PREFIX']
			def openshift_infra_node_instance_type = openshiftv4_properties['OPENSHIFT_INFRA_NODE_INSTANCE_TYPE']
			def openshift_workload_node_instance_type = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_INSTANCE_TYPE']
			def openshift_workload_node_volume_size = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_VOLUME_SIZE']
			def openshift_workload_node_volume_type = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_VOLUME_TYPE']
			def openshift_workload_node_volume_iops = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_VOLUME_IOPS']
			def openshift_prometheus_retention_period = openshiftv4_properties['OPENSHIFT_PROMETHEUS_RETENTION_PERIOD']
			def openshift_prometheus_storage_class = openshiftv4_properties['OPENSHIFT_PROMETHEUS_STORAGE_CLASS']
			def openshift_prometheus_storage_size = openshiftv4_properties['OPENSHIFT_PROMETHEUS_STORAGE_SIZE']
			def openshift_alertmanager_storage_class = openshiftv4_properties['OPENSHIFT_ALERTMANAGER_STORAGE_CLASS']
			def openshift_alertmanager_storage_size = openshiftv4_properties['OPENSHIFT_ALERTMANAGER_STORAGE_SIZE']
			def openshift_post_config = openshiftv4_properties['OPENSHIFT_POST_CONFIG']
			def openshift_debug_config = openshiftv4_properties['OPENSHIFT_DEBUG_CONFIG']
			def cerberus_config_path = openshiftv4_properties['CERBERUS_CONFIG_PATH']
			def cerberus_enable = openshiftv4_properties['CERBERUS_ENABLE']
			def cerberus_image = openshiftv4_properties['CERBERUS_IMAGE']
			def cerberus_url = openshiftv4_properties['CERBERUS_URL']
			def watch_nodes = openshiftv4_properties['WATCH_NODES']
			def watch_cluster_operators = openshiftv4_properties['WATCH_CLUSTER_OPERATORS']
			def cerberus_publish_status = openshiftv4_properties['CERBERUS_PUBLISH_STATUS']
			def inspect_components = openshiftv4_properties['INSPECT_COMPONENTS']
			def slack_integration = openshiftv4_properties['SLACK_INTEGRATION']
			def slack_api_token = openshiftv4_properties['SLACK_API_TOKEN']
			def slack_channel = openshiftv4_properties['SLACK_CHANNEL']
			def cop_slack_id = openshiftv4_properties['COP_SLACK_ID']
			def slack_team_alias = openshiftv4_properties['SLACK_TEAM_ALIAS']
			def iterations = openshiftv4_properties['ITERATIONS']
			def sleep_time = openshiftv4_properties['SLEEP_TIME']
			def daemon_mode = openshiftv4_properties['DAEMON_MODE']
			def kubeconfig_auth_dir_path = openshiftv4_properties['KUBECONFIG_AUTH_DIR_PATH']
			def job_iterations = openshiftv4_properties['JOB_ITERATIONS']
			
			// Install cluster using the payload captured at the build trigger url when scale_ci_build_trigget is set
			if ( scale_ci_build_trigger.toBoolean() ) {
				sh "curl ${scale_ci_build_trigger_url}/payload -o /tmp/payload"
				sh "curl ${scale_ci_build_trigger_url}/build.status -o /tmp/status"
				status = readFile "/tmp/status"
				if ( status.toString().trim().equals("PROCEED") ) {
					println "Build status is set to ${status}, proceeding with the cluster build"
					openshift_install_release_image_override = readFile "/tmp/payload"
				} else {
					println "Build status is set to ${status}, exiting without building the cluster"
					return 0
				}
			}

			try {
				openshiftv4_build = build job: 'ATS-SCALE-CI-OCP-OSP-DEPLOY',
				parameters: [  	[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_INSTALL', value: Boolean.valueOf(openshift_install) ],
						[$class: 'BooleanParameterValue', name: 'SCALE_CI_BUILD_TRIGGER', value: Boolean.valueOf(scale_ci_build_trigger) ],
						[$class: 'StringParameterValue', name: 'SCALE_CI_BUILD_TRIGGER_URL', value: scale_ci_build_trigger_url ],
						[$class: 'BooleanParameterValue', name: 'ENABLE_DITTYBOPPER', value: enable_dittybopper ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_PATH', value: kubeconfig_path ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_APIVERSION', value: openshift_install_apiversion ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_SSH_PUB_KEY_FILE', value: openshift_install_ssh_pub_key_file ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'OPENSHIFT_INSTALL_PULL_SECRET', value: openshift_install_pull_secret ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'OPENSHIFT_INSTALL_QUAY_REGISTRY_TOKEN', value: openshift_install_quay_registry_token ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_IMAGE_REGISTRY', value: openshift_install_image_registry ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'OPENSHIFT_INSTALL_REGISTRY_TOKEN', value: openshift_install_registry_token ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_TOGGLE_INFRA_NODE', value: openshift_toggle_infra_node ],
						[$class: 'StringParameterValue', name: 'SCALE_LAB_CLOUD_NAME', value: scale_lab_cloud_name ],
						[$class: 'StringParameterValue', name: 'ANSIBLE_SSH_PASSWORD', value: ansible_ssh_password ],
						[$class: 'StringParameterValue', name: 'OPENSTACK_VERSION', value: openstack_version ],
						[$class: 'BooleanParameterValue', name: 'OSP_PUBLIC_EXTERNAL_INTERFACE', value: osp_public_external_interface ],
						[$class: 'StringParameterValue', name: 'OSP_PUBLIC_NETWORK_NAME', value: osp_public_network_name ],
						[$class: 'StringParameterValue', name: 'OSP_EXTERNAL_GATEWAY', value: osp_external_gateway ],
						[$class: 'StringParameterValue', name: 'OSP_EXTERNAL_NET_CIDR', value: osp_external_net_cidr ],
						[$class: 'StringParameterValue', name: 'OSP_EXTERNAL_ALLOCATION_POOLS_START', value: osp_external_allocation_pools_start ],
						[$class: 'StringParameterValue', name: 'OSP_EXTERNAL_ALLOCATION_POOLS_END', value: osp_external_allocation_pools_end ],
						[$class: 'StringParameterValue', name: 'OSP_EXTERNAL_INTERFACE_DEFAULT_ROUTE', value: osp_external_interface_default_route ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_BASE_DOMAIN', value: openshift_base_domain  ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_CLUSTER_NAME', value: openshift_cluster_name ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_RELEASE', value: openshift_release ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_MASTER_COUNT', value: openshift_master_count ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKER_COUNT', value: openshift_worker_count ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_MASTER_INSTANCE_TYPE', value: openshift_master_instance_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKER_INSTANCE_TYPE', value: openshift_worker_instance_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_MASTER_ROOT_VOLUME_SIZE', value: openshift_master_root_volume_size ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_MASTER_ROOT_VOLUME_TYPE', value: openshift_master_root_volume_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_MASTER_ROOT_VOLUME_IOPS', value: openshift_master_root_volume_iops ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKER_ROOT_VOLUME_SIZE', value: openshift_worker_root_volume_size ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKER_ROOT_VOLUME_TYPE', value: openshift_worker_root_volume_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKER_ROOT_VOLUME_IOPS', value: openshift_worker_root_volume_iops ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_CIDR', value: openshift_cidr ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_MACHINE_CIDR', value: openshift_machine_cidr ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_NETWORK_TYPE', value: openshift_network_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_SERVICE_NETWORK', value: openshift_service_network ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_HOST_PREFIX', value: openshift_host_prefix ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_POST_INSTALL', value: openshift_post_install ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_POST_INSTALL_POLL_ATTEMPTS', value: openshift_post_install_poll_attempts ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_TOGGLE_WORKLOAD_NODE', value: openshift_toggle_workload_node ],
						[$class: 'StringParameterValue', name: 'MACHINESET_METADATA_LABEL_PREFIX', value: machineset_metadata_label_prefix ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INFRA_NODE_INSTANCE_TYPE', value: openshift_infra_node_instance_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_INSTANCE_TYPE', value: openshift_workload_node_instance_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_VOLUME_SIZE', value: openshift_workload_node_volume_size ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_VOLUME_TYPE', value: openshift_workload_node_volume_type ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_VOLUME_IOPS', value: openshift_workload_node_volume_iops ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_PROMETHEUS_RETENTION_PERIOD', value: openshift_prometheus_retention_period ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_PROMETHEUS_STORAGE_CLASS', value: openshift_prometheus_storage_class ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_PROMETHEUS_STORAGE_SIZE', value: openshift_prometheus_storage_size ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_ALERTMANAGER_STORAGE_CLASS', value: openshift_alertmanager_storage_class ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_ALERTMANAGER_STORAGE_SIZE', value: openshift_alertmanager_storage_size ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_POST_CONFIG', value: openshift_post_config ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_DEBUG_CONFIG', value: openshift_debug_config ],
						[$class: 'StringParameterValue', name: 'CERBERUS_CONFIG_PATH', value: cerberus_config_path ],
						[$class: 'BooleanParameterValue', name: 'CERBERUS_ENABLE', value: cerberus_enable ],
						[$class: 'StringParameterValue', name: 'CERBERUS_IMAGE', value: cerberus_image ],
						[$class: 'StringParameterValue', name: 'CERBERUS_URL', value: cerberus_url ],
						[$class: 'BooleanParameterValue', name: 'WATCH_NODES', value: watch_nodes ],
						[$class: 'BooleanParameterValue', name: 'WATCH_CLUSTER_OPERATORS', value: watch_cluster_operators ],
						[$class: 'BooleanParameterValue', name: 'CERBERUS_PUBLISH_STATUS', value: cerberus_publish_status ],
						[$class: 'BooleanParameterValue', name: 'INSPECT_COMPONENTS', value: inspect_components ],
						[$class: 'BooleanParameterValue', name: 'SLACK_INTEGRATION', value: slack_integration ],
						[$class: 'StringParameterValue', name: 'SLACK_API_TOKEN', value: slack_api_token ],
						[$class: 'StringParameterValue', name: 'SLACK_CHANNEL', value: slack_channel ],
						[$class: 'StringParameterValue', name: 'COP_SLACK_ID', value: cop_slack_id ],
						[$class: 'StringParameterValue', name: 'SLACK_TEAM_ALIAS', value: slack_team_alias ],
						[$class: 'StringParameterValue', name: 'ITERATIONS', value: iterations ],
						[$class: 'StringParameterValue', name: 'SLEEP_TIME', value: sleep_time ],
						[$class: 'BooleanParameterValue', name: 'DAEMON_MODE', value: daemon_mode ],
						[$class: 'StringParameterValue', name: 'KUBECONFIG_AUTH_DIR_PATH', value: kubeconfig_auth_dir_path ],
						[$class: 'StringParameterValue', name: 'JOB_ITERATIONS', value: job_iterations ]]
			} catch ( Exception e) {
				echo "ATS-SCALE-CI-OCP-OSP-DEPLOY Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'mkaliyam@redhat.com',
					subject: 'ats-scale-ci-ocp-osp-deploy job failed',
					body: """\
						Encoutered an error while running the ats-scale-ci-ocp-osp-deploy job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "ATS-SCALE-CI-OCP-OSP-DEPLOY build ${openshiftv4_build.getNumber()} completed successfully"
		}
	}
}
