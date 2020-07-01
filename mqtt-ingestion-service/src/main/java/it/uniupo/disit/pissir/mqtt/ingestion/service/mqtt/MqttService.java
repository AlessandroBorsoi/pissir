package it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.concurrent.CountDownLatch;

public class MqttService implements Runnable {
    private static final Logger logger = LogManager.getLogger(MqttService.class);

    private final MqttConfig mqttConfig;
    private final KafkaService kafkaService;
    private final CountDownLatch latch;

    public MqttService(MqttConfig mqttConfig, KafkaService kafkaService, CountDownLatch latch) {
        this.mqttConfig = mqttConfig;
        this.kafkaService = kafkaService;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            var mqttClient = new MqttClient(mqttConfig.getUrl(), MqttClient.generateClientId(), new MqttDefaultFilePersistence("/tmp"));
            var callback = new MqttServiceCallback(new ObjectMapper(), kafkaService, latch);
            mqttClient.setCallback(callback);
            mqttClient.connect();
            var topic = mqttConfig.getTopic();
            mqttClient.subscribe(topic);
            logger.info("The subscriber is now listening to " + topic + "...");
        } catch (Exception e) {
            logger.error(e);
        } finally {
            latch.countDown();
        }
    }

}
