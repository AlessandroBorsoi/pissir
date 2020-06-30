package it.uniupo.disit.pissir.mqtt.ingestion.service;

import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaConfig;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.MqttConfig;

public class AppConfig {
    private final MqttConfig mqttConfig;
    private final KafkaConfig kafkaConfig;

    public AppConfig(MqttConfig mqttConfig, KafkaConfig kafkaConfig) {
        this.mqttConfig = mqttConfig;
        this.kafkaConfig = kafkaConfig;
    }

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }
}
