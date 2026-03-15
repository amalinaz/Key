package com.example.keyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.keyapp.Models.Order;
import com.example.keyapp.R;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {
    private List<Order> orderList;
    private Context ctx;
    private OrderListAdapter.OnItemClickListener onItemClickListener;

    public OrderListAdapter(List<Order> orderList, Context ctx) {
        this.orderList = orderList;
        this.ctx = ctx;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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

        if(order.getStatus().equals("Confirmed") || order.getStatus().equals("Rejected")){
            holder.order_confirmBtn.setVisibility(View.GONE);
            holder.order_rejectBtn.setVisibility(View.GONE);
        }else{
            holder.order_confirmBtn.setVisibility(View.VISIBLE);
            holder.order_rejectBtn.setVisibility(View.VISIBLE);
        }

        holder.order_id.setText(order.getOrderId());
        holder.order_dateTime.setText(order.getSelectedDate() + " - "+ order.getSelectedTime());
        holder.order_serviceName.setText(order.getServiceName());
        holder.order_custName.setText(order.getUsername());
        holder.order_location.setText(" ");
        holder.order_status.setText(order.getStatus());

        holder.order_confirmBtn.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onConfirmClick(holder.getAdapterPosition());
            }
        });

        holder.order_rejectBtn.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onRejectClick(holder.getAdapterPosition());
            }
        });

        holder.order_viewDetailBtn.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onViewDetailClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface OnItemClickListener {
        void onConfirmClick(int position);
        void onRejectClick(int position);
        void onViewDetailClick(int position);
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder {

        Button order_confirmBtn, order_rejectBtn, order_viewDetailBtn;
        TextView order_id, order_dateTime, order_serviceName, order_custName, order_location, order_status;
        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            order_dateTime = itemView.findViewById(R.id.order_datetime);
            order_serviceName = itemView.findViewById(R.id.order_serviceName);
            order_custName = itemView.findViewById(R.id.order_custName);
            order_location = itemView.findViewById(R.id.order_location);
            order_status = itemView.findViewById(R.id.order_status);
            order_confirmBtn = itemView.findViewById(R.id.order_confirmBtn);
            order_rejectBtn = itemView.findViewById(R.id.order_rejectBtn);
            order_viewDetailBtn = itemView.findViewById(R.id.order_viewDetailBtn);
        }
    }
}
