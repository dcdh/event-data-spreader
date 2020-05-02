#!/bin/bash
# delete kafka connector
curl -X DELETE http://localhost:8083/connectors/todo-connector;
# setup vault account
$(dirname $0)/../scripts/init_vault.sh
mvn clean test install
