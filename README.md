## Create database

make sure Docker is running

docker compose -f compose.database.yaml up -d

docker exec -it postgres_db bash

psql -U myuser -d shorturldb

CREATE DATABASE shorturldb;

exit

## Compile and run project

./gradlew clean build

./gradlew bootRun

## Run app in docker (app and DB)

./gradlew clean build

docker-compose up -d

docker compose -f compose.database-and-app.yml up -d

## build and docker image (app only)

docker build --tag=urlshorterner:latest .

docker run -p8080:8080 urlshorterner:latest

docker inspect urlshorterner

docker stop urlshorterner

docker rm urlshorterner

## Useful commands

### remove docker container with docker volumes

docker-compose down -v
