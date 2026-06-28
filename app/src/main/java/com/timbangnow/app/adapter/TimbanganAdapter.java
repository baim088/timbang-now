package com.timbangnow.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbangnow.app.R;
import com.timbangnow.app.model.Timbangan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimbanganAdapter extends RecyclerView.Adapter<TimbanganAdapter.ViewHolder> {

    private List<Timbangan> list = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public TimbanganAdapter(List<Timbangan> list) {
        if (list != null) {
            this.list = list;
        }
    }

    public void updateList(List<Timbangan> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timbangan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Timbangan item = list.get(position);
        holder.tvTanggal.setText(dateFormat.format(new Date(item.getTimestamp())));
        holder.tvBerat.setText(item.getBeratBadan() + " kg");
        holder.tvBmi.setText("BMI: " + item.getBmi());
        holder.tvBodyFat.setText("Body Fat: " + item.getBodyFat() + "%");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvBerat, tvBmi, tvBodyFat;

        ViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvBerat = itemView.findViewById(R.id.tv_berat);
            tvBmi = itemView.findViewById(R.id.tv_bmi);
            tvBodyFat = itemView.findViewById(R.id.tv_body_fat);
        }
    }
}
