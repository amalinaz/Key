package com.example.keyapp.Schedule;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;

import com.example.keyapp.Adapter.ScheduleMenuAdapter;
import com.example.keyapp.MainActivity;
import com.example.keyapp.Models.Order;
import com.example.keyapp.Models.Schedule;
import com.example.keyapp.OrderListDetailFragment;
import com.example.keyapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ScheduleFragment extends Fragment implements ScheduleMenuAdapter.OnItemClickListener {
    ImageButton schedule_backBtn;
    CalendarView schedule_CalendarView;
    RecyclerView schedule_RV, schedule_calendarList;
    ChipGroup schedule_chipGroup;
    Chip schedule_calendarChip, schedule_listChip;
    FirebaseAuth auth;
    FirebaseFirestore db ;
    ScheduleMenuAdapter adapterCalendar, adapterList;
    List<Order> scheduleList = new ArrayList<>();
    List<Order> filteredList = new ArrayList<>();
    List<Order> orderList = new ArrayList<>();

    private  int role;
    private String selectedDate;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        schedule_backBtn = rootView.findViewById(R.id.schedule_BackBtn);
        schedule_CalendarView = rootView.findViewById(R.id.schedule_calendarView);
        schedule_RV = rootView.findViewById(R.id.schedule_RV);
        schedule_calendarList = rootView.findViewById(R.id.schedule_calendarlist_RV);
        schedule_chipGroup = rootView.findViewById(R.id.schedule_chipGroup);
        schedule_calendarChip = rootView.findViewById(R.id.schedule_calendarChip);
        schedule_listChip = rootView.findViewById(R.id.schedule_listChip);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        schedule_CalendarView.setVisibility(View.VISIBLE);
        schedule_calendarList.setVisibility(View.VISIBLE);
        schedule_RV.setVisibility(View.GONE);

        if(getArguments() != null){
            role = getArguments().getInt("role");
        }

        adapterList = new ScheduleMenuAdapter(scheduleList, getContext(), role);
        schedule_RV.setLayoutManager(new LinearLayoutManager(requireContext()));
        schedule_RV.setAdapter(adapterList);
        adapterList.setOnItemClickListener(this);

        adapterCalendar = new ScheduleMenuAdapter(filteredList,getContext(), role);
        schedule_calendarList.setLayoutManager(new LinearLayoutManager(requireContext()));
        schedule_calendarList.setAdapter(adapterCalendar);
        adapterCalendar.setOnItemClickListener(this);

        String uid = auth.getCurrentUser().getUid();

        schedule_calendarChip.setOnClickListener(v->{
            showCalendarView();
        });


        getSchedule(role, uid);
        schedule_CalendarView.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            filterScheduleByDate(selectedDate);
        }));

        schedule_listChip.setOnClickListener(v->{showListView();});


        return rootView;

    }

    private void filterScheduleByDate(String date){

        filteredList.clear();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate selectedCalendarDate =
                LocalDate.parse(date, formatter);
        for(Order s : scheduleList){
            try{
                LocalDate orderDate = LocalDate.parse(s.getSelectedDate(), formatter);

                if(orderDate.equals(selectedCalendarDate)){
                    filteredList.add(s);

                }

            }catch (Exception e){
                Log.e("Schedule",
                        "Date parse error: " + s.getSelectedDate());
            }
        }

        adapterCalendar.notifyDataSetChanged();
    }

    private void getSchedule(int role, String uid) {
        String field = role == 1 ? "userId" : "baid";
        db.collection("orders")
                .whereEqualTo(field, uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String orderId = doc.getString("orderId");
                        String baName = doc.getString("baname");
                        String baId = doc.getString("baid");
                        String userId = doc.getString("userId");
                        String serviceName = doc.getString("serviceName");
                        double servicePrice = doc.getDouble("servicePrice") != null ? doc.getDouble("servicePrice") : 0;
                        String status = doc.getString("status");
                        String selectedDate = doc.getString("selectedDate");
                        String selectedTime = doc.getString("selectedTime");
                        String location = doc.getString("location");
                        String username = doc.getString("username");



                        Order orderSchedule = new Order(orderId, userId, username, serviceName, selectedDate,
                                    selectedTime, servicePrice, status, baId, baName, location,null);

                        if(status.equals("Confirmed") || status.equals("Completed")){
                            scheduleList.add(orderSchedule);
                        }

                    }
                    Collections.sort(scheduleList, (s1, s2) -> {

                        LocalDate today = LocalDate.now();
                        if(s1.getSelectedDate() == null) s1.setSelectedDate(today.toString());
                        if(s2.getSelectedDate() == null) s2.setSelectedDate(today.toString());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                        LocalDate d1 = LocalDate.parse(s1.getSelectedDate(), formatter);
                        LocalDate d2 = LocalDate.parse(s2.getSelectedDate(), formatter);
                        return d2.compareTo(d1);
                    });

                    adapterList.notifyDataSetChanged();

                    long calendarDateMillis = schedule_CalendarView.getDate();
                    Date date = new Date(calendarDateMillis);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sdf.format(date);

                    filterScheduleByDate(selectedDate);
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", e.getMessage()));
    }

    private void showCalendarView() {
        schedule_CalendarView.setVisibility(View.VISIBLE);
        schedule_calendarList.setVisibility(View.VISIBLE);
        schedule_RV.setVisibility(View.GONE);
    }

    private void showListView() {
        schedule_CalendarView.setVisibility(View.GONE);
        schedule_calendarList.setVisibility(View.GONE);
        schedule_RV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(Order schedule, int position) {
        OrderListDetailFragment orderListDetailFragment = new OrderListDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("role", role);
        bundle.putSerializable("order", schedule);
        orderListDetailFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(orderListDetailFragment, true);

    }
}