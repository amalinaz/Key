package com.example.keyapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyapp.Adapter.PortofolioAdapter;
import com.example.keyapp.Models.Portofolio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PortofolioFragment extends Fragment {

    ImageButton porto_backBtn;
    RecyclerView porto_gridRV;
    FloatingActionButton porto_addBtn;
    PortofolioAdapter adapter;
    private ActivityResultLauncher<String> multiImagePickerLauncher;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid, uidBA;
    private List<Portofolio> imageList = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_portofolio, container, false);

        porto_backBtn = rootView.findViewById(R.id.oldetail_backBtn);
        porto_gridRV = rootView.findViewById(R.id.porto_gridRV);
        porto_addBtn = rootView.findViewById(R.id.porto_addBtn);
        porto_addBtn.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        int role = 2;
        uid = null;

        if(getArguments() != null){
            role = getArguments().getInt("role");
            uidBA = getArguments().getString("BAid");
        }

        if(role == 1){
            fetchPortofolio(uidBA);
        }else if(role == 2){
            porto_addBtn.setVisibility(View.VISIBLE);
            uid = auth.getUid();
            fetchPortofolio(uid);
        }

        porto_gridRV.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new PortofolioAdapter(imageList, getContext(), item -> showDeleteDialog(item), role);
        porto_gridRV.setAdapter(adapter);

        porto_addBtn.setOnClickListener(v -> {
            addImagePorto();
        });

        porto_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });


        return rootView;
    }

    private void showDeleteDialog(Portofolio item) {
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
        Button noBtn = view.findViewById(R.id.dialog_btnA);
        Button deleteBtn = view.findViewById(R.id.dialog_btnB);

        titleTV.setText("Delete Photo?");
        messageTV.setText("This photo will be deleted in portofolio.");
        noBtn.setText("No");
        deleteBtn.setText("Delete");

        deleteBtn.setOnClickListener(v -> {
            deletePhoto(item);
            dialog.dismiss();
        });

        noBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void deletePhoto(Portofolio item) {

        int position = imageList.indexOf(item);
        if (position == -1) return; // safety

        FirebaseStorage.getInstance()
                .getReference(item.storagePath)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    FirebaseFirestore.getInstance()
                            .collection("portofolio")
                            .document(uid)
                            .collection("photos")
                            .document(item.documentId)
                            .delete()
                            .addOnSuccessListener(v -> {
                                imageList.remove(position);
                                adapter.notifyItemRemoved(position);

                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Gagal hapus foto", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchPortofolio(String uid) {
        FirebaseFirestore.getInstance()
                .collection("portofolio")
                .document(uid)
                .collection("photos")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    imageList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String imageUrl = doc.getString("imageUrl");
                        String storagePath = doc.getString("storagePath");
                        String documentId = doc.getId();

                        imageList.add(new Portofolio(
                                imageUrl,
                                storagePath,
                                documentId
                        ));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        multiImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris == null || uris.isEmpty()) return;

                    for (Uri uri : uris) {

                        String fileName = UUID.randomUUID().toString();
                        String storagePath = "portofolio/" + uid + "/" + fileName;

                        StorageReference ref = FirebaseStorage.getInstance()
                                .getReference()
                                .child(storagePath);

                        ref.putFile(uri)
                                .addOnSuccessListener(taskSnapshot ->
                                        ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {

                                            Map<String, Object> data = new HashMap<>();
                                            data.put("imageUrl", downloadUri.toString());
                                            data.put("storagePath", storagePath);
                                            data.put("timestamp", System.currentTimeMillis());

                                            FirebaseFirestore.getInstance()
                                                    .collection("portofolio")
                                                    .document(uid)
                                                    .collection("photos")
                                                    .add(data).addOnSuccessListener(docRef -> {

                                                        imageList.add(new Portofolio(
                                                                downloadUri.toString(),
                                                                storagePath,
                                                                docRef.getId()
                                                        ));

                                                        adapter.notifyItemInserted(imageList.size() - 1);
                                                    });
                                        })
                                );
                    }
                }
        );

    }


    private void addImagePorto(){
        multiImagePickerLauncher.launch("image/*");
    }

}