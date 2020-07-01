package it.uniupo.disit.pissir.mqtt.ingestion.service;

import it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt.OpenPflowRaw;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class OpenPflowConverterTest {

    @Test
    public void fromCompleteObject() {
        var openPflowRaw = new OpenPflowRaw(1, "2020/06/27 15:10:30", 139.60923, 35.641745, 2, 2.0);
        var maybeOpenPflow = OpenPflowConverter.from(openPflowRaw);

        assertTrue(maybeOpenPflow.isPresent());
        var openPflow = maybeOpenPflow.get();
        assertEquals(1, openPflow.getId());
        assertEquals(2, openPflow.getTransport());
        assertEquals("Point", openPflow.getLocation().getType());
        assertEquals(139.60923, openPflow.getLocation().getCoordinates().get(0), 0.01);
        assertEquals(35.641745, openPflow.getLocation().getCoordinates().get(1), 0.01);
        assertEquals(Instant.ofEpochSecond(1593238230), openPflow.getTimestamp());
    }

    @Test
    public void fromWrongObject() {
        var openPflowRaw = new OpenPflowRaw(1, null, 139.60923, 35.641745, 2, 2.0);
        var maybeOpenPflow = OpenPflowConverter.from(openPflowRaw);

        assertFalse(maybeOpenPflow.isPresent());
    }
}