package com.timbangnow.app.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.model.Timbangan;
import com.timbangnow.app.repository.AuthRepository;
import com.timbangnow.app.viewmodel.AdminViewModel;

public class InputTimbanganActivity extends AppCompatActivity {

    private TextView tvMemberName, tvBmiResult;
    private TextInputLayout tilBerat, tilBodyFat, tilVisceral;
    private TextInputEditText etBerat, etBodyFat, etVisceral;
    private MaterialButton btnSimpan;
    private ProgressBar progressBar;

    private AdminViewModel adminViewModel;
    private String userId = "";
    private String namaUser = "";
    private double tinggiBadan = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_timbangan);

        userId = getIntent().getStringExtra("userId");
        namaUser = getIntent().getStringExtra("namaUser");
        tinggiBadan = getIntent().getDoubleExtra("tinggiBadan", 0.0);

        tvMemberName = findViewById(R.id.tv_member_name);
        tvBmiResult = findViewById(R.id.tv_bmi_result);
        tilBerat = findViewById(R.id.til_berat);
        tilBodyFat = findViewById(R.id.til_body_fat);
        tilVisceral = findViewById(R.id.til_visceral);
        etBerat = findViewById(R.id.et_berat);
        etBodyFat = findViewById(R.id.et_body_fat);
        etVisceral = findViewById(R.id.et_visceral);
        btnSimpan = findViewById(R.id.btn_simpan);
        progressBar = findViewById(R.id.progress_bar);

        tvMemberName.setText("Input Timbangan: " + (namaUser != null ? namaUser : ""));

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        etBerat.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateBmiPreview();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSimpan.setOnClickListener(v -> handleSave());
    }

    private void updateBmiPreview() {
        String beratStr = etBerat.getText() != null ? etBerat.getText().toString().trim() : "";
        if (!TextUtils.isEmpty(beratStr) && tinggiBadan > 0) {
            try {
                double berat = Double.parseDouble(beratStr);
                double tinggiM = tinggiBadan / 100.0;
                double bmi = berat / (tinggiM * tinggiM);
                bmi = Math.round(bmi * 10.0) / 10.0;
                tvBmiResult.setText("BMI: " + bmi);
            } catch (NumberFormatException e) {
                tvBmiResult.setText("BMI: -");
            }
        } else {
            tvBmiResult.setText("BMI: -");
        }
    }

    private void handleSave() {
        tilBerat.setError(null);
        tilBodyFat.setError(null);
        tilVisceral.setError(null);

        String beratStr = etBerat.getText() != null ? etBerat.getText().toString().trim() : "";
        String bodyFatStr = etBodyFat.getText() != null ? etBodyFat.getText().toString().trim() : "";
        String visceralStr = etVisceral.getText() != null ? etVisceral.getText().toString().trim() : "";

        if (TextUtils.isEmpty(beratStr)) {
            tilBerat.setError("Berat badan tidak boleh kosong");
            AudioAssistant.getInstance(this).speak("Berat badan tidak boleh kosong");
            return;
        }

        double berat = 0.0, bodyFat = 0.0;
        int visceral = 0;
        try {
            berat = Double.parseDouble(beratStr);
            if (!TextUtils.isEmpty(bodyFatStr)) bodyFat = Double.parseDouble(bodyFatStr);
            if (!TextUtils.isEmpty(visceralStr)) visceral = Integer.parseInt(visceralStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSimpan.setEnabled(false);

        Timbangan t = new Timbangan(null, System.currentTimeMillis(), berat, bodyFat, visceral, 0.0);
        final double finalBerat = berat;

        adminViewModel.inputTimbangan(userId, tinggiBadan, t, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InputTimbanganActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();

                // TTS audio confirmation as specified in agents.md section 4.3
                String konfirmasiMsg = "Data timbangan " + namaUser + " tersimpan. Berat " + finalBerat + " kilogram.";
                AudioAssistant.getInstance(InputTimbanganActivity.this).speak(konfirmasiMsg);

                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnSimpan.setEnabled(true);
                Toast.makeText(InputTimbanganActivity.this, error, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(InputTimbanganActivity.this).speak("Gagal menyimpan data");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
