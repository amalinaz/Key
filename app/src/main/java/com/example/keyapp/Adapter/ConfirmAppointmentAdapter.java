package com.example.keyapp.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConfirmAppointmentAdapter extends RecyclerView.Adapter<ConfirmAppointmentAdapter.ConfirmAppointmentViewHolder> {

    @NonNull
    @Override
    public ConfirmAppointmentAdapter.ConfirmAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmAppointmentAdapter.ConfirmAppointmentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ConfirmAppointmentViewHolder extends RecyclerView.ViewHolder {
        public ConfirmAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
