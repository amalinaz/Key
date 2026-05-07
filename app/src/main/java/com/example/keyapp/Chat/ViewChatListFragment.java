package com.example.keyapp.Chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.keyapp.Adapter.ChatListAdapter;
import com.example.keyapp.MainActivity;
import com.example.keyapp.Models.ChatListItem;
import com.example.keyapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ViewChatListFragment extends Fragment implements ChatListAdapter.OnItemClickListener{

    RecyclerView cl_ChatListRV;
    ImageButton cl_BackBtn;
    ChatListAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private String uid;
    List<ChatListItem> chatListItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_view_chat_list, container, false);
       cl_ChatListRV = rootView.findViewById(R.id.cl_chatListRV);
       cl_BackBtn = rootView.findViewById(R.id.cl_BackBtn);

       db =FirebaseFirestore.getInstance();
       auth = FirebaseAuth.getInstance();

        uid = auth.getCurrentUser().getUid();
        fetchChatListData();

       cl_ChatListRV.setLayoutManager(new LinearLayoutManager(getContext()));
       adapter = new ChatListAdapter(chatListItems, getContext());
       adapter.setOnItemClickListener(this);
       cl_ChatListRV.setAdapter(adapter);

       return rootView;
    }
    private void fetchChatListData() {
        chatListItems.clear();
        db.collection("ChatLists")
                .document(uid)
                .collection("chats")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(chatListSnapshot -> {
                    List<ChatListItem> chatItems = new ArrayList<>();

                    for (DocumentSnapshot chatDoc : chatListSnapshot.getDocuments()) {
                        String receiverId = chatDoc.getString("otherUserId");
                        String lastMessage = chatDoc.getString("lastMessage");
                        Timestamp timestamp = chatDoc.getTimestamp("timestamp");

                        db.collection("users")
                                .document(receiverId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String receiverName = userDoc.getString("userName");
                                    String profileImage = userDoc.getString("profileImageUrl");

                                    ChatListItem item = new ChatListItem();
                                    item.setChatId(chatDoc.getId());
                                    item.setReceiverId(receiverId);
                                    item.setReceiverName(receiverName);
                                    item.setReceiverProfileImage(profileImage);
                                    item.setLastMessage(lastMessage);
                                    item.setTimestamp(timestamp);

                                    chatItems.add(item);
                                    chatListItems.add(item);
                                    adapter.notifyDataSetChanged();
                                });
                    }
                });
    }

    @Override
    public void onItemClick(ChatListItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("receiverId",item.getReceiverId() );
        bundle.putString("receiverName",item.getReceiverName());

        ChatFragment chat = new ChatFragment();
        chat.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(chat, true);
    }
}