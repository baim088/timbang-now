package com.timbangnow.app.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private RadioGroup rgPagi, rgMalam;

    private ReservasiViewModel reservasiViewModel;
    private UserViewModel userViewModel;
    private long selectedDateMidnight = 0;
    private String userName = "";

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
        rgPagi = view.findViewById(R.id.rg_slot_pagi);
        rgMalam = view.findViewById(R.id.rg_slot_malam);

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

        btnPilihTanggal.setOnClickListener(v -> showDatePicker());

        // Ensure single radio selection between groups
        rgPagi.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) rgMalam.clearCheck();
        });
        rgMalam.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) rgPagi.clearCheck();
        });

        btnReservasi.setOnClickListener(v -> handleReservasi());

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

    private void handleReservasi() {
        int pagiId = rgPagi.getCheckedRadioButtonId();
        int malamId = rgMalam.getCheckedRadioButtonId();

        if (pagiId == -1 && malamId == -1) {
            Toast.makeText(getContext(), R.string.pilih_slot, Toast.LENGTH_SHORT).show();
            AudioAssistant.getInstance(requireContext()).speak(getString(R.string.pilih_slot));
            return;
        }

        String slot = "";
        if (pagiId != -1) {
            RadioButton rb = getView().findViewById(pagiId);
            if (rb != null) slot = rb.getText().toString();
        } else if (malamId != -1) {
            RadioButton rb = getView().findViewById(malamId);
            if (rb != null) slot = rb.getText().toString();
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        Reservasi r = new Reservasi(null, userId, userName, selectedDateMidnight, slot, false);
        reservasiViewModel.buatReservasi(r);
    }
}
