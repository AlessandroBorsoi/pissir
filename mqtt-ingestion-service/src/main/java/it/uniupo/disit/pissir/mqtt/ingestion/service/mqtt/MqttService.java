package it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.concurrent.BlockingQueue;

public class MqttService implements Runnable {
    private static final Logger logger = LogManager.getLogger(MqttService.class);

    private final MqttConfig mqttConfig;
    private final BlockingQueue<OpenPflowRaw> queue;

    public MqttService(MqttConfig mqttConfig, BlockingQueue<OpenPflowRaw> queue) {
        this.mqttConfig = mqttConfig;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            var mqttClient = new MqttClient(mqttConfig.getUrl(), MqttClient.generateClientId());
            var callback = new SubscribeCallback(new ObjectMapper(), queue);
            mqttClient.setCallback(callback);
            mqttClient.connect();
            var topic = mqttConfig.getTopic();
            mqttClient.subscribe(topic);
            logger.info("The subscriber is now listening to " + topic + "...");
        } catch (Exception e) {
            logger.error(e);
        }
    }

}
