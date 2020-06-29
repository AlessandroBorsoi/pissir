package it.uniupo.disit.pissir.mqtt.proxy.service;

import com.typesafe.config.Config;

public class AppConfig {
    private final String applicationId;
    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String sourceTopicName;
    private final String destinationTopicName;

    public AppConfig(Config config) {
        this.bootstrapServers = config.getString("kafka.bootstrap.servers");
        this.schemaRegistryUrl = config.getString("kafka.schema.registry.url");
        this.sourceTopicName = config.getString("kafka.source.topic.name");
        this.destinationTopicName = config.getString("kafka.destination.topic.name");
        this.applicationId = config.getString("kafka.streams.application.id");
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public String getSourceTopicName() {
        return sourceTopicName;
    }

    public String getDestinationTopicName() {
        return destinationTopicName;
    }
}
