package com.timbangnow.app.ui.admin;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timbangnow.app.R;
import com.timbangnow.app.accessibility.AudioAssistant;
import com.timbangnow.app.adapter.MemberAdapter;
import com.timbangnow.app.model.User;
import com.timbangnow.app.viewmodel.AdminViewModel;
import com.timbangnow.app.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MemberListFragment extends Fragment {

    private RecyclerView rvMembers;
    private MemberAdapter adapter;
    private AdminViewModel adminViewModel;
    private UserViewModel userViewModel;
    private TextView tvGreeting;
    private EditText etSearch;

    private List<User> allMembers = new ArrayList<>();
    private boolean hasGreeted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMembers = view.findViewById(R.id.rv_members);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        etSearch = view.findViewById(R.id.et_search);

        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MemberAdapter(null, user -> showMemberDetailDialog(user));
        rvMembers.setAdapter(adapter);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        adminViewModel.getMemberList().observe(getViewLifecycleOwner(), members -> {
            if (members != null) {
                allMembers = members;
                filterMembers(etSearch.getText().toString());
            }
        });

        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String greeting = getGreetingTime() + ", Coach " + user.getNama() + "!";
                tvGreeting.setText(greeting);
                if (!hasGreeted) {
                    AudioAssistant.getInstance(getContext()).speak(greeting + " Selamat bertugas hari ini.");
                    hasGreeted = true;
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMembers(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        adminViewModel.loadAllMembers();
        userViewModel.loadUserProfile();
    }

    private void filterMembers(String query) {
        if (query.isEmpty()) {
            adapter.updateList(allMembers);
            return;
        }
        List<User> filtered = new ArrayList<>();
        for (User u : allMembers) {
            if ((u.getNama() != null && u.getNama().toLowerCase().contains(query.toLowerCase())) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(query.toLowerCase()))) {
                filtered.add(u);
            }
        }
        adapter.updateList(filtered);
    }

    private void showMemberDetailDialog(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Profil Member")
                .setMessage(String.format(Locale.getDefault(),
                        "Nama: %s\n" +
                        "Email: %s\n" +
                        "No. Telp: %s\n" +
                        "Alamat: %s\n" +
                        "Usia: %d tahun\n" +
                        "Tinggi: %.1f cm\n" +
                        "Jenis Kelamin: %s",
                        user.getNama(),
                        user.getEmail(),
                        user.getTelepon() != null && !user.getTelepon().isEmpty() ? user.getTelepon() : "-",
                        user.getAlamat() != null && !user.getAlamat().isEmpty() ? user.getAlamat() : "-",
                        user.getUsia(),
                        user.getTinggiBadan(),
                        user.getJenisKelamin() != null && !user.getJenisKelamin().isEmpty() ? user.getJenisKelamin() : "-"
                ))
                .setPositiveButton("Tutup", null)
                .show();
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
