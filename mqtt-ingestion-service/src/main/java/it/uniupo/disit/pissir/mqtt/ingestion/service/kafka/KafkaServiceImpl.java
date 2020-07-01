package it.uniupo.disit.pissir.mqtt.ingestion.service.kafka;

import it.uniupo.disit.pissir.avro.OpenPflow;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaServiceImpl implements KafkaService {
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
