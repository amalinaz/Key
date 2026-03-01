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

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {
    private List<Order> orderList;
    private Context ctx;

    public OrderListAdapter(List<Order> orderList, Context ctx) {
        this.orderList = orderList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public OrderListAdapter.OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list, parent,false);
        return new OrderListAdapter.OrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.OrderListViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.order_id.setText("O...");
        holder.order_dateTime.setText(order.getSelectedDate() + " - "+ order.getSelectedTime());
        holder.order_serviceName.setText(order.getServiceName());
        holder.order_custName.setText(order.getUsername());
        holder.order_location.setText(" ");
        holder.order_status.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder {

        TextView order_id, order_dateTime, order_serviceName, order_custName, order_location, order_status;
        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            order_dateTime = itemView.findViewById(R.id.order_datetime);
            order_serviceName = itemView.findViewById(R.id.order_serviceName);
            order_custName = itemView.findViewById(R.id.order_custName);
            order_location = itemView.findViewById(R.id.order_location);
            order_status = itemView.findViewById(R.id.order_status);
        }
    }
}
