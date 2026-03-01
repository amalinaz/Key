package com.example.keyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.keyapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

public class RegisterBAActivity extends AppCompatActivity {

    ImageButton regBA_backbtn, regBA_signInBtn2, regBA_signUpBtn, regBA_signUpBtn2;
    EditText regBA_email, regBA_username, regBA_pass;
    TextView regBA_regUserBtn;

    FirebaseDatabase database;
    DatabaseReference refDatabase, counterRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_baactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        regBA_backbtn = findViewById(R.id.regBA_backbtn);
        regBA_signInBtn2 = findViewById(R.id.regBA_signInBtn2);
        regBA_signUpBtn2 = findViewById(R.id.regBA_signUpBtn2);
        regBA_signUpBtn = findViewById(R.id.regBA_signUpBtn);

        regBA_email = findViewById(R.id.regBA_email);
        regBA_pass = findViewById(R.id.regBA_pass);
        regBA_username = findViewById(R.id.regBA_username);

        regBA_regUserBtn = findViewById(R.id.regBA_regUserbtn);

        regBA_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterBAActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        regBA_signInBtn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterBAActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        regBA_signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                refDatabase = database.getReference("users");
                counterRef = database.getReference("userCounter");

                String username = String.valueOf(regBA_username.getText());
                String email = String.valueOf(regBA_email.getText());
                String pass = String.valueOf(regBA_pass.getText());

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterBAActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterBAActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(RegisterBAActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            counterRef.get().addOnCompleteListener(taskcounter -> {
                                if (taskcounter.isSuccessful()) {
                                    Integer userCounter = taskcounter.getResult().getValue(Integer.class);
                                    if (userCounter == null) {
                                        userCounter = 1;
                                        counterRef.setValue(userCounter);
                                        Log.d("Firebase", "Setting initial userCounter to 1");
                                    }

                                    String userId = "U" + String.format("%03d", userCounter+1);
                                    String uid = mAuth.getCurrentUser().getUid();
                                    String hashedPassword = hashPassword(pass);
                                    User newUser = new User(userId, username, email, hashedPassword, 2 , null);
                                    counterRef.setValue(userCounter + 1);

                                    saveUserDataToFirestore(uid, newUser);
                                    refDatabase.child(uid).child("userId").setValue(userId);

                                } else {
                                    Toast.makeText(RegisterBAActivity.this, "Error fetching counter", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Error fetching userCounter", taskcounter.getException());
                                }
                            });

                        }else{
                            Log.e("Registration Error", "Error: " + task.getException().getMessage());
                            Toast.makeText(RegisterBAActivity.this, "Registration failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });

                regBA_regUserBtn.setOnClickListener(views -> {
                    try {
                        Intent intent = new Intent(RegisterBAActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("Register", "Navigating to Register BA");
                    }catch (Exception e){
                        Log.e("RegisterBAError", "Error: " + e.getMessage());
                        Toast.makeText(RegisterBAActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    private void saveUserDataToFirestore(String uid, User user) {
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterBAActivity.this, "User data saved to Firestore", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterBAActivity.this, MainActivity2.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterBAActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error saving user: ", e);
                });
    }
}