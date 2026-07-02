package com.timbangnow.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.timbangnow.app.ui.user.UserDashboardActivity;
import com.timbangnow.app.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNama, tilTelepon, tilAlamat, tilUsia, tilJenisKelamin, tilEmail, tilPassword;
    private TextInputEditText etNama, etTelepon, etAlamat, etUsia, etEmail, etPassword;
    private AutoCompleteTextView actJenisKelamin;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilNama = findViewById(R.id.til_nama);
        tilTelepon = findViewById(R.id.til_telepon);
        tilAlamat = findViewById(R.id.til_alamat);
        tilUsia = findViewById(R.id.til_usia);
        tilJenisKelamin = findViewById(R.id.til_jenis_kelamin);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etNama = findViewById(R.id.et_nama);
        etTelepon = findViewById(R.id.et_telepon);
        etAlamat = findViewById(R.id.et_alamat);
        etUsia = findViewById(R.id.et_usia);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        actJenisKelamin = findViewById(R.id.act_jenis_kelamin);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);

        // ponytail: simple dropdown for gender
        String[] genderOptions = {"Pria", "Wanita"};
        actJenisKelamin.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions));

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getRegisterResult().observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);

            if ("SUCCESS".equals(result)) {
                Toast.makeText(RegisterActivity.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, UserDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(this).speak(getString(R.string.error_register_gagal));
            }
        });

        btnRegister.setOnClickListener(v -> handleRegister());
        findViewById(R.id.tv_login).setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        tilNama.setError(null);
        tilTelepon.setError(null);
        tilAlamat.setError(null);
        tilUsia.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        String nama = getText(etNama);
        String telepon = getText(etTelepon);
        String alamat = getText(etAlamat);
        String usiaStr = getText(etUsia);
        String jenisKelamin = actJenisKelamin.getText().toString().trim();
        String email = getText(etEmail);
        String password = getText(etPassword);

        if (TextUtils.isEmpty(nama)) {
            setErrorWithTTS(tilNama, R.string.error_nama_kosong);
            return;
        }
        if (TextUtils.isEmpty(telepon)) {
            setErrorWithTTS(tilTelepon, R.string.error_telepon_kosong);
            return;
        }
        if (TextUtils.isEmpty(alamat)) {
            setErrorWithTTS(tilAlamat, R.string.error_alamat_kosong);
            return;
        }
        if (TextUtils.isEmpty(usiaStr)) {
            setErrorWithTTS(tilUsia, R.string.error_usia_kosong);
            return;
        }
        int usia;
        try {
            usia = Integer.parseInt(usiaStr);
        } catch (NumberFormatException e) {
            setErrorWithTTS(tilUsia, R.string.error_usia_kosong);
            return;
        }
        if (TextUtils.isEmpty(jenisKelamin)) {
            jenisKelamin = "Pria"; // ponytail: default to Pria
        }
        if (TextUtils.isEmpty(email)) {
            setErrorWithTTS(tilEmail, R.string.error_email_kosong);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorWithTTS(tilEmail, R.string.error_email_format);
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            setErrorWithTTS(tilPassword, R.string.error_password_pendek);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        authViewModel.register(email, password, nama, alamat, telepon, usia, jenisKelamin);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setErrorWithTTS(TextInputLayout til, int stringResId) {
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
