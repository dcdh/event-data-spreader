# Goal

Create a sample cqrs library.

keywords: **Openshift 3.11**, **cqrs**, **Quarkus**, **Kafka**, **Debezium**  

## CQRS

Split in three modules:
1. debezium-event-consumer
Module used by write side and query side to consume message sent in kafka.
1. write-side
Write side module: allow to create event and push them by using Debezium in a Kafka topic.
1. query-side
Query side module: consume events from Kafka.

## Openshift

git clone https://github.com/dcdh/event-data-spreader.git

oc process -f templates/event-data-spreader-build-pipeline.yml | oc create -f - -n ci

docker pull openshift/jenkins-agent-maven-35-centos7:v3.11 && \
docker pull debezium/zookeeper:1.1.1.Final && \
docker pull debezium/kafka:1.1.1.Final && \
docker pull debezium/connect:1.1.1.Final && \
docker pull dcdh1983/postgresql-10-debezium-centos7:latest && \
docker pull giantswarm/tiny-tools && \
docker pull maven:3.6.3-jdk-8-slim && \
docker pull vault:1.3.2

### Fix jenkins build
> An issue is present following the last version of **openshift/jenkins-agent-maven-35-centos7:v3.11**
> https://github.com/openshift/jenkins/issues/997
> We need to downgrade the version of jenkins used to this commit id **e2a35ea**
> /!\ I do not know if earlier one from **e2a35ea** are working.

Follow theses steps to downgrade your version of Jenkins

1. oc scale --replicas=0 dc jenkins
1. git clone https://github.com/openshift/jenkins.git
1. cd jenkins
1. git checkout e2a35ea
1. make build
1. docker tag docker.io/openshift/jenkins-agent-maven-35-centos7:latest docker.io/openshift/jenkins-agent-maven-35-centos7:e2a35ea
1. docker tag docker.io/openshift/jenkins-2-centos7:latest 172.30.247.189:5000/openshift/jenkins-2-centos7:e2a35ea
1. oc login -u sandbox -p sandbox (log to openshift using our appropriate username and passord)
1. docker login -u openshift -p $(oc whoami -t) 172.30.247.189:5000
1. docker push 172.30.247.189:5000/openshift/jenkins-2-centos7:e2a35ea
1. purge volume used by previous jenkins version (if using persistent jenkins template)
> If you've used a persistent version of jenkins deployment, you'll need to purge the filesystem bound to it.
> However, the previous configuration, plugins... will clash with the ones used by this image.
> Do not worry. When Jenkins will be restarted it will setup all pipelines from the ones defined in OpenShift.
1. oc set triggers dc/jenkins --from-image=openshift/jenkins-2-centos7:e2a35ea --containers=jenkins
1. oc scale --replicas=1 dc jenkins
