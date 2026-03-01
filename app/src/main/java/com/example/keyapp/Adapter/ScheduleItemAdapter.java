package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.ScheduleItem;
import com.example.keyapp.R;

import java.util.List;

public class ScheduleItemAdapter extends RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder> {
    private List<ScheduleItem> scheduleItems;
    private Context ctx;
    private ScheduleItemAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(ScheduleItemAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public ScheduleItemAdapter(List<ScheduleItem> scheduleItems, Context ctx) {
        this.scheduleItems = scheduleItems;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public ScheduleItemAdapter.ScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick_schedule, parent, false);
        return new ScheduleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleItemAdapter.ScheduleItemViewHolder holder, int position) {
        ScheduleItem scheduleItem = scheduleItems.get(position);

        holder.ps_timeTV.setText(scheduleItem.getTime());
        holder.ps_estTimeTV.setText("|   " + (String.valueOf(scheduleItem.getEstimatedTime())) + " min");
        holder.ps_nameTV.setText(scheduleItem.getProviderName());
        holder.ps_bookBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ScheduleItemViewHolder extends RecyclerView.ViewHolder {

        TextView ps_timeTV, ps_estTimeTV , ps_nameTV;
        Button ps_bookBtn;
        public ScheduleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ps_timeTV = itemView.findViewById(R.id.ps_timeTV);
            ps_estTimeTV = itemView.findViewById(R.id.ps_estTimeTV);
            ps_nameTV = itemView.findViewById(R.id.ps_nameTV);
            ps_bookBtn = itemView.findViewById(R.id.ps_bookBtn);
        }
    }
}
