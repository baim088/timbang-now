package com.timbangnow.app.model;

public class User {
    private String uid;
    private String nama;
    private String alamat;
    private String telepon;
    private int usia;
    private String jenisKelamin;
    private double tinggiBadan;
    private String email;
    private long tanggalDaftar;
    private String role;

    public User() {}

    public User(String uid, String nama, String alamat, String telepon, int usia, String jenisKelamin, double tinggiBadan, String email, long tanggalDaftar, String role) {
        this.uid = uid;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
        this.usia = usia;
        this.jenisKelamin = jenisKelamin;
        this.tinggiBadan = tinggiBadan;
        this.email = email;
        this.tanggalDaftar = tanggalDaftar;
        this.role = role;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getTelepon() { return telepon; }
    public void setTelepon(String telepon) { this.telepon = telepon; }

    public int getUsia() { return usia; }
    public void setUsia(int usia) { this.usia = usia; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public double getTinggiBadan() { return tinggiBadan; }
    public void setTinggiBadan(double tinggiBadan) { this.tinggiBadan = tinggiBadan; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(long tanggalDaftar) { this.tanggalDaftar = tanggalDaftar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
