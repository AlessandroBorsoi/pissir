package it.uniupo.disit.pissir.mqtt.ingestion.service.mqtt;

import java.util.Objects;

public class OpenPflowRaw {
    private int id;
    private String time;
    private Double longitude;
    private Double latitude;
    private int transport;
    private Double magnification;

    public OpenPflowRaw() {
    }

    public OpenPflowRaw(int id, String time, Double longitude, Double latitude, int transport, Double magnification) {
        this.id = id;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.transport = transport;
        this.magnification = magnification;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public int getTransport() {
        return transport;
    }

    public Double getMagnification() {
        return magnification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenPflowRaw openPflowRaw = (OpenPflowRaw) o;
        return id == openPflowRaw.id &&
                transport == openPflowRaw.transport &&
                time.equals(openPflowRaw.time) &&
                longitude.equals(openPflowRaw.longitude) &&
                latitude.equals(openPflowRaw.latitude) &&
                magnification.equals(openPflowRaw.magnification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, longitude, latitude, transport, magnification);
    }
}
