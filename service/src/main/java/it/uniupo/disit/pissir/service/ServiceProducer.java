package it.uniupo.disit.pissir.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class ServiceProducer {
    private final KafkaConfig kafkaConfig;
    private final static String INPUT_PATH_NAME = "~/confluent-dev/labs/datasets/shakespeare";

    public ServiceProducer(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public void runProducer() throws IOException {
        KafkaProducer<String, String> producer = createProducer();
        File inputFile = new File(INPUT_PATH_NAME);
        if (inputFile.isDirectory()) {
            for (File fileInDirectory : requireNonNull(inputFile.listFiles())) {
                sendFile(fileInDirectory, producer);
            }
        } else {
            sendFile(inputFile, producer);
        }
    }

    private KafkaProducer<String, String> createProducer() {
        Properties settings = new Properties();
        settings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getServerConfig());
        settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaProducer<>(settings);
    }

    private void sendFile(File inputFile, KafkaProducer<String, String> producer) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String key = inputFile.getName().split("\\.")[0];
        String line;
        while ((line = reader.readLine()) != null) {
            ProducerRecord<String, String> record = new ProducerRecord<>("shakespeare_topic", key, line);
            producer.send(record);
        }
        reader.close();
        System.out.println("Finished producing file: " + inputFile.getName());
    }

}
