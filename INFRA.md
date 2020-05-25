# Useful commands when running on local

## Write
 
> docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose up

> docker exec -it eventdataspreader_eventstore_1 bash

> pg_dump -d eventstore -U postgres

> psql -d eventstore -U postgres

## Query

> docker exec -it queryside_query_1 bash

> pg_dump -d query -U postgresql

> psql -d query -U postgresql

> DROP SCHEMA public CASCADE;CREATE SCHEMA public;