#!/usr/bin/env bash

printf "Shutdown docker compose...\n"
docker-compose -f ./docker/service-architecture/docker-compose.yml down -v
