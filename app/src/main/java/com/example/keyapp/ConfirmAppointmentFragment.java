package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;


public class ConfirmAppointmentFragment extends Fragment {

    private ImageButton CA_backBtn;
    private Button CA_orderBtn;
    private RecyclerView CA_itemRV;
    private TextInputEditText CA_nameTV, CA_locTV;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_appointment, container, false);
        CA_backBtn = rootView.findViewById(R.id.CA_backBtn);
        CA_orderBtn = rootView.findViewById(R.id.CA_orderBtn);
        CA_itemRV = rootView.findViewById(R.id.CA_itemRV);
        CA_nameTV = rootView.findViewById(R.id.CA_nameTV);
        CA_locTV = rootView.findViewById(R.id.CA_locTV);


        return rootView;
    }




}