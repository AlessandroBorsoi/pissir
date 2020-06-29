package it.uniupo.disit.pissir.mqtt.proxy.service;

import it.uniupo.disit.pissir.avro.Location;
import it.uniupo.disit.pissir.avro.OpenPflow;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class OpenPflowConverter {
    public static final String LOCATION_TYPE = "Point";
    private static final String PATTERN = "yyyy/MM/dd HH:mm:ss";

    public static Optional<OpenPflow> from(OpenPflowRaw openPflowRaw) {
        try {
            Location location = new Location(LOCATION_TYPE, List.of(openPflowRaw.getLongitude(), openPflowRaw.getLatitude()));
            LocalDateTime localDateTime = LocalDateTime.parse(openPflowRaw.getTime(), DateTimeFormatter.ofPattern(PATTERN));
            Instant timestamp = localDateTime.atZone(ZoneId.of("Japan")).toInstant();
            return Optional.of(new OpenPflow(
                    openPflowRaw.getId(),
                    openPflowRaw.getTransport(),
                    openPflowRaw.getMagnification(),
                    location,
                    timestamp));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
