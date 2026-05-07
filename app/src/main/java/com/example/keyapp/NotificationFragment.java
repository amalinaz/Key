package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.keyapp.Adapter.NotificationAdapter;
import com.example.keyapp.Models.NotificationItem;
import com.example.keyapp.Schedule.ScheduleFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnItemClickListener{
    RecyclerView n_NotificationRV;
    ImageButton n_BackBtn;
    NotificationAdapter adapter;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private List<NotificationItem> notificationList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_notification, container, false);
        n_NotificationRV = rootview.findViewById(R.id.n_notificationRV);
        n_BackBtn = rootview.findViewById(R.id.n_BackBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        n_NotificationRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(notificationList,getContext());
        n_NotificationRV.setAdapter(adapter);
        adapter.setOnclickItemListener(this);

        n_BackBtn.setOnClickListener(v -> {
                getParentFragmentManager().popBackStack();

        });

        loadNotifications();
        return rootview;
    }

    private void loadNotifications() {
        if (auth.getCurrentUser() == null) {
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Notif", "uid skrg :"+currentUserId);
        db.collection("notifications")
                .whereEqualTo("receiverId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }
                     notificationList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String title = doc.getString("title");
                        String message = doc.getString("message");
                        String type = doc.getString("type");
                        String orderId = doc.getString("orderId");
                        Log.d("notif", "tipe" +type);
                        notificationList.add(new NotificationItem(title, message,type, orderId));
                        Log.d("Notif", "Notif yg ada "+title+" " +message);
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onClickItem(int position) {
        String type = notificationList.get(position).getType();
        String orderId = notificationList.get(position).getOrderId();
        Bundle bundle = new Bundle();
        Log.d("notif", "keklik kok");
        if(type.equals("new_order") ||type.equals("reschedule_request")){
            bundle.putInt("role", 2);
            OrderListFragment orderListFragment = new OrderListFragment();
            orderListFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(orderListFragment, true);
        }else if(type.equals("order_confirmed")|| type.equals("reschedule_rejected") || type.equals("reschedule_rejected")){
            bundle.putInt("role", 1);
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            scheduleFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(scheduleFragment, true);

        }
    }
}