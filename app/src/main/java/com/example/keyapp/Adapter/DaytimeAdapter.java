package com.example.keyapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.Daytime;
import com.example.keyapp.R;

import java.util.List;

public class DaytimeAdapter extends RecyclerView.Adapter<DaytimeAdapter.DaytimeAdapterHolder>{

    List<Daytime> daytimeList;

    public DaytimeAdapter(List<Daytime> daytimeList) {
        this.daytimeList = daytimeList;
    }


    @NonNull
    @Override
    public DaytimeAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_available_time, parent,false);
        return new DaytimeAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaytimeAdapterHolder holder, int position) {
        Daytime daytime = daytimeList.get(position);
        holder.dayTV.setText(daytime.getDay());
//        holder.timeTV.setText(daytime.getTime());
        StringBuilder timeStringBuilder = new StringBuilder();
        List<String> times = daytime.getTime();
        if (times != null && !times.isEmpty()) {
            for (String time : times) {
                if (timeStringBuilder.length() > 0) {
                    timeStringBuilder.append(" ");
                }
                timeStringBuilder.append(time);
            }
            holder.timeTV.setText(timeStringBuilder.toString());
        }
    }

    @Override
    public int getItemCount() {
        return daytimeList.size();
    }

    public class DaytimeAdapterHolder extends RecyclerView.ViewHolder {
        TextView dayTV,timeTV;
        LinearLayout timeContainer;
        public DaytimeAdapterHolder(@NonNull View itemView) {
            super(itemView);
            dayTV = itemView.findViewById(R.id.dayTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            timeContainer = itemView.findViewById(R.id.timeLLayout);
        }
    }
}
