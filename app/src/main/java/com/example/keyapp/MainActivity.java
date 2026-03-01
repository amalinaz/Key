package com.example.keyapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Adapter.ServiceRecAdapter;
import com.example.keyapp.Models.BAprofile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ServiceRecAdapter.OnItemCLickListener{

    ImageView main_profile;
    ImageButton MUABtn, NABtn, EyelashBtn;
    TextView recommendTV;
    ProgressBar pb;
    FirebaseAuth auth;
    FirebaseFirestore db;
    private ServiceRecAdapter srAdapter;
    RecyclerView srRV;
    List<BAprofile> BAlist = new ArrayList<>();
    private String selectedCategory = "";
    private double latUser, lonUser;



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String uid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> location = (Map<String, Object>) doc.get("location");

                        if (location != null) {
                            latUser = (double) location.get("latitude");
                            lonUser = (double) location.get("longitude");

                            Log.d("LOC", "Lat: " + latUser);
                            Log.d("LOC", "Lng: " + latUser);

                        }
                        Long roleLong = doc.getLong("userlvl");
                        int role = roleLong != null ? roleLong.intValue() : 1;

                        if (role == 2) {
                            Intent i = new Intent(MainActivity.this, MainActivity2.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.d("MAIN", "Role 1, stay in MainActivity");
                        }
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        main_profile = findViewById(R.id.main_profileIV);
        MUABtn = findViewById(R.id.MUAbtn);
        NABtn = findViewById(R.id.NABtn);
        EyelashBtn = findViewById(R.id.EyelashBtn);

        recommendTV = findViewById(R.id.recommendTV);
        pb = findViewById(R.id.progressBar);


        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            main_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            main_profile.setOnClickListener(v -> {
                showProfile();
            });
        }

        db = FirebaseFirestore.getInstance();
        srRV = findViewById(R.id.main_ServiceRV);

        srAdapter = new ServiceRecAdapter(BAlist, this);
        srAdapter.setOnItemClickListener(this);
        srRV.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        srRV.setAdapter(srAdapter);
        recommendTV.setVisibility(View.GONE);
        srRV.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);


        MUABtn.setOnClickListener(v -> {
            selectedCategory = "Make Up Artist";
            pb.setVisibility(View.VISIBLE);
            recommendTV.setVisibility(View.VISIBLE);
            srRV.setVisibility(View.VISIBLE);
            fetchBAProfiles("Make Up");
        });
        NABtn.setOnClickListener(v -> {
            selectedCategory = "Nail Artist";
            pb.setVisibility(View.VISIBLE);
            recommendTV.setVisibility(View.VISIBLE);
            srRV.setVisibility(View.VISIBLE);

            fetchBAProfiles("Nail Art");
        });
        EyelashBtn.setOnClickListener(v -> {
            selectedCategory = "Eyelash Artist";
            pb.setVisibility(View.VISIBLE);
            recommendTV.setVisibility(View.VISIBLE);
            srRV.setVisibility(View.VISIBLE);

            fetchBAProfiles("Eyelash");
        });

    }

    private void showProfile() {
        Fragment profileFragment = new ProfileFragment();
        FrameLayout main = findViewById(R.id.main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main.getId(), profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void fetchBAProfiles(String category) {
        BAlist.clear();
        srAdapter.notifyDataSetChanged();

        db.collection("users")
                .whereEqualTo("userlvl", 2)
                .get()
                .addOnSuccessListener(userQuery -> {

                    for (DocumentSnapshot userDoc : userQuery) {

                        String baId = userDoc.getId();
                        String name = userDoc.getString("userName");
                        String photo = userDoc.getString("profileImageUrl");
                        long minPrice = userDoc.contains("minPrice")
                                ? userDoc.getLong("minPrice")
                                : 0;

                        Map<String, Object> location =
                                (Map<String, Object>) userDoc.get("location");


                        Double latitude = userDoc.getDouble("location.latitude");
                        Double longitude = userDoc.getDouble("location.longitude");

                        if (latitude == null || longitude == null) {
                            continue;
                        }

                        float[] results = new float[1];

                        Location.distanceBetween(
                                latUser,
                                lonUser,
                                latitude,
                                longitude,
                                results
                        );

                        double jarakKm = results[0] / 1000;

                        db.collection("service")
                                .whereEqualTo("baid", baId)
                                .whereEqualTo("serviceCategory", category)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(serviceQuery -> {

                                    if (!serviceQuery.isEmpty()) {

                                        BAprofile item = new BAprofile(
                                                baId,
                                                name,
                                                photo,
                                                minPrice,
                                                jarakKm
                                        );

                                        BAlist.add(item);
                                        srAdapter.notifyDataSetChanged();
                                        pb.setVisibility(View.GONE);
                                        srRV.setVisibility(View.VISIBLE);

                                    }
                                });
                    }
                });
    }

    @Override
    public void getServiceDetail(int position) {
        BAprofile selectedBA = BAlist.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("baId", selectedBA.getBAid());
        bundle.putString("serviceName", selectedBA.getBAname());
        bundle.putString("servicePhoto", selectedBA.getPhotoUrl());
        bundle.putLong("minPrice", selectedBA.getMinPrice());
        bundle.putString("selectedCategory", selectedCategory);
        bundle.putDouble("distance", selectedBA.getDistance());

        ServiceDetailFragment serviceDetailFragment = new ServiceDetailFragment();
        serviceDetailFragment.setArguments(bundle);
        FrameLayout main = findViewById(R.id.main);
        getSupportFragmentManager().beginTransaction()
                .replace(main.getId(), serviceDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
