#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def ocp_install = OPENSHIFTv4_INSTALL_ON_AWS.toString().toUpperCase()
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
			sh "wget ${OPENSHIFTv4_ON_AWS_PROPERTY_FILE} -O ${property_file_name}"
			def openshiftv4_properties = readProperties file: property_file_name
			def orchestration_host = openshiftv4_properties['ORCHESTRATION_HOST']
			def orchestration_user = openshiftv4_properties['ORCHESTRATION_USER']
			def sshkey_token = openshiftv4_properties['SSHKEY_TOKEN']
			def openshift_cleanup = openshiftv4_properties['OPENSHIFT_CLEANUP']
			def openshift_install = openshiftv4_properties['OPENSHIFT_INSTALL']
			def openshift_post_install = openshiftv4_properties['OPENSHIFT_POST_INSTALL']
			def openshift_post_config = openshiftv4_properties['OPENSHIFT_POST_CONFIG']
			def openshift_debug_config = openshiftv4_properties['OPENSHIFT_DEBUG_CONFIG']
			def openshift_oc_client_url = openshiftv4_properties['OPENSHIFT_CLIENT_LOCATION']
			def openshift_install_release_image_override = openshiftv4_properties['OPENSHIFT_INSTALL_RELEASE_IMAGE_OVERRIDE']
			def openshift_install_binary_url = openshiftv4_properties['OPENSHIFT_INSTALL_BINARY_URL']
			def openshift_install_apiversion = openshiftv4_properties['OPENSHIFT_INSTALL_APIVERSION']
			def openshift_install_ssh_pub_key_file = openshiftv4_properties['OPENSHIFT_INSTALL_SSH_PUB_KEY_FILE']
			def openshift_install_pull_secret = openshiftv4_properties['OPENSHIFT_INSTALL_PULL_SECRET']
			def openshift_install_quay_registry_token = openshiftv4_properties['OPENSHIFT_INSTALL_QUAY_REGISTRY_TOKEN']
			def openshift_install_image_registry = openshiftv4_properties['OPENSHIFT_INSTALL_IMAGE_REGISTRY']
			def openshift_install_registry_token = openshiftv4_properties['OPENSHIFT_INSTALL_REGISTRY_TOKEN']
			def openshift_install_installer_from_source = openshiftv4_properties['OPENSHIFT_INSTALL_INSTALLER_FROM_SOURCE']
			def openshift_install_installer_from_source_version = openshiftv4_properties['OPENSHIFT_INSTALL_INSTALLER_FROM_SOURCE_VERSION']
			def gopath = openshiftv4_properties['GOPATH']
			def aws_profile = openshiftv4_properties['AWS_PROFILE']
			def aws_access_key_id = openshiftv4_properties['AWS_ACCESS_KEY_ID']
			def aws_secret_access_key = openshiftv4_properties['AWS_SECRET_ACCESS_KEY']
			def aws_region = openshiftv4_properties['AWS_REGION']
			def openshift_base_domain = openshiftv4_properties['OPENSHIFT_BASE_DOMAIN']
			def openshift_cluster_name = openshiftv4_properties['OPENSHIFT_CLUSTER_NAME']
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
			def openshift_service_network = openshiftv4_properties['OPENSHIFT_SERVICE_NETWORK']
			def openshift_host_prefix = openshiftv4_properties['OPENSHIFT_HOST_PREFIX']
                        def openshift_post_install_poll_attempts = openshiftv4_properties['OPENSHIFT_POST_INSTALL_POLL_ATTEMPTS']
                        def openshift_toggle_infra_node = openshiftv4_properties['OPENSHIFT_TOGGLE_INFRA_NODE']
                        def openshift_toggle_workload_node = openshiftv4_properties['OPENSHIFT_TOGGLE_WORKLOAD_NODE']
                        def machineset_metadata_label_prefix = openshiftv4_properties['MACHINESET_METADATA_LABEL_PREFIX']
                        def openshift_infra_node_instance_type = openshiftv4_properties['OPENSHIFT_INFRA_NODE_INSTANCE_TYPE']
                        def openshift_workload_node_instance_type = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_INSTANCE_TYPE']
                        def openshift_infra_node_volume_size = openshiftv4_properties['OPENSHIFT_INFRA_NODE_VOLUME_SIZE']
                        def openshift_infra_node_volume_type = openshiftv4_properties['OPENSHIFT_INFRA_NODE_VOLUME_TYPE']
                        def openshift_infra_node_volume_iops = openshiftv4_properties['OPENSHIFT_INFRA_NODE_VOLUME_IOPS']
                        def openshift_workload_node_volume_size = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_VOLUME_SIZE']
                        def openshift_workload_node_volume_type = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_VOLUME_TYPE']
                        def openshift_workload_node_volume_iops = openshiftv4_properties['OPENSHIFT_WORKLOAD_NODE_VOLUME_IOPS']
                        def openshift_prometheus_retention_period = openshiftv4_properties['OPENSHIFT_PROMETHEUS_RETENTION_PERIOD']
                        def openshift_prometheus_storage_class = openshiftv4_properties['OPENSHIFT_PROMETHEUS_STORAGE_CLASS']
                        def openshift_prometheus_storage_size = openshiftv4_properties['OPENSHIFT_PROMETHEUS_STORAGE_SIZE']
                        def openshift_alertmanager_storage_class = openshiftv4_properties['OPENSHIFT_ALERTMANAGER_STORAGE_CLASS']
                        def openshift_alertmanager_storage_size = openshiftv4_properties['OPENSHIFT_ALERTMANAGER_STORAGE_SIZE']
			def kubeconfig_auth_dir_path = openshiftv4_properties['KUBECONFIG_AUTH_DIR_PATH']			
	
			try {
				openshiftv4_build = build job: 'ATS-SCALE-CI-OCP-AWS-DEPLOY',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_USER', value: orchestration_user ],
						[$class: 'StringParameterValue', name: 'ORCHESTRATION_HOST', value: orchestration_host ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'SSHKEY_TOKEN', value: sshkey_token ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_CLEANUP', value: Boolean.valueOf(openshift_cleanup) ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_INSTALL', value: Boolean.valueOf(openshift_install) ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_POST_INSTALL', value: Boolean.valueOf(openshift_post_install) ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_POST_CONFIG', value: Boolean.valueOf(openshift_post_config) ],
						[$class: 'BooleanParameterValue', name: 'OPENSHIFT_DEBUG_CONFIG', value: Boolean.valueOf(openshift_debug_config) ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_CLIENT_LOCATION', value: openshift_oc_client_url ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_RELEASE_IMAGE_OVERRIDE', value: openshift_install_release_image_override ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_BINARY_URL', value: openshift_install_binary_url ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_APIVERSION', value: openshift_install_apiversion ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_SSH_PUB_KEY_FILE', value: openshift_install_ssh_pub_key_file ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'OPENSHIFT_INSTALL_PULL_SECRET', value: openshift_install_pull_secret ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'OPENSHIFT_INSTALL_QUAY_REGISTRY_TOKEN', value: openshift_install_quay_registry_token ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_IMAGE_REGISTRY', value: openshift_install_image_registry ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'OPENSHIFT_INSTALL_REGISTRY_TOKEN', value: openshift_install_registry_token ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_INSTALLER_FROM_SOURCE', value: openshift_install_installer_from_source ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_INSTALL_INSTALLER_FROM_SOURCE_VERSION', value: openshift_install_installer_from_source_version ],
						[$class: 'StringParameterValue', name: 'GOPATH', value: gopath ],
						[$class: 'StringParameterValue', name: 'AWS_PROFILE', value: aws_profile ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'AWS_ACCESS_KEY_ID', value: aws_access_key_id ],
						[$class: 'hudson.model.PasswordParameterValue', name: 'AWS_SECRET_ACCESS_KEY', value: aws_secret_access_key ],
						[$class: 'StringParameterValue', name: 'AWS_REGION', value: aws_region ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_BASE_DOMAIN', value: openshift_base_domain  ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_CLUSTER_NAME', value: openshift_cluster_name ],
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
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_SERVICE_NETWORK', value: openshift_service_network ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_HOST_PREFIX', value: openshift_host_prefix ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_POST_INSTALL_POLL_ATTEMPTS', value: openshift_post_install_poll_attempts ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_TOGGLE_INFRA_NODE', value: openshift_toggle_infra_node ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_TOGGLE_WORKLOAD_NODE', value: openshift_toggle_workload_node ],
                                                [$class: 'StringParameterValue', name: 'MACHINESET_METADATA_LABEL_PREFIX', value: machineset_metadata_label_prefix ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_INFRA_NODE_INSTANCE_TYPE', value: openshift_infra_node_instance_type ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_INSTANCE_TYPE', value: openshift_workload_node_instance_type ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_INFRA_NODE_VOLUME_SIZE', value: openshift_infra_node_volume_size ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_INFRA_NODE_VOLUME_TYPE', value: openshift_infra_node_volume_type ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_INFRA_NODE_VOLUME_IOPS', value: openshift_infra_node_volume_iops ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_VOLUME_SIZE', value: openshift_workload_node_volume_size ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_VOLUME_TYPE', value: openshift_workload_node_volume_type ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_WORKLOAD_NODE_VOLUME_IOPS', value: openshift_workload_node_volume_iops ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_PROMETHEUS_RETENTION_PERIOD', value: openshift_prometheus_retention_period ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_PROMETHEUS_STORAGE_CLASS', value: openshift_prometheus_storage_class ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_PROMETHEUS_STORAGE_SIZE', value: openshift_prometheus_storage_size ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_ALERTMANAGER_STORAGE_CLASS', value: openshift_alertmanager_storage_class ],
                                                [$class: 'StringParameterValue', name: 'OPENSHIFT_ALERTMANAGER_STORAGE_SIZE', value: openshift_alertmanager_storage_size ],
                                                [$class: 'StringParameterValue', name: 'KUBECONFIG_AUTH_DIR_PATH', value: kubeconfig_auth_dir_path ]]
			} catch ( Exception e) {
				echo "ATS-SCALE-CI-OCP-AWS-DEPLOY Job failed with the following error: "
				echo "${e.getMessage()}"
				echo "Sending an email"
				mail(
					to: 'nelluri@redhat.com',
					subject: 'ats-scale-ci-ocp-aws-deploy job failed',
					body: """\
						Encoutered an error while running the ats-scale-ci-ocp-aws-deploy job: ${e.getMessage()}\n\n
						Jenkins job: ${env.BUILD_URL}
				""")
				currentBuild.result = "FAILURE"
 				sh "exit 1"
			}
			println "ATS-SCALE-CI-OCP-AWS-DEPLOY build ${openshiftv4_build.getNumber()} completed successfully"
		}
	}
}
