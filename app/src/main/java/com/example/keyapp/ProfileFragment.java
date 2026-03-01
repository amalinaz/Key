package com.example.keyapp;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.keyapp.Adapter.TimeListAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class ProfileFragment extends Fragment {

    private String[] daysOfWeek;
    private String uid;
    private int role;
    ImageView profile_imageIV;
    TextView profile_changeProfile;
    ImageButton profile_backBtn, profile_saveBtn;
    Button profile_logoutBtn;
    EditText profile_username, profile_email, profile_phone, profile_location;
    Uri image;

    TimeListAdapter adapter;
    View line;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storage;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_profile, container, false);


        profile_backBtn = rootview.findViewById(R.id.profile_BackBtn);
        profile_saveBtn =  rootview.findViewById(R.id.profile_saveBtn);
        profile_logoutBtn = rootview.findViewById(R.id.profile_logoutBtn);

        profile_username =  rootview.findViewById(R.id.profile_username);
        profile_email=  rootview.findViewById(R.id.profile_email);
        profile_phone =  rootview.findViewById(R.id.profile_phone);
        profile_location = rootview.findViewById(R.id.profile_location);

        profile_changeProfile = rootview.findViewById(R.id.profile_changeProfile);
        profile_imageIV = rootview.findViewById(R.id.profile_imageIV);

        daysOfWeek = getResources().getStringArray(R.array.Hari);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View rootview, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootview, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();

        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            openGallery();
        }

        uid = currentUser.getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Long roleLong = doc.getLong("userlvl");
                        role = roleLong != null ? roleLong.intValue() : 1;

                        Log.d("PROFILE_FIRESTORE", "Data: " + doc.getData());

                        Map<String, Object> location = (Map<String, Object>) doc.get("location");

                        String loc = null;
                        if (location != null) {
                            loc = (String) location.get("address");
                        }

                        if (loc != null) {
                            profile_location.setText(loc);
                        }

                        String username = doc.getString("userName");
                        String email = doc.getString("email");

                        String phone = doc.getString("phone");
                        String imageUrl = doc.getString("profileImageUrl");
                        if (imageUrl != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .into(profile_imageIV);
                        }else{
                            profile_imageIV.setImageResource(R.drawable.profile_no);
                        }
                        if (username != null) profile_username.setText(username);
                        if (email != null) profile_email.setText(email);
                        if (loc != null) profile_location.setText(loc);
                        if (phone != null) profile_phone.setText(phone);

                    }else{
                        Log.d("PROFILE_FIRESTORE", "Document not found for uid: " + uid);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal mengambil data profil", Toast.LENGTH_SHORT).show();
                });

        profile_changeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        profile_logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return;
        });

        profile_backBtn.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

    }


    private void saveProfileToFirestore() {
        String newUsername = profile_username.getText().toString().trim();
        String newEmail = profile_email.getText().toString().trim();
        String phone = profile_phone.getText().toString().trim();


        FirebaseUser currentUser1 = mAuth.getCurrentUser();
        String uid1 = currentUser1.getUid();

        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("userName", newUsername);
        updateUser.put("email", newEmail);
        updateUser.put("phone", phone);


        if (image != null) {
            uploadImage(image, uid1);
        }

        db.collection("users").document(uid1)
                .update(updateUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });

        if (!newEmail.isEmpty()) {
            currentUser1.updateEmail(newEmail);
        }


    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        image = result.getData().getData();
                        if (image != null) {
                            Glide.with(requireContext()).load(image).into(profile_imageIV);
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
            });
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activityResultLauncher.launch(intent); // Launch the gallery
    }
    private void uploadImage(Uri file, String uid) {
        StorageReference ref = storage.child("images/" + UUID.randomUUID().toString());
        ref.putFile(file).addOnSuccessListener(ntaskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveProfileImageToFirestore(imageUrl, uid);
                Toast.makeText(requireContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveProfileImageToFirestore(String imageUrl, String uid) {
        db.collection("users").document(uid)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Image URL saved to Firestore!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show();
                });
    }
    private void saveLocationCombined() {
        String manualAddress = profile_location.getText().toString().trim();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            getAddressFromLocationAndSave(lat, lng, manualAddress);
                        } else {
                            if (!manualAddress.isEmpty()) {
                                geocodeManualAddressAndSave(manualAddress);
                            } else {
                                Toast.makeText(getContext(),
                                        "Lokasi tidak tersedia dan alamat kosong",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                        if (!manualAddress.isEmpty()) {
                            geocodeManualAddressAndSave(manualAddress);
                        } else {
                            Toast.makeText(getContext(),
                                    "Gagal mendapatkan lokasi dan alamat kosong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            if (!manualAddress.isEmpty()) {
                geocodeManualAddressAndSave(manualAddress);
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
//                Toast.makeText(getContext(),
//                        "Izinkan lokasi atau isi alamat dulu",
//                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void getAddressFromLocationAndSave(double latitude, double longitude, String fallbackManualText) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String finalAddressText;

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                finalAddressText = address.getAddressLine(0);
            } else {
                finalAddressText = fallbackManualText != null ? fallbackManualText : "";
            }

            saveLocationToFirestore(latitude, longitude, finalAddressText);

        } catch (IOException e) {
            Log.e("Location", "Geocoder failed", e);
            saveLocationToFirestore(latitude, longitude, fallbackManualText);
        }
    }
    private void geocodeManualAddressAndSave(String manualAddress) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String query = manualAddress + ", Indonesia";
            List<Address> addresses = geocoder.getFromLocationName(query, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                double lat = addr.getLatitude();
                double lng = addr.getLongitude();
                String resolvedAddress = addr.getAddressLine(0);

                saveLocationToFirestore(lat, lng, resolvedAddress);
            } else {
                saveLocationToFirestore(null, null, manualAddress);
            }
        } catch (IOException e) {
            Log.e("Geocoder", "Error getting address", e);
            saveLocationToFirestore(null, null, manualAddress);
        }
    }
    private void saveLocationToFirestore(@Nullable Double latitude, @Nullable Double longitude,String address) {

        Map<String, Object> locationMap = new HashMap<>();
        if (latitude != null && longitude != null) {
            locationMap.put("latitude", latitude);
            locationMap.put("longitude", longitude);
        }
        locationMap.put("address", address);

        Map<String, Object> data = new HashMap<>();
        data.put("location", locationMap);

        db.collection("users").document(uid)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Location", "Location saved to Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Location", "Error saving location", e);
                });
    }


}