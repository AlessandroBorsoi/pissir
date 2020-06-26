package it.uniupo.disit.pissir.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.BlockingQueue;

class SubscribeCallback implements MqttCallback {
    private static final Logger logger = LogManager.getLogger(SubscribeCallback.class);

    private final ObjectMapper objectMapper;
    private final BlockingQueue<Pflow> queue;

    public SubscribeCallback(ObjectMapper objectMapper, BlockingQueue<Pflow> queue) {
        this.objectMapper = objectMapper;
        this.queue = queue;
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            var event = objectMapper.readValue(message.getPayload(), Pflow.class);
            queue.put(event);
        } catch (Exception e) {
            logger.error("Cannot parse PFLOW event", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
