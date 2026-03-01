package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.keyapp.Adapter.SDPortofolioAdapter;
import com.example.keyapp.MenuBA.ServiceMenu.AddServiceFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ServiceDetailFragment extends Fragment implements SDPortofolioAdapter.OnItemCLickListener{

    FirebaseFirestore db;
    FirebaseAuth auth;
    ImageButton sd_BackBtn;
    ImageView sd_profileIV;
    TextView sd_categoryTV, sd_nameTV, sd_categoryTypeTV, sd_ratingTV, sd_distTV;
    MaterialButton sd_bookBtn;
    RecyclerView sd_portofolioRV, sd_reviewRV;
    SDPortofolioAdapter portoAdapter;
    private String BAid;
    private List<String> imageList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=  inflater.inflate(R.layout.fragment_service_detail, container, false);
        sd_BackBtn = rootView.findViewById(R.id.sd_BackBtn);
        sd_categoryTV = rootView.findViewById(R.id.sd_categoryTV);
        sd_nameTV = rootView.findViewById(R.id.sd_NameTV);
        sd_categoryTypeTV = rootView.findViewById(R.id.sd_categoryTypeTV);
        sd_ratingTV = rootView.findViewById(R.id.sd_ratingTV);
        sd_distTV = rootView.findViewById(R.id.sd_distTV);
        sd_portofolioRV = rootView.findViewById(R.id.sd_portofolioRV);
        sd_reviewRV = rootView.findViewById(R.id.sd_reviewRV);
        sd_profileIV = rootView.findViewById(R.id.sd_profileIV);
        sd_bookBtn = rootView.findViewById(R.id.sd_bookBtn);

        db = FirebaseFirestore.getInstance();

        sd_portofolioRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        portoAdapter = new SDPortofolioAdapter(imageList,getContext());
        portoAdapter.setOnItemClickListener(this);
        sd_portofolioRV.setAdapter(portoAdapter);

        sd_BackBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            BAid = getArguments().getString("baId");
            String serviceName = getArguments().getString("serviceName");
            String servicePhoto = getArguments().getString("servicePhoto");
            long minPrice = getArguments().getLong("minPrice");
            String selectedCategory = getArguments().getString("selectedCategory");
            double dist = getArguments().getDouble("distance");

            sd_nameTV.setText(serviceName);
            sd_categoryTV.setText(selectedCategory);
            String category = "";
            if(selectedCategory.equals("Make Up Artist")){
                category = "Make Up";
            }else if(selectedCategory.equals("Nail Artist")){
                category = "Nail Art";
            }else if(selectedCategory.equals("Eyelash Artist")){
                category = "Eyelash";
            }
            sd_categoryTypeTV.setText(category);
            sd_ratingTV.setText("blm");
            sd_distTV.setText(String.format("%.2f km", dist));

            Glide.with(getContext()).load(servicePhoto).into(sd_profileIV);
        }
        fetchPortofolioSnippet();

        sd_bookBtn.setOnClickListener(v -> {
            Fragment bookAppointment = new BookAppointmentFragment();
            FrameLayout sd = rootView.findViewById(R.id.serviceDetailLayout);
            Bundle bundle = new Bundle();
            bundle.putString("BAid", BAid);
            Log.d("Pricelist", "Data fetched: " + BAid);
            bookAppointment.setArguments(bundle);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(sd.getId(), bookAppointment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return rootView;
    }

    private void fetchPortofolioSnippet() {
        db.collection("portofolio")
                .document(BAid)
                .collection("photos")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                   imageList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String imageUrl = doc.getString("imageUrl");
                        if (imageUrl != null) {
                            imageList.add(imageUrl);
                        }
                    }

                    portoAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onItemClick(int position) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        PortofolioFragment portofolio = new PortofolioFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("BAid", BAid);
        Log.d("Porto", "Data fetched: " + BAid);
        bundle.putInt("role", 1);
        portofolio.setArguments(bundle);
        transaction.replace(R.id.serviceDetailLayout, portofolio);
        transaction.addToBackStack(null); 
        transaction.commit();
    }
}