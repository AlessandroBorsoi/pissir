package it.uniupo.disit.pissir.service;

import com.typesafe.config.Config;

public class KafkaConfig {
    private final String host;
    private final int port;

    public KafkaConfig(Config config) {
        this.host = config.getString("services.kafka.host");
        this.port = config.getInt("services.kafka.port");
    }

    public String getServerConfig() {
        return host + ":" + port;
    }
}
