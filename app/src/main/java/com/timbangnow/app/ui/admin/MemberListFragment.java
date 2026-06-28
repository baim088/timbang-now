package com.timbangnow.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.timbangnow.app.R;
import com.timbangnow.app.adapter.MemberAdapter;
import com.timbangnow.app.ui.auth.ChangePasswordActivity;
import com.timbangnow.app.ui.auth.LoginActivity;
import com.timbangnow.app.viewmodel.AdminViewModel;

public class MemberListFragment extends Fragment {

    private RecyclerView rvMembers;
    private MemberAdapter adapter;
    private AdminViewModel adminViewModel;
    private ImageButton btnAdminKey, btnAdminLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMembers = view.findViewById(R.id.rv_members);
        btnAdminKey = view.findViewById(R.id.btn_admin_key);
        btnAdminLogout = view.findViewById(R.id.btn_admin_logout);

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
                adapter.updateList(members);
            }
        });

        btnAdminKey.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class)));
        btnAdminLogout.setOnClickListener(v -> showLogoutDialog());

        adminViewModel.loadAllMembers();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.konfirmasi_logout)
                .setPositiveButton(R.string.ya, (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                })
                .setNegativeButton(R.string.batal, null)
                .show();
    }
}
