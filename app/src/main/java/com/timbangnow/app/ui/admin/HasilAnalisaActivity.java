package com.timbangnow.app.ui.admin;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HasilAnalisaActivity extends AppCompatActivity {

    private TextView tvNama, tvTanggal, tvDetailIdentitas;
    private TextView tvBmiVal, tvBmiStatus;
    private TextView tvBeratVal;
    private TextView tvBodyFatVal, tvBodyFatStatus;
    private TextView tvKadarAirVal, tvKadarAirStatus;
    private TextView tvMassaOtotVal, tvMassaOtotStatus;
    private TextView tvNilaiFisikVal;
    private TextView tvKaloriVal;
    private TextView tvUsiaSelVal, tvUsiaSelStatus;
    private TextView tvMassaTulangVal, tvMassaTulangStatus;
    private TextView tvLemakPerutVal, tvLemakPerutStatus;

    private View indBmi, indBodyFat, indKadarAir, indMassaOtot, indUsiaSel, indMassaTulang, indLemakPerut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_analisa);

        // Bind Views
        tvNama = findViewById(R.id.tv_hasil_nama);
        tvTanggal = findViewById(R.id.tv_hasil_tanggal);
        tvDetailIdentitas = findViewById(R.id.tv_hasil_detail_identitas);

        tvBmiVal = findViewById(R.id.tv_bmi_val);
        tvBmiStatus = findViewById(R.id.tv_bmi_status);
        tvBeratVal = findViewById(R.id.tv_berat_val);
        
        tvBodyFatVal = findViewById(R.id.tv_body_fat_val);
        tvBodyFatStatus = findViewById(R.id.tv_body_fat_status);
        
        tvKadarAirVal = findViewById(R.id.tv_kadar_air_val);
        tvKadarAirStatus = findViewById(R.id.tv_kadar_air_status);
        
        tvMassaOtotVal = findViewById(R.id.tv_massa_otot_val);
        tvMassaOtotStatus = findViewById(R.id.tv_massa_otot_status);
        
        tvNilaiFisikVal = findViewById(R.id.tv_nilai_fisik_val);
        tvKaloriVal = findViewById(R.id.tv_kalori_val);
        
        tvUsiaSelVal = findViewById(R.id.tv_usia_sel_val);
        tvUsiaSelStatus = findViewById(R.id.tv_usia_sel_status);
        
        tvMassaTulangVal = findViewById(R.id.tv_massa_tulang_val);
        tvMassaTulangStatus = findViewById(R.id.tv_massa_tulang_status);
        
        tvLemakPerutVal = findViewById(R.id.tv_lemak_perut_val);
        tvLemakPerutStatus = findViewById(R.id.tv_lemak_perut_status);

        indBmi = findViewById(R.id.indicator_bmi);
        indBodyFat = findViewById(R.id.indicator_body_fat);
        indKadarAir = findViewById(R.id.indicator_kadar_air);
        indMassaOtot = findViewById(R.id.indicator_massa_otot);
        indUsiaSel = findViewById(R.id.indicator_usia_sel);
        indMassaTulang = findViewById(R.id.indicator_massa_tulang);
        indLemakPerut = findViewById(R.id.indicator_lemak_perut);

        findViewById(R.id.btn_kembali_hasil).setOnClickListener(v -> finish());

        displayHasil();
    }

    private void displayHasil() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        String nama = extras.getString("nama", "");
        String telepon = extras.getString("telepon", "");
        String alamat = extras.getString("alamat", "");
        int usia = extras.getInt("usia", 0);
        double tinggi = extras.getDouble("tinggiBadan", 0.0);
        String jk = extras.getString("jenisKelamin", "Pria");
        long timestamp = extras.getLong("timestamp", System.currentTimeMillis());

        double berat = extras.getDouble("beratBadan", 0.0);
        double bodyFat = extras.getDouble("bodyFat", 0.0);
        double kadarAir = extras.getDouble("kadarAir", 0.0);
        int massaOtot = extras.getInt("massaOtot", 0);
        int nilaiFisik = extras.getInt("nilaiFisik", 0);
        int kalori = extras.getInt("kalori", 0);
        int usiaSel = extras.getInt("usiaSel", 0);
        double massaTulang = extras.getDouble("massaTulang", 0.0);
        int lemakPerut = extras.getInt("lemakPerut", 0);
        double bmi = extras.getDouble("bmi", 0.0);

        tvNama.setText(nama);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", new Locale("id", "ID"));
        tvTanggal.setText(sdf.format(new Date(timestamp)));

        String info = String.format(Locale.getDefault(), "%s, %d Tahun | Tinggi: %.1f cm\nTelp: %s\nAlamat: %s",
                jk, usia, tinggi, telepon, alamat);
        tvDetailIdentitas.setText(info);

        tvBeratVal.setText(String.format(Locale.getDefault(), "%.1f kg", berat));
        tvBmiVal.setText(String.format(Locale.getDefault(), "%.1f", bmi));
        tvBodyFatVal.setText(String.format(Locale.getDefault(), "%.1f %%", bodyFat));
        tvKadarAirVal.setText(String.format(Locale.getDefault(), "%.1f %%", kadarAir));
        tvMassaOtotVal.setText(String.valueOf(massaOtot));
        tvNilaiFisikVal.setText(String.valueOf(nilaiFisik));
        tvKaloriVal.setText(String.format(Locale.getDefault(), "%d kcal", kalori));
        tvUsiaSelVal.setText(String.format(Locale.getDefault(), "%d tahun", usiaSel));
        tvMassaTulangVal.setText(String.format(Locale.getDefault(), "%.2f kg", massaTulang));
        tvLemakPerutVal.setText(String.valueOf(lemakPerut));

        // Color definitions
        int green = Color.parseColor("#4CAF50");
        int yellow = Color.parseColor("#FFC107");
        int red = Color.parseColor("#D32F2F");

        // 1. BMI Interpretation
        String statusBmi;
        int colorBmi;
        if (bmi < 18.5) {
            statusBmi = getString(R.string.status_kurus);
            colorBmi = yellow;
        } else if (bmi <= 24.9) {
            statusBmi = getString(R.string.status_normal);
            colorBmi = green;
        } else if (bmi <= 29.9) {
            statusBmi = getString(R.string.status_gemuk);
            colorBmi = yellow;
        } else {
            statusBmi = getString(R.string.status_obesitas);
            colorBmi = red;
        }
        setIndicator(indBmi, tvBmiStatus, statusBmi, colorBmi);

        // 2. Body Fat Interpretation (based on gender & age)
        String statusFat;
        int colorFat;
        boolean isUnder30 = usia < 30;
        if ("Pria".equalsIgnoreCase(jk)) {
            double minHealthy = isUnder30 ? 14.0 : 17.0;
            double maxHealthy = isUnder30 ? 20.0 : 23.0;
            if (bodyFat < minHealthy) {
                statusFat = getString(R.string.status_kurang);
                colorFat = yellow;
            } else if (bodyFat <= maxHealthy) {
                statusFat = getString(R.string.status_sehat);
                colorFat = green;
            } else {
                statusFat = "Tinggi";
                colorFat = red;
            }
        } else { // Wanita
            double minHealthy = isUnder30 ? 17.0 : 20.0;
            double maxHealthy = isUnder30 ? 24.0 : 27.0;
            if (bodyFat < minHealthy) {
                statusFat = getString(R.string.status_kurang);
                colorFat = yellow;
            } else if (bodyFat <= maxHealthy) {
                statusFat = getString(R.string.status_sehat);
                colorFat = green;
            } else {
                statusFat = "Tinggi";
                colorFat = red;
            }
        }
        setIndicator(indBodyFat, tvBodyFatStatus, statusFat, colorFat);

        // 3. Kadar Air Interpretation
        String statusWater;
        int colorWater;
        if ("Pria".equalsIgnoreCase(jk)) {
            if (kadarAir < 60.0) {
                statusWater = getString(R.string.status_kurang);
                colorWater = yellow;
            } else if (kadarAir <= 65.0) {
                statusWater = getString(R.string.status_normal);
                colorWater = green;
            } else {
                statusWater = getString(R.string.status_berlebih);
                colorWater = yellow;
            }
        } else { // Wanita
            if (kadarAir < 50.0) {
                statusWater = getString(R.string.status_kurang);
                colorWater = yellow;
            } else if (kadarAir <= 55.0) {
                statusWater = getString(R.string.status_normal);
                colorWater = green;
            } else {
                statusWater = getString(R.string.status_berlebih);
                colorWater = yellow;
            }
        }
        setIndicator(indKadarAir, tvKadarAirStatus, statusWater, colorWater);

        // 4. Massa Otot Interpretation
        String statusOtot;
        int colorOtot;
        if (massaOtot >= 5 && massaOtot <= 6) {
            statusOtot = getString(R.string.status_standar);
            colorOtot = green;
        } else if (massaOtot > 6) {
            statusOtot = getString(R.string.status_berotot);
            colorOtot = green;
        } else if (massaOtot == 4) {
            statusOtot = getString(R.string.status_kurang);
            colorOtot = yellow;
        } else {
            statusOtot = "Rendah (Obesitas)";
            colorOtot = red;
        }
        setIndicator(indMassaOtot, tvMassaOtotStatus, statusOtot, colorOtot);

        // 5. Usia Sel Interpretation
        String statusUsiaSel;
        int colorUsiaSel;
        if (usiaSel < usia) {
            statusUsiaSel = getString(R.string.status_muda);
            colorUsiaSel = green;
        } else if (usiaSel == usia) {
            statusUsiaSel = getString(R.string.status_sesuai);
            colorUsiaSel = green;
        } else {
            statusUsiaSel = getString(R.string.status_tua);
            colorUsiaSel = yellow;
        }
        setIndicator(indUsiaSel, tvUsiaSelStatus, statusUsiaSel, colorUsiaSel);

        // 6. Massa Tulang Interpretation
        String statusTulang;
        int colorTulang;
        if ("Wanita".equalsIgnoreCase(jk)) {
            double req = (berat < 50.0) ? 1.95 : (berat <= 70.0) ? 2.40 : 2.95;
            if (massaTulang < req) {
                statusTulang = getString(R.string.status_kurang);
                colorTulang = red;
            } else {
                statusTulang = getString(R.string.status_normal);
                colorTulang = green;
            }
        } else { // Pria
            double req = (berat < 65.0) ? 2.66 : (berat <= 95.0) ? 3.29 : 3.69;
            if (massaTulang < req) {
                statusTulang = getString(R.string.status_kurang);
                colorTulang = red;
            } else {
                statusTulang = getString(R.string.status_normal);
                colorTulang = green;
            }
        }
        setIndicator(indMassaTulang, tvMassaTulangStatus, statusTulang, colorTulang);

        // 7. Lemak Perut Interpretation
        String statusPerut;
        int colorPerut;
        if (lemakPerut <= 4) {
            statusPerut = getString(R.string.status_sehat);
            colorPerut = green;
        } else if (lemakPerut <= 9) {
            statusPerut = getString(R.string.status_hati_hati);
            colorPerut = yellow;
        } else {
            statusPerut = getString(R.string.status_bahaya);
            colorPerut = red;
        }
        setIndicator(indLemakPerut, tvLemakPerutStatus, statusPerut, colorPerut);

        // TTS Speech output
        String speakText = String.format(Locale.getDefault(),
                "Hasil analisa kebugaran untuk %s. Indeks massa tubuh %.1f, kategori %s. Lemak perut %d, kategori %s.",
                nama, bmi, statusBmi, lemakPerut, statusPerut);
        AudioAssistant.getInstance(this).speak(speakText);
    }

    private void setIndicator(View indicator, TextView label, String status, int color) {
        label.setText(status);
        label.setTextColor(color);
        
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        indicator.setBackground(shape);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
