package com.example.keyapp.MenuBA.ServiceMenu;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.text.TextUtils;
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

import com.example.keyapp.Adapter.DaytimeAdapter;
import com.example.keyapp.LoginActivity;
import com.example.keyapp.Models.Daytime;
import com.example.keyapp.R;
import com.example.keyapp.Models.Service;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AddServiceFragment extends Fragment {

    private String uid, serviceID;
    private List<Daytime> daytimeList;

    Spinner categorySpinner;
    List<String> categoryService;


    EditText add_ServiceName, add_ServicePrice, add_ServiceDesc, add_estimatedTime;
    Button add_ServiceImg, add_SaveBtn;
    ImageButton add_backBtn;
    RecyclerView add_daytimeRV;
    Chip addImageNameChip;
    ImageView add_imagePreviewTV;

    FirebaseDatabase database;
    DatabaseReference refDatabase, counterRef;
    private FirebaseFirestore db;
    private StorageReference storage;
    FirebaseAuth auth;

    Uri imageUri;

    private String userId;
    private String getFileName(Uri uri) {
        Cursor c = requireContext().getContentResolver()
                .query(uri, null, null, null, null);
        if (c != null) {
            int nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            c.moveToFirst();
            String name = c.getString(nameIndex);
            c.close();
            return name;
        }
        return "unknown";
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_service, container, false);
        categorySpinner = rootView.findViewById(R.id.edit_ServiceCategory);
        add_ServiceName = rootView.findViewById(R.id.add_ServiceName);
        add_ServicePrice = rootView.findViewById(R.id.add_ServicePrice);
        add_ServiceDesc = rootView.findViewById(R.id.add_ServiceDesc);
        add_ServiceImg = rootView.findViewById(R.id.addImageBtn);
        add_SaveBtn = rootView.findViewById(R.id.add_Savebtn);
        add_backBtn = rootView.findViewById(R.id.add_BackBtn);
        add_daytimeRV = rootView.findViewById(R.id.add_daytimeRV);
        addImageNameChip= rootView.findViewById(R.id.add_imageChip);
        add_imagePreviewTV = rootView.findViewById(R.id.add_imgPreviewIV);
        add_estimatedTime = rootView.findViewById(R.id.add_serviceEstTime);

        addImageNameChip.setVisibility(View.GONE);
        add_imagePreviewTV.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        database= FirebaseDatabase.getInstance();
        counterRef = database.getReference("taskCounter");


        add_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });


        categoryService = new ArrayList<>();
        String[] categories = getResources().getStringArray(R.array.service_categories);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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
                Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();

            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == Activity.RESULT_OK){
                            Intent data = o.getData();
                            imageUri = data.getData();
                            if(imageUri != null){
                                String fileName = getFileName(imageUri);
                                addImageNameChip.setText(fileName);
                                addImageNameChip.setVisibility(View.VISIBLE);
                            }
                        }else{

                        }
                    }
                }
        );

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        userId = uid;

        add_ServiceImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });


        addImageNameChip.setOnClickListener(v -> {
            if (imageUri == null) return;

            if (add_imagePreviewTV.getVisibility() == View.VISIBLE) {
                add_imagePreviewTV.setVisibility(View.GONE);
            } else {
                add_imagePreviewTV.setImageURI(imageUri);
                add_imagePreviewTV.setVisibility(View.VISIBLE);
            }
        });

        addImageNameChip.setOnCloseIconClickListener(v -> {
            imageUri = null;

            add_imagePreviewTV.setVisibility(View.GONE);
            add_imagePreviewTV.setImageDrawable(null);
            add_imagePreviewTV.setVisibility(View.GONE);
            addImageNameChip.setVisibility(View.GONE);
        });

        add_SaveBtn.setOnClickListener(v -> {
            addServicetoDB(userId);
        });

        return rootView;

    }
    private void addServicetoDB(String userId) {
        String ServiceName = add_ServiceName.getText().toString();
        String ServicePrice = add_ServicePrice.getText().toString();
        String ServiceDesc = add_ServiceDesc.getText().toString();
        String EstimatedTime = add_estimatedTime.getText().toString().trim();


        if (TextUtils.isEmpty(ServiceName)) {
            Toast.makeText(getContext(), "Enter Service Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ServicePrice)) {
            Toast.makeText(getContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        long servicePrice;
        try {
            servicePrice = Long.parseLong(ServicePrice);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ServiceDesc)) {
            Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(EstimatedTime)) {
            Toast.makeText(getContext(), "Enter Estimated Time", Toast.LENGTH_SHORT).show();
            return;
        }

        int EstTime = Integer.parseInt(EstimatedTime);


        String selectedCategory = categorySpinner.getSelectedItem().toString();
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("service_images").child(System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {

                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imgUrl = uri.toString();
                    counterRef.get().addOnCompleteListener(taskcounter -> {
                        if (taskcounter.isSuccessful()) {
                            Integer serviceCounter = taskcounter.getResult().getValue(Integer.class);
                            if (serviceCounter == null) {
                                serviceCounter = 1;
                                counterRef.setValue(serviceCounter);
                            }

                            serviceID = "S" + String.format("%03d", serviceCounter + 1);
                            Service service = new Service(serviceID, ServiceName, servicePrice, ServiceDesc, selectedCategory, imgUrl, userId, EstTime);
                            counterRef.setValue(serviceCounter+1);
                            saveToFirestore(serviceID, service);

                        }

                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to Upload Image", Toast.LENGTH_SHORT).show();
                });
            });
        } else {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }


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
        db.collection("user_schedules")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> savedAvailableTimes =
                                (Map<String, Object>) documentSnapshot.get("availableTimes");

                        List<Daytime> daytimeList = new ArrayList<>();

                        if (savedAvailableTimes != null) {
                            for (Map.Entry<String, Object> entry : savedAvailableTimes.entrySet()) {
                                String day = entry.getKey();

                                List<?> timesForThisDay = (List<?>) entry.getValue();
                                List<String> times = new ArrayList<>();
                                if (timesForThisDay != null) {
                                    for (Object t : timesForThisDay) {
                                      times.add(String.valueOf(t));

                                    }
                                }
                                daytimeList.add(new Daytime(day, times));
                            }
                        }
                        add_daytimeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                        DaytimeAdapter adapter = new DaytimeAdapter(daytimeList);
                        add_daytimeRV.setAdapter(adapter);  // Set adapter ke RecyclerView
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });



    }


    private void saveToFirestore(String serviceID, Service service) {
        db.collection("service").document(serviceID)
                .set(service)
                .addOnSuccessListener(aVoid -> {
                    updateMinPrice(service.getBAid(), service.getServicePrice());
                    Toast.makeText(requireContext(), "Service data saved to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save Service data", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error saving user: ", e);
                });
    }

    private void updateMinPrice(String userId, long newPrice) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference userRef = firestore
                .collection("users")
                .document(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            long currentMin = snapshot.contains("minPrice")
                    ? snapshot.getLong("minPrice")
                    : Long.MAX_VALUE;

            if (newPrice < currentMin) {
                userRef.update("minPrice", newPrice);
            }
        });
    }

}



