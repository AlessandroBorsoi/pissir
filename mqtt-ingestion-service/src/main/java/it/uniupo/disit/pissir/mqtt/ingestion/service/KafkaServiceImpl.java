package it.uniupo.disit.pissir.mqtt.ingestion.service;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import it.uniupo.disit.pissir.avro.OpenPflow;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaConfig;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaService;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.OpenPflowRaw;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaServiceImpl implements KafkaService, Runnable {
    private static final Logger logger = LogManager.getLogger(KafkaServiceImpl.class);

    private final KafkaConfig kafkaConfig;
    private final BlockingQueue<OpenPflowRaw> queue;
    private final CountDownLatch latch;
    private final KafkaProducer<Long, OpenPflow> kafkaProducer;

    public KafkaServiceImpl(KafkaConfig kafkaConfig, BlockingQueue<OpenPflowRaw> queue, CountDownLatch latch) {
        this.kafkaConfig = kafkaConfig;
        this.queue = queue;
        this.latch = latch;
        this.kafkaProducer = createKafkaProducer(kafkaConfig);
    }

    @Override
    public void send(OpenPflow openPflow) {

    }

    @Override
    public void run() {
        try {
            while (latch.getCount() > 1 || queue.size() > 0) {
                OpenPflowRaw openPflowRaw = queue.poll(200, TimeUnit.MILLISECONDS);
                if (openPflowRaw != null) {
                    OpenPflowConverter.from(openPflowRaw).ifPresentOrElse(
                            pflow -> kafkaProducer.send(new ProducerRecord<>(kafkaConfig.getTopic(), pflow)),
                            () -> logger.error("Invalid message")
                    );
                }
            }
        } catch (InterruptedException e) {
            logger.warn("Avro Producer interrupted");
        } finally {
            logger.info("Closing Producer");
            kafkaProducer.close();
            latch.countDown();
        }
    }

    public KafkaProducer<Long, OpenPflow> createKafkaProducer(KafkaConfig kafkaConfig) {
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
