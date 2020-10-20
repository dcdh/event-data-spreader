#!/bin/bash
if [[ -n $(git status -s) ]]
then
  echo 'release cannot be performed when uncommited changes are presents'
  exit 1
fi
mvn versions:set -DnewVersion=1.0-$(git rev-parse --short HEAD) -DprocessAllModules=true
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f;
mvn clean test install
mvn versions:revert
