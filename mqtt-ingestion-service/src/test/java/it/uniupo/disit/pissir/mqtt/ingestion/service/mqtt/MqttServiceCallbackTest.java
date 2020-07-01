package it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniupo.disit.pissir.avro.Location;
import it.uniupo.disit.pissir.avro.OpenPflow;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MqttServiceCallbackTest {

    private MqttServiceCallback mqttServiceCallback;
    private KafkaService kafkaService;
    private CountDownLatch latch;

    @Before
    public void setUp() {
        kafkaService = mock(KafkaService.class);
        latch = new CountDownLatch(1);
        mqttServiceCallback = new MqttServiceCallback(new ObjectMapper(), kafkaService, latch);
    }

    @Test
    public void connectionLost() {
        mqttServiceCallback.connectionLost(new RuntimeException());

        assertEquals(0, latch.getCount());
    }

    @Test
    public void correctMessageArrived() {
        var message = "{\n" +
                "  \"id\": 1660,\n" +
                "  \"time\": \"2020/06/27 15:10:30\",\n" +
                "  \"longitude\": 139.60923,\n" +
                "  \"latitude\": 35.641745,\n" +
                "  \"transport\": 99,\n" +
                "  \"magnification\": 55.2 \n" +
                "}";

        mqttServiceCallback.messageArrived("", new MqttMessage(message.getBytes()));

        Location location = new Location("Point", List.of(139.60923, 35.641745));
        OpenPflow openPflow = new OpenPflow(1660, 99, 55.2, location, Instant.ofEpochSecond(1593238230));
        verify(kafkaService).send(openPflow);
    }

    @Test
    public void wrongMessageArrived() {
        mqttServiceCallback.messageArrived("", new MqttMessage("wrong".getBytes()));

        verify(kafkaService, never()).send(any(OpenPflow.class));
    }
}