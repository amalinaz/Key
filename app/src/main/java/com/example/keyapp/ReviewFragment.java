package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.keyapp.Adapter.ReviewAdapter;
import com.example.keyapp.Models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ReviewFragment extends Fragment {

    private ReviewAdapter adapter;
    ImageButton review_backBtn;
    RecyclerView review_RV;
    TextView review_avgRatingTV, review_totalRatingTV, review_total5TV, review_total4TV, review_total3TV, review_total2TV, review_total1TV;
    ProgressBar review_pb5, review_pb4, review_pb3, review_pb2, review_pb1;
    RatingBar review_avgRatingRB, review_rate5, review_rate4, review_rate3, review_rate2, review_rate1;
    private int role;
    private String baid;
    private  List<Review> reviewList = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        review_backBtn = rootView.findViewById(R.id.review_BackBtn);
        review_RV = rootView.findViewById(R.id.review_RV);

        review_avgRatingTV = rootView.findViewById(R.id.review_avgRatingTV);
        review_totalRatingTV = rootView.findViewById(R.id.review_totalRatingTV);
        review_total5TV = rootView.findViewById(R.id.review_total5TV);
        review_total4TV = rootView.findViewById(R.id.review_total4TV);
        review_total3TV = rootView.findViewById(R.id.review_total3TV);
        review_total2TV = rootView.findViewById(R.id.review_total2TV);
        review_total1TV = rootView.findViewById(R.id.review_total1TV);

        review_pb5 = rootView.findViewById(R.id.review_pb5);
        review_pb4 = rootView.findViewById(R.id.review_pb4);
        review_pb3 = rootView.findViewById(R.id.review_pb3);
        review_pb2 = rootView.findViewById(R.id.review_pb2);
        review_pb1 = rootView.findViewById(R.id.review_pb1);

        review_avgRatingRB = rootView.findViewById(R.id.review_avgRatingRB);
        review_rate5 = rootView.findViewById(R.id.review_rate5);
        review_rate4 = rootView.findViewById(R.id.review_rate4);
        review_rate3 = rootView.findViewById(R.id.review_rate3);
        review_rate2 = rootView.findViewById(R.id.review_rate2);
        review_rate1 = rootView.findViewById(R.id.review_rate1);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if(getArguments()!=null){
            role = getArguments().getInt("role");
        }

        if(role == 1){
            if(getArguments() != null){
                baid = getArguments().getString("BAid");
            }
        } else if(role == 2){
            baid = auth.getCurrentUser().getUid();
        }

        review_RV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(reviewList, getContext());
        review_RV.setAdapter(adapter);


        getReviewData();
        review_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return rootView;
    }

    private void getReviewData(){
        db.collection("reviews")
                .whereEqualTo("BAid", baid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    reviewList.clear();
                    for(DocumentSnapshot doc : querySnapshot){
                        Review r = doc.toObject(Review.class);
                        reviewList.add(r);
                    }
                    adapter.notifyDataSetChanged();
                    calculateRatingStats(reviewList, baid);
                });

    }
    private void calculateRatingStats(List<Review> reviewList, String baid){
        int count5 = 0, count4 = 0, count3 = 0, count2 = 0, count1 = 0;
        float sumRating = 0;

        for(Review r : reviewList){
            sumRating += r.getRating();

            switch((int) r.getRating()){
                case 5: count5++; break;
                case 4: count4++; break;
                case 3: count3++; break;
                case 2: count2++; break;
                case 1: count1++; break;
            }
        }

        float average = reviewList.size() > 0 ? sumRating / reviewList.size() : 0;
        int totalReviews = count5 + count4 + count3 + count2 + count1;

        review_totalRatingTV.setText("("+totalReviews+")");

        int percent5 = totalReviews > 0 ? (count5 * 100 / totalReviews) : 0;
        int percent4 = totalReviews > 0 ? (count4 * 100 / totalReviews) : 0;
        int percent3 = totalReviews > 0 ? (count3 * 100 / totalReviews) : 0;
        int percent2 = totalReviews > 0 ? (count2 * 100 / totalReviews) : 0;
        int percent1 = totalReviews > 0 ? (count1 * 100 / totalReviews) : 0;

        review_pb5.setProgress(percent5);
        review_pb4.setProgress(percent4);
        review_pb3.setProgress(percent3);
        review_pb2.setProgress(percent2);
        review_pb1.setProgress(percent1);

        review_total5TV.setText(String.valueOf(count5));
        review_total4TV.setText(String.valueOf(count4));
        review_total3TV.setText(String.valueOf(count3));
        review_total2TV.setText(String.valueOf(count2));
        review_total1TV.setText(String.valueOf(count1));

        review_avgRatingRB.setRating(average);
        review_avgRatingTV.setText(String.format(Locale.getDefault(), "%.1f", average));

        db.collection("users")
                .document(baid)
                .update("avgRating", average)
                .addOnSuccessListener(aVoid -> Log.d("ReviewFragment", "Average rating saved: " + average))
                .addOnFailureListener(e -> Log.e("ReviewFragment", "Failed to save average rating", e));
    }
}