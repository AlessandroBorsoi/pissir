package it.uniupo.disit.pissir.service;

import it.uniupo.disit.pissir.service.kafka.KafkaConfig;
import it.uniupo.disit.pissir.service.mqtt.MqttConfig;

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
