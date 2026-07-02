package com.timbangnow.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timbangnow.app.R;
import com.timbangnow.app.adapter.MemberAdapter;
import com.timbangnow.app.model.User;
import com.timbangnow.app.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

public class TimbanganListFragment extends Fragment {

    private RecyclerView rvMembers;
    private MemberAdapter adapter;
    private AdminViewModel adminViewModel;
    private EditText etSearch;
    private List<User> allMembers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timbangan_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMembers = view.findViewById(R.id.rv_timbangan_members);
        etSearch = view.findViewById(R.id.et_search);

        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MemberAdapter(null, user -> {
            Intent intent = new Intent(getContext(), InputTimbanganActivity.class);
            intent.putExtra("userId", user.getUid());
            intent.putExtra("namaUser", user.getNama());
            intent.putExtra("tinggiBadan", user.getTinggiBadan());
            startActivity(intent);
        });
        rvMembers.setAdapter(adapter);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        adminViewModel.getMemberList().observe(getViewLifecycleOwner(), members -> {
            if (members != null) {
                allMembers = members;
                filterMembers(etSearch.getText().toString());
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
}
