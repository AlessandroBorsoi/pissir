package it.uniupo.disit.pissir.it;

import java.util.Objects;

public class Csv {
    private long id;
    private String time;
    private Double longitude;
    private Double latitude;
    private int transport;
    private Double magnification;

    public Csv() {
    }

    public Csv(long id, String time, Double longitude, Double latitude, int transport, Double magnification) {
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
        Csv csv = (Csv) o;
        return id == csv.id &&
                transport == csv.transport &&
                Objects.equals(time, csv.time) &&
                Objects.equals(longitude, csv.longitude) &&
                Objects.equals(latitude, csv.latitude) &&
                Objects.equals(magnification, csv.magnification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, longitude, latitude, transport, magnification);
    }
}
