package it.uniupo.disit.pissir.it;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class MqttSetup {
    private final MqttTopic mqttTopic;

    public MqttSetup() throws MqttException {
        Config config = ConfigFactory.load();
        var brokerURL = config.getString("services.mosquitto.url");
        var topic = config.getString("services.mosquitto.topic");
        MqttClient clientPublisher = new MqttClient(brokerURL, MqttClient.generateClientId(), new MqttDefaultFilePersistence("/tmp"));
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setMaxInflight(1000);
        clientPublisher.connect(options);
        this.mqttTopic = clientPublisher.getTopic(topic);
    }

    public MqttTopic getTopic() {
        return mqttTopic;
    }
}
