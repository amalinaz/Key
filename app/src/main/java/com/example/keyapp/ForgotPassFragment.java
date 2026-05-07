package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassFragment extends Fragment {

    ImageButton fp_backBtn;
    Button fp_sendBtn;
    TextInputLayout fp_emailTIL;
    EditText fp_emailET;
    private String email;
    FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_pass, container, false);
        fp_backBtn = rootView.findViewById(R.id.fp_BackBtn);
        fp_sendBtn = rootView.findViewById(R.id.fp_sendBtn);
        fp_emailTIL = rootView.findViewById(R.id.fp_emailTIL);
        fp_emailET = rootView.findViewById(R.id.fp_email);

        auth = FirebaseAuth.getInstance();

        fp_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();

        });

        if(getArguments()!=null){
            email = getArguments().getString("email");
        }


        fp_emailET.setText(email);

        fp_sendBtn.setOnClickListener(v -> {
            String emailInput = fp_emailET.getText().toString().trim();

            if(emailInput == null){
                fp_emailTIL.setError("Email cannot be null");
            }else {
                fp_emailTIL.setError(null);
            }
                   auth.sendPasswordResetEmail(emailInput)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        });
        return rootView;
    }
}