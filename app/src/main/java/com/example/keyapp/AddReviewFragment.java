package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyapp.Helper.NotificationHelper;
import com.example.keyapp.Models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddReviewFragment extends Fragment {

    Button addReviewSubmitBtn;
    TextView addReviewErrorTV;
    EditText addReviewComment;
    RatingBar addReviewStarBar;
    ImageButton addreview_BackBtn;
    private int role;
    private Order order;
    private List<Order> orderList = new ArrayList<>();
    private String id, uid, custName, service, location, date, time, status, baid;
    private double price;
    float userRating;

    FirebaseFirestore db;
    FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_review, container, false);
        addReviewSubmitBtn = rootView.findViewById(R.id.addReviewSubmitBtn);
        addReviewComment = rootView.findViewById(R.id.addReviewComment);
        addReviewStarBar = rootView.findViewById(R.id.addReviewStar);
        addreview_BackBtn = rootView.findViewById(R.id.addreview_BackBtn);
        addReviewErrorTV = rootView.findViewById(R.id.addReviewErrorTV);
        addReviewErrorTV.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        if(getArguments() != null){
            order = (Order) getArguments().getSerializable("order");
            if(order != null){
                orderList.add(order);
                id = order.getOrderId();
                uid = order.getUserId();
                custName = order.getUsername();
                price = order.getServicePrice();
                date = order.getSelectedDate();
                location = order.getLocation();
                time = order.getSelectedTime();
                service = order.getServiceName();
                status = order.getStatus();
                baid = order.getBAid();
            }
            role = getArguments().getInt("role");
        }

        addreview_BackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        addReviewSubmitBtn.setOnClickListener(v -> {

        userRating = addReviewStarBar.getRating();
        String reviewComment = addReviewComment.getText().toString().trim();
            if(userRating == 0){
                addReviewErrorTV.setText("Please select rating");
                addReviewErrorTV.setVisibility(View.VISIBLE);
                return;
            }

            Map<String,Object> reviewData = new HashMap<>();
            reviewData.put("orderId", id);
            reviewData.put("rating", userRating);
            reviewData.put("comment", reviewComment.isEmpty() ? "" : reviewComment);
            reviewData.put("userId", uid);
            reviewData.put("userName", custName);
            reviewData.put("BAid",baid);
            reviewData.put("timestamp", System.currentTimeMillis());
            db.collection("reviews")
                    .document(id)
                    .set(reviewData)
                    .addOnSuccessListener(aVoid -> {
                        addReviewErrorTV.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_SHORT).show();
                        NotificationHelper.saveNotificationToFirestore(
                                baid,
                                "BA",
                                "New Review",
                                "You received a new review from a customer.",
                                "new_review",
                                id
                        );

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                    });
            Log.d("Review", "review isi" + userRating +", " +reviewComment+ ", order id" + id);
        });

        db.collection("reviews")
                .document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        float rating = doc.getDouble("rating").floatValue();
                        String comment = doc.getString("comment");

                        addReviewStarBar.setRating(rating);
                        addReviewComment.setText(comment);
                    }
                });

        return rootView;
    }

}