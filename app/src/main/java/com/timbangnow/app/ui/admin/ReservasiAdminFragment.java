package com.timbangnow.app.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.timbangnow.app.R;
import com.timbangnow.app.adapter.ReservasiAdapter;
import com.timbangnow.app.model.Reservasi;
import com.timbangnow.app.viewmodel.ReservasiViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReservasiAdminFragment extends Fragment {

    private MaterialButton btnPilihTanggal;
    private TextView tvTanggalTerpilih;
    private RecyclerView rvReservasi;
    private EditText etSearch;
    private ReservasiAdapter adapter;
    private List<Reservasi> allReservations = new ArrayList<>();

    private ReservasiViewModel reservasiViewModel;
    private long selectedDateMidnight = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservasi_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPilihTanggal = view.findViewById(R.id.btn_pilih_tanggal);
        tvTanggalTerpilih = view.findViewById(R.id.tv_tanggal_terpilih);
        rvReservasi = view.findViewById(R.id.rv_reservasi);
        etSearch = view.findViewById(R.id.et_search);

        rvReservasi.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReservasiAdapter(null, (r, hadir) -> {
            if (r.getId() != null) {
                reservasiViewModel.updateStatusHadir(r.getId(), hadir);
            }
        });
        rvReservasi.setAdapter(adapter);

        reservasiViewModel = new ViewModelProvider(this).get(ReservasiViewModel.class);
        reservasiViewModel.getReservasiList().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                allReservations = list;
                filterReservations(etSearch.getText().toString());
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDateMidnight = cal.getTimeInMillis();
        updateTanggalText();

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReservations(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        reservasiViewModel.loadByTanggal(selectedDateMidnight);
    }

    private void filterReservations(String query) {
        if (query.isEmpty()) {
            adapter.updateList(allReservations);
            return;
        }
        List<Reservasi> filtered = new ArrayList<>();
        for (Reservasi r : allReservations) {
            if (r.getNamaUser() != null && r.getNamaUser().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(r);
            }
        }
        adapter.updateList(filtered);
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
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
            reservasiViewModel.loadByTanggal(selectedDateMidnight);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER_ADMIN");
    }

    private void updateTanggalText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        tvTanggalTerpilih.setText("Tanggal: " + sdf.format(new Date(selectedDateMidnight)));
    }
}
