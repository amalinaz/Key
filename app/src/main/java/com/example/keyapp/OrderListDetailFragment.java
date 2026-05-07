package com.example.keyapp;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyapp.Adapter.OrderListDetailAdapter;
import com.example.keyapp.Chat.ChatFragment;
import com.example.keyapp.Helper.NotificationHelper;
import com.example.keyapp.Models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderListDetailFragment extends Fragment {

    ImageButton oldetail_backBtn;
    Button oldetail_chatBtn, oldetail_rescheduleBtn, oldetail_addReviewBtn, oldetail_completeBtn, oldetail_reAccBtn, oldetail_reRejBtn;
    RecyclerView oldetail_RV;
    TextView oldetail_statusTV, oldetail_rescheduleDate;
    OrderListDetailAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    int role;
    ConstraintLayout oldetail_rescheduleLayout;
    String id, custName, service, location, date, time, status, baid, newDate, newTime, custid;
    double price;
    private Order order;
    private List<Order> orderList = new ArrayList<>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_order_list_detail, container, false);
        oldetail_statusTV = rootview.findViewById(R.id.oldetail_statusTV);
        oldetail_backBtn = rootview.findViewById(R.id.oldetail_backBtn);
        oldetail_chatBtn = rootview.findViewById(R.id.oldetail_chatBtn);
        oldetail_rescheduleBtn = rootview.findViewById(R.id.oldetail_rescheduleBtn);
        oldetail_RV = rootview.findViewById(R.id.oldetail_RV);
        oldetail_rescheduleLayout = rootview.findViewById(R.id.oldetail_rescheduleBALayout);
        oldetail_rescheduleDate = rootview.findViewById(R.id.oldetail_rescheduleDate);
        oldetail_reAccBtn = rootview.findViewById(R.id.oldetail_reAccBtn);
        oldetail_reRejBtn = rootview.findViewById(R.id.oldetail_reRejBtn);
        oldetail_completeBtn = rootview.findViewById(R.id.oldetail_completeBtn);
        oldetail_addReviewBtn = rootview.findViewById(R.id.oldetail_addReviewBtn);


        oldetail_rescheduleBtn.setVisibility(View.GONE);
        oldetail_addReviewBtn.setVisibility(View.GONE);
        oldetail_completeBtn.setVisibility(View.GONE);

        oldetail_rescheduleLayout.setVisibility(View.GONE);


        if(getArguments() != null){
            order = (Order) getArguments().getSerializable("order");
            if(order != null){
                orderList.add(order);
                id = order.getOrderId();
                custName = order.getUsername();
                price = order.getServicePrice();
                date = order.getSelectedDate();
                location = order.getLocation();
                time = order.getSelectedTime();
                service = order.getServiceName();
                status = order.getStatus();
                baid = order.getBAid();
                custid = order.getUserId();
            }
            role = getArguments().getInt("role");
        }

        oldetail_RV.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderListDetailAdapter(orderList, requireContext());
        oldetail_RV.setAdapter(adapter);

        oldetail_statusTV.setText(status);

        db = FirebaseFirestore.getInstance();
        db.collection("orders").document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        Map<String,Object> resched = (Map<String,Object>) doc.get("rescheduleRequest");
                        if(resched != null){
                            newDate = (String) resched.get("newDate");
                            newTime = (String) resched.get("newTime");
                            String reschedStatus = (String) resched.get("status");

                            oldetail_rescheduleDate.setText(newDate + " - " + newTime);


                            if("accepted".equalsIgnoreCase(reschedStatus) || "rejected".equalsIgnoreCase(reschedStatus)){
                                oldetail_reAccBtn.setVisibility(View.GONE);
                                oldetail_reRejBtn.setVisibility(View.GONE);
                            } else {
                                oldetail_reAccBtn.setVisibility(View.VISIBLE);
                                oldetail_reRejBtn.setVisibility(View.VISIBLE);
                            }
                        } else if (resched == null) {
                            oldetail_rescheduleLayout.setVisibility(View.GONE);
                        }
                        status = doc.getString("status");
                        if("completed".equalsIgnoreCase(status)){
                            oldetail_completeBtn.setVisibility(View.GONE);
                        }
                    }
                });

        oldetail_reAccBtn.setOnClickListener(v -> {
            if(newDate != null && newTime != null){
                Map<String, Object> updates = new HashMap<>();
                updates.put("rescheduleRequest.status", "accepted");
                updates.put("selectedDate", newDate);
                updates.put("selectedTime", newTime);

                db.collection("orders").document(id)
                        .update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Reschedule accepted", Toast.LENGTH_SHORT).show();

                            if(orderList.size() > 0){
                                Order updatedOrder = orderList.get(0);
                                updatedOrder.setSelectedDate(newDate);
                                updatedOrder.setSelectedTime(newTime);

                                adapter.notifyItemChanged(0);
                            }

                            oldetail_statusTV.setText("Reschedule accepted");
                            oldetail_rescheduleDate.setText(newDate + " - " + newTime);
                            oldetail_reAccBtn.setVisibility(View.GONE);
                            oldetail_reRejBtn.setVisibility(View.GONE);
                            NotificationHelper.saveNotificationToFirestore(
                                    custid,
                                    "customer",
                                    "Reschedule Accepted",
                                    "Your reschedule request has been accepted. Please check the updated schedule.",
                                    "reschedule_accepted",
                                    id
                            );
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal update reschedule", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Gagal, reschedule data belum tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        oldetail_reRejBtn.setOnClickListener(v -> {
            db.collection("orders").document(id)
                    .update("rescheduleRequest.status", "rejected")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Reschedule rejected", Toast.LENGTH_SHORT).show();

                        oldetail_reAccBtn.setVisibility(View.GONE);
                        oldetail_reRejBtn.setVisibility(View.GONE);
                        oldetail_rescheduleDate.setText("Reschedule rejected");
                        NotificationHelper.saveNotificationToFirestore(
                                custid,
                                "customer",
                                "Reschedule Rejected",
                                "Your reschedule request has been rejected. Please check the order details.",
                                "reschedule_rejected",
                                id
                        );
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal update reschedule", Toast.LENGTH_SHORT).show());
        });

        Log.d("reschedule", "role:"+role);

        if(role == 1){
            oldetail_rescheduleBtn.setVisibility(View.VISIBLE);
            oldetail_rescheduleBtn.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                bundle.putInt("role", role);
                OrderRescheduleFragment orderRescheduleFragment = new OrderRescheduleFragment();
                orderRescheduleFragment.setArguments(bundle);
                ((MainActivity) requireActivity()).openFragment(orderRescheduleFragment, true);
            });
            if("completed".equalsIgnoreCase(status)){
                oldetail_rescheduleBtn.setVisibility(View.GONE);
                oldetail_addReviewBtn.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", order);
                    bundle.putInt("role", role);
                    AddReviewFragment addReviewFragment= new AddReviewFragment();
                    addReviewFragment.setArguments(bundle);
                    ((MainActivity) requireActivity()).openFragment(addReviewFragment, true);
                });
                oldetail_addReviewBtn.setVisibility(View.VISIBLE);

            }

        }else if(role == 2){
            oldetail_rescheduleLayout.setVisibility(View.VISIBLE);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
            LocalDate orderDate = LocalDate.parse(date, dateFormatter);
            LocalTime orderTime = LocalTime.parse(time);

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            if(nowDate.isAfter(orderDate) || (nowDate.isEqual(orderDate) && nowTime.isAfter(orderTime)) && status.equals("Confirmed")){
                oldetail_completeBtn.setVisibility(View.VISIBLE);
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Completed");

                oldetail_completeBtn.setOnClickListener(v -> {
                    db.collection("orders").document(id)
                            .update("status", "Completed")
                            .addOnSuccessListener(aVoid -> {
                                db.collection("service_provider_orders")
                                        .document(baid)
                                        .collection("orders")
                                        .document(order.getOrderId())
                                        .update(updates)
                                        .addOnSuccessListener(aVoid2 -> {
                                            Toast.makeText(getContext(), "Order Completed", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e2 -> {
                                            Toast.makeText(getContext(), "Failed update service provider orders", Toast.LENGTH_SHORT).show();
                                        });
                                Toast.makeText(getContext(), "Order marked as completed", Toast.LENGTH_SHORT).show();
                                oldetail_completeBtn.setVisibility(View.GONE);
                                oldetail_statusTV.setText("Completed");

                                if(orderList.size() > 0){
                                    orderList.get(0).setStatus("completed");
                                    adapter.notifyItemChanged(0);
                                }
                                NotificationHelper.saveNotificationToFirestore(baid, "BA", "Order Completed",
                                        "Your order has been completed. You can now add a review.",
                                        "order_completed",
                                        id);
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal update status", Toast.LENGTH_SHORT).show());
                });
            }
        }

        oldetail_chatBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            String receiverId = "";

            if(role == 1){
                receiverId = baid;
            } else if(role == 2){
                receiverId = custid;
            }
            bundle.putString("receiverId",receiverId);
            bundle.putString("receiverName", role == 2 ? order.getUsername() : order.getBAName());

            ChatFragment chat = new ChatFragment();
            chat.setArguments(bundle);

            ((MainActivity) requireActivity()).openFragment(chat, true);

        });

        oldetail_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        return rootview;
    }
}