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
import android.widget.Toast;

import com.example.keyapp.Adapter.OrderListAdapter;
import com.example.keyapp.Models.Order;
import com.example.keyapp.Models.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderListFragment extends Fragment {

    ImageButton ol_backBtn;
    RecyclerView ol_orderListRV;
    OrderListAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;
    private String uid;

    private List<Order> orderList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_order_list, container, false);
      ol_backBtn = rootView.findViewById(R.id.ol_backBtn);
      ol_orderListRV = rootView.findViewById(R.id.ol_orderListRV);
      ol_orderListRV.setLayoutManager(new LinearLayoutManager(requireContext()));
      adapter = new OrderListAdapter(orderList,getContext());
      ol_orderListRV.setAdapter(adapter);

      db = FirebaseFirestore.getInstance();
      auth = FirebaseAuth.getInstance();

      uid = auth.getCurrentUser().getUid();

      getOrderData();
      return rootView;
    }

    private void getOrderData() {
        db.collection("service_provider_orders")
                .document(uid)
                .collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Map<String, Object>> orders = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> orderData = document.getData();
                            if (orderData != null) {
                                String orderId = document.getId();
                                orderData.put("orderId", orderId);
                                orders.add(orderData);
                            }
                        }

//                        // Mengatur RecyclerView dengan data pesanan
//                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        OrderAdapter orderAdapter = new OrderAdapter(orders);  // Adapter untuk menampilkan data
//                        recyclerView.setAdapter(orderAdapter);
                    } else {
                        Log.d("Firestore", "No orders found for this BA");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting orders", e);
                });
    }

}