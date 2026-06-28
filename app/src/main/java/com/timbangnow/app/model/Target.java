package com.timbangnow.app.model;

public class Target {
    private String id;
    private double beratAwal;
    private double beratTarget;
    private long tanggalMulai;

    public Target() {}

    public Target(String id, double beratAwal, double beratTarget, long tanggalMulai) {
        this.id = id;
        this.beratAwal = beratAwal;
        this.beratTarget = beratTarget;
        this.tanggalMulai = tanggalMulai;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getBeratAwal() { return beratAwal; }
    public void setBeratAwal(double beratAwal) { this.beratAwal = beratAwal; }

    public double getBeratTarget() { return beratTarget; }
    public void setBeratTarget(double beratTarget) { this.beratTarget = beratTarget; }

    public long getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(long tanggalMulai) { this.tanggalMulai = tanggalMulai; }
}
