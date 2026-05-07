package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyapp.Models.Order;
import com.example.keyapp.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderListDetailAdapter extends RecyclerView.Adapter<OrderListDetailAdapter.OrderListDetailViewHolder> {

    private List<Order> orderListDetail;
    private Context ctx;
    public OrderListDetailAdapter(List<Order> orderListDetail, Context ctx) {
        this.orderListDetail = orderListDetail;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public OrderListDetailAdapter.OrderListDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list_detail, parent,false);
        return new OrderListDetailAdapter.OrderListDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListDetailAdapter.OrderListDetailViewHolder holder, int position) {
        Order order = orderListDetail.get(position);
        NumberFormat formatter =
                NumberFormat.getInstance(new Locale("id", "ID"));

        holder.old_idTV.setText(order.getOrderId());
        holder.old_dateTimeTV.setText(order.getSelectedDate() + " - "+ order.getSelectedTime());
        holder.old_serviceTV.setText(order.getServiceName());
        holder.old_custNameTV.setText(order.getUsername());
        holder.old_locationTV.setText(order.getLocation());
        holder.old_priceTV.setText("Rp."+ formatter.format(order.getServicePrice()));

    }

    @Override
    public int getItemCount() {
        return orderListDetail.size();
    }

    public class OrderListDetailViewHolder extends RecyclerView.ViewHolder {
        TextView old_idTV, old_serviceTV, old_custNameTV, old_dateTimeTV, old_locationTV, old_priceTV;
        public OrderListDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            old_idTV = itemView.findViewById(R.id.old_idTV);
            old_serviceTV = itemView.findViewById(R.id.old_serviceTV);
            old_custNameTV = itemView.findViewById(R.id.old_custNameTV);
            old_dateTimeTV = itemView.findViewById(R.id.old_dateTimeTV);
            old_locationTV = itemView.findViewById(R.id.old_locationTV);
            old_priceTV = itemView.findViewById(R.id.old_priceTV);
        }
    }
}
