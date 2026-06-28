package com.timbangnow.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.timbangnow.app.R;
import com.timbangnow.app.repository.AuthRepository;
import com.timbangnow.app.ui.admin.AdminDashboardActivity;
import com.timbangnow.app.ui.user.UserDashboardActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthAndDispatch, 1500);
    }

    private void checkAuthAndDispatch() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            new AuthRepository().getUserRole(user.getUid(), new AuthRepository.RoleCallback() {
                @Override
                public void onResult(String role) {
                    if ("admin".equalsIgnoreCase(role)) {
                        startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, UserDashboardActivity.class));
                    }
                    finish();
                }

                @Override
                public void onError(String error) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
    }
}
