package com.timbangnow.app.model;

public class AirMinum {
    private String id;
    private long tanggalTimestamp;
    private int totalAirMl;

    public AirMinum() {}

    public AirMinum(String id, long tanggalTimestamp, int totalAirMl) {
        this.id = id;
        this.tanggalTimestamp = tanggalTimestamp;
        this.totalAirMl = totalAirMl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTanggalTimestamp() { return tanggalTimestamp; }
    public void setTanggalTimestamp(long tanggalTimestamp) { this.tanggalTimestamp = tanggalTimestamp; }

    public int getTotalAirMl() { return totalAirMl; }
    public void setTotalAirMl(int totalAirMl) { this.totalAirMl = totalAirMl; }
}
