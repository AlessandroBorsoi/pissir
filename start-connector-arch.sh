#!/usr/bin/env bash

printf "Running docker compose...\n"
docker-compose -f ./docker/connector-architecture/docker-compose.yml up -d

echo "Creating topic"
#docker run --net=host --rm confluentinc/cp-kafka:5.5.0 kafka-topics --create --topic open-pflow --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:2181

echo "Waiting for connectors up"
until $(curl --output /dev/null --silent --head --fail localhost:8083/connectors); do
    printf '.'
    sleep 5
done
printf "\nUp\n"

printf "Configuring connectors...\n"
printf "MQTT source connector...\n"
curl -d @./docker/connect-mqtt-source.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
printf "\nMongoDB sink connector...\n"
curl -d @./docker/connect-mongodb-sink.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors

