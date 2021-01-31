## Local

### docker

docker pull debezium/postgres:11-alpine \
    && docker pull hazelcast/hazelcast:4.0.3 \
    && docker pull dcdh1983/postgresql-10-debezium-centos7:latest \
    && docker pull debezium/kafka:1.4.1.Final \
    && docker pull debezium/zookeeper:1.4.1.Final \
    && docker pull debezium/connect:1.4.1.Final \
    && docker pull registry.access.redhat.com/ubi8/ubi-minimal:8.1
