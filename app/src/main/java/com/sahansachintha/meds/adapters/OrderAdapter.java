package com.sahansachintha.meds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.model.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final Context context;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orders, Context context, OnOrderClickListener listener) {
        this.orders = orders;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(this.orders, newOrders));
        this.orders.clear();
        this.orders.addAll(newOrders);
        diffResult.dispatchUpdatesTo(this);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIdText, orderDateText, orderTotalText, orderStatusText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.order_id);
            orderDateText = itemView.findViewById(R.id.order_date);
            orderTotalText = itemView.findViewById(R.id.order_total);
            orderStatusText = itemView.findViewById(R.id.order_status);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Order order) {
            orderIdText.setText(String.format(Locale.US, "Order ID: %s", (order.getId() != null) ? order.getId() : order.getOrderId()));
            orderTotalText.setText(String.format(Locale.US, "LKR %.2f", order.getTotalPrice()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(order.getTimestamp());
            orderDateText.setText(dateFormat.format(calendar.getTime()));

            orderStatusText.setText(order.getStatus());
            orderStatusText.setTextColor(getStatusColor(order.getStatus()));
        }

        private int getStatusColor(String status) {
            if (status.equalsIgnoreCase("Paid")) return getThemeColor(context, R.attr.colorSuccess);
            if (status.equalsIgnoreCase("Pending")) return getThemeColor(context, R.attr.colorAccent);
            if (status.equalsIgnoreCase("Cancelled")) return getThemeColor(context, com.google.android.material.R.attr.colorError);
            return getThemeColor(context, R.attr.colorNeutral500);
        }

        private int getThemeColor(Context context, int attribute) {
            return AppHelper.getInstance().getThemeColor(context, attribute);
        }
    }

    private static class OrderDiffCallback extends DiffUtil.Callback {
        private final List<Order> oldList;
        private final List<Order> newList;

        public OrderDiffCallback(List<Order> oldList, List<Order> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getOrderId().equals(newList.get(newItemPosition).getOrderId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
