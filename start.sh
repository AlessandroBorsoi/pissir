#!/usr/bin/env bash

printf "Running docker compose...\n"
docker-compose -f ./docker/service-architecture/docker-compose.yml up -d

echo "Waiting for connectors up"
until $(curl --output /dev/null --silent --head --fail localhost:8083/connectors); do
    printf '.'
    sleep 5
done
printf "\nUp\n"

printf "Configuring connectors...\n"
printf "MongoDB sink connector...\n"
curl -d @./docker/service-architecture/connect-mongodb-sink.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
