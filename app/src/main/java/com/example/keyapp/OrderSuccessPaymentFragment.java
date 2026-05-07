package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class OrderSuccessPaymentFragment extends Fragment {

    Button success_backBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_success_payment, container, false);
        success_backBtn = rootView.findViewById(R.id.success_backBtn);

        success_backBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFragment(new Home1Fragment(), true);
        });

        return  rootView;
    }
}