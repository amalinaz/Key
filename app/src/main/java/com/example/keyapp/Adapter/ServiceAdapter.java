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

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    Context ctx;
    private OnItemClickListener onItemClickListener;

    public ServiceAdapter(List<Service> serviceList, Context ctx) {
        this.serviceList = serviceList;
        this.ctx = ctx;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.viewservicecard, parent, false);
        return new ServiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service);

    }


    @Override
    public int getItemCount() {
        return  serviceList.size();
    }

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price;
        private ImageView serviceImage;
        private Button editbtn, deletebtn;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.TVServiceName);
            price = itemView.findViewById(R.id.TVServicePrice);
            serviceImage = itemView.findViewById(R.id.IVServicePhoto);
            editbtn = itemView.findViewById(R.id.BtnEdit);
            deletebtn = itemView.findViewById(R.id.BtnDelete);
        }

        public void bind(Service service) {
            name.setText(service.getServiceName());
            price.setText("Rp. " + service.getServicePrice());
            Glide.with(serviceImage.getContext())
                    .load(service.getImgUrl())
                    .into(serviceImage);

            editbtn.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onEditClick(getAdapterPosition());
                }
            });
            deletebtn.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
