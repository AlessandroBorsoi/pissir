package it.uniupo.disit.pissir.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static org.junit.Assert.fail;

class SubscribeCallback implements MqttCallback {

    final private ObjectMapper objectMapper;
    private int messageCounter;

    public SubscribeCallback(ObjectMapper objectMapper) {
        this.messageCounter = 0;
        this.objectMapper = objectMapper;
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            objectMapper.readValue(message.getPayload(), Csv.class);
            messageCounter++;
        } catch (Exception e) {
            fail();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public int getMessageCounter() {
        return messageCounter;
    }
}
