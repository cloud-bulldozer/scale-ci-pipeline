# SCALE-CI-PIPELINE
Automates the installation of OCP on various cloud platforms and runs performance and scale tests related to kubelet density, control plane, http, storage, prometheus and cluster limits.

## Components
- Properties files
- Pipeline scripts
- scale-ci-watcher

### Properties files
The parameters/configuration of each Job in the scale-ci-pipeline is supplied through the properties files. It contains key=value pairs, the sample properties for all the supported jobs are hosted in scale-ci/properties.

### Pipeline scripts
These scripts are responsible for parsing the properties files and building the respectibe Job.

### scale-ci-watcher
This looks for changes to the JJB templates or new templates and updates/onboards the Jobs into the scale-ci-pipeline. The watcher also supports xml format, it has the support to convert them to JJB format.
