package com.timbangnow.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbangnow.app.R;
import com.timbangnow.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    public interface OnMemberClickListener {
        void onMemberClick(User user);
    }

    private List<User> members = new ArrayList<>();
    private final OnMemberClickListener listener;

    public MemberAdapter(List<User> members, OnMemberClickListener listener) {
        if (members != null) {
            this.members = members;
        }
        this.listener = listener;
    }

    public void updateList(List<User> newList) {
        this.members = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = members.get(position);
        holder.tvNama.setText(user.getNama());
        holder.tvEmail.setText(user.getEmail());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMemberClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvEmail;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_member_nama);
            tvEmail = itemView.findViewById(R.id.tv_member_email);
        }
    }
}
