package it.uniupo.disit.pissir.service.mqtt;

import com.typesafe.config.Config;

public class MqttConfig {
    private final String url;
    private final String topic;

    public MqttConfig(Config config) {
        this.url = config.getString("services.mqtt.url");
        this.topic = config.getString("services.mqtt.topic");
    }

    public String getUrl() {
        return url;
    }

    public String getTopic() {
        return topic;
    }
}
