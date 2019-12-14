> docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose up

> docker exec -it eventdataspreader_eventstore_1 bash

> pg_dump -d eventstore -U postgres

> psql -d eventstore -U postgres


> docker exec -it queryside_query_1 bash

> pg_dump -d query -U postgres

> psql -d query -U postgres


> DROP SCHEMA public CASCADE;CREATE SCHEMA public;

## Openshift

git clone https://github.com/dcdh/event-data-spreader.git

oc process -f templates/event-data-spreader-build-pipeline.yml | oc create -f - -n ci

docker pull openshift/jenkins-agent-maven-35-centos7:v3.11 && \
docker pull debezium/zookeeper:0.10 && \
docker pull debezium/kafka:0.10 && \
docker pull debezium/connect:0.10 && \
docker pull dcdh1983/postgresql-10-debezium-centos7:latest && \
docker pull giantswarm/tiny-tools && \
docker pull maven:3.6.3-jdk-8-slim
