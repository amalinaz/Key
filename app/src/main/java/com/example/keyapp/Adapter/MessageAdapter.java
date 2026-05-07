package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.ChatMessage;
import com.example.keyapp.R;
import com.google.firebase.Timestamp;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;
    private List<ChatMessage> messageList;
    private Context context;
    private String currentUserId;

    public MessageAdapter(List<ChatMessage> messageList, Context context, String currentUserId) {
        this.messageList = messageList;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_container_send_messages, parent, false);
            return new SenderMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_container_received_message, parent, false);
            return new ReceiverMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        String timeText = "";
        Timestamp timestamp = message.getTimestamp();

        if (timestamp != null) {
            timeText = DateFormat.format("HH:mm", timestamp.toDate()).toString();
        }

        if (holder instanceof SenderMessageViewHolder) {
            ((SenderMessageViewHolder) holder).messageTV.setText(message.getMessage());
            ((SenderMessageViewHolder) holder).timeTV.setText(timeText);
            if(message.isRead()){
                ((SenderMessageViewHolder) holder).readTV.setText("Read");
            }else{
                ((SenderMessageViewHolder) holder).readTV.setText("Read");
            }
        } else if (holder instanceof ReceiverMessageViewHolder) {
            ((ReceiverMessageViewHolder) holder).messageTV.setText(message.getMessage());
            ((ReceiverMessageViewHolder) holder).timeTV.setText(timeText);
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class SenderMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV, timeTV, readTV;

        public SenderMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.csMessageTV);
            timeTV = itemView.findViewById(R.id.csDateTimeTV);
            readTV = itemView.findViewById(R.id.csReadTV);
        }
    }

    public static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV, timeTV;

        public ReceiverMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.crMessageTV);
            timeTV = itemView.findViewById(R.id.crDateTimeTV);
        }
    }
}
