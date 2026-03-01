package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Models.Portofolio;
import com.example.keyapp.R;

import java.util.ArrayList;
import java.util.List;

public class PortofolioAdapter extends RecyclerView.Adapter<PortofolioAdapter.PortofolioViewHolder> {
    private List<Portofolio> imageList = new ArrayList<>();
    private Context context;
    private OnPhotoLongClickListener longClickListener;
    private int role;

    public PortofolioAdapter(List<Portofolio> imageList, Context context, OnPhotoLongClickListener longClickListener, int role) {
        this.imageList = imageList;
        this.context = context;
        this.longClickListener = longClickListener;
        this.role = role;
    }

    @NonNull
    @Override
    public PortofolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_portofolio, parent, false);
        return new PortofolioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PortofolioViewHolder holder, int position) {
        Portofolio item = imageList.get(position);

        Glide.with(context)
                .load(item.imageUrl)
                .into(holder.imgPhoto);

        if(role == 2){
            holder.itemView.setOnLongClickListener(v -> {
                longClickListener.onPhotoLongClick(item);
                return true;
            });
        } else {
            // Role A tidak bisa long click
            holder.itemView.setOnLongClickListener(null); // Hapus long click listener untuk Role A
        }


    }



    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface OnPhotoLongClickListener {
        void onPhotoLongClick(Portofolio item);
    }

    public class PortofolioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;

        public PortofolioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.item_portoIV);
        }
    }
}
