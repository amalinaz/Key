package com.example.keyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.keyapp.Register.RegisterActivity;
import com.example.keyapp.Register.RegisterBAActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    ImageButton login_backbtn, login_signInBtn2, login_signInBtn, login_signUpBtn2;
    EditText login_email, login_pass;
    private String email, pass;
    TextView login_cpass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, imeInsets.bottom));
            return insets;
        });

        login_backbtn = findViewById(R.id.login_backbtn);
        login_signInBtn2 = findViewById(R.id.login_signInBtn2);
        login_signUpBtn2 = findViewById(R.id.login_signUpBtn2);
        login_signInBtn = findViewById(R.id.login_signInBtn);

        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);

        login_cpass = findViewById(R.id.login_cpass);

        mAuth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();


        login_signInBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                email =  login_email.getText().toString();
                pass =  login_pass.getText().toString();

                if(!validateData(email,pass)){
                    Toast.makeText(LoginActivity.this, "Please fill out this field", Toast.LENGTH_SHORT).show();
                }else{
                    checkUser();
                }
            }
        });

        login_cpass.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("email", login_email.getText().toString());
            ForgotPassFragment fp = new ForgotPassFragment();
            fp.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login, fp).addToBackStack(null)
                    .commit();
        });


        login_signUpBtn2.setOnClickListener(v -> {
            Intent newintent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(newintent);
            finish();
        });

        login_backbtn.setOnClickListener(v -> {
            Intent newintent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(newintent);
            finish();
        });

    }

    private boolean validateData(String email, String pass){
        boolean isValid = true;
        if(email.isEmpty()){
            login_email.setError("Email cannot be empty");
            isValid = false;
        }else{
            login_email.setError(null);
        }

        if(pass.isEmpty()){
            login_pass.setError("Password cannot be empty");
            isValid = false;
        }else{
            login_pass.setError(null);

        }

        return isValid;
    }
    public void checkUser(){
        String email = login_email.getText().toString();
        String pass = login_pass.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        login_pass.setError("Something went wrong. Please Try Again.");
                        login_pass.requestFocus();
                        return;
                    }

                    String uid = firebaseUser.getUid();


                    db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    Long roleLong = doc.getLong("userRole");
                                    int role = roleLong != null ? roleLong.intValue() : 1;
                                    if (role == 1) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (role == 2) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                } else {
                                    login_email.setError("Data user tidak ditemukan");
                                    login_email.requestFocus();
                                }
                            })
                            .addOnFailureListener(e -> {
                                login_email.setError("Gagal ambil data user");
                                login_email.requestFocus();
                            });

                })
                .addOnFailureListener(e -> {
                    login_pass.setError("Email atau password salah");
                    login_pass.requestFocus();
                });



    }
}