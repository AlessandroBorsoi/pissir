package it.uniupo.disit.pissir.service.mqtt;

import com.typesafe.config.Config;

public class MqttConfig {
    private final String host;
    private final int port;
    private final String topic;

    public MqttConfig(Config config) {
        this.host = config.getString("services.mqtt.host");
        this.port = config.getInt("services.mqtt.port");
        this.topic = config.getString("services.mqtt.topic");
    }

    public String serverURI() {
        return host + ":" + port;
    }

    public String getTopic() {
        return topic;
    }
}
