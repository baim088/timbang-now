package com.timbangnow.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.timbangnow.app.R;
import com.timbangnow.app.adapter.AnalisaHistoryAdapter;
import com.timbangnow.app.model.AnalisaKebugaran;
import com.timbangnow.app.viewmodel.AnalisaViewModel;

public class AnalisaListFragment extends Fragment {

    private RecyclerView rvAnalisaList;
    private MaterialButton btnBuatAnalisa;
    private AnalisaHistoryAdapter adapter;
    private AnalisaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analisa_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAnalisaList = view.findViewById(R.id.rv_analisa_list);
        btnBuatAnalisa = view.findViewById(R.id.btn_buat_analisa);

        rvAnalisaList.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new AnalisaHistoryAdapter(null, item -> {
            Intent intent = new Intent(getContext(), HasilAnalisaActivity.class);
            // Put all fields into Intent extras
            populateIntentWithAnalisaData(intent, item);
            startActivity(intent);
        });
        rvAnalisaList.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalisaViewModel.class);
        viewModel.getAnalisaList().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                adapter.updateList(list);
            }
        });

        btnBuatAnalisa.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InputAnalisaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadAll();
    }

    private void populateIntentWithAnalisaData(Intent intent, AnalisaKebugaran item) {
        intent.putExtra("nama", item.getNama());
        intent.putExtra("telepon", item.getTelepon());
        intent.putExtra("alamat", item.getAlamat());
        intent.putExtra("usia", item.getUsia());
        intent.putExtra("tinggiBadan", item.getTinggiBadan());
        intent.putExtra("jenisKelamin", item.getJenisKelamin());
        intent.putExtra("beratBadan", item.getBeratBadan());
        intent.putExtra("bodyFat", item.getBodyFat());
        intent.putExtra("kadarAir", item.getKadarAir());
        intent.putExtra("massaOtot", item.getMassaOtot());
        intent.putExtra("nilaiFisik", item.getNilaiFisik());
        intent.putExtra("kalori", item.getKalori());
        intent.putExtra("usiaSel", item.getUsiaSel());
        intent.putExtra("massaTulang", item.getMassaTulang());
        intent.putExtra("lemakPerut", item.getLemakPerut());
        intent.putExtra("bmi", item.getBmi());
        intent.putExtra("timestamp", item.getTimestamp());
        intent.putExtra("isMember", item.isMember());
    }
}
