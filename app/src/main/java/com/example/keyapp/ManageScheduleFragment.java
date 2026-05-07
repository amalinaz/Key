package com.example.keyapp;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.keyapp.Adapter.TimeListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ManageScheduleFragment extends Fragment {

    LinearLayout availableTimeLayout;
    Button ms_saveBtn;
    ImageButton ms_backBtn;
    private String[] daysOfWeek;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_schedule, container, false);
        availableTimeLayout = rootView.findViewById(R.id.availableTimeLayout);
        ms_saveBtn = rootView.findViewById(R.id.ms_saveBtn);
        ms_backBtn = rootView.findViewById(R.id.oldetail_backBtn);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        daysOfWeek = getResources().getStringArray(R.array.Hari);

        Map<String, List<String>> availableTimes = new HashMap<>();
        Map<String, Boolean> daysChecked = new HashMap<>();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();


        ms_backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        for (String day : daysOfWeek) {

            View row = inflater.inflate(R.layout.item_available_time_row, availableTimeLayout, false);
            CheckBox cb = row.findViewById(R.id.checkBox);
            ListView timeList = row.findViewById(R.id.listView_time);
            Button setTimeBtn = row.findViewById(R.id.profile_setTimeBtn);

            cb.setText(day);

            List<String> timesForThisDay = new ArrayList<>();

            TimeListAdapter adapter = new TimeListAdapter(requireContext(), timesForThisDay);
            timeList.setAdapter(adapter);

            daysChecked.put(day, false);

            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                daysChecked.put(day, isChecked);
            });
            setTimeBtn.setOnClickListener(v -> {
                if (!cb.isChecked()) {
                    Toast.makeText(getContext(), "Please check the day first", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                new TimePickerDialog(
                        requireContext(),
                        (view, selectedHour, selectedMinute) -> {
                            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                            timesForThisDay.add(time);
                            adapter.notifyDataSetChanged();
                            if (cb.isChecked()) {
                                availableTimes.put(day, timesForThisDay);
                            }
                        },
                        hour,
                        minute,
                        true
                ).show();
            });

            timeList.setOnItemClickListener((parent, view, position, id) -> {
                String oldTime = timesForThisDay.get(position);
                int oldHour = Integer.parseInt(oldTime.split(":")[0]);
                int oldMinute = Integer.parseInt(oldTime.split(":")[1]);

                new TimePickerDialog(
                        requireContext(),
                        (views, selectedHour, selectedMinute) -> {
                            String newTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                            timesForThisDay.set(position, newTime);
                            adapter.notifyDataSetChanged();
                        },
                        oldHour,
                        oldMinute,
                        true
                ).show();
            });

            timeList.setOnItemLongClickListener((parent, view, position, id) -> {
                timesForThisDay.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            });



            availableTimeLayout.addView(row);
        }
        db.collection("user_schedules")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Boolean> savedDaysChecked = (Map<String, Boolean>) documentSnapshot.get("daysChecked");
                        Map<String, List<String>> savedAvailableTimes = (Map<String, List<String>>) documentSnapshot.get("availableTimes");

                        for (int i = 0; i < daysOfWeek.length; i++) {
                            String day = daysOfWeek[i];

                            boolean isChecked = savedDaysChecked.getOrDefault(day, false);
                            CheckBox cb = (CheckBox) availableTimeLayout.getChildAt(i).findViewById(R.id.checkBox);
                            if (cb != null) {
                                cb.setChecked(isChecked);
                            }

                            if (savedAvailableTimes.containsKey(day)) {
                                List<String> timesForThisDay = savedAvailableTimes.get(day);
                                ListView timeList = (ListView) availableTimeLayout.getChildAt(i).findViewById(R.id.listView_time);
                                TimeListAdapter adapter = (TimeListAdapter) timeList.getAdapter();
                                adapter.clear();
                                adapter.addAll(timesForThisDay);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });

        ms_saveBtn.setOnClickListener(v -> {
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("daysChecked", daysChecked);
            scheduleData.put("availableTimes", availableTimes);

                if (!availableTimes.isEmpty()) {
                    db.collection("user_schedules").document(uid)
                            .set(scheduleData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(),
                                        "Waktu berhasil disimpan",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(),
                                        "Gagal menyimpan waktu",
                                        Toast.LENGTH_SHORT).show();
                            });
                }
        });


        return rootView;
    }
}