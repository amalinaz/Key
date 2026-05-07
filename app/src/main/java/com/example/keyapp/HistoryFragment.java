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

import com.example.keyapp.Adapter.OrderListAdapter;
import com.example.keyapp.Models.Order;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryFragment extends Fragment implements OrderListAdapter.OnItemClickListener{

    ImageButton history_backBtn;
    RecyclerView history_orderRV;
    OrderListAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private String uid, orderId;
    private int role;
    private List<Order> orderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        history_backBtn = rootView.findViewById(R.id.history_BackBtn);
        history_orderRV = rootView.findViewById(R.id.history_orderRV);
        history_orderRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderListAdapter(orderList,getContext());
        adapter.setOnItemClickListener(this);
        history_orderRV.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            uid = auth.getCurrentUser().getUid();
        }

        if(getArguments() != null){
            role = getArguments().getInt("role");
        }

        getOrderIdData(uid);

        history_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return rootView;
    }

    private void sortOrdersByDateDesc(List<Order> orders) {
        Collections.sort(orders, (o1, o2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date d1 = sdf.parse(o1.getSelectedDate() + " " + o1.getSelectedTime());
                Date d2 = sdf.parse(o2.getSelectedDate() + " " + o2.getSelectedTime());

                if (d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            } catch (Exception e) {
                return 0;
            }
        });
    }
    private void getOrderIdData(String uid) {
        db.collection("service_provider_orders")
                .document(uid)
                .collection("orders").whereEqualTo("status", "Completed")
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
                            sortOrdersByDateDesc(orderList);
                            adapter.notifyDataSetChanged();
                        }

                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting order", e);
                });
    }

    @Override
    public void onConfirmClick(int position) {

    }

    @Override
    public void onRejectClick(int position) {

    }

    @Override
    public void onViewDetailClick(int position) {
        Order order = orderList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("role", role);
        bundle.putSerializable("order", order);
        OrderListDetailFragment orderDetailFragment = new OrderListDetailFragment();
        orderDetailFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(orderDetailFragment, true);

    }
}