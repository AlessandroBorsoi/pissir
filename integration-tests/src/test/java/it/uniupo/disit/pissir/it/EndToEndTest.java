package it.uniupo.disit.pissir.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class EndToEndTest {
    private final CsvParser parser = new CsvParser();
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static String mongoDbHost;
    private static int mongoDbPort;
    private static String mongoDbDatabase;
    private MongoCollection<Document> collection;
    private MqttSetup mqttSetup;

    @BeforeClass
    public static void setup() {
        Config config = ConfigFactory.load();
        mongoDbHost = config.getString("services.mongodb.host");
        mongoDbPort = config.getInt("services.mongodb.port");
        mongoDbDatabase = config.getString("services.mongodb.database");
    }

    @Before
    public void setUp() throws Exception {
        this.mqttSetup = new MqttSetup();
        MongoClient mongoClient = new MongoClient(mongoDbHost, mongoDbPort);
        MongoDatabase database = mongoClient.getDatabase(mongoDbDatabase);
        this.collection = database.getCollection("OpenPFLOW");
        collection.drop();
    }

    @After
    public void tearDown() {
        collection.drop();
    }

    @Test
    public void parseCsv() throws Exception {
        URL resource = classLoader.getResource("small.csv");
        assertNotNull(resource);
        File file = new File(resource.getFile());

        Stream<Csv> csvStream = parser.csvLines(file)
                .map(parser::parse)
                .flatMap(Optional::stream);

        assertEquals(71, csvStream.count());
    }

    @Test
    public void publish() throws Exception {
        MqttTopic testTopic = mqttSetup.getTopic();

        URL resource = classLoader.getResource("small.csv");
        assertNotNull(resource);
        File file = new File(resource.getFile());

        Stream<Csv> csvStream = parser.csvLines(file)
                .map(parser::parse)
                .flatMap(Optional::stream);

        csvStream.forEach(csv -> {
            try {
                byte[] payload = objectMapper.writeValueAsBytes(csv);
                testTopic.publish(new MqttMessage(payload));
            } catch (Exception e) {
                fail();
            }
        });

        await().atMost(10, SECONDS).until(collection::countDocuments, equalTo(71L));
    }

}
