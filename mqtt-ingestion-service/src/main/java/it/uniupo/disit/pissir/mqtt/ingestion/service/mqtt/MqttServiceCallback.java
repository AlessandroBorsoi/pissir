package it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniupo.disit.pissir.avro.OpenPflow;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaService;
import it.uniupo.disit.pissir.mqtt.ingestion.service.OpenPflowConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Optional;

class MqttServiceCallback implements MqttCallback {
    private static final Logger logger = LogManager.getLogger(MqttServiceCallback.class);

    private final ObjectMapper objectMapper;
    private final KafkaService kafkaService;

    public MqttServiceCallback(ObjectMapper objectMapper, KafkaService kafkaService) {
        this.objectMapper = objectMapper;
        this.kafkaService = kafkaService;
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            var event = objectMapper.readValue(message.getPayload(), OpenPflowRaw.class);
            Optional<OpenPflow> optionalOpenPflow = OpenPflowConverter.from(event);
            optionalOpenPflow.ifPresentOrElse(
                    kafkaService::send,
                    () -> logger.error("Cannot convert MQTT message: " + event.toString())
            );
        } catch (Exception e) {
            logger.error("Cannot parse OpenPflowRaw event", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
