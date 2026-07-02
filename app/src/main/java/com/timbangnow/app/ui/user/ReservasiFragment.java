package com.timbangnow.app.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.model.Reservasi;
import com.timbangnow.app.viewmodel.ReservasiViewModel;
import com.timbangnow.app.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReservasiFragment extends Fragment {

    private MaterialButton btnPilihTanggal, btnReservasi;
    private TextView tvTanggalTerpilih;
    private AutoCompleteTextView actPilihSesi, actPilihSlot;

    private ReservasiViewModel reservasiViewModel;
    private UserViewModel userViewModel;
    private long selectedDateMidnight = 0;
    private String userName = "";

    private final String[] sesiOptions = {"Sesi Pagi (06:00 - 11:00)", "Sesi Malam (18:30 - 21:00)"};
    private final String[] slotPagi = {"06:00 - 07:00", "07:00 - 08:00", "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00"};
    private final String[] slotMalam = {"18:30 - 19:00", "19:00 - 19:30", "19:30 - 20:00", "20:00 - 20:30", "20:30 - 21:00"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservasi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPilihTanggal = view.findViewById(R.id.btn_pilih_tanggal);
        btnReservasi = view.findViewById(R.id.btn_reservasi);
        tvTanggalTerpilih = view.findViewById(R.id.tv_tanggal_terpilih);
        actPilihSesi = view.findViewById(R.id.act_pilih_sesi);
        actPilihSlot = view.findViewById(R.id.act_pilih_slot);

        reservasiViewModel = new ViewModelProvider(this).get(ReservasiViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userName = user.getNama();
            }
        });
        userViewModel.loadUserProfile();

        // Default to today
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDateMidnight = cal.getTimeInMillis();
        updateTanggalText();

        // Setup dropdown for Sesi
        actPilihSesi.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, sesiOptions));
        actPilihSesi.setOnItemClickListener((parent, view1, position, id) -> {
            actPilihSlot.setText(""); // clear slot selection
            if (position == 0) {
                actPilihSlot.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, slotPagi));
            } else {
                actPilihSlot.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, slotMalam));
            }
        });

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        btnReservasi.setOnClickListener(v -> handleReservasiClick());

        reservasiViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if ("SUCCESS".equals(result)) {
                Toast.makeText(getContext(), R.string.reservasi_berhasil, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(requireContext()).speak(getString(R.string.reservasi_berhasil));
            } else if (result != null) {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                AudioAssistant.getInstance(requireContext()).speak(getString(R.string.reservasi_gagal));
            }
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Reservasi")
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
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void updateTanggalText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        tvTanggalTerpilih.setText("Tanggal: " + sdf.format(new Date(selectedDateMidnight)));
    }

    private void handleReservasiClick() {
        String slot = actPilihSlot.getText().toString().trim();
        if (TextUtils.isEmpty(slot)) {
            Toast.makeText(getContext(), R.string.pilih_slot, Toast.LENGTH_SHORT).show();
            AudioAssistant.getInstance(requireContext()).speak(getString(R.string.pilih_slot));
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        String dateStr = sdf.format(new Date(selectedDateMidnight));

        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Reservasi")
                .setMessage("Apakah Anda yakin ingin melakukan reservasi kehadiran untuk tanggal " + dateStr + " pada jam slot " + slot + "?")
                .setPositiveButton("Ya, Reservasi", (dialog, which) -> {
                    String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                            FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

                    Reservasi r = new Reservasi(null, userId, userName, selectedDateMidnight, slot, false);
                    reservasiViewModel.buatReservasi(r);
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
