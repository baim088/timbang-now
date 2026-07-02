package com.timbangnow.app.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.timbangnow.app.R;
import com.timbangnow.app.adapter.NutrisiHistoryAdapter;
import com.timbangnow.app.model.Nutrisi;
import com.timbangnow.app.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NutrisiFragment extends Fragment {

    private MaterialButton btnPilihTanggal, btnSimpanNutrisi;
    private TextView tvTanggalNutrisi;
    private MaterialCheckBox cbPagiShake, cbPagiTeh, cbPagiAloe;
    private MaterialCheckBox cbSiangShake, cbSiangTeh, cbSiangAloe;
    private MaterialCheckBox cbMalamShake, cbMalamTeh, cbMalamAloe;

    private RecyclerView rvRiwayatNutrisi;
    private NutrisiHistoryAdapter historyAdapter;

    private UserViewModel userViewModel;
    private long selectedDateMidnight = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrisi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPilihTanggal = view.findViewById(R.id.btn_pilih_tanggal_nutrisi);
        btnSimpanNutrisi = view.findViewById(R.id.btn_simpan_nutrisi);
        tvTanggalNutrisi = view.findViewById(R.id.tv_tanggal_nutrisi);

        cbPagiShake = view.findViewById(R.id.cb_pagi_shake);
        cbPagiTeh = view.findViewById(R.id.cb_pagi_teh);
        cbPagiAloe = view.findViewById(R.id.cb_pagi_aloe);

        cbSiangShake = view.findViewById(R.id.cb_siang_shake);
        cbSiangTeh = view.findViewById(R.id.cb_siang_teh);
        cbSiangAloe = view.findViewById(R.id.cb_siang_aloe);

        cbMalamShake = view.findViewById(R.id.cb_malam_shake);
        cbMalamTeh = view.findViewById(R.id.cb_malam_teh);
        cbMalamAloe = view.findViewById(R.id.cb_malam_aloe);

        rvRiwayatNutrisi = view.findViewById(R.id.rv_riwayat_nutrisi);
        rvRiwayatNutrisi.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new NutrisiHistoryAdapter(null);
        rvRiwayatNutrisi.setAdapter(historyAdapter);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Default to today
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDateMidnight = cal.getTimeInMillis();
        updateTanggalText();

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        
        btnSimpanNutrisi.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin menyimpan data nutrisi untuk tanggal ini?")
                    .setPositiveButton("Ya", (dialog, which) -> saveAllNutrisi())
                    .setNegativeButton("Batal", null)
                    .show();
        });

        userViewModel.getNutrisiHariIni().observe(getViewLifecycleOwner(), list -> {
            resetCheckboxes();
            if (list != null) {
                for (Nutrisi n : list) {
                    if ("Pagi".equalsIgnoreCase(n.getKategoriWaktu())) {
                        cbPagiShake.setChecked(n.isKonsumsiShake());
                        cbPagiTeh.setChecked(n.isKonsumsiTeh());
                        cbPagiAloe.setChecked(n.isKonsumsiAloe());
                    } else if ("Siang".equalsIgnoreCase(n.getKategoriWaktu())) {
                        cbSiangShake.setChecked(n.isKonsumsiShake());
                        cbSiangTeh.setChecked(n.isKonsumsiTeh());
                        cbSiangAloe.setChecked(n.isKonsumsiAloe());
                    } else if ("Malam".equalsIgnoreCase(n.getKategoriWaktu())) {
                        cbMalamShake.setChecked(n.isKonsumsiShake());
                        cbMalamTeh.setChecked(n.isKonsumsiTeh());
                        cbMalamAloe.setChecked(n.isKonsumsiAloe());
                    }
                }
            }
        });

        userViewModel.getRiwayatNutrisi().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                historyAdapter.updateList(list);
            }
        });

        userViewModel.loadNutrisiByTanggal(selectedDateMidnight);
        userViewModel.loadRiwayatNutrisi();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Nutrisi")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            TimeZone timeZoneUTC = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZoneUTC);
            cal.setTimeInMillis(selection);

            Calendar localCal = Calendar.getInstance();
            localCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            localCal.set(Calendar.MILLISECOND, 0);

            selectedDateMidnight = localCal.getTimeInMillis();
            updateTanggalText();
            userViewModel.loadNutrisiByTanggal(selectedDateMidnight);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER_NUTRISI");
    }

    private void updateTanggalText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        tvTanggalNutrisi.setText("Tanggal Pengisian: " + sdf.format(new Date(selectedDateMidnight)));
    }

    private void resetCheckboxes() {
        cbPagiShake.setChecked(false);
        cbPagiTeh.setChecked(false);
        cbPagiAloe.setChecked(false);
        cbSiangShake.setChecked(false);
        cbSiangTeh.setChecked(false);
        cbSiangAloe.setChecked(false);
        cbMalamShake.setChecked(false);
        cbMalamTeh.setChecked(false);
        cbMalamAloe.setChecked(false);
    }

    private void saveAllNutrisi() {
        long timestampToUse = selectedDateMidnight + (12 * 60 * 60 * 1000); // midday
        Nutrisi pagi = new Nutrisi(null, timestampToUse, "Pagi", cbPagiShake.isChecked(), cbPagiTeh.isChecked(), cbPagiAloe.isChecked());
        Nutrisi siang = new Nutrisi(null, timestampToUse, "Siang", cbSiangShake.isChecked(), cbSiangTeh.isChecked(), cbSiangAloe.isChecked());
        Nutrisi malam = new Nutrisi(null, timestampToUse, "Malam", cbMalamShake.isChecked(), cbMalamTeh.isChecked(), cbMalamAloe.isChecked());
        
        userViewModel.saveNutrisi(pagi, selectedDateMidnight);
        userViewModel.saveNutrisi(siang, selectedDateMidnight);
        userViewModel.saveNutrisi(malam, selectedDateMidnight);
        
        Toast.makeText(getContext(), "Data nutrisi berhasil disimpan", Toast.LENGTH_SHORT).show();
    }
}
