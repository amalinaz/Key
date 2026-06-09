package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.Notification;
import com.example.keyapp.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private Context ctx;

    public void setOnclickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    NotificationAdapter.OnItemClickListener listener;



    public NotificationAdapter(List<Notification> notificationList, Context ctx) {
        this.notificationList = notificationList;
        this.ctx = ctx;
    }



    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.bodyTextView.setText(notification.getMessage());
        holder.notif_layout.setOnClickListener(v -> {
            if(listener != null){
                listener.onClickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public interface OnItemClickListener {
        void onClickItem(int position);
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView bodyTextView;
        CardView notif_layout;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notif_titleTV);
            bodyTextView = itemView.findViewById(R.id.notif_descTV);
            notif_layout = itemView.findViewById(R.id.notif_layout);
        }
    }
}
