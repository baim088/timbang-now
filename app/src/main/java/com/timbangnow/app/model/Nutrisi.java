package com.timbangnow.app.model;

public class Nutrisi {
    private String id;
    private long timestamp;
    private String kategoriWaktu;
    private boolean konsumsiShake;
    private boolean konsumsiTeh;
    private boolean konsumsiAloe;

    public Nutrisi() {}

    public Nutrisi(String id, long timestamp, String kategoriWaktu, boolean konsumsiShake, boolean konsumsiTeh, boolean konsumsiAloe) {
        this.id = id;
        this.timestamp = timestamp;
        this.kategoriWaktu = kategoriWaktu;
        this.konsumsiShake = konsumsiShake;
        this.konsumsiTeh = konsumsiTeh;
        this.konsumsiAloe = konsumsiAloe;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getKategoriWaktu() { return kategoriWaktu; }
    public void setKategoriWaktu(String kategoriWaktu) { this.kategoriWaktu = kategoriWaktu; }

    public boolean isKonsumsiShake() { return konsumsiShake; }
    public void setKonsumsiShake(boolean konsumsiShake) { this.konsumsiShake = konsumsiShake; }

    public boolean isKonsumsiTeh() { return konsumsiTeh; }
    public void setKonsumsiTeh(boolean konsumsiTeh) { this.konsumsiTeh = konsumsiTeh; }

    public boolean isKonsumsiAloe() { return konsumsiAloe; }
    public void setKonsumsiAloe(boolean konsumsiAloe) { this.konsumsiAloe = konsumsiAloe; }
}
