package com.example.keyapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.keyapp.Chat.ViewChatListFragment;
import com.example.keyapp.Schedule.ScheduleFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity{

    FirebaseAuth auth;
    FirebaseFirestore db;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView navView;
    private int currentrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,  Math.max(systemBars.bottom, imeInsets.bottom));
            return insets;
        });


        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if(prefs.getBoolean("isFirstLaunch", true)){
            startActivity(new Intent(this, GetStartedActivity.class));
            finish();
            return;
        }

        boolean showPopup = getIntent().getBooleanExtra("showCompleteProfilePopup", false);
        if(showPopup){
            showCompleteProfileDialog();
        }
        
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }


        String uid = currentUser.getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Bundle bundle = new Bundle();
                        Long roleLong = doc.getLong("userRole");
                        int role = roleLong != null ? roleLong.intValue() : 1;
                        currentrole = role;
                        if (role == 2) {
                            bundle.putInt("role", currentrole);
                            Home2Fragment home2Fragment = new Home2Fragment();
                            home2Fragment.setArguments(bundle);
                            openFragment(home2Fragment, true);
                        } else if(role == 1) {
                            bundle.putInt("role", currentrole);
                            Home1Fragment home1Fragment = new Home1Fragment();
                            openFragment(home1Fragment,true);
                        }
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

        bottomAppBar = findViewById(R.id.bottomAppBar);
        navView = findViewById(R.id.navview);
        int homeId = getResources().getIdentifier("home", "id", getPackageName());
        int scheduleId = getResources().getIdentifier("schedule", "id", getPackageName());
        int searchId = getResources().getIdentifier("search", "id", getPackageName());
        int chatId = getResources().getIdentifier("chat", "id", getPackageName());
        int profileId = getResources().getIdentifier("user", "id", getPackageName());

        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            bundle.putInt("role", currentrole);
            int itemId = item.getItemId();

            if (itemId == homeId) {
                if(currentrole == 1){
                    selectedFragment = new Home1Fragment();
                }else if(currentrole == 2){
                    selectedFragment =  new Home2Fragment();
                }
            } else if (itemId == scheduleId) {
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                scheduleFragment.setArguments(bundle);
                selectedFragment = scheduleFragment;
            }else if (itemId == searchId){
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                selectedFragment = searchFragment;
            }else if (itemId == chatId){
                selectedFragment = new ViewChatListFragment();
            }else if (itemId == profileId){
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        saveFcmToken();
    }

    private void showCompleteProfileDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.layout_alert_dialog, null);
        dialog.setView(view);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        dialog.setCancelable(false);

        TextView titleTV = view.findViewById(R.id.dialog_title);
        TextView messageTV = view.findViewById(R.id.dialog_message);
        Button laterBtn = view.findViewById(R.id.dialog_btnA);
        Button completeBtn = view.findViewById(R.id.dialog_btnB);

        titleTV.setText("Complete Your Profile");
        messageTV.setText("To get the best experience, please complete your profile.");
        laterBtn.setText("Later");
        completeBtn.setText("Complete Profile");

        completeBtn.setOnClickListener(v -> {
            loadProfileFragment();
            dialog.dismiss();
        });

        laterBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void loadProfileFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment())
                .commit();
    }

    public void showBottomBar() {
        bottomAppBar.setVisibility(View.VISIBLE);
        navView.setVisibility(View.VISIBLE);
    }

    public void hideBottomBar() {
        bottomAppBar.setVisibility(View.GONE);
        navView.setVisibility(View.GONE);
    }

    public void openFragment(Fragment fragment, boolean showBar) {
        if (showBar) {
            showBottomBar();
        } else {
            hideBottomBar();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void saveFcmToken() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .update("fcmToken", token);
                   Log.d("fcm", "token : "+token);
                });
    }
}
