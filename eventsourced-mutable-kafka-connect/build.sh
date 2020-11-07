#!/bin/bash
mvn clean install

docker build -f Dockerfile -t damdamdeo/eventsourced-mutable-kafka-connect:1.3.0.Final .
