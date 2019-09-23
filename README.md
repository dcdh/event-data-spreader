> docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose up

> docker exec -it eventdataspreader_eventstore_1 bash

> pg_dump -d eventstore -U postgres

> psql -d eventstore -U postgres


> docker exec -it queryside_query_1 bash

> pg_dump -d query -U postgres

> psql -d query -U postgres


> DROP SCHEMA public CASCADE;CREATE SCHEMA public;
