#!/usr/bin/env bash

printf "Shutdown docker compose...\n"
docker-compose -f ./docker/service-architecture/docker-compose.yml down -v
docker-compose -f ./docker/connector-architecture/docker-compose.yml down -v
docker-compose -f ./docker/proxy-architecture/docker-compose.yml down -v
