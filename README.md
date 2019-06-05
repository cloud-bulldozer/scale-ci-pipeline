# SCALE-CI-PIPELINE
Automates the installation of OCP on various cloud platforms and runs performance and scale tests related to kubelet density, control plane, http, storage, prometheus and cluster limits.

NOTE: This is also maintained at openshift/aos-cd-jobs repo [https://github.com/openshift/aos-cd-jobs].

### Dependencies
```
- Running Jenkins instance.
```

## Components
- Properties files
- Pipeline scripts
- scale-ci-watcher
- scale-ci-linter

### Properties files
The parameters/configuration of each Job in the scale-ci-pipeline is supplied through the properties files. It contains key=value pairs, the sample properties for all the supported jobs are hosted in scale-ci/properties.

### Pipeline scripts
These scripts are responsible for parsing the properties files and building the respective Job.

### Scale-ci-watcher
This looks for changes to the JJB templates or new templates and updates/onboards the Jobs into the scale-ci-pipeline. The watcher also supports xml format, it has the support to convert them to JJB format.

### Scale-ci-linter
Validates scale-ci templates to analyze them for potential errors for every commit. This can be run locally as well in case we want to check before pushing the commit:
```
$ pip install yamllint
$ ./scale-ci-linter.sh <path-to-the-template>
```

### Supported Jobs by Scale-CI
Job   | OCP component/category | Description | Managed by scale-ci-watcher | OCP-3.X | OCP-4.X
----------------- | --------- | -------------------- | ----------- | ------------------ | ----------- |  
OpenShift install | Installer | Installs OCP cluster | In progress | :heavy_check_mark: | In progress |  
Conformance | OCP/kubernetes e2e tests | Checks if the OCP cluster is sane | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Scaleup | scaleup | Scales up OCP cluster to a desired node count | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Node Vertical | Kubelet density | Creates max pods per compute node | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Master Vertical | Control plane density | Creates bunch of objects to stress ApiServer, Etcd and Controller | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Networking | SDN | uperf benchmarks testing node to node, pod to pod, and svc to svc throughput and latency for TCP and UDP protocols | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Prometheus | Monitoring | prometheus density focused test that creates pods, and nodes and some workloads especially for openshift-monitoring | In progress | :heavy_check_mark: | :heavy_check_mark: |  
HTTP | Router | Data-plane workload generator that runs http requests through HAProxy into deployed pods | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Pgbench | Storage | Checks how well Postgresql performs with RHOCS for glusterfs and gluster-block storage backend | In progress | :heavy_check_mark: | :heavy_check_mark: |  
MongoDB | Storage | Checks how MongoDB performs with RHOCS for glusterfs and gluster-block storage backend | In progess | :heavy_check_mark: | :heavy_check_mark: |  
Pod Vertical | Cluster Limits | Tests pods per namespace limit | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Deployments per namespaces | Cluster Limits | Tests deployments per namespace limit | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Namespaces per cluster | Cluster Limits | Tests namespaces per cluster limit | In progress | :heavy_check_mark: | :heavy_check_mark: |  
Services per namespace | Cluster Limits | Tests maximum number of services possible per namespace | In progress | :heavy_check_mark: | :heavy_check_mark: |
