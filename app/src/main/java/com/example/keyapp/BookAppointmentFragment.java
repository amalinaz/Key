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

import com.example.keyapp.Adapter.PricelistAdapter;
import com.example.keyapp.Models.Service;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BookAppointmentFragment extends Fragment {

    ImageButton ba_backBtn;
    RecyclerView ba_viewRV;
    PricelistAdapter adapter;
    List<Service> pricelist = new ArrayList<>();
    private String BAid;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_appointment, container, false);
        ba_viewRV = rootView.findViewById(R.id.ba_viewRV);
        ba_backBtn = rootView.findViewById(R.id.pay_backBtn);
        db = FirebaseFirestore.getInstance();
        if (getArguments()!= null){
            BAid = getArguments().getString("BAid");

        }
        pricelistData();
        ba_viewRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PricelistAdapter(pricelist, getContext(), v -> {

            String serviceId = (String) v.getTag();
            if (serviceId == null) return;
            Service selectedService = null;
            for (Service service : pricelist) {
                if (service.getId().equals(serviceId)) {
                    selectedService = service;
                    break;
                }
            }

            Bundle bundle = new Bundle();
            bundle.putString("SERVICE_ID", serviceId);
            bundle.putString("SERVICE_NAME", selectedService.getServiceName());
            bundle.putDouble("SERVICE_PRICE", selectedService.getServicePrice());
            bundle.putInt("SERVICE_ESTTIME", selectedService.getEstTime());
            bundle.putString("BAid", selectedService.getBAid());

            AppointmentFragment appointmentFragment = new AppointmentFragment();
            appointmentFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(appointmentFragment, false);
        });
        ba_viewRV.setAdapter(adapter);
        ba_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        return rootView;

    }

    private void pricelistData() {
        db.collection("service")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    pricelist.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Service service = doc.toObject(Service.class);
                        if (service != null && service.getBAid().equals(BAid)) {
                            pricelist.add(service);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }
}