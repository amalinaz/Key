package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Adapter.SDPortofolioAdapter;
import com.example.keyapp.Adapter.SDRatingAdapter;
import com.example.keyapp.Chat.ChatFragment;
import com.example.keyapp.Models.Review;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class ServiceDetailFragment extends Fragment implements SDPortofolioAdapter.OnItemCLickListener, SDRatingAdapter.OnItemCLickListener{

    FirebaseFirestore db;
    FirebaseAuth auth;
    ImageButton sd_BackBtn, sd_chatBtn;
    ImageView sd_profileIV;
    TextView sd_categoryTV, sd_nameTV, sd_categoryTypeTV, sd_ratingTV, sd_distTV,sd_noreviewTV, sd_locTypeTV;
    MaterialButton sd_bookBtn;
    RecyclerView sd_portofolioRV, sd_reviewRV;
    SDPortofolioAdapter portoAdapter;
    SDRatingAdapter ratingAdapter;
    Button sd_viewAllReviewBtn;
    private String BAid, serviceName;

    private List<String> imageList = new ArrayList<>();
    private List<Review> reviewList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=  inflater.inflate(R.layout.fragment_service_detail, container, false);
        sd_BackBtn = rootView.findViewById(R.id.cl_BackBtn);
        sd_categoryTV = rootView.findViewById(R.id.cl_title);
        sd_nameTV = rootView.findViewById(R.id.sd_NameTV);
        sd_categoryTypeTV = rootView.findViewById(R.id.sd_categoryTypeTV);
        sd_ratingTV = rootView.findViewById(R.id.sd_ratingTV);
        sd_distTV = rootView.findViewById(R.id.sd_distTV);
        sd_portofolioRV = rootView.findViewById(R.id.sd_portofolioRV);
        sd_reviewRV = rootView.findViewById(R.id.sd_reviewRV);
        sd_profileIV = rootView.findViewById(R.id.sd_profileIV);
        sd_bookBtn = rootView.findViewById(R.id.sd_bookBtn);
        sd_noreviewTV = rootView.findViewById(R.id.sd_noreviewTV);
        sd_chatBtn = rootView.findViewById(R.id.sd_chatBtn);
        sd_locTypeTV = rootView.findViewById(R.id.sd_locTypeTV);

        sd_noreviewTV.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();
        sd_portofolioRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        portoAdapter = new SDPortofolioAdapter(imageList,getContext());
        portoAdapter.setOnItemClickListener(this);
        sd_portofolioRV.setAdapter(portoAdapter);

        ratingAdapter = new SDRatingAdapter(reviewList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        sd_reviewRV.setLayoutManager(layoutManager);
        ratingAdapter.setOnItemClickListener(this);
        sd_reviewRV.setAdapter(ratingAdapter);


        sd_BackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            BAid = getArguments().getString("baId");
            serviceName = getArguments().getString("serviceName");
            String servicePhoto = getArguments().getString("servicePhoto");
            long minPrice = getArguments().getLong("minPrice");
            String selectedCategory = getArguments().getString("selectedCategory");
            String locType = getArguments().getString("locType");
            double dist = getArguments().getDouble("distance");

            sd_nameTV.setText(serviceName);
            sd_categoryTV.setText(selectedCategory);
            String category = "";
            if(selectedCategory.equals("Make Up Artist")){
                category = "Make Up";
            }else if(selectedCategory.equals("Nail Artist")){
                category = "Nail Art";
            }else if(selectedCategory.equals("Eyelash Artist")){
                category = "Eyelash";
            }

            String locationType ="";
            if(locType.equals("homevisit")){
                locationType = "Home Visit";
            }else if(locType.equals("studiovisit")){
                locationType = "On-site Studio / Studio Visit";
            }

            sd_locTypeTV.setText(locationType);
            sd_categoryTypeTV.setText(category);
            sd_distTV.setText(String.format("%.2f km", dist));

            Glide.with(getContext()).load(servicePhoto).into(sd_profileIV);
        }


        fetchData();
        sd_bookBtn.setOnClickListener(v -> {
            Fragment bookAppointment = new PricelistFragment();
            FrameLayout sd = rootView.findViewById(R.id.serviceDetailLayout);
            Bundle bundle = new Bundle();
            bundle.putString("BAid", BAid);
            bookAppointment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(bookAppointment, true);
        });

        sd_chatBtn.setOnClickListener(v -> {
            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("receiverId", BAid);
            bundle.putString("receiverName", serviceName);
            chatFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).openFragment(chatFragment, true);
        });

        return rootView;
    }

    private void fetchData(){
        fetchPortofolioSnippet();
        fetchRatingSnippet();
    }

    private void fetchPortofolioSnippet() {
        db.collection("portofolio")
                .document(BAid)
                .collection("photos")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                   imageList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String imageUrl = doc.getString("imageUrl");
                        if (imageUrl != null) {
                            imageList.add(imageUrl);
                        }
                    }

                    portoAdapter.notifyDataSetChanged();
                });
    }

    private void fetchRatingSnippet(){
        db.collection("reviews")
                .whereEqualTo("BAid", BAid)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        Review r = doc.toObject(Review.class);
                        if (r != null) {
                            List<Review> oneReviewList = new ArrayList<>();
                            oneReviewList.add(r);
                            sd_ratingTV.setText("⭐"+ String.valueOf(r.getRating()));
                            ratingAdapter.setReviewList(oneReviewList);
                        }
                    }else{
                        Review defaultReview = new Review();
                        defaultReview.setRating(5);
                        sd_ratingTV.setText(String.valueOf("⭐"+defaultReview.getRating()));
                        sd_noreviewTV.setText("No review yet");
                        sd_noreviewTV.setVisibility(View.VISIBLE);
                        sd_reviewRV.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("ServiceDetail", "Gagal ambil review", e));
    }


    @Override
    public void onItemClick(int position) {

        PortofolioFragment portofolio = new PortofolioFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("BAid", BAid);
        bundle.putInt("role", 1);
        portofolio.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(portofolio, true);

    }

    @Override
    public void onRatingItemClick(int position) {
        ReviewFragment review = new ReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("BAid", BAid);
        bundle.putInt("role", 1);
        review.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(review, true);
    }
}