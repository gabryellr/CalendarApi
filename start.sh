#!/bin/bash

echo "###################### Bulding jar file ######################"
mvn package

echo "###################### Building application docker image ######################"
docker build -t springbootapplication .

echo "###################### Runnning docker compose ######################"
docker-compose up

$SHELL