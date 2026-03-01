package com.example.keyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.MenuBA.ServiceMenu.ServiceFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {


    ImageButton main2_notifbtn, main2_viewServicebtn, main2_PortofolioBtn, main2_RatingBtn, main2_OrderListbtn, main2_historyBtn, main2_mScheduleBtn;
    RecyclerView mainScheduleRV;

    ImageView main2_profile;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        main2_notifbtn = findViewById(R.id.main2_notifbtn);
        main2_profile = findViewById(R.id.main2_profileIV);
        main2_viewServicebtn = findViewById(R.id.ViewServicebtn);
        main2_PortofolioBtn = findViewById(R.id.PortofolioBtn);
        main2_RatingBtn = findViewById(R.id.RatingBtn);
        main2_OrderListbtn = findViewById(R.id.OrderListbtn);
        main2_historyBtn = findViewById(R.id.historyBtn);
        main2_mScheduleBtn = findViewById(R.id.mSchedule_btn);


        main2_viewServicebtn.setOnClickListener(v -> {
            Log.d("MainActivity2", "View Service Button clicked");
            showViewService();
        });

        main2_PortofolioBtn.setOnClickListener(v -> {
            Log.d("MainActivity2", "View Portofolio clicked");
            showViewPortofolio();
        });

        main2_mScheduleBtn.setOnClickListener(v -> {
            Log.d("MainActivity2", "Manage Schedule clicked");
            showManageSchedule();
        });


        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            main2_profile.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity2.this, LoginActivity.class));
                    finish();
                }
            });
        }else{
            main2_profile.setOnClickListener(v -> {
                Log.d("MainActivity2", "Profile clicked");
                showProfile();
            });
        }

    }

    private void showViewService(){
        Fragment serviceFragment = new ServiceFragment();
        Log.d("MainActivity2", "View Service Button called");
        FrameLayout main = findViewById(R.id.main2);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main.getId(), serviceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showViewPortofolio(){
        Fragment portofolio = new PortofolioFragment();
        Log.d("MainActivity2", "Portofolio Button called");
        FrameLayout main = findViewById(R.id.main2);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main.getId(), portofolio);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showManageSchedule(){
        Fragment manageSchedule = new ManageScheduleFragment();
        Log.d("MainActivity2", "Portofolio Button called");
        FrameLayout main = findViewById(R.id.main2);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main.getId(), manageSchedule);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showProfile(){
        Fragment profileFragment = new ProfileFragment();
        Log.d("MainActivity2", "View Service Button called");
        FrameLayout main = findViewById(R.id.main2);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main.getId(), profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}