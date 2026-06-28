package com.timbangnow.app.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.timbangnow.app.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        MaterialButton btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(v -> finish());
    }
}
