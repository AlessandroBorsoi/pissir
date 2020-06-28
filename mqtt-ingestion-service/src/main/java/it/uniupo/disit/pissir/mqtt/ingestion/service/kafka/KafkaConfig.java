package it.uniupo.disit.pissir.mqtt.ingestion.service.kafka;

import com.typesafe.config.Config;

public class KafkaConfig {
    private final String url;
    private final String schemaRegistryUrl;
    private final String topic;

    public KafkaConfig(Config config) {
        this.url = config.getString("services.kafka.url");
        this.schemaRegistryUrl = config.getString("services.kafka.schema.registry.url");
        this.topic = config.getString("services.kafka.topic");
    }

    public String getTopic() {
        return topic;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public String getUrl() {
        return url;
    }
}
