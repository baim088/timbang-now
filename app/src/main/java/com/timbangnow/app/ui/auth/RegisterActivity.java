package com.timbangnow.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.timbangnow.app.ui.user.UserDashboardActivity;
import com.timbangnow.app.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNama, tilAlamat, tilEmail, tilPassword;
    private TextInputEditText etNama, etAlamat, etEmail, etPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView tvLogin;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilNama = findViewById(R.id.til_nama);
        tilAlamat = findViewById(R.id.til_alamat);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etNama = findViewById(R.id.et_nama);
        etAlamat = findViewById(R.id.et_alamat);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        tvLogin = findViewById(R.id.tv_login);

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
        tvLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        tilNama.setError(null);
        tilAlamat.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        String nama = etNama.getText() != null ? etNama.getText().toString().trim() : "";
        String alamat = etAlamat.getText() != null ? etAlamat.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(nama)) {
            tilNama.setError(getString(R.string.error_nama_kosong));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_nama_kosong));
            return;
        }

        if (TextUtils.isEmpty(alamat)) {
            tilAlamat.setError(getString(R.string.error_alamat_kosong));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_alamat_kosong));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_kosong));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_email_kosong));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_format));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_email_format));
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_pendek));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_password_pendek));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        authViewModel.register(email, password, nama, alamat);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
