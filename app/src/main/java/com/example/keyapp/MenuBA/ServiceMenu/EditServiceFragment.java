package com.example.keyapp.MenuBA.ServiceMenu;


import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.keyapp.Models.Daytime;
import com.example.keyapp.R;
import com.example.keyapp.Models.Service;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EditServiceFragment extends Fragment {


    private String uid, serviceID;
    private List<Daytime> daytimeList;

    Spinner categorySpinner;
    List<String> categoryService;


    EditText edit_ServiceName, edit_ServicePrice, edit_ServiceDesc, edit_EstTime;
    Button edit_ServiceImg, edit_SaveBtn;
    ImageButton edit_backBtn;
    RecyclerView edit_daytimeRV;
    Chip editImageNameChip;
    ImageView edit_imagePreviewTV;

    FirebaseDatabase database;
    DatabaseReference refDatabase, counterRef;
    private FirebaseFirestore db;
    private StorageReference storage;
    FirebaseAuth auth;

    Uri imageUri;
    private Service service;
    private String userId;
    private String getFileNameFromUrl(String url) {
        try {
            Uri uri = Uri.parse(url);
            String lastPath = uri.getLastPathSegment(); // images%2Fphoto_ayam.png

            if (lastPath != null && lastPath.contains("%2F")) {
                return lastPath.substring(lastPath.lastIndexOf("%2F") + 3);
            }

            return lastPath;
        } catch (Exception e) {
            return "Image";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_service, container, false);
        categorySpinner = rootView.findViewById(R.id.edit_ServiceCategory);
        edit_ServiceName = rootView.findViewById(R.id.edit_ServiceName);
        edit_ServicePrice = rootView.findViewById(R.id.edit_ServicePrice);
        edit_ServiceDesc = rootView.findViewById(R.id.edit_ServiceDesc);
        edit_ServiceImg = rootView.findViewById(R.id.editImageBtn);
        edit_SaveBtn = rootView.findViewById(R.id.edit_Savebtn);
        edit_backBtn = rootView.findViewById(R.id.edit_BackBtn);
//        edit_daytimeRV = rootView.findViewById(R.id.edit_daytimeRV);
        editImageNameChip = rootView.findViewById(R.id.edit_imageChip);
        edit_imagePreviewTV = rootView.findViewById(R.id.edit_imgPreviewIV);
        edit_EstTime = rootView.findViewById(R.id.edit_serviceEstTime);

        editImageNameChip.setVisibility(View.GONE);
        edit_imagePreviewTV.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        counterRef = database.getReference("taskCounter");


        edit_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            service = (Service) getArguments().getSerializable("service_data");

            if (service != null) {
                // Isi EditText dengan data service yang diterima
                edit_ServiceName.setText(service.getServiceName());
                edit_ServicePrice.setText(String.valueOf(service.getServicePrice()));
                edit_ServiceDesc.setText(service.getServiceDesc());  // Menampilkan deskripsi yang bisa diedit
                edit_EstTime.setText(String.valueOf(service.getEstTime()));

                String selectedCategory = service.getServiceCategory();
                String[] categories = getResources().getStringArray(R.array.service_categories);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                for(int i = 0; i< categories.length; i++){
                    if(categories[i].equals(selectedCategory)){
                        categorySpinner.setSelection(i);
                        break;
                    }
                }

                String filename = getFileNameFromUrl(service.getImgUrl());
                editImageNameChip.setVisibility(View.VISIBLE);
                editImageNameChip.setText(filename);
                edit_imagePreviewTV.setVisibility(View.VISIBLE);
                String imageUrl = service.getImgUrl();
                Glide.with(getContext())
                        .load(imageUrl)
                        .into(edit_imagePreviewTV);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String selectedCategory = parentView.getItemAtPosition(position).toString();
                        if (position > 0) {
                            Log.d("Spinner", "Selected Category: " + selectedCategory);
                        } else {
                            Log.d("Spinner", "Please select a valid category");
                            Toast.makeText(getContext(), "Please select a valid category", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {

                    }
                });

                editImageNameChip.setOnClickListener(v -> {
                    if (imageUri == null) return;

                    if (edit_imagePreviewTV.getVisibility() == View.VISIBLE) {
                        edit_imagePreviewTV.setVisibility(View.GONE);
                    } else {
                        edit_imagePreviewTV.setImageURI(imageUri);
                        edit_imagePreviewTV.setVisibility(View.VISIBLE);
                    }
                });

                editImageNameChip.setOnCloseIconClickListener(v -> {
                    imageUri = null;

                    edit_imagePreviewTV.setVisibility(View.GONE);
                    edit_imagePreviewTV.setImageDrawable(null);
                    edit_imagePreviewTV.setVisibility(View.GONE);
                    editImageNameChip.setVisibility(View.GONE);
                });

            }

        }

        edit_SaveBtn.setOnClickListener(v -> saveService());

        return rootView;
    }
    private void saveService() {
        String newServiceName = edit_ServiceName.getText().toString();
        String newServicePrice = edit_ServicePrice.getText().toString();
        String newServiceDesc = edit_ServiceDesc.getText().toString();
        String newEstTime = edit_EstTime.getText().toString();

        service.setServiceName(newServiceName);
        service.setServicePrice(Long.parseLong(newServicePrice));
        service.setServiceDesc(newServiceDesc);
        service.setEstTime(Integer.parseInt(newEstTime));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("services").document(service.getId())
                .set(service)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Service updated", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update service", Toast.LENGTH_SHORT).show();
                });
    }

}
