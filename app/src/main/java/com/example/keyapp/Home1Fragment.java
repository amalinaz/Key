package com.example.keyapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Adapter.ServiceRecAdapter;
import com.example.keyapp.Helper.BAProfileHelper;
import com.example.keyapp.Models.BAprofile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home1Fragment extends Fragment implements ServiceRecAdapter.OnItemCLickListener{
    ImageView main_profile;
    ImageButton MUABtn, NABtn, EyelashBtn, main_notifbtn;
    TextView recommendTV;
    ProgressBar pb;
    FirebaseAuth auth;
    FirebaseFirestore db;
    private ServiceRecAdapter srAdapter;
    RecyclerView srRV;
    List<BAprofile> BAlist = new ArrayList<>();
    private String selectedCategory = "";
    private Map<String, String> locationTypeMap = new HashMap<>();
//    private String locationType;
    private double latUser, lonUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_home1, container, false);

        main_profile = rootview.findViewById(R.id.main_profileIV);
        MUABtn = rootview.findViewById(R.id.MUAbtn);
        NABtn = rootview.findViewById(R.id.NABtn);
        EyelashBtn = rootview.findViewById(R.id.EyelashBtn);
        main_notifbtn = rootview.findViewById(R.id.main_notifbtn);

        recommendTV = rootview.findViewById(R.id.recommendTV);
        pb = rootview.findViewById(R.id.progressBar);


        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            main_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            main_profile.setOnClickListener(v -> {
                ((MainActivity) requireActivity()).openFragment(new ProfileFragment(), true);
            });
        }


        db = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        Map<String, Object> location = (Map<String, Object>) doc.get("location");

                        if (location != null) {
                            latUser = (double) location.get("latitude");
                            lonUser = (double) location.get("longitude");
                        }

                        String getImage = doc.getString("profileImageUrl");
                        if(getImage!= null){
                            Glide.with(this)
                                    .load(getImage)
                                    .into(main_profile);
                        }else{
                            main_profile.setImageResource(R.drawable.profile_no);
                        }

                    }
                });


        srRV = rootview.findViewById(R.id.main_ServiceRV);

        srAdapter = new ServiceRecAdapter(BAlist, getContext());
        srAdapter.setOnItemClickListener(this);
        srRV.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
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
            fetchBAProfiles("Makeup");
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

        main_notifbtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new NotificationFragment(), true);
        });
        return rootview;
    }

    private void fetchBAProfiles(String category) {
        BAlist.clear();
        srAdapter.notifyDataSetChanged();

        Map<String, List<DocumentSnapshot>> serviceMap = new HashMap<>();
        db.collection("service")
                .get().addOnSuccessListener(serviceQuery -> {
            for(DocumentSnapshot doc : serviceQuery){
                String BAid = doc.getString("baid");
                String Category = doc.getString("serviceCategory");

                if(Category.equals(category)){
                    if (!serviceMap.containsKey(BAid))
                        serviceMap.put(BAid, new ArrayList<>());
                    serviceMap.get(BAid).add(doc);
                }
            }
        });

        db.collection("users")
                .whereEqualTo("userRole", 2)
                .get()
                .addOnSuccessListener(userQuery -> {
                    List<BAprofile> finalList = new ArrayList<>();

                    for (DocumentSnapshot userDoc : userQuery) {
                        String uid = userDoc.getId();
                        String locType = userDoc.getString("locationType");
                        Log.d("Home", "Loctyope pas baca" + locType);
                        locationTypeMap.put(uid, locType);
                        if(serviceMap.containsKey(uid)){
                            Double latitude = userDoc.getDouble("location.latitude");
                            Double longitude = userDoc.getDouble("location.longitude");
                            if (latitude == null || longitude == null) continue;

                            float[] results = new float[1];
                            Location.distanceBetween(latUser, lonUser, latitude, longitude, results);
                            double jarakKm = results[0] / 1000.0;

                            BAprofile ba = new BAprofile(uid,
                                    userDoc.getString("userName"),
                                    userDoc.getString("profileImageUrl"),
                                    userDoc.getLong("minPrice") != null ? userDoc.getLong("minPrice") : 0,
                                    jarakKm,
                                    userDoc.getDouble("avgRating") != null ? userDoc.getDouble("avgRating") : 5,
                                    userDoc.getDouble("experience") != null ? userDoc.getDouble("experience") : 0);
                            finalList.add(ba);
                        }
                    }

                    List<BAprofile> ranked = BAProfileHelper.rankProviders(finalList);

                    BAlist.clear();
                    BAlist.addAll(ranked);
                    srAdapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                    srRV.setVisibility(View.VISIBLE);
                });
        }


    @Override
    public void getServiceDetail(int position) {
        BAprofile selectedBA = BAlist.get(position);

        String locationType = locationTypeMap.get(selectedBA.getBAid());

        Bundle bundle = new Bundle();
        bundle.putString("baId", selectedBA.getBAid());
        bundle.putString("serviceName", selectedBA.getBAname());
        bundle.putString("servicePhoto", selectedBA.getPhotoUrl());
        bundle.putLong("minPrice", selectedBA.getMinPrice());
        bundle.putString("selectedCategory", selectedCategory);
        bundle.putDouble("distance", selectedBA.getDistance());
        bundle.putString("locType", locationType);

        Log.d("Home", "Loctype" +locationType);
        ServiceDetailFragment serviceDetailFragment = new ServiceDetailFragment();
        serviceDetailFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(serviceDetailFragment, true);
    }
}