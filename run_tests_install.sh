#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f; \
  export TESTCONTAINERS_RYUK_DISABLED=true; \
  mvn clean test install
