package it.uniupo.disit.pissir.mqtt.proxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import it.uniupo.disit.pissir.avro.OpenPflow;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class MqttProxyService {
    private final Logger logger = LoggerFactory.getLogger(MqttProxyService.class.getSimpleName());
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    public static void main(String[] args) {
        MqttProxyService mqttProxyService = new MqttProxyService();
        mqttProxyService.start();
    }

    public MqttProxyService() {
        this.appConfig = new AppConfig(ConfigFactory.load());
        this.objectMapper = new ObjectMapper();
    }

    private void start() {
        Properties properties = kafkaProperties();
        KafkaStreams streams = createKafkaStream(properties);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private Properties kafkaProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appConfig.getApplicationId());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.AT_LEAST_ONCE);
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, appConfig.getSchemaRegistryUrl());
        return properties;
    }

    private KafkaStreams createKafkaStream(Properties properties) {
        Serde<String> stringSerde = Serdes.String();
        SpecificAvroSerde<OpenPflow> openPflowSpecificAvroSerde = new SpecificAvroSerde<>();
        openPflowSpecificAvroSerde.configure(Map.of(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, appConfig.getSchemaRegistryUrl()), false);
        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream(appConfig.getSourceTopicName(), Consumed.with(stringSerde, stringSerde))
                .mapValues(parseMessage())
                .filter((k, v) -> Objects.nonNull(v))
                .mapValues(OpenPflowConverter::from)
                .filter((k, v) -> v.isPresent())
                .mapValues(Optional::get)
                .map((k, v) -> KeyValue.pair(null, v))
                .to(appConfig.getDestinationTopicName(), Produced.with(null, openPflowSpecificAvroSerde));
        return new KafkaStreams(builder.build(), properties);
    }

    private ValueMapper<String, OpenPflowRaw> parseMessage() {
        return value -> {
            try {
                return objectMapper.readValue(value, OpenPflowRaw.class);
            } catch (Exception e) {
                logger.error("Cannot parse message", e);
                return null;
            }
        };
    }
}
