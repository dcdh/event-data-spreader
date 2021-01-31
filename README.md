# Goal

Create a sample cqrs library.

keywords: **Openshift 3.11**, **cqrs**, **eventsourcing**, **Quarkus**, **Kafka**, **Debezium**  

## CQRS

Split in two parts:
1. mutable
The mutable part deal with data manipulation using event sourcing pattern.
1. consumer
The consumer part deal with consuming event sourced data

## How to

### local installation

From the console under the project run this:

```bash
docker pull debezium/postgres:11-alpine && \
  docker pull debezium/connect:1.4.1.Final && \
  docker pull confluentinc/cp-kafka:5.2.1 && \
  docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; \
  mvn clean test install
```

### OKD 3.11 installation

> git clone https://github.com/dcdh/event-data-spreader.git

```bash
ssh damien@master.okd.local 'docker pull debezium/postgres:11-alpine' && \
  ssh damien@master.okd.local 'docker pull debezium/connect:1.4.1.Final' && \
  ssh damien@master.okd.local 'docker pull confluentinc/cp-kafka:5.2.1' && \
  ssh damien@master.okd.local 'docker pull adoptopenjdk/maven-openjdk8' && \
  ssh damien@master.okd.local 'docker pull docker:18.09.7-dind' && \
  ssh damien@master.okd.local 'docker pull busybox' && \
  scp -r -p openshift/jenkins-pipeline.yml damien@master.okd.local:/tmp && \
  ssh damien@master.okd.local 'oc process -f /tmp/jenkins-pipeline.yml | oc apply -f - -n ci-cd' && \
  ssh damien@master.okd.local 'oc adm policy add-scc-to-user -z jenkins privileged -n ci-cd'
```

## References

> testcontainers : https://www.testcontainers.org/
>
> testcontainers Spring Boot : https://github.com/testcontainers/testcontainers-spring-boot
>
> jenkins kubernetes : https://timmhirsens.de/posts/2019/07/testcontainers_on_jenkins_with_kubernetes/
>
> OKD 3.11 fix jenkins version : https://github.com/dcdh/okd-3x-local-installation/blob/master/jenkins_fix.md
>
> OKD 3.11 local installation : https://github.com/dcdh/okd-3x-local-installation/blob/master/nexus_docker_proxy_repository_installation.md
> 