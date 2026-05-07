package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.Order;
import com.example.keyapp.Models.Schedule;
import com.example.keyapp.R;

import java.util.List;

public class ScheduleMenuAdapter extends RecyclerView.Adapter<ScheduleMenuAdapter.ScheduleMenuViewHolder>{

    private List<Order> scheduleList;
    Context ctx;
    private  int role;
    private ScheduleMenuAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Order schedule, int position);
    }

    public void setOnItemClickListener(ScheduleMenuAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public ScheduleMenuAdapter(List<Order> scheduleList,  Context ctx, int role) {
        this.scheduleList = scheduleList;
        this.ctx = ctx;
        this.role = role;
    }

    @NonNull
    @Override
    public ScheduleMenuAdapter.ScheduleMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent,false);
        return new ScheduleMenuAdapter.ScheduleMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleMenuAdapter.ScheduleMenuViewHolder holder, int position) {
        Order schedule = scheduleList.get(position);

        String displayName;
        if(role == 1){
            displayName = schedule.getBAName();
        } else {
            displayName = schedule.getUsername();
        }
        holder.s_serviceNameTV.setText(schedule.getServiceName());
        holder.s_date.setText(schedule.getSelectedDate());
        holder.s_time.setText(schedule.getSelectedTime());
        holder.s_userNameTV.setText(displayName);
        holder.s_locTV.setText(schedule.getLocation());

        holder.scheduleLayout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(schedule, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
    public void setData(List<Order> newList) {
        this.scheduleList.clear();
        this.scheduleList.addAll(newList);
        notifyDataSetChanged();
    }

    public class ScheduleMenuViewHolder extends RecyclerView.ViewHolder {
        TextView s_serviceNameTV, s_date, s_time, s_userNameTV, s_locTV;
        CardView scheduleLayout;
        public ScheduleMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            s_serviceNameTV = itemView.findViewById(R.id.s_serviceNameTV);
            s_date = itemView.findViewById(R.id.s_dateTV);
            s_time = itemView.findViewById(R.id.s_timeTV);
            s_userNameTV = itemView.findViewById(R.id.s_usernameTV);
            s_locTV = itemView.findViewById(R.id.s_locTV);
            scheduleLayout = itemView.findViewById(R.id.scheduleLayout);
        }
    }
}
