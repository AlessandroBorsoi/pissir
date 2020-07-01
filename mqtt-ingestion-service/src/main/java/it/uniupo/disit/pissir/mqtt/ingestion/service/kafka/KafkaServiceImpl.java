package it.uniupo.disit.pissir.mqtt.ingestion.service.kafka;

import it.uniupo.disit.pissir.avro.OpenPflow;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KafkaServiceImpl implements KafkaService {
    private static final Logger logger = LogManager.getLogger(KafkaServiceImpl.class);

    private final KafkaConfig kafkaConfig;
    private final KafkaProducer<Long, OpenPflow> kafkaProducer;

    public KafkaServiceImpl(KafkaConfig kafkaConfig, KafkaProducer<Long, OpenPflow> kafkaProducer) {
        this.kafkaConfig = kafkaConfig;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void send(OpenPflow openPflow) {
        kafkaProducer.send(new ProducerRecord<>(kafkaConfig.getTopic(), openPflow));
    }
}
