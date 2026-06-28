package com.timbangnow.app.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.viewmodel.UserViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout tilNama, tilAlamat;
    private TextInputEditText etNama, etAlamat;
    private MaterialButton btnSimpan;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tilNama = findViewById(R.id.til_edit_nama);
        tilAlamat = findViewById(R.id.til_edit_alamat);
        etNama = findViewById(R.id.et_edit_nama);
        etAlamat = findViewById(R.id.et_edit_alamat);
        btnSimpan = findViewById(R.id.btn_simpan_profil);
        progressBar = findViewById(R.id.progress_bar);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                if (etNama.getText() == null || etNama.getText().toString().isEmpty()) {
                    etNama.setText(user.getNama());
                }
                if (etAlamat.getText() == null || etAlamat.getText().toString().isEmpty()) {
                    etAlamat.setText(user.getAlamat());
                }
            }
        });

        userViewModel.getOperationResult().observe(this, result -> {
            if ("PROFILE_UPDATED".equals(result)) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, R.string.profil_berhasil_diperbarui, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(EditProfileActivity.this).speak(getString(R.string.profil_berhasil_diperbarui));
                finish();
            } else if (result != null) {
                progressBar.setVisibility(View.GONE);
                btnSimpan.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });

        btnSimpan.setOnClickListener(v -> handleSaveProfile());
        userViewModel.loadUserProfile();
    }

    private void handleSaveProfile() {
        tilNama.setError(null);
        tilAlamat.setError(null);

        String nama = etNama.getText() != null ? etNama.getText().toString().trim() : "";
        String alamat = etAlamat.getText() != null ? etAlamat.getText().toString().trim() : "";

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

        progressBar.setVisibility(View.VISIBLE);
        btnSimpan.setEnabled(false);
        userViewModel.updateUserProfile(nama, alamat);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
