#!/bin/bash
curl -X DELETE http://localhost:8083/connectors/todo-connector;
mvn clean test
