package com.example.myquizzapplication.Repository;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myquizzapplication.R;
import com.example.myquizzapplication.models.MonHoc;

import java.util.List;

public class MonHocAdapter1 extends RecyclerView.Adapter<MonHocAdapter1.MonHocViewHolder> {

    private final List<MonHoc> list;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(MonHoc monHoc);
    }

    public MonHocAdapter1(Context context, List<MonHoc> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MonHocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monhoc, parent, false);
        return new MonHocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonHocViewHolder holder, int position) {
        MonHoc monHoc = list.get(position);
        holder.tvTenMonHoc.setText(monHoc.getTenMon());

        // Icon theo tên môn học (có thể thêm logic khác)
        if (monHoc.getTenMon().toLowerCase().contains("math")) {
//            holder.imgMonHoc.setImageResource(R.drawable.ic_math);
        } else if (monHoc.getTenMon().toLowerCase().contains("geo")) {
//            holder.imgMonHoc.setImageResource(R.drawable.ic_math);
        } else {
//            holder.imgMonHoc.setImageResource(R.drawable.ic_math);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(monHoc));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MonHocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMonHoc;
//        ImageView imgMonHoc;

        public MonHocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMonHoc = itemView.findViewById(R.id.tvTenMonHoc);

        }
    }
}
