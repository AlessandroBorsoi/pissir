package it.uniupo.disit.pissir.mqtt.ingestion.service;

import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import it.uniupo.disit.pissir.avro.OpenPflow;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaConfig;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaServiceImpl;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.MqttConfig;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.MqttService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class MqttIngestionService {
    private static final Logger logger = LogManager.getLogger(MqttIngestionService.class);

    private final CountDownLatch latch;
    private final MqttService mqttService;

    public static void main(String[] args) {
        logger.info("Starting MQTT Ingestion Service");

        var config = ConfigFactory.load();
        var appConfig = new AppConfig(new MqttConfig(config), new KafkaConfig(config));
        var kafkaProducer = createKafkaProducer(appConfig.getKafkaConfig());
        var kafkaService = new KafkaServiceImpl(appConfig.getKafkaConfig(), kafkaProducer);
        var latch = new CountDownLatch(1);
        var mqttService = new MqttService(appConfig.getMqttConfig(), kafkaService, latch);

        try {
            MqttIngestionService mqttIngestionService = new MqttIngestionService(mqttService, latch);
            mqttIngestionService.run();
        } catch (Exception e) {
            logger.error("Cannot start the service", e);
        }
    }

    public MqttIngestionService(MqttService mqttService, CountDownLatch latch) {
        this.latch = latch;
        this.mqttService = mqttService;
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Shutdown requested...");
            latch.countDown();
        }));

        logger.info("Application started!");
        mqttService.run();
        try {
            logger.info("Latch await");
            latch.await();
            logger.info("Threads completed");
        } catch (InterruptedException e) {
            logger.error(e);
        } finally {
            logger.info("Application closed");
        }
    }

    private static KafkaProducer<Long, OpenPflow> createKafkaProducer(KafkaConfig kafkaConfig) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getUrl());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrl());
        return new KafkaProducer<>(properties);
    }

}
