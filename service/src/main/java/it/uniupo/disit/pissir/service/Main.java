package it.uniupo.disit.pissir.service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Service Producer");
        Config config = ConfigFactory.load();
        KafkaConfig kafkaConfig = new KafkaConfig(config);
        try {
            ServiceProducer serviceProducer = new ServiceProducer(kafkaConfig);
            serviceProducer.runProducer();
        } catch (Exception e) {
            logger.error("Cannot start the producer", e);
        }
    }
}
