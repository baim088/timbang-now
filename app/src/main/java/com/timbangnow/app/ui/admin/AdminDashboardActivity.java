package com.timbangnow.app.ui.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.ui.AboutFragment;
import com.timbangnow.app.ui.user.ProfilFragment;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_admin);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MemberListFragment())
                    .commit();
            AudioAssistant.getInstance(this).speak(getString(R.string.dashboard_admin));
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_member) {
                selectedFragment = new MemberListFragment();
            } else if (itemId == R.id.nav_timbangan) {
                selectedFragment = new TimbanganListFragment();
            } else if (itemId == R.id.nav_analisa) {
                selectedFragment = new AnalisaListFragment();
            } else if (itemId == R.id.nav_reservasi_admin) {
                selectedFragment = new ReservasiAdminFragment();
            } else if (itemId == R.id.nav_profil_admin) {
                selectedFragment = new ProfilFragment();
            } else if (itemId == R.id.nav_tentang_admin) {
                selectedFragment = new AboutFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}
