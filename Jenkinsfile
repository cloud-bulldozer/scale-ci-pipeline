#!/usr/bin/env groovy

def contact = "nelluri@redhat.com"
def watcher = SCALE_CI_WATCHER.toString().toUpperCase()
def pipeline = PIPELINE.toString().toUpperCase()
def stage_two = STAGE_TWO.toString().toUpperCase()
def stage_three = STAGE_THREE.toString().toUpperCase()
def stage_four = STAGE_FOUR.toString().toUpperCase()
def stage_five = STAGE_FIVE.toString().toUpperCase()
def stage_six = STAGE_SIX.toString().toUpperCase()
def build_tracker = SCALE_CI_BUILD_TRACKER.toString().toUpperCase()
def tooling = TOOLING.toString().toUpperCase()
def run_conformance = CONFORMANCE.toString().toUpperCase()
def openshiftv4_install_on_aws = OPENSHIFTv4_INSTALL_ON_AWS.toString().toUpperCase()
def openshiftv4_install_on_azure = OPENSHIFTv4_INSTALL_ON_AZURE.toString().toUpperCase()
def openshiftv4_install_on_gcp = OPENSHIFTv4_INSTALL_ON_GCP.toString().toUpperCase()
def openshiftv4_install_on_osp = OPENSHIFTv4_INSTALL_ON_OSP.toString().toUpperCase()
def ocpv4_scale = OPENSHIFTv4_SCALE.toString().toUpperCase()
def cluster_density = CLUSTER_DENSITY.toString().toUpperCase()
def kubelet_density = KUBELET_DENSITY.toString().toUpperCase()
def kubelet_density_light = KUBELET_DENSITY_LIGHT.toString().toUpperCase()
def install_openstack = OPENSTACK_INSTALL.toString().toUpperCase()
def browbeat = BROWBEAT_INSTALL.toString().toUpperCase()
def http = HTTP_TEST.toString().toUpperCase()
def logging = LOGGING_SCALE_TEST.toString().toUpperCase()
def pgbench_test = PGBENCH_TEST.toString().toUpperCase()
def prometheus_test = PROMETHEUS_TEST.toString().toUpperCase()
def mongodb_ycsb_test = MONGODB_YCSB_TEST.toString().toUpperCase()
def services_per_namespace = SERVICES_PER_NAMESPACE.toString().toUpperCase()
def deployments_per_ns = DEPLOYMENTS_PER_NS.toString().toUpperCase()
def ns_per_cluster = NS_PER_CLUSTER.toString().toUpperCase()
def networking = NETWORKING.toString().toUpperCase()
def byo = BYO_SCALE_TEST.toString().toUpperCase()
def baseline = BASELINE_SCALE_TEST.toString().toUpperCase()
def run_uperf = UPERF.toString().toUpperCase()
def kraken = KRAKEN.toString().toUpperCase()
def node_label = NODE_LABEL.toString()
def run_id = "${env.JOB_NAME}-${env.BUILD_NUMBER}"

