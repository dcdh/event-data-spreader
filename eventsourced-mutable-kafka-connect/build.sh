#!/bin/bash
mvn clean install

docker build -f Dockerfile -t damdamdeo/eventsourced-mutable-kafka-connect:1.4.1.Final .
