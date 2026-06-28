package com.timbangnow.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.timbangnow.app.R;
import com.timbangnow.app.model.Reservasi;

import java.util.ArrayList;
import java.util.List;

public class ReservasiAdapter extends RecyclerView.Adapter<ReservasiAdapter.ViewHolder> {

    public interface OnHadirChangeListener {
        void onHadirChanged(Reservasi r, boolean hadir);
    }

    private List<Reservasi> list = new ArrayList<>();
    private final OnHadirChangeListener listener;

    public ReservasiAdapter(List<Reservasi> list, OnHadirChangeListener listener) {
        if (list != null) {
            this.list = list;
        }
        this.listener = listener;
    }

    public void updateList(List<Reservasi> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservasi, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservasi r = list.get(position);
        holder.tvNama.setText(r.getNamaUser());
        holder.tvSlot.setText(r.getSlotWaktu());

        holder.cbHadir.setOnCheckedChangeListener(null);
        holder.cbHadir.setChecked(r.isStatusHadir());

        holder.cbHadir.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onHadirChanged(r, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvSlot;
        MaterialCheckBox cbHadir;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_reservasi);
            tvSlot = itemView.findViewById(R.id.tv_slot_waktu);
            cbHadir = itemView.findViewById(R.id.cb_hadir);
        }
    }
}
