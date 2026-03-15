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

public class OrderListFragment extends Fragment{

    ImageButton ol_backBtn;
    RecyclerView ol_orderListRV;
    OrderListAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;
    private String uid;
    private String orderId;

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

        if(auth.getCurrentUser() != null){
            uid = auth.getCurrentUser().getUid();
            Log.d("FirestoreTest","UID: "+uid);
        }else{
            Log.d("FirestoreTest","User null");
        }

      getOrderIdData(uid);

        adapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onConfirmClick(int position) {
                Order order = orderList.get(position);
                order.setStatus("Confirmed");
                FirebaseFirestore.getInstance()
                        .collection("orders")
                        .document(order.getOrderId())
                        .update("status", "Confirmed")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Order Confirmed", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                            Log.d("Firestore", "Confirm");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed update", Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Confirm gagal");
                        });
            }

            @Override
            public void onRejectClick(int position) {
                Order order = orderList.get(position);
                order.setStatus("Rejected");
                FirebaseFirestore.getInstance()
                        .collection("orders")
                        .document(order.getOrderId())
                        .update("status", "Rejected")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Order Rejected", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed update", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onViewDetailClick(int position) {
                Order order = orderList.get(position);

//                Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
//                intent.putExtra("orderId", order.getOrderId());
//                startActivity(intent);
            }
        });


      return rootView;
    }

    private void getOrderIdData(String uid) {
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
                                orderId = document.getId();
                                orderData.put("orderId", orderId);
                                orders.add(orderData);
                                getOrderData(orderId);
                            }
                        }
                    } else {
                        Log.d("Firestore", "No orders found for this BA");
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting orders", e);
                });
    }

    private void getOrderData(String orderId){
        db.collection("orders").document(orderId).get().addOnSuccessListener(doc -> {

                    if(doc.exists()){
                        Order order = doc.toObject(Order.class);

                        if(order != null){
                            order.setOrderId(orderId);
                            orderList.add(order);
                            adapter.notifyDataSetChanged();
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting order", e);
                });
    }


}