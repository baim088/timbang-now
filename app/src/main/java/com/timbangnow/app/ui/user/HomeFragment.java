package com.timbangnow.app.ui.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.model.Timbangan;
import com.timbangnow.app.model.User;
import com.timbangnow.app.ui.admin.InputAnalisaActivity;
import com.timbangnow.app.viewmodel.AnalisaViewModel;
import com.timbangnow.app.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvHomeGreeting;
    private TextView tvBeratValue, tvBmiValue, tvTargetValue, tvAirProgress;
    private ProgressBar progressAir;
    private MaterialButton btnTambahAir;
    private LineChart lineChart;
    
    private LinearLayout layoutAnalisaSummary;
    private TextView tvAnalisaEmpty;
    private TextView tvAnalisaBmi, tvAnalisaFat, tvAnalisaOtot, tvAnalisaVfat;
    private MaterialButton btnLakukanAnalisa;

    private UserViewModel userViewModel;
    private AnalisaViewModel analisaViewModel;
    private User currentUserProfile = null;
    private boolean hasGreeted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHomeGreeting = view.findViewById(R.id.tv_home_greeting);
        tvBeratValue = view.findViewById(R.id.tv_berat_value);
        tvBmiValue = view.findViewById(R.id.tv_bmi_value);
        tvTargetValue = view.findViewById(R.id.tv_target_value);
        tvAirProgress = view.findViewById(R.id.tv_air_progress);
        progressAir = view.findViewById(R.id.progress_air);
        btnTambahAir = view.findViewById(R.id.btn_tambah_air);
        lineChart = view.findViewById(R.id.line_chart);

        layoutAnalisaSummary = view.findViewById(R.id.layout_analisa_summary);
        tvAnalisaEmpty = view.findViewById(R.id.tv_analisa_empty);
        tvAnalisaBmi = view.findViewById(R.id.tv_analisa_bmi);
        tvAnalisaFat = view.findViewById(R.id.tv_analisa_fat);
        tvAnalisaOtot = view.findViewById(R.id.tv_analisa_otot);
        tvAnalisaVfat = view.findViewById(R.id.tv_analisa_vfat);
        btnLakukanAnalisa = view.findViewById(R.id.btn_lakukan_analisa);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        analisaViewModel = new ViewModelProvider(requireActivity()).get(AnalisaViewModel.class);

        userViewModel.getLatestTimbangan().observe(getViewLifecycleOwner(), timbangan -> {
            if (timbangan != null) {
                tvBeratValue.setText(timbangan.getBeratBadan() + " kg");
                tvBmiValue.setText(String.format(Locale.getDefault(), "%.1f", timbangan.getBmi()));
            } else {
                tvBeratValue.setText(R.string.belum_ada_data);
                tvBmiValue.setText("-");
            }
        });

        userViewModel.getAirMinumHariIni().observe(getViewLifecycleOwner(), airMinum -> {
            if (airMinum != null) {
                tvAirProgress.setText(airMinum.getTotalAirMl() + " / 2000 ml");
                progressAir.setProgress(Math.min(airMinum.getTotalAirMl(), 2000));
            }
        });

        userViewModel.getTarget().observe(getViewLifecycleOwner(), target -> {
            if (target != null) {
                tvTargetValue.setText(target.getBeratTarget() + " kg");
            } else {
                tvTargetValue.setText("-");
            }
        });

        userViewModel.getTimbanganList().observe(getViewLifecycleOwner(), this::setupChart);

        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUserProfile = user;
                String greeting = getGreetingTime() + ", " + user.getNama() + "!";
                tvHomeGreeting.setText(greeting);
                if (!hasGreeted) {
                    AudioAssistant.getInstance(getContext()).speak(greeting + " Selamat datang kembali di TimbangNow.");
                    hasGreeted = true;
                }
            }
        });

        analisaViewModel.getLatestAnalisa().observe(getViewLifecycleOwner(), latest -> {
            if (latest != null) {
                tvAnalisaEmpty.setVisibility(View.GONE);
                layoutAnalisaSummary.setVisibility(View.VISIBLE);
                
                tvAnalisaBmi.setText(String.format(Locale.getDefault(), "%.1f", latest.getBmi()));
                tvAnalisaFat.setText(String.format(Locale.getDefault(), "%.1f%%", latest.getBodyFat()));
                tvAnalisaOtot.setText(String.valueOf(latest.getMassaOtot()));
                tvAnalisaVfat.setText(String.valueOf(latest.getLemakPerut()));
            } else {
                tvAnalisaEmpty.setVisibility(View.VISIBLE);
                layoutAnalisaSummary.setVisibility(View.GONE);
            }
        });

        btnTambahAir.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin mencatat penambahan 250 ml air minum?")
                    .setPositiveButton("Ya", (dialog, which) -> userViewModel.tambahAirMinum(250))
                    .setNegativeButton("Batal", null)
                    .show();
        });
        
        btnLakukanAnalisa.setOnClickListener(v -> {
            if (currentUserProfile != null) {
                Intent intent = new Intent(getContext(), InputAnalisaActivity.class);
                intent.putExtra("prefillUserId", currentUserProfile.getUid());
                intent.putExtra("prefillNama", currentUserProfile.getNama());
                intent.putExtra("prefillTelepon", currentUserProfile.getTelepon());
                intent.putExtra("prefillAlamat", currentUserProfile.getAlamat());
                intent.putExtra("prefillUsia", currentUserProfile.getUsia());
                intent.putExtra("prefillTinggi", currentUserProfile.getTinggiBadan());
                intent.putExtra("prefillJenisKelamin", currentUserProfile.getJenisKelamin());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        userViewModel.loadLatestTimbangan();
        userViewModel.loadAirMinumHariIni();
        userViewModel.loadTarget();
        userViewModel.loadTimbanganList();
        userViewModel.loadUserProfile();
        
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        if (!uid.isEmpty()) {
            analisaViewModel.loadLatestByUser(uid);
        }
    }

    private void setupChart(List<Timbangan> list) {
        if (list == null || list.isEmpty()) {
            lineChart.clear();
            return;
        }

        List<Timbangan> sortedList = new ArrayList<>(list);
        Collections.reverse(sortedList); // oldest to newest for chart

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < sortedList.size(); i++) {
            entries.add(new Entry(i, (float) sortedList.get(i).getBeratBadan()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Berat Badan (kg)");
        
        // ponytail: beautiful premium curve styling
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.parseColor("#1B5E20")); // Emerald Green
        dataSet.setCircleColor(Color.parseColor("#4CAF50")); // Green Point
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setDrawValues(false);
        
        // Draw elegant gradient fill below the curve
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#C8E6C9")); // Soft green transparent fill
        dataSet.setFillAlpha(100);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize Axes for premium clean design
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false); // Hide right axis
        
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // Hide X grid lines
        xAxis.setTextColor(Color.parseColor("#757575"));
        
        lineChart.getAxisLeft().setGridColor(Color.parseColor("#E0E0E0"));
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#757575"));
        lineChart.getLegend().setTextColor(Color.parseColor("#212121"));

        lineChart.animateY(1200); // Smooth premium load animation
        lineChart.invalidate();
    }

    private String getGreetingTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Selamat Pagi";
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            return "Selamat Siang";
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            return "Selamat Sore";
        } else {
            return "Selamat Malam";
        }
    }
}
