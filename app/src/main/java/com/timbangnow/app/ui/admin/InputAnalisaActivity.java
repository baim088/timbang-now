package com.timbangnow.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.model.AnalisaKebugaran;
import com.timbangnow.app.model.User;
import com.timbangnow.app.viewmodel.AdminViewModel;
import com.timbangnow.app.viewmodel.AnalisaViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputAnalisaActivity extends AppCompatActivity {

    private SwitchMaterial switchMember;
    private TextInputLayout tilPilihMember, tilNama, tilTelepon, tilAlamat, tilUsia, tilTinggi;
    private AutoCompleteTextView actPilihMember;
    private TextInputEditText etNama, etTelepon, etAlamat, etUsia, etTinggi;
    private RadioGroup rgJenisKelamin;
    
    private TextInputEditText etBerat, etBodyFat, etKadarAir, etMassaOtot, etNilaiFisik, etKalori, etUsiaSel, etMassaTulang, etLemakPerut;
    private MaterialButton btnSimpan;
    private ProgressBar progressBar;

    private AdminViewModel adminViewModel;
    private AnalisaViewModel analisaViewModel;
    
    private List<User> memberList = new ArrayList<>();
    private Map<String, User> memberMap = new HashMap<>();
    private String selectedUserId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_analisa);

        // Bind Views
        switchMember = findViewById(R.id.switch_member);
        tilPilihMember = findViewById(R.id.til_pilih_member);
        actPilihMember = findViewById(R.id.act_pilih_member);
        
        tilNama = findViewById(R.id.til_nama);
        tilTelepon = findViewById(R.id.til_telepon);
        tilAlamat = findViewById(R.id.til_alamat);
        tilUsia = findViewById(R.id.til_usia);
        tilTinggi = findViewById(R.id.til_tinggi);
        
        etNama = findViewById(R.id.et_nama);
        etTelepon = findViewById(R.id.et_telepon);
        etAlamat = findViewById(R.id.et_alamat);
        etUsia = findViewById(R.id.et_usia);
        etTinggi = findViewById(R.id.et_tinggi);
        rgJenisKelamin = findViewById(R.id.rg_jenis_kelamin);

        etBerat = findViewById(R.id.et_berat_badan);
        etBodyFat = findViewById(R.id.et_body_fat);
        etKadarAir = findViewById(R.id.et_kadar_air);
        etMassaOtot = findViewById(R.id.et_massa_otot);
        etNilaiFisik = findViewById(R.id.et_nilai_fisik);
        etKalori = findViewById(R.id.et_kalori);
        etUsiaSel = findViewById(R.id.et_usia_sel);
        etMassaTulang = findViewById(R.id.et_massa_tulang);
        etLemakPerut = findViewById(R.id.et_lemak_perut);
        
        btnSimpan = findViewById(R.id.btn_simpan_analisa);
        progressBar = findViewById(R.id.progress_bar);

        // ViewModels
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        analisaViewModel = new ViewModelProvider(this).get(AnalisaViewModel.class);

        // Member selection logic
        switchMember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tilPilihMember.setVisibility(View.VISIBLE);
                // ponytail: load members if not loaded yet
                if (memberList.isEmpty()) {
                    adminViewModel.loadAllMembers();
                }
            } else {
                tilPilihMember.setVisibility(View.GONE);
                selectedUserId = null;
                clearIdentitas();
            }
        });

        adminViewModel.getMemberList().observe(this, list -> {
            if (list != null) {
                memberList = list;
                memberMap.clear();
                List<String> names = new ArrayList<>();
                for (User u : list) {
                    String displayName = u.getNama() + " (" + u.getEmail() + ")";
                    names.add(displayName);
                    memberMap.put(displayName, u);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
                actPilihMember.setAdapter(adapter);
            }
        });

        actPilihMember.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            User u = memberMap.get(selected);
            if (u != null) {
                selectedUserId = u.getUid();
                etNama.setText(u.getNama());
                etTelepon.setText(u.getTelepon());
                etAlamat.setText(u.getAlamat());
                etUsia.setText(String.valueOf(u.getUsia()));
                etTinggi.setText(String.valueOf(u.getTinggiBadan()));
                if ("Wanita".equalsIgnoreCase(u.getJenisKelamin())) {
                    rgJenisKelamin.check(R.id.rb_wanita);
                } else {
                    rgJenisKelamin.check(R.id.rb_pria);
                }
            }
        });

        analisaViewModel.getOperationResult().observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            btnSimpan.setEnabled(true);
            if ("SUCCESS".equals(result)) {
                Toast.makeText(this, R.string.analisa_berhasil, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(this).speak(getString(R.string.analisa_berhasil));
                finish();
            } else if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        });

        // Check if prefilled (from user self-analysis)
        checkPrefillData();

        btnSimpan.setOnClickListener(v -> handleSaveAnalisa());
    }

    private void clearIdentitas() {
        etNama.setText("");
        etTelepon.setText("");
        etAlamat.setText("");
        etUsia.setText("");
        etTinggi.setText("");
        rgJenisKelamin.check(R.id.rb_pria);
    }

    private void checkPrefillData() {
        Intent intent = getIntent();
        if (intent.hasExtra("prefillUserId")) {
            // Self-analysis trigger
            selectedUserId = intent.getStringExtra("prefillUserId");
            switchMember.setChecked(true);
            switchMember.setEnabled(false); // lock it
            tilPilihMember.setVisibility(View.GONE); // hide dropdown

            etNama.setText(intent.getStringExtra("prefillNama"));
            etTelepon.setText(intent.getStringExtra("prefillTelepon"));
            etAlamat.setText(intent.getStringExtra("prefillAlamat"));
            int usia = intent.getIntExtra("prefillUsia", 0);
            etUsia.setText(usia > 0 ? String.valueOf(usia) : "");
            double tinggi = intent.getDoubleExtra("prefillTinggi", 0.0);
            etTinggi.setText(tinggi > 0 ? String.valueOf(tinggi) : "");
            
            String jk = intent.getStringExtra("prefillJenisKelamin");
            if ("Wanita".equalsIgnoreCase(jk)) {
                rgJenisKelamin.check(R.id.rb_wanita);
            } else {
                rgJenisKelamin.check(R.id.rb_pria);
            }
        }
    }

    private void handleSaveAnalisa() {
        tilNama.setError(null);
        tilTelepon.setError(null);
        tilAlamat.setError(null);
        tilUsia.setError(null);
        tilTinggi.setError(null);

        String nama = getText(etNama);
        String telepon = getText(etTelepon);
        String alamat = getText(etAlamat);
        String usiaStr = getText(etUsia);
        String tinggiStr = getText(etTinggi);

        if (TextUtils.isEmpty(nama)) {
            setErrorTTS(tilNama, R.string.error_nama_kosong);
            return;
        }
        if (TextUtils.isEmpty(telepon)) {
            setErrorTTS(tilTelepon, R.string.error_telepon_kosong);
            return;
        }
        if (TextUtils.isEmpty(alamat)) {
            setErrorTTS(tilAlamat, R.string.error_alamat_kosong);
            return;
        }
        if (TextUtils.isEmpty(usiaStr)) {
            setErrorTTS(tilUsia, R.string.error_usia_kosong);
            return;
        }
        if (TextUtils.isEmpty(tinggiStr)) {
            setErrorTTS(tilTinggi, R.string.error_tinggi_kosong);
            return;
        }

        // Validate composition fields
        double berat = getDouble(etBerat);
        double bodyFat = getDouble(etBodyFat);
        double kadarAir = getDouble(etKadarAir);
        int massaOtot = getInt(etMassaOtot);
        int nilaiFisik = getInt(etNilaiFisik);
        int kalori = getInt(etKalori);
        int usiaSel = getInt(etUsiaSel);
        double massaTulang = getDouble(etMassaTulang);
        int lemakPerut = getInt(etLemakPerut);

        if (berat <= 0) {
            Toast.makeText(this, "Berat badan harus lebih dari 0", Toast.LENGTH_SHORT).show();
            return;
        }

        double tinggi = Double.parseDouble(tinggiStr);
        int usia = Integer.parseInt(usiaStr);
        String jk = rgJenisKelamin.getCheckedRadioButtonId() == R.id.rb_wanita ? "Wanita" : "Pria";

        // Calculate BMI
        double heightInMeters = tinggi / 100.0;
        double bmi = berat / (heightInMeters * heightInMeters);

        progressBar.setVisibility(View.VISIBLE);
        btnSimpan.setEnabled(false);

        AnalisaKebugaran data = new AnalisaKebugaran(
                null,
                System.currentTimeMillis(),
                nama,
                telepon,
                alamat,
                usia,
                tinggi,
                jk,
                selectedUserId,
                switchMember.isChecked(),
                berat,
                bodyFat,
                kadarAir,
                massaOtot,
                nilaiFisik,
                kalori,
                usiaSel,
                massaTulang,
                lemakPerut,
                bmi
        );

        analisaViewModel.simpanAnalisa(data);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private double getDouble(TextInputEditText et) {
        String s = getText(et);
        if (s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int getInt(TextInputEditText et) {
        String s = getText(et);
        if (s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setErrorTTS(TextInputLayout til, int stringResId) {
        String msg = getString(stringResId);
        til.setError(msg);
        AudioAssistant.getInstance(this).speak(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
