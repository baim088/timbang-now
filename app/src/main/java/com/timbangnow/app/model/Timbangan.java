package com.timbangnow.app.model;

public class Timbangan {
    private String id;
    private long timestamp;
    private double beratBadan;
    private double bodyFat;
    private int visceralFat;
    private double bmi;

    public Timbangan() {}

    public Timbangan(String id, long timestamp, double beratBadan, double bodyFat, int visceralFat, double bmi) {
        this.id = id;
        this.timestamp = timestamp;
        this.beratBadan = beratBadan;
        this.bodyFat = bodyFat;
        this.visceralFat = visceralFat;
        this.bmi = bmi;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getBeratBadan() { return beratBadan; }
    public void setBeratBadan(double beratBadan) { this.beratBadan = beratBadan; }

    public double getBodyFat() { return bodyFat; }
    public void setBodyFat(double bodyFat) { this.bodyFat = bodyFat; }

    public int getVisceralFat() { return visceralFat; }
    public void setVisceralFat(int visceralFat) { this.visceralFat = visceralFat; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }
}
