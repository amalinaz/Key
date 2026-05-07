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
import com.example.keyapp.Helper.NotificationHelper;
import com.example.keyapp.Models.Order;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderListFragment extends Fragment{

    ImageButton ol_backBtn;
    RecyclerView ol_orderListRV;
    OrderListAdapter adapter;
    Chip ol_allChip, ol_pendingChip, ol_confirmedChip, ol_rejectedChip;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private String uid;
    private String orderId;
    private int role;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredOrderList = new ArrayList<>();
    private String currentFilter = "All";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_order_list, container, false);
      ol_backBtn = rootView.findViewById(R.id.oldetail_backBtn);
      ol_orderListRV = rootView.findViewById(R.id.ol_orderListRV);
      ol_allChip = rootView.findViewById(R.id.ol_allChip);
      ol_pendingChip = rootView.findViewById(R.id.ol_pendingChip);
      ol_confirmedChip = rootView.findViewById(R.id.ol_confirmChip);
      ol_rejectedChip = rootView.findViewById(R.id.ol_rejectedChip);

      ol_orderListRV.setLayoutManager(new LinearLayoutManager(requireContext()));
      adapter = new OrderListAdapter(filteredOrderList,getContext());
      ol_orderListRV.setAdapter(adapter);

      db = FirebaseFirestore.getInstance();
      auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            uid = auth.getCurrentUser().getUid();
        }

        if(getArguments() != null){
            role = getArguments().getInt("role");
        }

      getOrderIdData(uid);

        adapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onConfirmClick(int position) {
                Order order = filteredOrderList.get(position);

                String orderid = order.getOrderId();
                String custId = order.getUserId();
                order.setStatus("Confirmed");
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Confirmed");

                db.collection("orders")
                        .document(order.getOrderId())
                        .update("status", "Confirmed")
                        .addOnSuccessListener(aVoid -> {
                            db.collection("service_provider_orders")
                                    .document(uid)
                                    .collection("orders")
                                    .document(orderid)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid2 -> {
                                        Toast.makeText(getContext(), "Order Confirmed", Toast.LENGTH_SHORT).show();
                                        filterOrders(currentFilter);
                                    })
                                    .addOnFailureListener(e2 -> {
                                        Toast.makeText(getContext(), "Failed update service provider orders", Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore", "SP update failed", e2);
                                    });
                            Toast.makeText(getContext(), "Order Confirmed", Toast.LENGTH_SHORT).show();
                            filterOrders(currentFilter);
                            NotificationHelper.saveNotificationToFirestore(
                                    custId,
                                    "customer",
                                    "Order Confirmed",
                                    "Your order has been confirmed. Please check the order details.",
                                    "order_confirmed",
                                    orderid
                            );
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed update", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onRejectClick(int position) {
                Order order = filteredOrderList.get(position);

                String orderid = order.getOrderId();
                String custId = order.getUserId();
                order.setStatus("Rejected");

                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Rejected");

                db.collection("orders")
                        .document(order.getOrderId())
                        .update("status", "Rejected")
                        .addOnSuccessListener(aVoid -> {
                            db.collection("service_provider_orders")
                                    .document(uid)
                                    .collection("orders")
                                    .document(orderid)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid2 -> {
                                        Toast.makeText(getContext(), "Order Rejected", Toast.LENGTH_SHORT).show();
                                        filterOrders(currentFilter);
                                    })
                                    .addOnFailureListener(e2 -> {
                                        Toast.makeText(getContext(), "Failed update service provider orders", Toast.LENGTH_SHORT).show();
                                    });
                            Toast.makeText(getContext(), "Order Rejected", Toast.LENGTH_SHORT).show();
                            filterOrders(currentFilter);
                            NotificationHelper.saveNotificationToFirestore(
                                    custId,
                                    "customer",
                                    "Order Rejected",
                                    "Your order has been rejected. Please check the order details.",
                                    "order_rejected",
                                    orderid
                            );
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed update", Toast.LENGTH_SHORT).show();
                        });

            }

            @Override
            public void onViewDetailClick(int position) {
                Order order = filteredOrderList.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("role", role);
                bundle.putSerializable("order", order);
                OrderListDetailFragment orderDetailFragment = new OrderListDetailFragment();
                orderDetailFragment.setArguments(bundle);
                ((MainActivity) requireActivity()).openFragment(orderDetailFragment, true);
            }
        });

        ol_allChip.setOnClickListener(v -> filterOrders("All"));
        ol_pendingChip.setOnClickListener(v -> filterOrders("Pending"));
        ol_confirmedChip.setOnClickListener(v -> filterOrders("Confirmed"));
        ol_rejectedChip.setOnClickListener(v -> filterOrders("Rejected"));

        ol_backBtn.setOnClickListener(v -> {

            getParentFragmentManager().popBackStack();
        });

      return rootView;
    }
    private void filterOrders(String status) {
        currentFilter = status;
        filteredOrderList.clear();

        if (status.equalsIgnoreCase("All")) {
            filteredOrderList.addAll(orderList);
        } else {
            for (Order order : orderList) {
                if (order.getStatus() != null &&
                        order.getStatus().equalsIgnoreCase(status)) {
                    filteredOrderList.add(order);
                }
            }
        }
        sortOrdersByDateDesc(filteredOrderList);

        adapter.notifyDataSetChanged();
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
                            if(!"Completed".equalsIgnoreCase(order.getStatus())){
                                orderList.add(order);
                                sortOrdersByDateDesc(orderList);
                                filterOrders(currentFilter);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting order", e);
                });
    }


}