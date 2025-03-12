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
import com.google.android.material.chip.Chip;
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
        holder.orderPrice.setText(String.format(Locale.US, "LKR %.2f",
                (Double.parseDouble(orderItem.getProduct().getPrice()) * orderItem.getQuantity())));
        holder.orderQuantityChip.setText(String.format(Locale.US, "× %d", orderItem.getQuantity()));

        Glide.with(context)
                .load(orderItem.getProduct().getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.orderImage);

        holder.orderHolder.setOnClickListener(v -> listener.onOderItemClick(orderItem));

        // Apply a fade-in and scale animation for a dynamic appearance
        holder.itemView.setAlpha(0f);
        holder.itemView.setScaleX(0.95f);
        holder.itemView.setScaleY(0.95f);
        holder.itemView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderPrice;
        Chip orderQuantityChip;
        ImageView orderImage;
        MaterialCardView orderHolder;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.order_item_title);
            orderPrice = itemView.findViewById(R.id.order_item_price);
            orderQuantityChip = itemView.findViewById(R.id.order_item_quantity_chip);
            orderImage = itemView.findViewById(R.id.order_item_img);
            orderHolder = itemView.findViewById(R.id.order_item_holder);
        }
    }
}
