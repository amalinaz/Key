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

public class RegisterActivity extends AppCompatActivity {

    ImageButton reg_backbtn, reg_signInBtn2, reg_signUpBtn, reg_signUpBtn2;
    EditText reg_email, reg_username, reg_pass;
    TextView reg_BAregBtn;

    FirebaseDatabase database;
    DatabaseReference refDatabase, counterRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        reg_backbtn = findViewById(R.id.reg_backbtn);
        reg_signInBtn2 = findViewById(R.id.reg_signInBtn2);
        reg_signUpBtn2 = findViewById(R.id.reg_signUpBtn2);
        reg_signUpBtn = findViewById(R.id.reg_signUpBtn);

        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_pass);
        reg_username = findViewById(R.id.reg_username);

        reg_BAregBtn = findViewById(R.id.reg_BAregbtn);

        db = FirebaseFirestore.getInstance();


        reg_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reg_signInBtn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reg_signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                refDatabase = database.getReference("users");
                counterRef = database.getReference("userCounter");

                String username = String.valueOf(reg_username.getText());
                String email = String.valueOf(reg_email.getText());
                String pass = String.valueOf(reg_pass.getText());

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
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
                                    User newUser = new User(userId, username, email, hashedPassword, 1, null);
                                    counterRef.setValue(userCounter + 1);

                                    saveUserDataToFirestore(uid, newUser);
                                    refDatabase.child(uid).child("userId").setValue(userId);

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error fetching counter", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Error fetching userCounter", taskcounter.getException());
                                }
                            });

                        }else{
                            Log.e("Registration Error", "Error: " + task.getException().getMessage());
                            Toast.makeText(RegisterActivity.this, "Registration failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });

            }
        });

        reg_BAregBtn.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(RegisterActivity.this, RegisterBAActivity.class);
                startActivity(intent);
                finish();
            }catch (Exception e){
                Log.e("RegisterError", "Error: " + e.getMessage());
                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }



    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    private void saveUserDataToFirestore(String uid, User user) {
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "User data saved to Firestore", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error saving user: ", e);
                });
    }


}