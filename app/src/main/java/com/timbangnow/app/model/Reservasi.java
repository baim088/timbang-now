package com.timbangnow.app.model;

public class Reservasi {
    private String id;
    private String userId;
    private String namaUser;
    private long tanggalPilihan;
    private String slotWaktu;
    private boolean statusHadir;

    public Reservasi() {}

    public Reservasi(String id, String userId, String namaUser, long tanggalPilihan, String slotWaktu, boolean statusHadir) {
        this.id = id;
        this.userId = userId;
        this.namaUser = namaUser;
        this.tanggalPilihan = tanggalPilihan;
        this.slotWaktu = slotWaktu;
        this.statusHadir = statusHadir;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNamaUser() { return namaUser; }
    public void setNamaUser(String namaUser) { this.namaUser = namaUser; }

    public long getTanggalPilihan() { return tanggalPilihan; }
    public void setTanggalPilihan(long tanggalPilihan) { this.tanggalPilihan = tanggalPilihan; }

    public String getSlotWaktu() { return slotWaktu; }
    public void setSlotWaktu(String slotWaktu) { this.slotWaktu = slotWaktu; }

    public boolean isStatusHadir() { return statusHadir; }
    public void setStatusHadir(boolean statusHadir) { this.statusHadir = statusHadir; }
}
