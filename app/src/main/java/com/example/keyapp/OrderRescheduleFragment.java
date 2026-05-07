package com.example.keyapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Adapter.ScheduleItemAdapter;
import com.example.keyapp.Helper.NotificationHelper;
import com.example.keyapp.Models.Order;
import com.example.keyapp.Models.ScheduleItem;
import com.example.keyapp.Schedule.ScheduleFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRescheduleFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView scheduleRV;
    private ImageButton backBtn;
    private LocalDate selectedDate;
    private String selectedTime;
    private List<ScheduleItem> scheduleList = new ArrayList<>();
    private ScheduleItemAdapter adapter;
    private Order order;
    private Map<String, Boolean> daysChecked;
    private Map<String, List<String>> availableTimes;
    String BAid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_reschedule, container, false);

        calendarView = rootView.findViewById(R.id.or_calendarCV);
        scheduleRV = rootView.findViewById(R.id.or_scheduleRV);
        backBtn = rootView.findViewById(R.id.or_backBtn);

        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
            BAid = order.getBAid();
        }

        adapter = new ScheduleItemAdapter(scheduleList, getContext());
        scheduleRV.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduleRV.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            ScheduleItem item = scheduleList.get(position);
            selectedTime = item.getTime();

            confirmDialog(selectedDate, selectedTime);
        });

        FirebaseFirestore.getInstance()
                .collection("user_schedules")
                .document(BAid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        daysChecked = (Map<String, Boolean>) doc.get("daysChecked");
                        availableTimes = (Map<String, List<String>>) doc.get("availableTimes");
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Gagal load jadwal BA", Toast.LENGTH_SHORT).show());

            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth);

            DayOfWeek dow = selectedDate.getDayOfWeek();
            String dayName = dow.name().substring(0, 1) + dow.name().substring(1).toLowerCase();

            scheduleList.clear();

            if (daysChecked != null && daysChecked.getOrDefault(dayName, false)) {
                List<String> times = availableTimes.get(dayName);
                if (times != null) {
                    for (String t : times) {
                        scheduleList.add(new ScheduleItem(t, 60, order.getBAName()));
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "BA tidak tersedia hari ini", Toast.LENGTH_SHORT).show();
            }
            selectedTime = null;
        });

        backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return rootView;
    }

    private void confirmDialog(LocalDate selectedDate, String selectedTime){

        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View view = getLayoutInflater().inflate(R.layout.layout_alert_dialog, null);
        dialog.setView(view);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        dialog.setCancelable(false);

        TextView titleTV = view.findViewById(R.id.dialog_title);
        TextView messageTV = view.findViewById(R.id.dialog_message);
        Button rejectBtn = view.findViewById(R.id.dialog_btnA);
        Button acceptBtn = view.findViewById(R.id.dialog_btnB);

        titleTV.setText("Confirm Reschedule");
        messageTV.setText("Are you sure you want to reschedule to " +
                selectedDate + " at " + selectedTime + "?");
        rejectBtn.setText("No");
        acceptBtn.setText("Reschedule");

        acceptBtn.setOnClickListener(v -> {
            submitReschedule();
            dialog.dismiss();
        });

        rejectBtn.setOnClickListener(v -> dialog.dismiss());
    }
    private void submitReschedule() {
        if (selectedDate != null && selectedTime != null) {
            Map<String, Object> req = new HashMap<>();
            req.put("newDate", selectedDate.toString());
            req.put("newTime", selectedTime);
            req.put("status", "pending");

            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(order.getOrderId())
                    .update("rescheduleRequest", req)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Reschedule request sent", Toast.LENGTH_SHORT).show();
                        NotificationHelper.saveNotificationToFirestore(BAid, "BA", "Reschedule Request", "Customer has requested a schedule change. Please check the order details.", "reschedule_request", order.getOrderId());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Gagal kirim request", Toast.LENGTH_SHORT).show()
                    );
            goToSchedule();
        }


    }

    private void goToSchedule(){
        Bundle bundle = new Bundle();
        bundle.putInt("role", 1);
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        scheduleFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).openFragment(scheduleFragment, true);
    }


}