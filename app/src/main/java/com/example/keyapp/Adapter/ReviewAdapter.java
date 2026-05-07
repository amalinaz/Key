package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.Review;
import com.example.keyapp.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList = new ArrayList<>();
    private Context ctx;

    public ReviewAdapter(List<Review> reviewList, Context ctx) {
        this.reviewList = reviewList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent,false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.ratingUserNameTV.setText(review.getUserName());
        holder.ratingCommentTV.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());

        long ts = review.getTimestamp();
        java.util.Date date = new java.util.Date(ts);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy, HH.mm");
        String formattedDate = sdf.format(date);

        holder.ratingDateTV.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView ratingUserNameTV, ratingCommentTV, ratingDateTV;
        RatingBar ratingBar;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingUserNameTV = itemView.findViewById(R.id.ratingName);
            ratingCommentTV = itemView.findViewById(R.id.ratingCommentTV);
            ratingDateTV = itemView.findViewById(R.id.ratingDateTV);
        }
    }
}
