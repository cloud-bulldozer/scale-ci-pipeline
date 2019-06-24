#!/usr/bin/env groovy

def contact = "nelluri@redhat.com"
def setup_tooling = SETUP_TOOLING.toString().toUpperCase()
def run_conformance = CONFORMANCE.toString().toUpperCase()
def openshift_install = OPENSHIFT_INSTALL.toString().toUpperCase()
def nodevertical = NODEVERTICAL_SCALE_TEST.toString().toUpperCase()
def mastervertical = MASTERVERTICAL_SCALE_TEST.toString().toUpperCase()
def install_openstack = OPENSTACK_INSTALL.toString().toUpperCase()
def browbeat = BROWBEAT_INSTALL.toString().toUpperCase()
def scaleup = OPENSHIFT_SCALEUP.toString().toUpperCase()
def http = HTTP_TEST.toString().toUpperCase()
def logging = LOGGING_SCALE_TEST.toString().toUpperCase()
def pgbench_test = PGBENCH_TEST.toString().toUpperCase()
def prometheus_test = PROMETHEUS_TEST.toString().toUpperCase()
def mongodb_ycsb_test = MONGODB_YCSB_TEST.toString().toUpperCase()
def podvertical = PODVERTICAL.toString().toUpperCase()
def deployments_per_ns = DEPLOYMENTS_PER_NS.toString().toUpperCase()
def ns_per_cluster = NS_PER_CLUSTER.toString().toUpperCase()
def scale_ci_update_jobs = SCALE_CI_UPDATE_JOBS.toString().toUpperCase()
def networking = NETWORKING.toString().toUpperCase()
def byo = BYO_SCALE_TEST.toString().toUpperCase()
def ocp_4.x_scaleup = OPENSHIFT_4.X_SCALEUP.toString().toUpperCase()
def node_label = NODE_LABEL.toString()

node (node_label) {
	// setup the repo containing the pipeline scripts
	stage('cloning pipeline repo') {
		checkout scm
	}

	// creates/updates jenkins jobs using the jjb templates
	if (scale_ci_update_jobs == "TRUE") {
		load "scale-ci/pipeline-scripts/scale_ci_update_jobs.groovy"
	}

	// stage to install openstack
	if (install_openstack == "TRUE") {
		load "scale-ci/pipeline-scripts/openstack.groovy"
	}

	// stage to set up browbeat
	if (browbeat == "TRUE") {
		load "scale-ci/pipeline-scripts/browbeat.groovy"
	}

	// stage to install openshift
	if (openshift_install == "TRUE") {
		load "scale-ci/pipeline-scripts/openshift.groovy"
	}

	// stage to setup pbench
	if (setup_tooling == "TRUE") {
		load "scale-ci/pipeline-scripts/tooling.groovy"
	}

	// stage to run conformance
	if (run_conformance == "TRUE") {
		load "scale-ci/pipeline-scripts/conformance.groovy"
	}

	// stage to scaleup the cluster
	if (scaleup == "TRUE") {
		load "scale-ci/pipeline-scripts/scaleup.groovy"
	}

	// stage to run nodevertical scale test
	if (nodevertical == "TRUE") {
		load "pipeline-scripts/nodevertical.groovy"
	}

	// stage to run http scale test
	if (http == "TRUE") {
		load "scale-ci/pipeline-scripts/http.groovy"
	}

	// stage to run deployments per ns test
	if ( deployments_per_ns == "TRUE" ) {
		load "scale-ci/pipeline-scripts/deployments_per_ns.groovy"
	}

	// stage to run podvertical test
	if ( podvertical == "TRUE" ) {
		load "scale-ci/pipeline-scripts/podvertical.groovy"
	}

	// stage to run networking test
	if ( networking == "TRUE" ) {
		load "scale-ci/pipeline-scripts/networking.groovy"
	}

	// stage to run pgbench scale test
	if ( pgbench_test == "TRUE" ) {
		load "scale-ci/pipeline-scripts/pgbench.groovy"
	}

	// stage to run mongodb ycsb scale test
	if ( mongodb_ycsb_test == "TRUE" ) {
		load "scale-ci/pipeline-scripts/mongodbycsb.groovy"
	}

	// stage to run mastervertical scale test
	if (mastervertical == "TRUE") {
		load "scale-ci/pipeline-scripts/mastervertical.groovy"
	}

	// stage to run ns_per_cluster test
	if ( ns_per_cluster == "TRUE" ) {
		load "scale-ci/pipeline-scripts/ns_per_cluster.groovy"
	}

	// stage to run logging scale test
	if (logging == "TRUE") {
		load "scale-ci/pipeline-scripts/logging.groovy"
	}

	// stage to run prometheus scale test
	if ( prometheus_test == "TRUE" ) {
		load "scale-ci/pipeline-scripts/prometheus.groovy"
	}

	// stage to run BYO scale test
	if (byo == "TRUE") {
		load "scale-ci/pipeline-scripts/byo.groovy"
	}

	// stage to run OCP 4.X scaleup
	if (ocp_4.x_scaleup == "TRUE") {
		load "scale-ci/pipeline-scripts/OCP-4.X/scaleup.groovy"
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
