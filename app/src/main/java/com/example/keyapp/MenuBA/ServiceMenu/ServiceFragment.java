package com.example.keyapp.MenuBA.ServiceMenu;

import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import java.util.List;


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
            getActivity().onBackPressed();
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
        db.collection("service").get().addOnSuccessListener(queryDocumentSnapshots -> {
            serviceList.clear();  // Clear list agar data lama tidak tercampur
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                Service service = snapshot.toObject(Service.class);  // Map data ke objek Service

                // Periksa apakah UID cocok dengan pengguna yang sedang login
                if (service != null && service.getBAid().equals(uid)) {
                    serviceList.add(service);  // Tambahkan service ke list jika UID cocok
                }
            }
            serviceAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
        });
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
        String serviceId = serviceToDelete.getId();  // Ambil serviceId dari objek Service
        Log.d("Service Fragment", serviceId);
        // Hapus data dari Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("service").document(serviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Data berhasil dihapus dari Firestore
                    serviceList.remove(position);  // Hapus data dari list
                    serviceAdapter.notifyItemRemoved(position);  // Update RecyclerView
                    Toast.makeText(requireContext(), "Item Deleted from Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Gagal menghapus data dari Firestore
                    Toast.makeText(requireContext(), "Failed to delete item from Firebase", Toast.LENGTH_SHORT).show();
                });
    }
}