node (node_label) {
	env.RUN_ID = "${env.JOB_NAME}-${env.BUILD_NUMBER}"
	println "Run ID is ${env.RUN_ID}"
	// setup the repo containing the pipeline scripts
	stage('cloning pipeline repo') {
		checkout scm
	}

	// creates/updates jenkins jobs using the jjb templates
	if (watcher == "TRUE") {
		env.WORKLOAD="ATS-SCALE-CI-WATCHER"
		env.WORKLOAD_PROPERTIES_FILE=SCALE_CI_WATCHER_PROPERTY_FILE
		load "pipeline-scripts/workload.groovy"
	}

	if (pipeline == "TRUE") {
		env.PIPELINE_STAGE=1
		if ( build_tracker == "TRUE") {
			env.WORKLOAD="SCALE-CI-BUILD-TRACKER"
			env.WORKLOAD_PROPERTIES_FILE=SCALE_CI_BUILD_TRACKER_PROPERTY_FILE
			load "pipeline-scripts/workload.groovy"
		}
		if (openshiftv4_install_on_aws == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_AWS_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-AWS-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}
		if (openshiftv4_install_on_azure == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_AZURE_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-AZURE-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}
		if (openshiftv4_install_on_gcp == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_GCP_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-GCP-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}
		if (openshiftv4_install_on_osp == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_OSP_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-OSP-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}
		if (http == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=HTTP_TEST_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-HTTP"
			load "pipeline-scripts/workload.groovy"
		}
		if (run_uperf == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=UPERF_PROPERTIES_FILE
			env.WORKLOAD="RIPSAW-UPERF"
			load "pipeline-scripts/workload.groovy"
		}
		if (ocpv4_scale == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-SCALE"
			load "pipeline-scripts/workload.groovy"
		}
		
		if (stage_two == "TRUE") {
			env.PIPELINE_STAGE=2
			if (http == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=HTTP_TEST_PROPERTY_FILE
				env.WORKLOAD="ATS-SCALE-CI-HTTP"
				load "pipeline-scripts/workload.groovy"
			}
			if (kubelet_density == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=KUBELET_DENSITY_PROPERTIES_FILE
				env.WORKLOAD="RIPSAW-KUBELET-DENSITY"
				load "pipeline-scripts/workload.groovy"
			}
			if (cluster_density == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=CLUSTER_DENSITY_PROPERTIES_FILE
				env.WORKLOAD="RIPSAW-CLUSTER-DENSITY"
				load "pipeline-scripts/workload.groovy"
			}
			if (ocpv4_scale == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
				env.WORKLOAD="ATS-SCALE-CI-SCALE"
				load "pipeline-scripts/workload.groovy"
			}
		}

		if (stage_three == "TRUE") {
			env.PIPELINE_STAGE=3
			if (cluster_density == "TRUE") {
                                env.WORKLOAD_PROPERTIES_FILE=CLUSTER_DENSITY_PROPERTIES_FILE
				env.WORKLOAD="RIPSAW-CLUSTER-DENSITY"
				load "pipeline-scripts/workload.groovy"
			}
			if (ocpv4_scale == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
				env.WORKLOAD="ATS-SCALE-CI-SCALE"
 				load "pipeline-scripts/workload.groovy"
			}
 		}

		if (stage_four == "TRUE") {
			env.PIPELINE_STAGE=4
			if (cluster_density == "TRUE") {
                                env.WORKLOAD_PROPERTIES_FILE=CLUSTER_DENSITY_PROPERTIES_FILE
				env.WORKLOAD="RIPSAW-CLUSTER-DENSITY"
				load "pipeline-scripts/workload.groovy"
			}
			if (ocpv4_scale == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
				env.WORKLOAD="ATS-SCALE-CI-SCALE"
				load "pipeline-scripts/workload.groovy"
			}
		}

		if (stage_five == "TRUE") {
			env.PIPELINE_STAGE=5
			if (cluster_density == "TRUE") {
                                env.WORKLOAD_PROPERTIES_FILE=CLUSTER_DENSITY_PROPERTIES_FILE
				env.WORKLOAD="RIPSAW-CLUSTER-DENSITY"
				load "pipeline-scripts/workload.groovy"
			}
			if (ocpv4_scale == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
				env.WORKLOAD="ATS-SCALE-CI-SCALE"
				load "pipeline-scripts/workload.groovy"
			}
		}

		if (stage_six == "TRUE") {
			env.PIPELINE_STAGE=6
			if (cluster_density == "TRUE") {
                                env.WORKLOAD_PROPERTIES_FILE=CLUSTER_DENSITY_PROPERTIES_FILE
				env.WORKLOAD="RIPSAW-CLUSTER-DENSITY"
				load "pipeline-scripts/workload.groovy"
			}
 			if (ocpv4_scale == "TRUE") {
				env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
				env.WORKLOAD="ATS-SCALE-CI-SCALE"
				load "pipeline-scripts/workload.groovy"
			}
		}

	} else {

		// Queries UMB message to capture the OCP 4.x payloads
		if ( build_tracker == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=SCALE_CI_BUILD_TRACKER_PROPERTY_FILE
			env.WORKLOAD="SCALE-CI-BUILD-TRACKER"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to install openstack
		if (install_openstack == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_OSP_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-OSP-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to set up browbeat
		if (browbeat == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=BROWBEAT_PROPERTY_FILE
			env.WORKLOAD="scale-ci_install_Browbeat"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to install openshift 4.x on AWS
		if (openshiftv4_install_on_aws == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_AWS_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-AWS-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to install openshift 4.x on Azure
		if (openshiftv4_install_on_azure == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_AZURE_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-AZURE-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to install openshift 4.x on GCP
		if (openshiftv4_install_on_gcp == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_GCP_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-GCP-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to install OSP and OCP using jetpack
		if (openshiftv4_install_on_osp == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_ON_OSP_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-OCP-OSP-DEPLOY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to setup pbench
		if (tooling == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=TOOLING_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-TOOLING"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run conformance
		if (run_conformance == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=CONFORMANCE_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-CONFORMANCE"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run OCP 4.X scaleup
		if (ocpv4_scale == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=OPENSHIFTv4_SCALE_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-SCALE"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run http scale test
		if (http == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=HTTP_TEST_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-HTTP"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run services per namespace test
		if (services_per_namespace == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=SERVICES_PER_NAMESPACE_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-SERVICES-PER-NAMESPACE"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run deployments per ns test
		if ( deployments_per_ns == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=DEPLOYMENTS_PER_NS_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-DEPLOYMENTS-PER-NAMESPACE"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run pgbench scale test
		if ( pgbench_test == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=PGBENCH_PROPERTY_FILE
			env.WORKLOAD="PGBENCH_SCALE_TEST"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run mongodb ycsb scale test
		if ( mongodb_ycsb_test == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=MONGOYCSB_PROPERTY_FILE
			env.WORKLOAD="MONGODB_YCSB_TEST"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run cluster-density scale test
		if (cluster_density == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=CLUSTER_DENSITY_PROPERTIES_FILE
			env.WORKLOAD="RIPSAW-CLUSTER-DENSITY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run kubelet-density scale test
		if (kubelet_density == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=KUBELET_DENSITY_PROPERTIES_FILE
			env.WORKLOAD="RIPSAW-KUBELET-DENSITY"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run kubelet-density-light scale test
		if (kubelet_density_light == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=KUBELET_DENSITY_LIGHT_PROPERTIES_FILE
			env.WORKLOAD="RIPSAW-KUBELET-DENSITY-LITE"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run ns_per_cluster test
		if ( ns_per_cluster == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=NS_PER_CLUSTER_PROPERTY_FILE
			env.WORKLOAD="ATS-SCALE-CI-NAMESPACES-PER-CLUSTER"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run logging scale test
		if (logging == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=LOGGING_PROPERTY_FILE
			env.WORKLOAD="LOGGING-SCALE-TEST"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run prometheus scale test
		if ( prometheus_test == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=PROMETHEUS_PROPERTY_FILE
			env.WORKLOAD=PROMETHEUS_TEST
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run BYO scale test
		if (byo == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=BYO_PROPERTY_FILE
			env.WORKLOAD=BYO-SCALE-TEST
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run baseline test
		if (baseline == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=BASELINE_PROPERTY_FILE
                        env.WORKLOAD=BASELINE-SCALE-TEST
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run uperf test
		if (run_uperf == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=UPERF_PROPERTIES_FILE
			env.WORKLOAD="RIPSAW-UPERF"
			load "pipeline-scripts/workload.groovy"
		}

		// stage to run kraken test
		if (kraken == "TRUE") {
			env.WORKLOAD_PROPERTIES_FILE=KRAKEN_PROPERTY_FILE
			env.WORKLOAD="KRAKEN"
			load "pipeline-scripts/workload.groovy"
		}

	}

		// cleanup the workspace
		stage('cleaning workspace') {
			deleteDir()
		}

		mail(
			to: contact,
			subject: "${env.JOB_NAME} ${env.BUILD_NUMBER} completed successfully",
			body: """\
				Jenkins job: ${env.BUILD_URL}\n\n
				See the console output for more details:  ${env.BUILD_URL}consoleFull\n\n
			"""
		)
}
