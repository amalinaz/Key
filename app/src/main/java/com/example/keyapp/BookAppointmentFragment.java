package com.example.keyapp;

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

public class BookAppointmentFragment extends Fragment implements ScheduleItemAdapter.OnItemClickListener{

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
        View rootView = inflater.inflate(R.layout.fragment_book_appointment, container, false);
        a_backBtn = rootView.findViewById(R.id.a_backBtn);
        a_calendarCV = rootView.findViewById(R.id.a_calendarCV);
        a_scheduleRV = rootView.findViewById(R.id.a_scheduleRV);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            serviceID = getArguments().getString("SERVICE_ID");
            Baid = getArguments().getString("BAid");
            serviceName = getArguments().getString("SERVICE_NAME");
            servicePrice = getArguments().getDouble("SERVICE_PRICE");
            estimatedTime = getArguments().getInt("SERVICE_ESTTIME");
        }

        long calendarDateMillis = a_calendarCV.getDate();
        Date date = new Date(calendarDateMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(date);
        String dayOfWeeks = getDayOfWeek(today);
        selectedDate = today;

        getSchedule(dayOfWeeks, Baid);

        a_calendarCV.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            String dayOfWeek = getDayOfWeek(selectedDate);
            getSchedule(dayOfWeek, Baid);
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

    private void getSchedule(String selectedDate, String baid) {
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
                                scheduleItems.clear();
                                for (String day : availableTimes.keySet()) {
                                    if (daysChecked.getOrDefault(day, false)) {

                                        List<String> timesForDay = availableTimes.get(day);
                                        for (String time : timesForDay) {
                                            if (day.equals(selectedDate)) {
                                                ScheduleItem scheduleItem = new ScheduleItem(time, estimatedTime, BAName);
                                                scheduleItems.add(scheduleItem);
                                            }
                                        }
                                    }
                                }
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

        ConfirmAppointmentFragment confirmAppointmentFragment = new ConfirmAppointmentFragment();
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
        confirmAppointmentFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(confirmAppointmentFragment, true);
    }
}