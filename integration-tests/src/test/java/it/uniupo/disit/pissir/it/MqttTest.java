package it.uniupo.disit.pissir.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class MqttTest {
    CsvParser parser = new CsvParser();
    ClassLoader classLoader = getClass().getClassLoader();
    ObjectMapper objectMapper = new ObjectMapper();
    int messageCounter = 0;

    @Test
    public void parseCsv() throws Exception {
        URL resource = classLoader.getResource("small.csv");
        assertNotNull(resource);
        File file = new File(resource.getFile());

        Stream<Csv> csvStream = parser.csvLines(file)
                .map(line -> parser.parse(line))
                .flatMap(Optional::stream);

        assertEquals(71, csvStream.count());
    }

    @Test
    public void publish() throws Exception {
        String brokerURL = "tcp://localhost:1883";

        MqttClient clientPublisher = new MqttClient(brokerURL, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        clientPublisher.connect(options);
        MqttTopic testTopic = clientPublisher.getTopic("test");

        MqttClient clientSubscriber = new MqttClient(brokerURL, MqttClient.generateClientId());
        clientSubscriber.setCallback(new SubscribeCallback());
        clientSubscriber.connect();
        clientSubscriber.subscribe("test");

        URL resource = classLoader.getResource("small.csv");
        assertNotNull(resource);
        File file = new File(resource.getFile());

        Stream<Csv> csvStream = parser.csvLines(file)
                .map(line -> parser.parse(line))
                .flatMap(Optional::stream);

        csvStream.forEach(csv -> {
            try {
                byte[] payload = objectMapper.writeValueAsBytes(csv);
                testTopic.publish(new MqttMessage(payload));
            } catch (Exception e) {
                fail();
            }
        });

        await().atMost(5, SECONDS).until(() -> messageCounter == 71);
        assertEquals(71, messageCounter);
    }

    class SubscribeCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            messageCounter++;
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    }
}
