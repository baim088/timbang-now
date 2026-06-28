package com.timbangnow.app.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tilPasswordLama, tilPasswordBaru, tilKonfirmasiBaru;
    private TextInputEditText etPasswordLama, etPasswordBaru, etKonfirmasiBaru;
    private MaterialButton btnSimpan;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tilPasswordLama = findViewById(R.id.til_password_lama);
        tilPasswordBaru = findViewById(R.id.til_password_baru);
        tilKonfirmasiBaru = findViewById(R.id.til_konfirmasi_baru);
        etPasswordLama = findViewById(R.id.et_password_lama);
        etPasswordBaru = findViewById(R.id.et_password_baru);
        etKonfirmasiBaru = findViewById(R.id.et_konfirmasi_baru);
        btnSimpan = findViewById(R.id.btn_simpan_password);
        progressBar = findViewById(R.id.progress_bar);

        btnSimpan.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        tilPasswordLama.setError(null);
        tilPasswordBaru.setError(null);
        tilKonfirmasiBaru.setError(null);

        String passwordLama = etPasswordLama.getText() != null ? etPasswordLama.getText().toString().trim() : "";
        String passwordBaru = etPasswordBaru.getText() != null ? etPasswordBaru.getText().toString().trim() : "";
        String konfirmasiBaru = etKonfirmasiBaru.getText() != null ? etKonfirmasiBaru.getText().toString().trim() : "";

        if (TextUtils.isEmpty(passwordLama)) {
            tilPasswordLama.setError(getString(R.string.error_password_lama_kosong));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_password_lama_kosong));
            return;
        }

        if (TextUtils.isEmpty(passwordBaru) || passwordBaru.length() < 6) {
            tilPasswordBaru.setError(getString(R.string.error_password_baru_pendek));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_password_baru_pendek));
            return;
        }

        if (!passwordBaru.equals(konfirmasiBaru)) {
            tilKonfirmasiBaru.setError(getString(R.string.error_password_tidak_cocok));
            AudioAssistant.getInstance(this).speak(getString(R.string.error_password_tidak_cocok));
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Sesi tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSimpan.setEnabled(false);

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passwordLama);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(passwordBaru).addOnCompleteListener(updateTask -> {
                    progressBar.setVisibility(View.GONE);
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, R.string.ganti_password_berhasil, Toast.LENGTH_SHORT).show();
                        AudioAssistant.getInstance(ChangePasswordActivity.this).speak(getString(R.string.ganti_password_berhasil));
                        finish();
                    } else {
                        btnSimpan.setEnabled(true);
                        String err = updateTask.getException() != null ? updateTask.getException().getLocalizedMessage() : getString(R.string.ganti_password_gagal);
                        Toast.makeText(ChangePasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                        AudioAssistant.getInstance(ChangePasswordActivity.this).speak(getString(R.string.ganti_password_gagal));
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                btnSimpan.setEnabled(true);
                tilPasswordLama.setError(getString(R.string.ganti_password_gagal));
                AudioAssistant.getInstance(ChangePasswordActivity.this).speak(getString(R.string.ganti_password_gagal));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
