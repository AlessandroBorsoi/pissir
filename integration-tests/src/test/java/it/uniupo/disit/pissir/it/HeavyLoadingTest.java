package it.uniupo.disit.pissir.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

public class HeavyLoadingTest {
    private final CsvParser parser = new CsvParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static String brokerURL;
    private static String topic;
    private static String dataDir;

    @BeforeClass
    public static void setup() {
        Config config = ConfigFactory.load();
        brokerURL = config.getString("services.mosquitto.url");
        topic = config.getString("services.mosquitto.topic");
        dataDir = config.getString("services.csv.directory");
    }

    @Test
    public void test() throws Exception {
        MqttClient clientPublisher = new MqttClient(brokerURL, MqttClient.generateClientId(), new MqttDefaultFilePersistence("/tmp"));
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        clientPublisher.connect(options);
        MqttTopic testTopic = clientPublisher.getTopic(topic);
        List<String> csvFiles = getCsvFiles(dataDir);

        for (String csvFile : csvFiles) {
            File file = new File(csvFile);
            Stream<Csv> csvStream = parser.csvLines(file)
                    .map(parser::parse)
                    .flatMap(Optional::stream)
                    .parallel();

            csvStream.parallel().forEach(csv -> {
                try {
                    byte[] payload = objectMapper.writeValueAsBytes(csv);
                    MqttMessage message = new MqttMessage(payload);
                    message.setQos(0);
                    testTopic.publish(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail();
                }
            });
        }
    }

    private List<String> getCsvFiles(String directory) {
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {
            return walk
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".csv"))
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
            fail("No csv files found");
            return emptyList();
        }
    }
}
