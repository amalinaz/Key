package com.example.keyapp.MenuBA.ServiceMenu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.keyapp.ProfileFragment;
import com.example.keyapp.R;


public class TimepickerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_timepicker, container, false);
        TimePicker timePicker = rootView.findViewById(R.id.timePicker);
        Button timepickerSaveBtn = rootView.findViewById(R.id.timePickerSaveBtn);

        timePicker.setIs24HourView(true);

        timepickerSaveBtn.setOnClickListener(v -> {

            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();


            Fragment profileFragment = new ProfileFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("hour", hour);
            bundle.putInt("minute", minute);


            profileFragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.timepicker, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return rootView;
    }
}