package com.timbangnow.app.model;

public class User {
    private String uid;
    private String nama;
    private double tinggiBadan;
    private String email;
    private long tanggalDaftar;
    private String role;

    public User() {}

    public User(String uid, String nama, double tinggiBadan, String email, long tanggalDaftar, String role) {
        this.uid = uid;
        this.nama = nama;
        this.tinggiBadan = tinggiBadan;
        this.email = email;
        this.tanggalDaftar = tanggalDaftar;
        this.role = role;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getTinggiBadan() { return tinggiBadan; }
    public void setTinggiBadan(double tinggiBadan) { this.tinggiBadan = tinggiBadan; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(long tanggalDaftar) { this.tanggalDaftar = tanggalDaftar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
