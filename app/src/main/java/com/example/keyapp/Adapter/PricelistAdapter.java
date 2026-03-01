package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Models.Service;
import com.example.keyapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PricelistAdapter extends RecyclerView.Adapter<PricelistAdapter.PriceListViewHolder> {
    List<Service> pricelist = new ArrayList<>();
    Context ctx;
    private View.OnClickListener buttonClickListener;

    public PricelistAdapter(List<Service> pricelist, Context ctx, View.OnClickListener buttonClickListener) {
        this.pricelist =pricelist;
        this.ctx = ctx;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public PricelistAdapter.PriceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pricelist, parent, false);
        return new PricelistAdapter.PriceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricelistAdapter.PriceListViewHolder holder, int position) {
        Service item = pricelist.get(position);
        String serviceId = item.getId();
        holder.pl_viewScheduleBtn.setTag(serviceId);

        Glide.with(ctx)
                .load(item.getImgUrl())
                .into(holder.pl_imageIV);

        holder.pl_nameTV.setText(item.getServiceName());
        NumberFormat formatter =
                NumberFormat.getInstance(new Locale("id", "ID"));
        holder.pl_priceTV.setText("Rp." + formatter.format(item.getServicePrice()));
        holder.pl_viewScheduleBtn.setOnClickListener(buttonClickListener);

    }

    @Override
    public int getItemCount() {
        return pricelist.size();
    }

    public interface OnItemClickListener {
        void viewSchedule();
    }

    public class PriceListViewHolder extends RecyclerView.ViewHolder {
        ImageView pl_imageIV;
        TextView pl_nameTV, pl_priceTV;
        Button pl_viewScheduleBtn;
        public PriceListViewHolder(@NonNull View itemView) {
            super(itemView);
            pl_imageIV = itemView.findViewById(R.id.pl_imageIV);
            pl_nameTV = itemView.findViewById(R.id.pl_nameTV);
            pl_priceTV = itemView.findViewById(R.id.pl_priceTV);
            pl_viewScheduleBtn = itemView.findViewById(R.id.pl_viewScheduleBtn);
        }
    }
}
