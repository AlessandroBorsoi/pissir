package it.uniupo.disit.pissir.mqtt.ingestion.service;

import com.typesafe.config.ConfigFactory;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaConfig;
import it.uniupo.disit.pissir.mqtt.ingestion.service.kafka.KafkaService;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.MqttConfig;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.MqttService;
import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.OpenPflowRaw;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

public class MqttIngestionService {
    private static final Logger logger = LogManager.getLogger(MqttIngestionService.class);

    private final ExecutorService executorService;
    private final CountDownLatch latch;
    private final MqttService mqttService;
    private final KafkaService kafkaService;

    public static void main(String[] args) {
        logger.info("Starting Service Producer");
        var config = ConfigFactory.load();
        var appConfig = new AppConfig(new MqttConfig(config), new KafkaConfig(config));
        var executorService = Executors.newFixedThreadPool(2);
        var queue = new LinkedBlockingDeque<OpenPflowRaw>();
        try {
            MqttIngestionService mqttIngestionService = new MqttIngestionService(appConfig, executorService, queue);
            mqttIngestionService.run();
        } catch (Exception e) {
            logger.error("Cannot start the service", e);
        }
    }

    public MqttIngestionService(AppConfig appConfig, ExecutorService executorService, BlockingQueue<OpenPflowRaw> queue) {
        this.executorService = executorService;
        this.latch = new CountDownLatch(2);
        this.mqttService = new MqttService(appConfig.getMqttConfig(), queue);
        this.kafkaService = new KafkaService(appConfig.getKafkaConfig(), queue, latch);
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!executorService.isShutdown()) {
                logger.info("Shutdown requested");
                shutdown();
            }
        }));

        logger.info("Application started!");
        executorService.submit(mqttService);
        executorService.submit(kafkaService);
        try {
            logger.info("Latch await");
            latch.await();
            logger.info("Threads completed");
        } catch (InterruptedException e) {
            logger.error(e);
        } finally {
            shutdown();
            logger.info("Application closed successfully");
        }
    }

    private void shutdown() {
        if (!executorService.isShutdown()) {
            logger.info("Shutting down");
            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                    logger.warn("Executor did not terminate in the specified time.");
                    List<Runnable> droppedTasks = executorService.shutdownNow();
                    logger.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed.");
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

}
