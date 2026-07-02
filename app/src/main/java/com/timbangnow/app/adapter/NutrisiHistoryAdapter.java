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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NutrisiHistoryAdapter extends RecyclerView.Adapter<NutrisiHistoryAdapter.ViewHolder> {

    private final List<DailyNutrisi> dailyList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));

    public static class DailyNutrisi {
        public long dateMidnight;
        public Nutrisi pagi;
        public Nutrisi siang;
        public Nutrisi malam;
    }

    public NutrisiHistoryAdapter(List<Nutrisi> list) {
        updateList(list);
    }

    private long getMidnight(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public void updateList(List<Nutrisi> newList) {
        dailyList.clear();
        if (newList != null) {
            Map<Long, DailyNutrisi> map = new LinkedHashMap<>();
            for (Nutrisi n : newList) {
                long midnight = getMidnight(n.getTimestamp());
                DailyNutrisi dn = map.get(midnight);
                if (dn == null) {
                    dn = new DailyNutrisi();
                    dn.dateMidnight = midnight;
                    map.put(midnight, dn);
                }
                if ("Pagi".equalsIgnoreCase(n.getKategoriWaktu())) {
                    dn.pagi = n;
                } else if ("Siang".equalsIgnoreCase(n.getKategoriWaktu())) {
                    dn.siang = n;
                } else if ("Malam".equalsIgnoreCase(n.getKategoriWaktu())) {
                    dn.malam = n;
                }
            }
            dailyList.addAll(map.values());
        }
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
        DailyNutrisi item = dailyList.get(position);
        holder.tvTanggal.setText(dateFormat.format(new Date(item.dateMidnight)));
        
        holder.tvPagiDetail.setText(formatDetail(item.pagi));
        holder.tvSiangDetail.setText(formatDetail(item.siang));
        holder.tvMalamDetail.setText(formatDetail(item.malam));
    }

    private String formatDetail(Nutrisi item) {
        if (item == null) return "-";
        List<String> list = new ArrayList<>();
        if (item.isKonsumsiShake()) list.add("Shake");
        if (item.isKonsumsiTeh()) list.add("Teh");
        if (item.isKonsumsiAloe()) list.add("Aloe");
        if (list.isEmpty()) return "Kosong";
        return String.join(", ", list);
    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvPagiDetail, tvSiangDetail, tvMalamDetail;

        ViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_history_tanggal);
            tvPagiDetail = itemView.findViewById(R.id.tv_pagi_detail);
            tvSiangDetail = itemView.findViewById(R.id.tv_siang_detail);
            tvMalamDetail = itemView.findViewById(R.id.tv_malam_detail);
        }
    }
}
