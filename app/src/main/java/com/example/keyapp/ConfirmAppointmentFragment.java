package com.example.keyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Toast;

import com.example.keyapp.Adapter.BookScheduleAdapter;
import com.example.keyapp.Models.BookScheduleItem;
import com.example.keyapp.Models.Service;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

public class ConfirmAppointmentFragment extends Fragment {

    private ImageButton CA_backBtn;
    private Button CA_orderBtn;
    private RecyclerView CA_itemRV;
    private TextInputEditText CA_nameTV, CA_locTV;
    BookScheduleAdapter bookScheduleAdapter;
    List<BookScheduleItem> bookScheduleItemsList = new ArrayList<>();
    private String userId, Username;
    private String serviceId, BAid,BAName, selectedDate, selectedTime, date, serviceName;
    private double servicePrice;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference counterRef;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_appointment, container, false);
        CA_backBtn = rootView.findViewById(R.id.CA_backBtn);
        CA_orderBtn = rootView.findViewById(R.id.CA_orderBtn);
        CA_itemRV = rootView.findViewById(R.id.CA_itemRV);
        CA_nameTV = rootView.findViewById(R.id.CA_nameTV);
        CA_locTV = rootView.findViewById(R.id.CA_locTV);

        CA_locTV.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://key-app-42f22-default-rtdb.asia-southeast1.firebasedatabase.app");
        counterRef = database.getReference("orderCounter");


        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String username = doc.getString("userName");
                        if (username != null) CA_nameTV.setText(username);

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal mengambil data profil", Toast.LENGTH_SHORT).show();
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

        db.collection("users").document(BAid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String locType = doc.getString("locationType");
                        Map<String, Object> location = (Map<String, Object>) doc.get("location");
                        String BAlocation = (String) location.get("address");

                        if(locType.equals("homevisit")){
                            CA_locTV.setVisibility(View.VISIBLE);
                            CA_locTV.setFocusableInTouchMode(true);
                            CA_locTV.setFocusable(true);
                            CA_locTV.setClickable(true);
                        } else if (locType.equals("studiovisit")) {
                            CA_locTV.setVisibility(View.VISIBLE);
                            CA_locTV.setText(BAlocation);
                            CA_locTV.setFocusable(false);
                            CA_locTV.setFocusableInTouchMode(false);
                            CA_locTV.setClickable(false);
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal mengambil data profil", Toast.LENGTH_SHORT).show();
                });

        date = formatDate(selectedDate);
        BookScheduleItem bookScheduleItem = new BookScheduleItem(BAName, serviceName, date, selectedTime);
        bookScheduleItemsList.add(bookScheduleItem);
        CA_itemRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookScheduleAdapter = new BookScheduleAdapter(bookScheduleItemsList, getContext());
        CA_itemRV.setAdapter(bookScheduleAdapter);

        getUsername(uid, new PaymentFragment.OnUsernameFetchedListener() {
            @Override
            public void onUsernameFetched(String username) {
                if (username != null) {
                    CA_nameTV.setText(username);
                    Log.d("Firestore", "Username: " + username);
                }
            }
        });

        CA_orderBtn.setOnClickListener(v -> {
            String username = CA_nameTV.getText().toString();
            String location = CA_locTV.getText().toString();

            Bundle bundle = new Bundle();

            bundle.putSerializable("bookScheduleItems", (Serializable) bookScheduleItemsList);
            bundle.putString("username", username);
            bundle.putString("location", location);
            bundle.putString("BAid", BAid);
            bundle.putString("BAname", BAName);
            bundle.putString("SelectedTime", selectedTime);
            bundle.putString("SelectedDate", selectedDate);
            bundle.putString("ServiceName", serviceName);
            bundle.putString("BAid", BAid);

            bundle.putDouble("servicePrice", servicePrice);

            PaymentFragment paymentFragment = new PaymentFragment();
            paymentFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(paymentFragment, false);

        });

        CA_backBtn.setOnClickListener(v -> {
             getParentFragmentManager().popBackStack();
        });

        return rootView;
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

    private void getUsername(String userId, final PaymentFragment.OnUsernameFetchedListener listener){
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            listener.onUsernameFetched(username);
                        } else {
                            listener.onUsernameFetched(null);
                        }
                    } else {
                        listener.onUsernameFetched(null);
                    }
                });
    }


}