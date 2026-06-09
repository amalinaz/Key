package com.example.keyapp.MenuBA.ServiceMenu;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyapp.LoginActivity;
import com.example.keyapp.Models.Service;
import com.example.keyapp.R;
import com.example.keyapp.Adapter.ServiceAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServiceFragment extends Fragment implements ServiceAdapter.OnItemClickListener {
    ImageButton Service_BackBtn, Service_AddBtn;
    RecyclerView Service_ViewRV;
    private List<Service> serviceList;
    private ServiceAdapter serviceAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service, container, false);
        Service_BackBtn = rootView.findViewById(R.id.Service_BackBtn);
        Service_AddBtn = rootView.findViewById(R.id.Service_AddBtn);
        Service_ViewRV = rootView.findViewById(R.id.Service_ViewRV);

        Service_ViewRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(serviceList, requireContext());
        serviceAdapter.setOnItemClickListener(this);
        Service_ViewRV.setAdapter(serviceAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Service_BackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        Service_AddBtn.setOnClickListener(v -> {
            Fragment addServiceFragment = new AddServiceFragment();
            FrameLayout viewService = rootView.findViewById(R.id.viewService);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(viewService.getId(), addServiceFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();

        }

        uid = currentUser.getUid();
        getService(uid);

    }

    private  void getService(String uid){
        db.collection("service").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            serviceList.clear();
            for (DocumentSnapshot snapshot : value.getDocuments()) {
                Service service = snapshot.toObject(Service.class);
                if (service != null && service.getBAid().equals(uid)) {
                    serviceList.add(service);
                }
            }
            serviceAdapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (uid != null) {
            getService(uid);
        }
    }

    @Override
    public void onEditClick(int position) {
        Service service = serviceList.get(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("service_data", service);

        Fragment editServiceFragment = new EditServiceFragment();
        editServiceFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.viewService, editServiceFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteClick(int position) {
        Service serviceToDelete = serviceList.get(position);
        String serviceId = serviceToDelete.getId();

        deleteDialog(serviceId, position);

    }

    private void deleteDialog(String serviceId, int position){
        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View view = getLayoutInflater().inflate(R.layout.layout_alert_dialog, null);
        dialog.setView(view);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        dialog.setCancelable(false);

        TextView titleTV = view.findViewById(R.id.dialog_title);
        TextView messageTV = view.findViewById(R.id.dialog_message);
        Button rejectBtn = view.findViewById(R.id.dialog_btnA);
        Button acceptBtn = view.findViewById(R.id.dialog_btnB);

        titleTV.setText("Delete Service");
        messageTV.setText("Are you sure you want to delete this service? This action cannot be undone.");
        rejectBtn.setText("No");
        acceptBtn.setText("Delete");

        acceptBtn.setOnClickListener(v -> {
            deleteService(serviceId, position);
            dialog.dismiss();
        });

        rejectBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void deleteService(String serviceId, int position){
        db.collection("service").document(serviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    serviceList.remove(position);
                    serviceAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "Service Deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to delete Service", Toast.LENGTH_SHORT).show();
                });
    }
}