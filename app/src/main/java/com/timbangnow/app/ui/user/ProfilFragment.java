package com.timbangnow.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.timbangnow.app.R;
import com.timbangnow.app.adapter.TimbanganAdapter;
import com.timbangnow.app.model.Target;

import com.timbangnow.app.ui.auth.ChangePasswordActivity;
import com.timbangnow.app.ui.auth.LoginActivity;
import com.timbangnow.app.viewmodel.AuthViewModel;
import com.timbangnow.app.viewmodel.UserViewModel;

public class ProfilFragment extends Fragment {

    private TextView tvNama, tvEmail, tvAlamat, tvTinggi, tvHeaderRiwayat;
    private MaterialButton btnEditProfil, btnSetTarget, btnGantiPassword, btnLogout;
    private RecyclerView rvRiwayat;
    private TimbanganAdapter adapter;

    private UserViewModel userViewModel;
    private AuthViewModel authViewModel;
    private double currentBerat = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNama = view.findViewById(R.id.tv_profil_nama);
        tvEmail = view.findViewById(R.id.tv_profil_email);
        tvAlamat = view.findViewById(R.id.tv_profil_alamat);
        tvTinggi = view.findViewById(R.id.tv_profil_tinggi);
        tvHeaderRiwayat = view.findViewById(R.id.tv_header_riwayat);
        btnEditProfil = view.findViewById(R.id.btn_edit_profil);
        btnSetTarget = view.findViewById(R.id.btn_set_target);
        btnGantiPassword = view.findViewById(R.id.btn_ganti_password);
        btnLogout = view.findViewById(R.id.btn_logout);
        rvRiwayat = view.findViewById(R.id.rv_riwayat);

        rvRiwayat.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TimbanganAdapter(null);
        rvRiwayat.setAdapter(adapter);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvNama.setText(user.getNama());
                tvEmail.setText(user.getEmail());
                tvAlamat.setText("Alamat: " + (user.getAlamat() != null && !user.getAlamat().isEmpty() ? user.getAlamat() : "Belum diisi"));
                
                if ("admin".equalsIgnoreCase(user.getRole())) {
                    // ponytail: hide admin weight details and target
                    tvTinggi.setVisibility(View.GONE);
                    btnSetTarget.setVisibility(View.GONE);
                    tvHeaderRiwayat.setVisibility(View.GONE);
                    rvRiwayat.setVisibility(View.GONE);
                } else {
                    tvTinggi.setVisibility(View.VISIBLE);
                    tvTinggi.setText("Tinggi: " + (user.getTinggiBadan() > 0 ? user.getTinggiBadan() + " cm" : "Belum diisi oleh coach"));
                    btnSetTarget.setVisibility(View.VISIBLE);
                    tvHeaderRiwayat.setVisibility(View.VISIBLE);
                    rvRiwayat.setVisibility(View.VISIBLE);
                }
            }
        });

        userViewModel.getLatestTimbangan().observe(getViewLifecycleOwner(), t -> {
            if (t != null) {
                currentBerat = t.getBeratBadan();
            }
        });

        userViewModel.getTimbanganList().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                adapter.updateList(list);
            }
        });

        btnEditProfil.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
        btnSetTarget.setOnClickListener(v -> showSetTargetDialog());
        btnGantiPassword.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class)));
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        userViewModel.loadUserProfile();
        userViewModel.loadLatestTimbangan();
        userViewModel.loadTimbanganList();
    }

    private void showSetTargetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.set_target);

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint(R.string.berat_target_hint);
        builder.setView(input);

        builder.setPositiveButton(R.string.simpan, (dialog, which) -> {
            String targetStr = input.getText().toString().trim();
            if (!targetStr.isEmpty()) {
                try {
                    double targetVal = Double.parseDouble(targetStr);
                    Target t = new Target(null, currentBerat, targetVal, System.currentTimeMillis());
                    userViewModel.saveTarget(t);
                    Toast.makeText(getContext(), R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException ignored) {}
            }
        });
        builder.setNegativeButton(R.string.batal, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.konfirmasi_logout)
                .setPositiveButton(R.string.ya, (dialog, which) -> {
                    authViewModel.logout();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                })
                .setNegativeButton(R.string.batal, null)
                .show();
    }
}
