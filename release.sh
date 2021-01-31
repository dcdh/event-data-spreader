#!/bin/bash
# https://gist.github.com/ricardozanini/fa65e485251913e1467837b1c5a8ed28
export GRAALVM_HOME=/usr/lib/jvm/graalvm/
export JAVA_HOME=${GRAALVM_HOME}
export PATH=${GRAALVM_HOME}/bin:$PATH

if [[ -n $(git status -s) ]]
then
  echo 'release cannot be performed when uncommited changes are presents'
  exit 1
fi
mvn clean test install -pl eventsourced-mutable-kafka-connect
docker build -f eventsourced-mutable-kafka-connect/Dockerfile -t damdamdeo/eventsourced-mutable-kafka-connect:1.4.1.Final eventsourced-mutable-kafka-connect

mvn versions:set -DnewVersion=1.0-$(git rev-parse --short HEAD) -DprocessAllModules=true
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f;
mvn clean test install
mvn versions:revert
