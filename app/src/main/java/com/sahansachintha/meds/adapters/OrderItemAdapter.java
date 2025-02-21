package com.sahansachintha.meds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.model.ProductItem;

import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private final List<ProductItem> orderItems;
    private final Context context;
    private final OnOderItemClickListener listener;

    public interface OnOderItemClickListener {
        void onOderItemClick(ProductItem productItem);
    }

    public OrderItemAdapter(List<ProductItem> orderItems, Context context, OnOderItemClickListener listener) {
        this.orderItems = orderItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        ProductItem orderItem = orderItems.get(position);

        holder.orderTitle.setText(orderItem.getProduct().getTitle());
        holder.orderQuantity.setText(String.format(Locale.US, "%d", orderItem.getQuantity()));
        holder.orderPrice.setText(String.format(Locale.US, "LKR %.2f", (Double.parseDouble(orderItem.getProduct().getPrice()) * orderItem.getQuantity())));

        Glide.with(context)
                .load(orderItem.getProduct().getImage())
                .placeholder(R.drawable.placeholder_image) // Use a default placeholder
                .into(holder.orderImage);

        holder.orderHolder.setOnClickListener(v -> listener.onOderItemClick(orderItem));

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    // ViewHolder
    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderPrice, orderQuantity;
        ImageView orderImage;
        MaterialCardView orderHolder;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.order_item_title);
            orderPrice = itemView.findViewById(R.id.order_item_price);
            orderQuantity = itemView.findViewById(R.id.order_item_quantity);
            orderImage = itemView.findViewById(R.id.order_item_img);
            orderHolder = itemView.findViewById(R.id.order_item_holder);
        }
    }
}
