package com.example.keyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;

import com.example.keyapp.Adapter.ScheduleItemAdapter;
import com.example.keyapp.Models.ScheduleItem;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AppointmentFragment extends Fragment implements ScheduleItemAdapter.OnItemClickListener{

    ImageButton a_backBtn;
    CalendarView a_calendarCV;
    RecyclerView a_scheduleRV;
    ScheduleItemAdapter itemAdapter;
    private List<ScheduleItem> scheduleItems = new ArrayList<>();
    FirebaseFirestore db;
    private String Baid, serviceID, selectedDate, BAName, serviceName, serviceTime;
    private double servicePrice;
    private int estimatedTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointment, container, false);
        a_backBtn = rootView.findViewById(R.id.pay_backBtn);
        a_calendarCV = rootView.findViewById(R.id.a_calendarCV);
        a_scheduleRV = rootView.findViewById(R.id.a_scheduleRV);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            serviceID = getArguments().getString("SERVICE_ID");
            Baid = getArguments().getString("BAid");
            serviceName = getArguments().getString("SERVICE_NAME");
            servicePrice = getArguments().getDouble("SERVICE_PRICE");
            estimatedTime = getArguments().getInt("SERVICE_ESTTIME");
            if (Baid == null || serviceID == null) {
                Log.e("AppointmentFragment", "Baid or ServiceID is null");


            }
            Log.d("AppointmentFragment", "Service id" +serviceID);
            Log.d("AppointmentFragment", "BA id" +Baid);
        }


        a_calendarCV.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            String dayOfWeek = getDayOfWeek(selectedDate);
            Log.d("AppointmentFragment", "Selected day: " + dayOfWeek);
            updateScheduleForDate(dayOfWeek, Baid);

            Log.e("AppointmentFragment", "Update schedule for date: " + selectedDate);
        });

        a_scheduleRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemAdapter = new ScheduleItemAdapter(scheduleItems, getContext());
        itemAdapter.setOnItemClickListener(this);
        a_scheduleRV.setAdapter(itemAdapter);
        a_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        return rootView;
    }

    private void updateScheduleForDate(String selectedDate, String baid) {
        DocumentReference scheduleRef = db.collection("user_schedules").document(Baid);
        scheduleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> scheduleData = document.getData();
                    Map<String, List<String>> availableTimes = (Map<String, List<String>>) scheduleData.get("availableTimes");
                    Map<String, Boolean> daysChecked = (Map<String, Boolean>) scheduleData.get("daysChecked");

                    DocumentReference profileRef = db.collection("users").document(baid);
                    profileRef.get().addOnCompleteListener(profileTask -> {
                        if (profileTask.isSuccessful()) {
                            DocumentSnapshot profileDocument = profileTask.getResult();
                            if (profileDocument.exists()) {
                                BAName = profileDocument.getString("userName");
                                Log.d("Firestore", "UserName:" + BAName);

                                scheduleItems.clear();
                                for (String day : availableTimes.keySet()) {
                                    if (daysChecked.getOrDefault(day, false)) {

                                        List<String> timesForDay = availableTimes.get(day);
                                        Log.d("Firestore", "time" + timesForDay);
                                        for (String time : timesForDay) {
                                            Log.d("Firestore", "time loop" + time);
                                            if (day.equals(selectedDate)) {
                                                ScheduleItem scheduleItem = new ScheduleItem(time, estimatedTime, BAName);
                                                scheduleItems.add(scheduleItem);
                                                Log.d("Firestore", "scheduleItem" + scheduleItem);

                                            }
                                        }
                                    }
                                }
                                Log.d("AppointmentFragment", "Schedule items count before notify: " + scheduleItems.size());
                                itemAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }


    private String getDayOfWeek(String selectedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(selectedDate);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onItemClick(int position) {
        String selectedTime = scheduleItems.get(position).getTime();

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        PaymentFragment paymentFragment = new PaymentFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("BAid", Baid);
        bundle.putString("selectedDate", selectedDate);
        Log.d("Appointment", "tanggalnyah"+ selectedDate);
        bundle.putString("selectedTime", selectedTime);
        bundle.putString("BAName", BAName);
        bundle.putString("serviceID", serviceID);
        bundle.putString("serviceName", serviceName);
        bundle.putDouble("servicePrice", servicePrice);


//        bundle.putString();


        paymentFragment.setArguments(bundle);
        transaction.replace(R.id.appointmentLayout, paymentFragment);
        transaction.addToBackStack(null);  // Agar FragmentA tetap di-back stack
        transaction.commit();
    }
}