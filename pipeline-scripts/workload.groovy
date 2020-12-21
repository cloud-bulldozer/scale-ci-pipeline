#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
def node_label = NODE_LABEL.toString()
def property_file_name = "workload.properties"
def contact = "msheth@redhat.com, nelluri@redhat.com"
def workload = env.WORKLOAD
def workload_properties_file = env.WORKLOAD_PROPERTIES_FILE

println "Current pipeline job build id is '${pipeline_id}'"

// run workload
stage (workload) {
	currentBuild.result = "SUCCESS"
	node(node_label) {
	// get properties file
	if (fileExists(property_file_name)) {
		println "Looks like the property file already exists, erasing it"
		sh "rm ${property_file_name}"
	}
	// get properties file
	sh "wget ${workload_properties_file} -O ${property_file_name}"
	sh "cat ${property_file_name}"

	// Load the properties file.
	def properties = readProperties file: property_file_name
	def job_parameters = []
	job_parameters.add([$class: 'LabelParameterValue', name: 'node', label: node_label ])
	
	// Convert properties to parameters.
	for (property in properties) {
		job_parameters.add([$class: 'StringParameterValue', name: property.key, value: property.value ])
	}

	// run the job with the properties
	try {
		run_workload = build job: workload, parameters: job_parameters
                println "${workload} build ${run_workload.getNumber()} completed successfully"
	} catch ( Exception e) {
		echo "${workload} Job failed with the following error: "
		echo "${e.getMessage()}"
		mail(
			to: contact,
			subject: "${pipeline_id}: ${workload} job failed",
			body: """\
			Encoutered an error while running the ${workload}: ${e.getMessage()}\n\n
			Jenkins job: ${env.BUILD_URL}
			""")
			currentBuild.result = "FAILURE"
			sh "exit 1"
		}
	}
}
