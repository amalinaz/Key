package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.BookScheduleItem;
import com.example.keyapp.Models.ScheduleItem;
import com.example.keyapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BookScheduleAdapter extends RecyclerView.Adapter<BookScheduleAdapter.BookScheduleViewHolder> {
    private List<BookScheduleItem> bookScheduleItems;
    private Context ctx;

    public BookScheduleAdapter(List<BookScheduleItem> bookScheduleItems, Context ctx) {
        this.bookScheduleItems = bookScheduleItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public BookScheduleAdapter.BookScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookschedule, parent,false);
        return new BookScheduleAdapter.BookScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookScheduleAdapter.BookScheduleViewHolder holder, int position) {
        BookScheduleItem bookScheduleItem = bookScheduleItems.get(position);
//        LocalDate date = LocalDate.parse(bookScheduleItem.getDate());
//
//        DateTimeFormatter formatter =
//                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
//
//        String formattedDate = date.format(formatter);


        holder.bs_BANameTV.setText(bookScheduleItem.getBAname());
        holder.bs_serviceNameTV.setText(bookScheduleItem.getServiceName());
        holder.bs_dateTV.setText(bookScheduleItem.getDate());
        holder.bs_timeTV.setText(bookScheduleItem.getTime());
    }

    @Override
    public int getItemCount() {
        return bookScheduleItems.size();
    }

    public class BookScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView bs_BANameTV, bs_dateTV, bs_timeTV, bs_serviceNameTV;
        public BookScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            bs_BANameTV = itemView.findViewById(R.id.bs_BAnameTV);
            bs_serviceNameTV = itemView.findViewById(R.id.bs_serviceNameTV);
            bs_dateTV = itemView.findViewById(R.id.bs_dateTV);
            bs_timeTV = itemView.findViewById(R.id.bs_timeTV);
        }
    }
}
