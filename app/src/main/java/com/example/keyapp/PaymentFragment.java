package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.keyapp.Adapter.BookScheduleAdapter;
import com.example.keyapp.Models.BookScheduleItem;
import com.example.keyapp.Models.Order;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PaymentFragment extends Fragment {

    private ImageButton pay_backBtn;
    private Button pay_orderBtn;
    private RecyclerView pay_itemRV;
    private TextInputEditText pay_usernameTV, pay_inputTF;
    private RadioGroup pay_paymentMethodRG;
    private TextView pay_totalTV, pay_subtotalTV;
    private String selectedPaymentMethod;
    private String userId, Username;
    private String serviceId, BAid,BAName, selectedDate, selectedTime, date, serviceName;

    private double servicePrice;
    BookScheduleAdapter bookScheduleAdapter;
    List<BookScheduleItem> bookScheduleItemsList = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference counterRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_payment, container, false);
        pay_backBtn = rootview.findViewById(R.id.pay_backBtn);
        pay_orderBtn = rootview.findViewById(R.id.pay_orderBtn);
        pay_itemRV = rootview.findViewById(R.id.pay_itemRV);
        pay_usernameTV = rootview.findViewById(R.id.pay_nameTV);
        pay_inputTF = rootview.findViewById(R.id.pay_inputTF);
        pay_paymentMethodRG = rootview.findViewById(R.id.pay_paymentMethodRG);
        pay_totalTV = rootview.findViewById(R.id.pay_totalTV);
        pay_subtotalTV = rootview.findViewById(R.id.pay_subtotalTV);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        counterRef = database.getReference("orderCounter");

        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String username = doc.getString("userName");

                        if (username != null) pay_usernameTV.setText(username);

                    }else{
                        Log.d("paymentfirestore", "Document not found for uid: " + uid);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal mengambil data profil", Toast.LENGTH_SHORT).show();
                });

        pay_inputTF.setVisibility(View.GONE);

        pay_paymentMethodRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.pay_ewalletRB) {
                pay_inputTF.setHint("Enter Phone Number");
                pay_inputTF.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.pay_ccRB) {
                pay_inputTF.setHint("Enter Card Number");
                pay_inputTF.setVisibility(View.VISIBLE);
            } else {
                pay_inputTF.setVisibility(View.GONE);
            }
        });

        if(getArguments() != null ){
            serviceId = getArguments().getString("serviceID");
            BAid = getArguments().getString("BAid");
            BAName = getArguments().getString("BAName");
            selectedDate = getArguments().getString("selectedDate");
            selectedTime = getArguments().getString("selectedTime");
            serviceName = getArguments().getString("serviceName");
            servicePrice = getArguments().getDouble("servicePrice");

        }

        date = formatDate(selectedDate);
        BookScheduleItem bookScheduleItem = new BookScheduleItem(BAName, serviceName, date, selectedTime);
        bookScheduleItemsList.add(bookScheduleItem);
        pay_itemRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookScheduleAdapter = new BookScheduleAdapter(bookScheduleItemsList, getContext());
        pay_itemRV.setAdapter(bookScheduleAdapter);

        pay_usernameTV.setText(getUsername(uid));
        Log.d("Payment", "usn" + getUsername(uid));
        Log.d("uid", "uid" + uid);
        pay_subtotalTV.setText(Double.toString(servicePrice));
        pay_totalTV.setText(Double.toString(servicePrice));

        pay_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        pay_orderBtn.setOnClickListener(v -> {
            String userId = auth.getCurrentUser().getUid();
            String username = pay_usernameTV.getText().toString();
            Order newOrder = new Order(userId, username, serviceName, selectedDate,selectedTime,servicePrice, "Pending", BAid, BAName);
            counterRef.get().addOnCompleteListener(orderCounter ->{
                if(orderCounter.isSuccessful()){
                    Integer order = orderCounter.getResult().getValue(Integer.class);
                    if (order == null) {
                        order = 1;
                        counterRef.setValue(orderCounter);
                        Log.d("Firebase", "Setting initial userCounter to 1");
                    }

                    String orderId = "O" + String.format("%03d", order+1);
                    db.collection("orders")
                            .document(orderId)
                            .set(newOrder)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Order submitted successfully", Toast.LENGTH_SHORT).show();
                                passOrderToServiceProvider(orderId, BAid, BAName);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to submit order", Toast.LENGTH_SHORT).show();
                            });
                    goToOrderSuccess();

                }
            });

        });

        return rootview;
    }

    private void passOrderToServiceProvider(String orderId, String BAid, String BAName) {
        Map<String, Object> orderForServiceProvider = new HashMap<>();
        orderForServiceProvider.put("orderId", orderId);
        orderForServiceProvider.put("status", "pending");
        orderForServiceProvider.put("BAid", BAid);
        orderForServiceProvider.put("BAName", BAName);


        db.collection("service_provider_orders")
                .document(BAid)
                .collection("orders")
                .document(orderId)
                .set(orderForServiceProvider)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PaymentFragment", "Order passed to service provider");
                })
                .addOnFailureListener(e -> {
                    Log.e("PaymentFragment", "Failed to pass order to service provider", e);
                });
    }


    public void goToOrderSuccess() {
        OrderSuccessPaymentFragment orderSuccessFragment = new OrderSuccessPaymentFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.paymentLayout, orderSuccessFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private String formatDate(String selectedDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(selectedDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return selectedDate;
    }

    private String getUsername(String userId){
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                Username = username;
                                // Log atau lakukan operasi lain dengan username
                                Log.d("AppointmentFragment", "Fetched Username: " + username);
                            } else {
                                Log.d("AppointmentFragment", "Document does not exist.");
                            }
                        } else {
                            Log.e("AppointmentFragment", "Error fetching username: " + task.getException());
                        }
                    });

        return Username;
    }
}