#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f; \
  mvn clean test install
