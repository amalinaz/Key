package com.example.keyapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.keyapp.MenuBA.ServiceMenu.ServiceFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Home2Fragment extends Fragment {

    ImageButton main2_notifbtn, main2_viewServicebtn, main2_PortofolioBtn, main2_RatingBtn, main2_OrderListbtn, main2_historyBtn, main2_mScheduleBtn;
    RecyclerView mainScheduleRV;
    private int role;
    ImageView main2_profile;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_home2, container, false);
        main2_notifbtn = rootview.findViewById(R.id.main2_notifbtn);
        main2_profile = rootview.findViewById(R.id.main2_profileIV);
        main2_viewServicebtn = rootview.findViewById(R.id.ViewServicebtn);
        main2_PortofolioBtn = rootview.findViewById(R.id.PortofolioBtn);
        main2_RatingBtn = rootview.findViewById(R.id.RatingBtn);
        main2_OrderListbtn = rootview.findViewById(R.id.OrderListbtn);
        main2_historyBtn = rootview.findViewById(R.id.historyBtn);
        main2_mScheduleBtn = rootview.findViewById(R.id.mSchedule_btn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String uid = auth.getUid();

        if(getArguments()!= null){
            role = getArguments().getInt("role");
        }

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        String getImage = doc.getString("profileImageUrl");
                        if(getImage!= null){
                            Glide.with(this)
                                    .load(getImage)
                                    .into(main2_profile);
                        }else{
                            main2_profile.setImageResource(R.drawable.profile_no);
                        }

                    }
                });

        main2_viewServicebtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new ServiceFragment(), true);
        });

        main2_PortofolioBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new PortofolioFragment(), true);
        });

        main2_mScheduleBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new ManageScheduleFragment(), true);
        });
        main2_historyBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new HistoryFragment(), true);
        });

        main2_OrderListbtn.setOnClickListener(v -> {
            OrderListFragment orderListFragment = new OrderListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("role", role);
            orderListFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(orderListFragment, true);
        });
        main2_RatingBtn.setOnClickListener(v -> {
            ReviewFragment reviewFragment = new ReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("role", role);
            reviewFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(reviewFragment, true);
        });

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            main2_profile.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            main2_profile.setOnClickListener(v -> {
                ((MainActivity) requireActivity()).openFragment(new ProfileFragment(), true);
            });
        }

        main2_notifbtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new NotificationFragment(), true);
        });
        return rootview;
    }



}