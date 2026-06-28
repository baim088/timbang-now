package com.timbangnow.app.ui.user;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.timbangnow.app.R;
import com.timbangnow.app.model.Timbangan;
import com.timbangnow.app.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvBeratValue, tvBmiValue, tvTargetValue, tvAirProgress;
    private ProgressBar progressAir;
    private MaterialButton btnTambahAir;
    private LineChart lineChart;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBeratValue = view.findViewById(R.id.tv_berat_value);
        tvBmiValue = view.findViewById(R.id.tv_bmi_value);
        tvTargetValue = view.findViewById(R.id.tv_target_value);
        tvAirProgress = view.findViewById(R.id.tv_air_progress);
        progressAir = view.findViewById(R.id.progress_air);
        btnTambahAir = view.findViewById(R.id.btn_tambah_air);
        lineChart = view.findViewById(R.id.line_chart);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getLatestTimbangan().observe(getViewLifecycleOwner(), timbangan -> {
            if (timbangan != null) {
                tvBeratValue.setText(timbangan.getBeratBadan() + " kg");
                tvBmiValue.setText(String.valueOf(timbangan.getBmi()));
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

        btnTambahAir.setOnClickListener(v -> userViewModel.tambahAirMinum(250));

        userViewModel.loadLatestTimbangan();
        userViewModel.loadAirMinumHariIni();
        userViewModel.loadTarget();
        userViewModel.loadTimbanganList();
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
        dataSet.setColor(Color.parseColor("#1B5E20"));
        dataSet.setCircleColor(Color.parseColor("#4CAF50"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }
}
