#!/usr/bin/env bash

printf "Running docker compose...\n"
docker-compose -f ./docker/service-architecture/docker-compose.yml up -d


