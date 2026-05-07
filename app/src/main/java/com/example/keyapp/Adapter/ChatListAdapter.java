package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Models.ChatListItem;
import com.example.keyapp.R;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    List<ChatListItem> chatList = new ArrayList<>();
    Context ctx;

    private ChatListAdapter.OnItemClickListener listener;
    public ChatListAdapter( List<ChatListItem> chatList, Context ctx) {
        this.chatList = chatList;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent,false);
        return new ChatListAdapter.ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
       ChatListItem item = chatList.get(position);

        String receiverName = item.getReceiverName();
        String lastMessage = item.getLastMessage();
        String receiverImage = item.getReceiverProfileImage();
        Timestamp timestamp = item.getTimestamp();
        String timeText = "";
        holder.icl_NameTV.setText(receiverName);
        holder.icl_MessageTV.setText(lastMessage);

        if (timestamp != null) {
            Date date = timestamp.toDate();
            timeText = android.text.format.DateFormat.format("HH:mm", date).toString();
            holder.icl_time.setText(timeText);
        }

        Glide.with(ctx)
                .load(receiverImage)
                .placeholder(R.drawable.profile_no)
                .into(holder.icl_profileIV);

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ChatListItem item);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView icl_NameTV, icl_MessageTV, icl_time;
        ImageView icl_profileIV;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            icl_NameTV = itemView.findViewById(R.id.icl_NameTV);
            icl_MessageTV = itemView.findViewById(R.id.icl_MessageTV);
            icl_time = itemView.findViewById(R.id.icl_time);
            icl_profileIV = itemView.findViewById(R.id.icl_imageIV);

        }
    }


}
