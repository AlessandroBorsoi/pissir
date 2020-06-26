package it.uniupo.disit.pissir.service.mqtt;

import java.util.Objects;

public class Pflow {
    private long id;
    private String time;
    private Double longitude;
    private Double latitude;
    private int transport;
    private Double magnification;

    public Pflow() {
    }

    public Pflow(long id, String time, Double longitude, Double latitude, int transport, Double magnification) {
        this.id = id;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.transport = transport;
        this.magnification = magnification;
    }

    public long getId() {
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
        Pflow pflow = (Pflow) o;
        return id == pflow.id &&
                transport == pflow.transport &&
                time.equals(pflow.time) &&
                longitude.equals(pflow.longitude) &&
                latitude.equals(pflow.latitude) &&
                magnification.equals(pflow.magnification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, longitude, latitude, transport, magnification);
    }
}
