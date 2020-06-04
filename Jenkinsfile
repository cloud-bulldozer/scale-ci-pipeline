#!/usr/bin/env groovy

def contact = "nelluri@redhat.com"
def watcher = SCALE_CI_WATCHER.toString().toUpperCase()
def pipeline = PIPELINE.toString().toUpperCase()
def build_tracker = SCALE_CI_BUILD_TRACKER.toString().toUpperCase()
def tooling = TOOLING.toString().toUpperCase()
def run_conformance = CONFORMANCE.toString().toUpperCase()
def openshiftv4_install_on_aws = OPENSHIFTv4_INSTALL_ON_AWS.toString().toUpperCase()
def openshiftv4_install_on_azure = OPENSHIFTv4_INSTALL_ON_AZURE.toString().toUpperCase()
def openshiftv4_install_on_gcp = OPENSHIFTv4_INSTALL_ON_GCP.toString().toUpperCase()
def ocpv3_scale = OPENSHIFTv3_SCALE.toString().toUpperCase()
def ocpv4_scale = OPENSHIFTv4_SCALE.toString().toUpperCase()
def nodevertical = NODEVERTICAL_SCALE_TEST.toString().toUpperCase()
def mastervertical = MASTERVERTICAL_SCALE_TEST.toString().toUpperCase()
def install_openstack = OPENSTACK_INSTALL.toString().toUpperCase()
def browbeat = BROWBEAT_INSTALL.toString().toUpperCase()
def http = HTTP_TEST.toString().toUpperCase()
def logging = LOGGING_SCALE_TEST.toString().toUpperCase()
def pgbench_test = PGBENCH_TEST.toString().toUpperCase()
def prometheus_test = PROMETHEUS_TEST.toString().toUpperCase()
def mongodb_ycsb_test = MONGODB_YCSB_TEST.toString().toUpperCase()
def services_per_namespace = SERVICES_PER_NAMESPACE.toString().toUpperCase()
def podvertical = PODVERTICAL.toString().toUpperCase()
def deployments_per_ns = DEPLOYMENTS_PER_NS.toString().toUpperCase()
def ns_per_cluster = NS_PER_CLUSTER.toString().toUpperCase()
def networking = NETWORKING.toString().toUpperCase()
def byo = BYO_SCALE_TEST.toString().toUpperCase()
def baseline = BASELINE_SCALE_TEST.toString().toUpperCase()
def run_uperf = UPERF.toString().toUpperCase()
def node_label = NODE_LABEL.toString()

