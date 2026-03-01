package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Models.Portofolio;
import com.example.keyapp.PortofolioFragment;
import com.example.keyapp.R;

import java.util.ArrayList;
import java.util.List;

public class SDPortofolioAdapter extends RecyclerView.Adapter<SDPortofolioAdapter.SDPortofolioViewHolder> {

    private List<String> imageList = new ArrayList<>();
    Context ctx;
    private SDPortofolioAdapter.OnItemCLickListener listener;

    public void setOnItemClickListener(SDPortofolioAdapter.OnItemCLickListener listener) {
        this.listener = listener;
    }

    public SDPortofolioAdapter(List<String> imageList, Context ctx) {
        this.imageList = imageList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public SDPortofolioAdapter.SDPortofolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sd_portofolio, parent, false);
        return new SDPortofolioAdapter.SDPortofolioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SDPortofolioAdapter.SDPortofolioViewHolder holder, int position) {
        if (imageList.size() > 0) {
            Glide.with(ctx).load(imageList.get(0)).into(holder.porto1IV);
        }

        if (imageList.size() > 1) {
            Glide.with(ctx).load(imageList.get(1)).into(holder.porto2IV);
        } else {
            holder.porto2IV.setVisibility(View.INVISIBLE);
        }

        holder.viewAllBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageList == null || imageList.isEmpty() ? 0 : 1;
    }

    public interface OnItemCLickListener {
        void onItemClick(int position);
    }

    public class SDPortofolioViewHolder extends RecyclerView.ViewHolder {
        ImageView porto1IV, porto2IV;
        Button viewAllBtn;
        public SDPortofolioViewHolder(@NonNull View itemView) {
            super(itemView);
            porto1IV = itemView.findViewById(R.id.item_sd_IV1);
            porto2IV = itemView.findViewById(R.id.item_sd_IV2);
            viewAllBtn = itemView.findViewById(R.id.item_sd_viewBtn);

        }
    }
}
