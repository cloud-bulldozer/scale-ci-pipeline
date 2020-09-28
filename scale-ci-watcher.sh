#!/bin/bash

set -o pipefail

function help() {
	printf "\n"
        printf "Usage: source scale_ci_watcher_options.sh; $0\n"
	printf "\n"
	printf "Options supported:\n"
	printf "\t JJB_CONFIG_PATH=str,              str=path to the jenkins job builder config to connect to jenkins\n"
	printf "\t SCALE_CI_WATCHER_REPO=str,        str=repo hosting scale-ci watcher\n"
	printf "\t SCALE_CI_WATCHER_REPO_BRANCH=str, str=branch to clone\n"
	printf "\t SCALE_CI_WATCHER_REPO_PATH=str,   str=location of the scale-ci-watcher repo\n"
	printf "\t WORKDIR=str,                      str=location of the scale-ci-watcher repo\n"
	printf "\t UPDATE_SCALE_CI_JOBS=str,         str=true or false\n"
	printf "\t ADD_PUBLIC_KEY=str,               str=true or false\n"
	printf "\t PUBLIC_KEY=str,                   str=public_key to add to jump host, this provides access to the scale cluster\n"
	printf "\t KEYS_PATH=str,                    str=path to the keys\n"
}

# Defaults
watcher_retries=10
sleep_time=10

if [[ "$#" -ne 0 ]]; then
	help
	exit 1
fi

# options
if [[ -z "$JJB_CONFIG_PATH" ]]; then
	echo "JJB_CONFIG_PATH is not defined, please check if it's defined as an environment variable"
	help
	exit 1
fi
if [[ -z "$SCALE_CI_WATCHER_REPO" ]]; then
        echo "SCALE_CI_WATCHER_REPO is not defined, please check if it's defined as an environment variable"  
	help
	exit 1
fi
if [[ -z "$SCALE_CI_WATCHER_REPO_BRANCH" ]]; then
        echo "SCALE_CI_WATCHER_REPO_BRANCH is not defined, please check if it's defined as an environment variable"
        help
        exit 1
fi
if [[ -z "$SCALE_CI_WATCHER_REPO_PATH" ]]; then
	echo "SCALE_CI_WATCHER_REPO_path is not defined, please check if it's defined as an environment variable"
	help
	exit 1
fi
if [[ -z "$WORKDIR" ]]; then
	echo "WORKDIR is not defined, please check if it's defined as an environment variable"  
	help
	exit 1
fi
if [[ -z "$UPDATE_SCALE_CI_JOBS" ]]; then
	echo "UPDATE_SCALE_CI_JOBS is not defined, please check if it's defined as an environment variable"  
	help
	exit 1
fi
if [[ -z "$ADD_PUBLIC_KEY" ]]; then
        echo "ADD_PUBLIC_KEY is not defined, please check if it's defined as an environment variable" 
        help
        exit 1
fi

# install jenkins job builder and dependencies if not already installed
function setup_jenkins_job_builder() {
	which jenkins-jobs &>/dev/null
	if [[ $? != 0 ]]; then
		echo "Looks like the jenkins-job-builder is not installed"
		echo "Installing Jenkins job builder"
		pip install jenkins-job-builder
		pip install six
	fi
}

# setup jjwrecker and convert xml to jjb
function setup_jjwrecker() {
	which jjwrecker &>/dev/null
	if [[ $? != 0 ]]; then
        	echo "Looks like the jjwrecker is not installed"
        	echo "Installing"
        	pip install jenkins-job-wrecker
	fi
}

# create/update scale-ci jobs
function update_scale_ci_jobs() {
        echo "------------------------------------------------------------------"
	echo "                 Running scale-ci-watcher                         "
	echo "------------------------------------------------------------------"
	if [[ ! -d "$SCALE_CI_WATCHER_REPO_PATH" ]]; then
        	git clone $SCALE_CI_WATCHER_REPO $SCALE_CI_WATCHER_REPO_PATH
		pushd $SCALE_CI_WATCHER_REPO_PATH
		git checkout $SCALE_CI_WATCHER_REPO_BRANCH
		popd
	fi
	
	echo "------------------------------------------------------------------"
	echo "Converting xml to jjb templates"
	echo "------------------------------------------------------------------"
	pushd $WORKDIR/xml
	for xml_template in $(ls $WORKDIR/xml); do
		if [[ "$xml_template" != "README.md" ]]; then
			jjb_filename=$(basename $xml_template | cut -d '.' -f1)
			jjb_output_path=$WATCHER_TARGET_DIRECTORY
			jjwrecker -f $xml_template -n $jjb_filename -o $jjb_output_path
			if [[ $? != 0 ]]; then
				echo "Failed to convert xml to jjb template, please check"
				exit 1
			fi
		fi
	done
	popd

	echo "INFO: Template type is jjb"	
	pushd $WATCHER_TARGET_DIRECTORY
	for template in $(ls $WATCHER_TARGET_DIRECTORY); do
        	if [[ "$template" != "README.md" ]]; then
			echo "--------------------------------------------------"
			echo "INFO: Found $template"
			echo -e "--------------------------------------------------\n"
			retries=0
         		for (( iter=1; iter<=$watcher_retries; iter++ ))
			do
                		jenkins-jobs --conf "$JJB_CONFIG_PATH" update "$template"
				exit_code=$?
				if [[ $exit_code == 0 ]]; then
					break
				else
					echo "Iteration $iter: Failed to create/update the jenkins job, retrying after $sleep_time seconds"
					sleep $sleep_time
				fi
			done
                	if [[ $exit_code != 0 ]]; then
                        	echo "Failed to create/update the jenkins job, please check"
                        	exit 1
                	fi
			echo -e "\n"
        	fi
	done
	popd
}

# allow users to access the cluster
function add_keys() {
	echo "Adding public key to the authorized_keys file on the jump host to provide access to the scale cluster"
	echo $PUBLIC_KEY >> $KEYS_PATH
	chmod 400 $KEYS_PATH
}

if [[ "$UPDATE_SCALE_CI_JOBS" == "true" ]]; then
	setup_jenkins_job_builder
	setup_jjwrecker
	update_scale_ci_jobs
fi

if [[ "$ADD_PUBLIC_KEY" == "true" ]]; then
	add_keys
fi
