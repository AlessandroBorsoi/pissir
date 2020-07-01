package it.uniupo.disit.pissir.mqtt.ingestion.service.kafka;

import it.uniupo.disit.pissir.avro.OpenPflow;

@FunctionalInterface
public interface KafkaService {
    void send(OpenPflow openPflow);
}
