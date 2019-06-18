#!/bin/bash

# Path to the jenkins job builder config to connect to jenkins
export JJB_CONFIG_PATH="/root/scale-ci-pipeline/config/jjb.ini"
# Repo hosting scale-ci watcher
export SCALE_CI_WATCHER_REPO="https://github.com/openshift-scale/scale-ci-pipeline.git"
# branch to clone
export SCALE_CI_WATCHER_REPO_BRANCH="master"
# Location of the scale-ci-watcher repo
export SCALE_CI_WATCHER_REPO_PATH="/root/scale-ci-pipeline"
# Location of the scale-ci-watcher repo
export WORKDIR="/root/scale-ci-pipeline"
# Whether to update the scale-ci jobs or not
export UPDATE_SCALE_CI_JOBS=true
# This is useful when running the watcher job in jenkins, it adds any public key to the jump host, this way other teams can access the jump host with having the perf key.
# Whether to add public key or not
export ADD_PUBLIC_KEY=false
# Public key to be added to the jump host
export PUBLIC_KEY=""
# Location of the authorized keys file to add the public key
export KEYS_PATH="/root/.ssh/authorized_keys" 
