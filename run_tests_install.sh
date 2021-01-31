#!/bin/bash
# https://gist.github.com/ricardozanini/fa65e485251913e1467837b1c5a8ed28
export GRAALVM_HOME=/usr/lib/jvm/graalvm/
export JAVA_HOME=${GRAALVM_HOME}
export PATH=${GRAALVM_HOME}/bin:$PATH

mvn clean test install -pl eventsourced-mutable-kafka-connect
docker build -f eventsourced-mutable-kafka-connect/Dockerfile -t damdamdeo/eventsourced-mutable-kafka-connect:1.4.1.Final eventsourced-mutable-kafka-connect

docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f; \
  mvn clean test install
