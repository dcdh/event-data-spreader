#!/bin/bash
mvn clean install

docker build -f Dockerfile -t damdamdeo/eventsourced-mutable-kafka-connect:1.2.0.Final .