node (node_label) {
	// setup the repo containing the pipeline scripts
	stage('cloning pipeline repo') {
		checkout scm
	}

	// creates/updates jenkins jobs using the jjb templates
	if (watcher == "TRUE") {
		load "pipeline-scripts/scale_ci_watcher.groovy"
	}

        if (pipeline == "TRUE") {
		env.PIPELINE_STAGE=1
		if ( build_tracker == "TRUE") {
			load "pipeline-scripts/scale_ci_build_tracker.groovy"
		}
		if (openshiftv4_install_on_aws == "TRUE") {
			load "pipeline-scripts/openshiftv4_on_aws.groovy"
		}
		if (openshiftv4_install_on_azure == "TRUE") {
			load "pipeline-scripts/openshiftv4_on_azure.groovy"
		}
		if (openshiftv4_install_on_gcp == "TRUE") {
                        load "pipeline-scripts/openshiftv4_on_gcp.groovy"
                }
		if (http == "TRUE") {
			load "pipeline-scripts/http.groovy"
		}
		if (run_uperf == "TRUE") {
			load "pipeline-scripts/uperf.groovy"
		}
		if (ocpv4_scale == "TRUE") {
                	load "pipeline-scripts/openshiftv4_scale.groovy"
		}

		env.PIPELINE_STAGE=2
		if (http == "TRUE") {
			 load "pipeline-scripts/http.groovy"
		}
		if (ocpv4_scale == "TRUE") {
			load "pipeline-scripts/openshiftv4_scale.groovy"
		}

	} else {

		// Queries UMB message to capture the OCP 4.x payloads
		if ( build_tracker == "TRUE") {
			load "pipeline-scripts/scale_ci_build_tracker.groovy"
		}

		// stage to install openstack
		if (install_openstack == "TRUE") {
			load "pipeline-scripts/openstack.groovy"
		}

		// stage to set up browbeat
		if (browbeat == "TRUE") {
			load "pipeline-scripts/browbeat.groovy"
		}

		// stage to install openshift 3.x
		if (openshiftv3_install == "TRUE") {
			load "pipeline-scripts/openshiftv3.groovy"
		}

		// stage to install openshift 4.x on AWS
		if (openshiftv4_install_on_aws == "TRUE") {
			load "pipeline-scripts/openshiftv4_on_aws.groovy"
		}

		// stage to install openshift 4.x on Azure
		if (openshiftv4_install_on_azure == "TRUE") {
			load "pipeline-scripts/openshiftv4_on_azure.groovy"
		}
		
		// stage to install openshift 4.x on GCP
                if (openshiftv4_install_on_gcp == "TRUE") {
                        load "pipeline-scripts/openshiftv4_on_gcp.groovy"
                }

		// stage to setup pbench
		if (tooling == "TRUE") {
			load "pipeline-scripts/tooling.groovy"
		}

		// stage to run conformance
		if (run_conformance == "TRUE") {
			load "pipeline-scripts/conformance.groovy"
		}

		// stage to scaleup the cluster
		if (ocpv3_scale == "TRUE") {
			load "pipeline-scripts/openshiftv3_scale.groovy"
		}

		// stage to run OCP 4.X scaleup
		if (ocpv4_scale == "TRUE") {
			load "pipeline-scripts/openshiftv4_scale.groovy"
		}

		// stage to run nodevertical scale test
		if (nodevertical == "TRUE") {
			load "pipeline-scripts/nodevertical.groovy"
		}

		// stage to run http scale test
		if (http == "TRUE") {
			load "pipeline-scripts/http.groovy"
		}

		// stage to run services per namespace test
		if (services_per_namespace == "TRUE") {
			load "pipeline-scripts/services_per_namespace.groovy"
		}

		// stage to run deployments per ns test
		if ( deployments_per_ns == "TRUE") {
			load "pipeline-scripts/deployments_per_ns.groovy"
		}

		// stage to run podvertical test
		if ( podvertical == "TRUE") {
			load "pipeline-scripts/podvertical.groovy"
		}

		// stage to run networking test
		if ( networking == "TRUE") {
			load "pipeline-scripts/networking.groovy"
		}

		// stage to run pgbench scale test
		if ( pgbench_test == "TRUE") {
			load "pipeline-scripts/pgbench.groovy"
		}

		// stage to run mongodb ycsb scale test
		if ( mongodb_ycsb_test == "TRUE") {
			load "pipeline-scripts/mongodbycsb.groovy"
		}

		// stage to run mastervertical scale test
		if (mastervertical == "TRUE") {
			load "pipeline-scripts/mastervertical.groovy"
		}

		// stage to run ns_per_cluster test
		if ( ns_per_cluster == "TRUE") {
			load "pipeline-scripts/ns_per_cluster.groovy"
		}

		// stage to run logging scale test
		if (logging == "TRUE") {
			load "pipeline-scripts/logging.groovy"
		}

		// stage to run prometheus scale test
		if ( prometheus_test == "TRUE") {
			load "pipeline-scripts/prometheus.groovy"
		}

		// stage to run BYO scale test
		if (byo == "TRUE") {
			load "pipeline-scripts/byo.groovy"
		}

		// stage to run baseline test
		if (baseline == "TRUE") {
			load "pipeline-scripts/baseline.groovy"
		}

		// stage to run uperf test
		if (run_uperf == "TRUE") {
			load "pipeline-scripts/uperf.groovy"
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
