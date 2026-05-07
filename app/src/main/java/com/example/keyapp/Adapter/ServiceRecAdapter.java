package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Models.BAprofile;
import com.example.keyapp.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiceRecAdapter extends RecyclerView.Adapter<ServiceRecAdapter.ServiceRecViewHolder> {

    List<BAprofile>BAlist;
    private Context ctx;
    private ServiceRecAdapter.OnItemCLickListener onItemClickListener;
    public ServiceRecAdapter(List<BAprofile> BAlist, Context ctx) {
        this.BAlist = BAlist;
        this.ctx = ctx;
    }

    public void setOnItemClickListener(ServiceRecAdapter.OnItemCLickListener listener) {
        this.onItemClickListener = listener;
    }



    @NonNull
    @Override
    public ServiceRecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_recommendation, parent, false);

        return new ServiceRecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRecAdapter.ServiceRecViewHolder holder, int position) {

        BAprofile item = BAlist.get(position);
        holder.name.setText(item.getBAname());
        holder.distance.setText(String.format("%.2f km" ,item.getDistance()));
        holder.rate.setText("⭐ "+item.getRating());
        NumberFormat formatter =
                NumberFormat.getInstance(new Locale("id", "ID"));
        holder.price.setText("Start from Rp."+ formatter.format(item.getMinPrice()));
        Glide.with(ctx)
                .load(item.getPhotoUrl())
                .placeholder(R.drawable.profile_no)
                .into(holder.profileIV);

        holder.servicerec_layout.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.getServiceDetail(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return BAlist.size();
    }

    public interface OnItemCLickListener{
        void getServiceDetail(int position);
    }

    public List<BAprofile> getList() {
        return this.BAlist;
    }
    public class ServiceRecViewHolder extends RecyclerView.ViewHolder{

        TextView name, distance, rate, price;
        ImageView profileIV;
        CardView servicerec_layout;

        public ServiceRecViewHolder(@NonNull View itemView) {
            super(itemView);
            servicerec_layout = itemView.findViewById(R.id.servicerec_layout);
            name = itemView.findViewById(R.id.sr_name_TV);
            distance = itemView.findViewById(R.id.sr_dist_TV);
            rate = itemView.findViewById(R.id.sr_rate_TV);
            price = itemView.findViewById(R.id.sr_range_TV);
            profileIV = itemView.findViewById(R.id.srec_IV);
        }
    }
}
