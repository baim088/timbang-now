package com.timbangnow.app.model;

public class AnalisaKebugaran {
    private String id;
    private long timestamp;
    private String nama;
    private String telepon;
    private String alamat;
    private int usia;
    private double tinggiBadan;
    private String jenisKelamin; // "Pria" or "Wanita"
    private String userId; // Nullable, filled if member
    private boolean isMember;
    private double beratBadan;
    private double bodyFat;
    private double kadarAir;
    private int massaOtot; // 1-9
    private int nilaiFisik; // 1-9
    private int kalori;
    private int usiaSel;
    private double massaTulang;
    private int lemakPerut; // 1-15
    private double bmi;

    public AnalisaKebugaran() {}

    public AnalisaKebugaran(String id, long timestamp, String nama, String telepon, String alamat, int usia, double tinggiBadan, String jenisKelamin, String userId, boolean isMember, double beratBadan, double bodyFat, double kadarAir, int massaOtot, int nilaiFisik, int kalori, int usiaSel, double massaTulang, int lemakPerut, double bmi) {
        this.id = id;
        this.timestamp = timestamp;
        this.nama = nama;
        this.telepon = telepon;
        this.alamat = alamat;
        this.usia = usia;
        this.tinggiBadan = tinggiBadan;
        this.jenisKelamin = jenisKelamin;
        this.userId = userId;
        this.isMember = isMember;
        this.beratBadan = beratBadan;
        this.bodyFat = bodyFat;
        this.kadarAir = kadarAir;
        this.massaOtot = massaOtot;
        this.nilaiFisik = nilaiFisik;
        this.kalori = kalori;
        this.usiaSel = usiaSel;
        this.massaTulang = massaTulang;
        this.lemakPerut = lemakPerut;
        this.bmi = bmi;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getTelepon() { return telepon; }
    public void setTelepon(String telepon) { this.telepon = telepon; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public int getUsia() { return usia; }
    public void setUsia(int usia) { this.usia = usia; }

    public double getTinggiBadan() { return tinggiBadan; }
    public void setTinggiBadan(double tinggiBadan) { this.tinggiBadan = tinggiBadan; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isMember() { return isMember; }
    public void setMember(boolean member) { isMember = member; }

    public double getBeratBadan() { return beratBadan; }
    public void setBeratBadan(double beratBadan) { this.beratBadan = beratBadan; }

    public double getBodyFat() { return bodyFat; }
    public void setBodyFat(double bodyFat) { this.bodyFat = bodyFat; }

    public double getKadarAir() { return kadarAir; }
    public void setKadarAir(double kadarAir) { this.kadarAir = kadarAir; }

    public int getMassaOtot() { return massaOtot; }
    public void setMassaOtot(int massaOtot) { this.massaOtot = massaOtot; }

    public int getNilaiFisik() { return nilaiFisik; }
    public void setNilaiFisik(int nilaiFisik) { this.nilaiFisik = nilaiFisik; }

    public int getKalori() { return kalori; }
    public void setKalori(int kalori) { this.kalori = kalori; }

    public int getUsiaSel() { return usiaSel; }
    public void setUsiaSel(int usiaSel) { this.usiaSel = usiaSel; }

    public double getMassaTulang() { return massaTulang; }
    public void setMassaTulang(double massaTulang) { this.massaTulang = massaTulang; }

    public int getLemakPerut() { return lemakPerut; }
    public void setLemakPerut(int lemakPerut) { this.lemakPerut = lemakPerut; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }
}
