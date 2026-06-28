package com.timbangnow.app.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.timbangnow.app.R;
import com.timbangnow.app.model.Nutrisi;
import com.timbangnow.app.viewmodel.UserViewModel;

import java.util.List;

public class NutrisiFragment extends Fragment {

    private MaterialCheckBox cbPagiShake, cbPagiTeh, cbPagiAloe;
    private MaterialCheckBox cbSiangShake, cbSiangTeh, cbSiangAloe;
    private MaterialCheckBox cbMalamShake, cbMalamTeh, cbMalamAloe;

    private UserViewModel userViewModel;
    private boolean isUpdatingUi = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrisi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cbPagiShake = view.findViewById(R.id.cb_pagi_shake);
        cbPagiTeh = view.findViewById(R.id.cb_pagi_teh);
        cbPagiAloe = view.findViewById(R.id.cb_pagi_aloe);

        cbSiangShake = view.findViewById(R.id.cb_siang_shake);
        cbSiangTeh = view.findViewById(R.id.cb_siang_teh);
        cbSiangAloe = view.findViewById(R.id.cb_siang_aloe);

        cbMalamShake = view.findViewById(R.id.cb_malam_shake);
        cbMalamTeh = view.findViewById(R.id.cb_malam_teh);
        cbMalamAloe = view.findViewById(R.id.cb_malam_aloe);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getNutrisiHariIni().observe(getViewLifecycleOwner(), list -> {
            if (list == null) return;
            isUpdatingUi = true;
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
            isUpdatingUi = false;
        });

        setupListeners();
        userViewModel.loadNutrisiHariIni();
    }

    private void setupListeners() {
        View.OnClickListener listenerPagi = v -> saveNutrisiCategory("Pagi", cbPagiShake.isChecked(), cbPagiTeh.isChecked(), cbPagiAloe.isChecked());
        cbPagiShake.setOnClickListener(listenerPagi);
        cbPagiTeh.setOnClickListener(listenerPagi);
        cbPagiAloe.setOnClickListener(listenerPagi);

        View.OnClickListener listenerSiang = v -> saveNutrisiCategory("Siang", cbSiangShake.isChecked(), cbSiangTeh.isChecked(), cbSiangAloe.isChecked());
        cbSiangShake.setOnClickListener(listenerSiang);
        cbSiangTeh.setOnClickListener(listenerSiang);
        cbSiangAloe.setOnClickListener(listenerSiang);

        View.OnClickListener listenerMalam = v -> saveNutrisiCategory("Malam", cbMalamShake.isChecked(), cbMalamTeh.isChecked(), cbMalamAloe.isChecked());
        cbMalamShake.setOnClickListener(listenerMalam);
        cbMalamTeh.setOnClickListener(listenerMalam);
        cbMalamAloe.setOnClickListener(listenerMalam);
    }

    private void saveNutrisiCategory(String kategori, boolean shake, boolean teh, boolean aloe) {
        if (isUpdatingUi) return;
        Nutrisi n = new Nutrisi(null, System.currentTimeMillis(), kategori, shake, teh, aloe);
        userViewModel.saveNutrisi(n);
    }
}
