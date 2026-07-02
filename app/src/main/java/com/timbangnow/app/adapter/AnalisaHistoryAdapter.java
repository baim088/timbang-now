package com.timbangnow.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.timbangnow.app.R;
import com.timbangnow.app.model.AnalisaKebugaran;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnalisaHistoryAdapter extends RecyclerView.Adapter<AnalisaHistoryAdapter.ViewHolder> {

    private List<AnalisaKebugaran> list = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AnalisaKebugaran item);
    }

    public AnalisaHistoryAdapter(List<AnalisaKebugaran> list, OnItemClickListener listener) {
        if (list != null) {
            this.list = list;
        }
        this.listener = listener;
    }

    public void updateList(List<AnalisaKebugaran> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analisa_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalisaKebugaran item = list.get(position);
        holder.tvNama.setText(item.getNama());
        holder.tvTanggal.setText(dateFormat.format(new Date(item.getTimestamp())));
        holder.tvBmi.setText(String.format(Locale.getDefault(), "BMI: %.1f", item.getBmi()));
        
        if (item.isMember()) {
            holder.tvBadge.setText("Member");
            holder.cardBadge.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.emerald_surface));
            holder.tvBadge.setTextColor(holder.itemView.getContext().getColor(R.color.emerald_green));
        } else {
            holder.tvBadge.setText("Umum");
            holder.cardBadge.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.light_gray));
            holder.tvBadge.setTextColor(holder.itemView.getContext().getColor(R.color.medium_gray));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvTanggal, tvBmi, tvBadge;
        MaterialCardView cardBadge;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_analisa_nama);
            tvTanggal = itemView.findViewById(R.id.tv_analisa_tanggal);
            tvBmi = itemView.findViewById(R.id.tv_analisa_bmi);
            tvBadge = itemView.findViewById(R.id.tv_analisa_status_badge);
            cardBadge = itemView.findViewById(R.id.card_badge);
        }
    }
}
