## Create database

make sure Docker is running
docker-compose up -d
docker exec -it postgres_db bash
psql -U myuser -d shorturldb
CREATE DATABASE shorturldb;
exit

## Compile project

./gradlew clean build



## Useful commands
### remove docker container with docker volumes
docker-compose down -v
