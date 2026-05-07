package com.example.keyapp;

import android.location.Location;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;

import com.example.keyapp.Adapter.OrderListAdapter;
import com.example.keyapp.Adapter.ServiceRecAdapter;
import com.example.keyapp.Helper.BAProfileHelper;
import com.example.keyapp.Models.BAprofile;
import com.example.keyapp.Models.FilterOptions;
import com.example.keyapp.Models.Order;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment implements ServiceRecAdapter.OnItemCLickListener, OrderListAdapter.OnItemClickListener {

    ImageButton search_backBtn;
    Button search_applyFilterBtn;
    Chip search_filterchip, search_MUAchip, search_NAChip, search_EyelashChip;
    ChipGroup search_chipGroup;
    RatingBar search_ratingFilter;
    SeekBar search_priceFilter;
    TextInputEditText search_textInputET;
    TextInputLayout search_textInputLayout;
    ConstraintLayout filterContainer;
    ProgressBar search_pb;
    RecyclerView search_RV;

    FirebaseAuth auth;
    FirebaseFirestore db;

    private int role;
    private String BAid;
    private float rating;
    private double latUser, lonUser;
    private FilterOptions currentFilterOptions = new FilterOptions();
    private Map<String, Set<String>> serviceCategoryMap = new HashMap<>();
    private Map<String, Set<String>> serviceSearchMap = new HashMap<>();
    private List<BAprofile> allBAprofiles = new ArrayList<>();
    private List<Order> allOrders = new ArrayList<>();

    ServiceRecAdapter adapter;
    OrderListAdapter adapterBA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_search, container, false);
        search_backBtn = rootview.findViewById(R.id.search_backBtn);
        search_applyFilterBtn = rootview.findViewById(R.id.search_applyFilterBtn);
        search_filterchip = rootview.findViewById(R.id.search_filterChip);
        search_MUAchip = rootview.findViewById(R.id.search_MUAchip);
        search_NAChip = rootview.findViewById(R.id.search_NAchip);
        search_EyelashChip = rootview.findViewById(R.id.search_EyelashChip);
        search_ratingFilter = rootview.findViewById(R.id.search_ratingFilter);
        search_priceFilter = rootview.findViewById(R.id.search_priceFilter);
        search_textInputET = rootview.findViewById(R.id.search_textInputET);
        search_textInputLayout = rootview.findViewById(R.id.search_searchTextInputLayout);
        filterContainer = rootview.findViewById(R.id.filterContainer);
        search_chipGroup = rootview.findViewById(R.id.search_chipGroup);
        search_RV = rootview.findViewById(R.id.search_itemRV);
        search_pb = rootview.findViewById(R.id.search_pb);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        search_RV.setVisibility(View.GONE);

        search_filterchip.setVisibility(View.GONE);
        filterContainer.setVisibility(View.GONE);
        search_pb.setVisibility(View.GONE);

        String uid = auth.getCurrentUser().getUid();
        Log.d("debug_search","uid" +uid);
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        Map<String, Object> location = (Map<String, Object>) doc.get("location");

                        if (location != null) {
                            latUser = (double) location.get("latitude");
                            lonUser = (double) location.get("longitude");
                        }
                        fetchServiceAndBA();
                    }
                });


        if(getArguments() != null){
            role = getArguments().getInt("role");
        }

        if(role == 1){
            search_priceFilter.setMax(5_000_000);
            search_priceFilter.setProgress(5_000_000);
            fetchServiceAndBA();
            search_filterchip.setVisibility(View.VISIBLE);
            search_filterchip.setOnClickListener(v -> {
                filterContainer.setVisibility(View.VISIBLE);
            });

            search_applyFilterBtn.setOnClickListener(v -> {
                applyFilter();
                search_pb.setVisibility(View.VISIBLE);
            });

            search_textInputLayout.setEndIconOnClickListener(v -> {
                String query = normalize(search_textInputET.getText().toString());
                performSearch(query, currentFilterOptions);

                search_pb.setVisibility(View.VISIBLE);
            });

            search_textInputET.setOnEditorActionListener((v, actionId, event) -> {
                String query =normalize(search_textInputET.getText().toString());
                performSearch(query, currentFilterOptions);
//                search_pb.setVisibility(View.VISIBLE);
                return true;
            });

        }else if(role ==2){
            fetchOrderBA(uid);
            search_textInputLayout.setEndIconOnClickListener(v -> {
                String query = normalize(search_textInputET.getText().toString());
                performSearchRoleBA(query);
            });

            search_textInputET.setOnEditorActionListener((v, actionId, event) -> {
                String query =normalize(search_textInputET.getText().toString());
                performSearchRoleBA(query);
                search_pb.setVisibility(View.VISIBLE);
                return true;
            });
        }

        search_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        return rootview;
    }



    private String normalize(String s){
        if(s == null) return "";
        return s.replaceAll("\\s+", "").replace("\u00A0","").trim().toLowerCase();
    }

    private void fetchOrderBA(String baId){
        db.collection("orders")
                .whereEqualTo("baid", baId)
                .get()
                .addOnSuccessListener(query -> {
                    allOrders.clear();
                    for(DocumentSnapshot doc : query){
                        Order o = doc.toObject(Order.class);
                        o.setOrderId(doc.getString("orderId"));
                        o.setUsername(normalize(doc.getString("username"))) ;
                        o.setLocation(normalize(doc.getString("location")));
                        o.setServiceName(normalize(doc.getString("serviceName")));
                        allOrders.add(o);

                    }
                });
    }


    private void fetchServiceAndBA() {
        db.collection("service").get().addOnSuccessListener(serviceQuery -> {
            serviceCategoryMap.clear();
            serviceSearchMap.clear();
            for (DocumentSnapshot doc : serviceQuery) {
                String BAid = doc.getString("baid");
                if (BAid == null) continue;

                String category = doc.getString("serviceCategory");
                String serviceName = doc.getString("serviceName");

                String normCategory = category != null ? normalize(category) : null;
                String normServiceName = serviceName != null ? normalize(serviceName) : null;

                if (normCategory != null) {
                    serviceCategoryMap.computeIfAbsent(BAid, k -> new HashSet<>()).add(normCategory);
                }

                Set<String> keywords = serviceSearchMap.computeIfAbsent(BAid, k -> new HashSet<>());
                if (normCategory != null) keywords.add(normCategory);
                if (normServiceName != null) keywords.add(normServiceName);
            }
            fetchBA();
        });
    }
    private void fetchBA() {
        db.collection("users").whereEqualTo("userlvl", 2).get().addOnSuccessListener(userQuery -> {
            allBAprofiles.clear();
            for (DocumentSnapshot userDoc : userQuery) {
                String uid = userDoc.getId();
                if (!serviceCategoryMap.containsKey(uid) && !serviceSearchMap.containsKey(uid)) {
                    continue;
                }
                Double latitude = userDoc.getDouble("location.latitude");
                Double longitude = userDoc.getDouble("location.longitude");
                if (latitude == null || longitude == null) continue;

                float[] results = new float[1];
                Location.distanceBetween(latUser, lonUser, latitude, longitude, results);
                double jarakKm = results[0] / 1000.0;

                BAprofile ba = new BAprofile(
                        uid,
                        userDoc.getString("userName"),
                        userDoc.getString("profileImageUrl"),
                        userDoc.getLong("minPrice") != null ? userDoc.getLong("minPrice") : 0,
                        jarakKm,
                        userDoc.getDouble("avgRating") != null ? userDoc.getDouble("avgRating") : 5,
                        userDoc.getDouble("experience") != null ? userDoc.getDouble("experience") : 0
                );
                allBAprofiles.add(ba);
            }
        });
    }
    private void applyFilter() {
        currentFilterOptions.minRating = search_ratingFilter.getRating();
        currentFilterOptions.maxPrice = search_priceFilter.getProgress();
        currentFilterOptions.serviceTypes.clear();

        Chip[] chips = new Chip[]{search_MUAchip, search_NAChip, search_EyelashChip};
        for (Chip chip : chips) {
            if (chip.isChecked()) {
                currentFilterOptions.serviceTypes.add(normalize(chip.getText().toString()));
            }
        }
        filterContainer.setVisibility(View.GONE);
        String query =normalize(search_textInputET.getText().toString());
        performSearch(query, currentFilterOptions);
    }
    private void performSearch(String query, FilterOptions filter) {

        List<BAprofile> filtered = allBAprofiles.stream()
                .filter(p -> p.getRating() >= filter.minRating)
                .filter(p -> p.getMinPrice() >= filter.minPrice && p.getMinPrice() <= filter.maxPrice )
                .filter(p -> filter.serviceTypes.isEmpty() ||
                        !Collections.disjoint(serviceCategoryMap.getOrDefault(p.getBAid(), new HashSet<>()),
                                filter.serviceTypes))
                .filter(p -> {
                    if (query == null || query.isEmpty()) return true;

                    boolean nameMatch = p.getBAname() != null && p.getBAname().toLowerCase().contains(query);
                    boolean distanceMatch = false;
                    boolean serviceCategoryMatch = serviceCategoryMap.getOrDefault(p.getBAid(), new HashSet<>())
                            .stream().anyMatch(s -> s.toLowerCase().contains(query));
                    boolean serviceMatch = serviceSearchMap.getOrDefault(p.getBAid(), new HashSet<>())
                            .stream()
                            .anyMatch(keyword -> normalize(keyword).equals(query));
                    try {
                        double km = Double.parseDouble(query);
                        distanceMatch = p.getDistance() <= km;
                    } catch (NumberFormatException ignored) {}

                    return nameMatch || distanceMatch || serviceMatch || serviceCategoryMatch;
                })
                .collect(Collectors.toList());

        List<BAprofile> ranked = BAProfileHelper.rankProviders(filtered);

        search_RV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceRecAdapter(ranked, getContext());
        adapter.setOnItemClickListener(this);
        search_RV.setAdapter(adapter);
        search_pb.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        search_RV.setVisibility(View.VISIBLE);
    }

    private void performSearchRoleBA(String query){
        search_pb.setVisibility(View.VISIBLE);
        String q = normalize(query);
        List<Order> filtered = allOrders.stream()
                .filter(o -> normalize(o.getUsername()).contains(q)
                        || normalize(o.getUserId()).contains(q)
                        || normalize(o.getLocation()).contains(q)
                        || normalize(o.getServiceName()).contains(q))
                .collect(Collectors.toList());


        search_RV.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapterBA = new OrderListAdapter(filtered,getContext());
        search_RV.setAdapter(adapterBA);
        adapterBA.setOnItemClickListener(this);
        adapterBA.notifyDataSetChanged();
        search_pb.setVisibility(View.GONE);
        search_RV.setVisibility(View.VISIBLE);
    }


    @Override
    public void getServiceDetail(int position) {
        if(adapter == null) return;

        List<BAprofile> currentList = adapter.getList();
        if(position < 0 || position >= currentList.size()) return;

        BAprofile selectedBA = currentList.get(position);

        String selectedCategory = currentFilterOptions.serviceTypes.isEmpty() ? "" :
                String.join(", ", currentFilterOptions.serviceTypes);

        Bundle bundle = new Bundle();
        bundle.putString("baId", selectedBA.getBAid());
        bundle.putString("serviceName", selectedBA.getBAname());
        bundle.putString("servicePhoto", selectedBA.getPhotoUrl());
        bundle.putLong("minPrice", selectedBA.getMinPrice());
        bundle.putString("selectedCategory", selectedCategory);
        bundle.putDouble("distance", selectedBA.getDistance());

        ServiceDetailFragment serviceDetailFragment = new ServiceDetailFragment();
        serviceDetailFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(serviceDetailFragment, true);
    }

    @Override
    public void onConfirmClick(int position) {

    }

    @Override
    public void onRejectClick(int position) {

    }

    @Override
    public void onViewDetailClick(int position) {
        Order order = allOrders.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("role", role);
        bundle.putSerializable("order", order);
        OrderListDetailFragment orderDetailFragment = new OrderListDetailFragment();
        orderDetailFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(orderDetailFragment, true);

    }
}