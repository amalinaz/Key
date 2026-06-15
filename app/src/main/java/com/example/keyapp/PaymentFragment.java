package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyapp.Adapter.BookScheduleAdapter;
import com.example.keyapp.Helper.NotificationHelper;
import com.example.keyapp.Models.BookScheduleItem;
import com.example.keyapp.Models.Order;
import com.example.keyapp.Models.Payment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaymentFragment extends Fragment {

    private ImageButton pay_backBtn;
    private Button pay_orderBtn;
    private RecyclerView pay_itemRV;
    private TextInputEditText pay_usernameTV, pay_inputTF, pay_locTV;
    private RadioGroup pay_paymentMethodRG;
    private TextView pay_totalTV, pay_subtotalTV;
    private String selectedPaymentMethod;
    private String userId, username, location;
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
        View rootview = inflater.inflate(R.layout.fragment_payment, container, false);
        pay_backBtn = rootview.findViewById(R.id.pay_backBtn);
        pay_orderBtn = rootview.findViewById(R.id.pay_orderBtn);
        pay_itemRV = rootview.findViewById(R.id.pay_itemRV);
        pay_usernameTV = rootview.findViewById(R.id.pay_nameTV);
        pay_inputTF = rootview.findViewById(R.id.pay_inputTF);
        pay_paymentMethodRG = rootview.findViewById(R.id.pay_paymentMethodRG);
        pay_totalTV = rootview.findViewById(R.id.pay_totalTV);
        pay_subtotalTV = rootview.findViewById(R.id.pay_subtotalTV);
        pay_locTV = rootview.findViewById(R.id.pay_locTV);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://key-app-42f22-default-rtdb.asia-southeast1.firebasedatabase.app");
        counterRef = database.getReference("orderCounter");

        String uid = auth.getCurrentUser().getUid();
        getUserData(uid);

        pay_inputTF.setVisibility(View.GONE);

        pay_paymentMethodRG.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRB = group.findViewById(checkedId);
            selectedPaymentMethod = selectedRB.getText().toString();

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

        if (getArguments() != null) {
            bookScheduleItemsList = (List<BookScheduleItem>) getArguments().getSerializable("bookScheduleItems");
            username = getArguments().getString("username");
            location = getArguments().getString("location");
            BAid = getArguments().getString("BAid");
            BAName = getArguments().getString("BAname");
            serviceName = getArguments().getString("ServiceName");
            selectedDate = getArguments().getString("SelectedDate");
            selectedTime = getArguments().getString("SelectedTime");
            servicePrice = getArguments().getDouble("servicePrice");

        }

        pay_locTV.setText(location);
        pay_itemRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookScheduleAdapter = new BookScheduleAdapter(bookScheduleItemsList, getContext());
        pay_itemRV.setAdapter(bookScheduleAdapter);

        pay_subtotalTV.setText(Double.toString(servicePrice));
        pay_totalTV.setText(Double.toString(servicePrice));

        pay_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        pay_orderBtn.setOnClickListener(v -> {
            String userId = auth.getCurrentUser().getUid();
            String username = pay_usernameTV.getText().toString();
            saveOrdertoDB(userId, username, selectedPaymentMethod);
        });

        return rootview;
    }
    private void saveOrdertoDB(String userId, String username, String selectedPaymentMethod){
        counterRef.get().addOnCompleteListener(orderCounter -> {
            if(orderCounter.isSuccessful()){
                long timestamp = System.currentTimeMillis();
                Payment payment = new Payment(selectedPaymentMethod, servicePrice,timestamp);
                Integer order = orderCounter.getResult().getValue(Integer.class);
                if(order == null){
                    order = 1;
                }
                int orderCount = order + 1;
                String orderId = "O" + String.format("%03d",orderCount);
                Order newOrder = new Order(orderId, userId, username, serviceName, selectedDate,selectedTime, servicePrice, "Pending", BAid, BAName, location, payment);
                db.collection("orders")
                        .document(orderId)
                        .set(newOrder)
                        .addOnSuccessListener(aVoid -> {
                            counterRef.setValue(orderCount);
                            Toast.makeText(getContext(), "Order submitted successfully", Toast.LENGTH_SHORT).show();
                            passOrderToServiceProvider(orderId, BAid, BAName);

                            goToOrderSuccess();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to submit order", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    private void getUserData(String uid){
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
        NotificationHelper.saveNotificationToFirestore(BAid,"BA", "New Order", "new order received", "new_order", orderId);

    }

    public void goToOrderSuccess() {
        ((MainActivity) requireActivity()).openFragment(new OrderSuccessPaymentFragment(), true);
    }

    public interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }
}