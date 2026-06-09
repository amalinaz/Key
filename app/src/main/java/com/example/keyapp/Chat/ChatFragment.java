package com.example.keyapp.Chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyapp.Adapter.MessageAdapter;
import com.example.keyapp.Helper.NotificationHelper;
import com.example.keyapp.Models.ChatMessage;
import com.example.keyapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends Fragment {

   EditText chat_textInputET;
   ImageButton chat_BackBtn, chat_sendBtn;
   RecyclerView chat_chatMessageRV;
   TextView chat_NameTV;

   MessageAdapter adapter;
   FirebaseAuth auth;
   FirebaseFirestore db;

   private String uid, receiverName, receiverId, chatId;

   List<ChatMessage> chatMessageList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        chat_textInputET = rootView.findViewById(R.id.chat_textInputET);
        chat_BackBtn = rootView.findViewById(R.id.chat_BackBtn);
        chat_sendBtn = rootView.findViewById(R.id.chat_sendBtn);
        chat_chatMessageRV = rootView.findViewById(R.id.chat_chatMessageRV);
        chat_NameTV = rootView.findViewById(R.id.chat_NameTV);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User belum login", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        uid = auth.getCurrentUser().getUid();

        if (getArguments() != null) {
            receiverId = getArguments().getString("receiverId");
            receiverName = getArguments().getString("receiverName");
        }

        if (receiverId == null) {
            Toast.makeText(getContext(), "Receiver tidak ditemukan", Toast.LENGTH_SHORT).show();
            return rootView;
        }
        chatId = generateChatId(uid, receiverId);

        loadMessages();
        chat_chatMessageRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(chatMessageList, getContext(), uid);
        chat_chatMessageRV.setAdapter(adapter);
        chat_NameTV.setText(receiverName != null ? receiverName : "Chat");


        chat_sendBtn.setOnClickListener(v -> {
            String messageText = chat_textInputET.getText().toString().trim();
            Log.d("ChatDebug", "Message text: '" + messageText + "'");
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(uid, receiverId, messageText);
            }
        });

        chat_BackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });



        return rootView;
    }
    private String generateChatId(String uid1, String uid2) {
        if (uid1.compareTo(uid2) < 0) {
            return uid1 + "_" + uid2;
        } else {
            return uid2 + "_" + uid1;
        }
    }


    private void loadMessages() {
        db.collection("Chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }

                    chatMessageList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ChatMessage message = doc.toObject(ChatMessage.class);

                        if (message != null) {
                            chatMessageList.add(message);
                            if (message.getReceiverId().equals(uid)
                                    && !message.isRead()) {

                                doc.getReference().update("read", true);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (!chatMessageList.isEmpty()) {
                        chat_chatMessageRV.scrollToPosition(chatMessageList.size() - 1);
                    }
                });
    }


    private void sendMessage(String senderId, String receiverId, String messageText) {
        String chatId = senderId.compareTo(receiverId) < 0 ?
                senderId + "_" + receiverId : receiverId + "_" + senderId;

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", senderId);
        message.put("receiverId", receiverId);
        message.put("message", messageText);
        message.put("timestamp", FieldValue.serverTimestamp());
        message.put("read", false);

        db.collection("Chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(docRef -> {
                    Log.d("Chat", "Message sent to chatId: " + chatId);
                    chat_textInputET.setText("");

                    Map<String, Object> chatListSender = new HashMap<>();
                    chatListSender.put("otherUserId", receiverId);
                    chatListSender.put("lastMessage", messageText);
                    chatListSender.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("ChatLists")
                            .document(senderId)
                            .collection("chats")
                            .document(chatId)
                            .set(chatListSender, SetOptions.merge());

                    Map<String, Object> chatListReceiver = new HashMap<>();
                    chatListReceiver.put("otherUserId", senderId);
                    chatListReceiver.put("lastMessage", messageText);
                    chatListReceiver.put("timestamp", FieldValue.serverTimestamp());
                    chatListReceiver.put("unreadCount", FieldValue.increment(1));

                    db.collection("ChatLists")
                            .document(receiverId)
                            .collection("chats")
                            .document(chatId)
                            .set(chatListReceiver, SetOptions.merge());
                });
    }
}