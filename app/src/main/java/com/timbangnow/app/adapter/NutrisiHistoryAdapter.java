package com.timbangnow.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbangnow.app.R;
import com.timbangnow.app.model.Nutrisi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NutrisiHistoryAdapter extends RecyclerView.Adapter<NutrisiHistoryAdapter.ViewHolder> {

    private List<Nutrisi> list = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));

    public NutrisiHistoryAdapter(List<Nutrisi> list) {
        if (list != null) {
            this.list = list;
        }
    }

    public void updateList(List<Nutrisi> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nutrisi_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nutrisi item = list.get(position);
        holder.tvTanggal.setText(dateFormat.format(new Date(item.getTimestamp())));
        holder.tvKategori.setText("Waktu: " + item.getKategoriWaktu());

        List<String> consumed = new ArrayList<>();
        if (item.isKonsumsiShake()) consumed.add("Shake");
        if (item.isKonsumsiTeh()) consumed.add("Teh");
        if (item.isKonsumsiAloe()) consumed.add("Aloe");

        if (consumed.isEmpty()) {
            holder.tvDetail.setText("Belum dikonsumsi");
            holder.tvDetail.setTextColor(holder.itemView.getContext().getColor(R.color.medium_gray));
        } else {
            holder.tvDetail.setText(String.join(", ", consumed));
            holder.tvDetail.setTextColor(holder.itemView.getContext().getColor(R.color.emerald_green));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvKategori, tvDetail;

        ViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_history_tanggal);
            tvKategori = itemView.findViewById(R.id.tv_history_kategori);
            tvDetail = itemView.findViewById(R.id.tv_history_detail);
        }
    }
}
