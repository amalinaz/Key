package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.Review;
import com.example.keyapp.R;

import java.util.ArrayList;
import java.util.List;

public class SDRatingAdapter extends RecyclerView.Adapter<SDRatingAdapter.SDRatingViewHolder> {

    List<Review> reviewList = new ArrayList<>();
    Context ctx;

    private SDRatingAdapter.OnItemCLickListener listener;
    public void setOnItemClickListener(SDRatingAdapter.OnItemCLickListener listener) {
        this.listener = listener;
    }

    public SDRatingAdapter(List<Review> reviewList, Context ctx) {
        this.reviewList = reviewList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public SDRatingAdapter.SDRatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rating_snippet, parent, false);
        return new SDRatingAdapter.SDRatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SDRatingAdapter.SDRatingViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.ratingSnippet_NameTV.setText(review.getUserName());
        holder.ratingSnippet_CommentTV.setText(review.getComment());
        holder.ratingSnippet_RB.setRating(review.getRating());
        holder.ratingSnippet_ViewAllBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRatingItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setReviewList(List<Review> list) {
        this.reviewList.clear();
        this.reviewList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemCLickListener {
        void onRatingItemClick(int position);
    }

    public class SDRatingViewHolder extends RecyclerView.ViewHolder {
        TextView ratingSnippet_NameTV, ratingSnippet_CommentTV;
        RatingBar ratingSnippet_RB;
        Button ratingSnippet_ViewAllBtn;
        public SDRatingViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingSnippet_NameTV = itemView.findViewById(R.id.ratingSnippet_NameTV);
            ratingSnippet_CommentTV = itemView.findViewById(R.id.ratingSnippet_CommentTV);
            ratingSnippet_RB = itemView.findViewById(R.id.ratingSnippet_RB);
            ratingSnippet_ViewAllBtn = itemView.findViewById(R.id.ratingSnippet_ViewAllBtn);
        }
    }
}
