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
import com.google.firebase.auth.FirebaseAuth;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.repository.AuthRepository;
import com.timbangnow.app.ui.admin.AdminDashboardActivity;
import com.timbangnow.app.ui.user.UserDashboardActivity;
import com.timbangnow.app.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegister;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvRegister = findViewById(R.id.tv_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getLoginResult().observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);

            if ("SUCCESS".equals(result)) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                new AuthRepository().getUserRole(uid, new AuthRepository.RoleCallback() {
                    @Override
                    public void onResult(String role) {
                        if ("admin".equalsIgnoreCase(role)) {
                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                        finish();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(this).speak(getString(R.string.error_login_gagal));
            }
        });

        btnLogin.setOnClickListener(v -> handleLogin());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void handleLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

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
        btnLogin.setEnabled(false);
        authViewModel.login(email, password);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioAssistant.getInstance(this).stop();
    }
}
