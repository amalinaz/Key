package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.keyapp.R;

import java.util.List;
import java.util.Objects;

public class TimeListAdapter extends ArrayAdapter<String> {

    public TimeListAdapter(@NonNull Context context, @NonNull List<String> times) {
        super(context, 0, times);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_time_list, parent, false);
        }

        TextView timeTV = view.findViewById(R.id.time_timelistTV);
        String time = getItem(position);
        timeTV.setText(time);

        return view;
    }

}
