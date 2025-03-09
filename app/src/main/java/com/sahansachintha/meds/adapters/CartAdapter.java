package com.sahansachintha.meds.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.data.CartManager;
import com.sahansachintha.meds.model.ProductItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<ProductItem> productItems;
    private final Context context;
    private final CartUpdateListener cartUpdateListener;

//    public CartAdapter(List<CartItem> cartItems, Context context) {
//        this.cartItems = cartItems;
//        this.context = context;
//    }

    public CartAdapter(List<ProductItem> productItems, Context context, CartUpdateListener cartUpdateListener) {
        this.productItems = productItems;
        this.context = context;
        this.cartUpdateListener = cartUpdateListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductItem productItem = productItems.get(position);

        holder.cartTitle.setText(productItem.getProduct().getTitle());
        //holder.cartPrice.setText(cartItem.getProduct().getPrice());
        holder.cartQuantity.setText(String.format(Locale.US, "%d", productItem.getQuantity()));
        holder.cartPrice.setText(String.format(Locale.US, "LKR %.2f", (Double.parseDouble(productItem.getProduct().getPrice()) * productItem.getQuantity())));
        holder.cartCategory.setText(productItem.getProduct().getCategoryName());

        Glide.with(context)
                .load(productItem.getProduct().getImage())
                .placeholder(R.drawable.placeholder_image) // Use a default placeholder
                .into(holder.cartImage);

        // Handle quantity input change with a dialog
        holder.cartQuantity.setOnClickListener(v -> showQuantityDialog(productItem, holder, position));

        holder.removeBtn.setOnClickListener(v -> removeItem(position));
        holder.incrementBtn.setOnClickListener(v -> updateQuantity(position, productItem, 1));
        holder.decrementBtn.setOnClickListener(v -> updateQuantity(position, productItem, -1));
    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }

    // ViewHolder
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartTitle, cartPrice, cartQuantity, cartCategory;
        ImageView cartImage, removeBtn, incrementBtn, decrementBtn;
        MaterialCardView cartHolder;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartTitle = itemView.findViewById(R.id.cart_item_title);
            cartPrice = itemView.findViewById(R.id.cart_item_price);
            cartCategory = itemView.findViewById(R.id.cart_item_category);
            cartQuantity = itemView.findViewById(R.id.cart_item_quantity);
            cartImage = itemView.findViewById(R.id.cart_item_img);
            removeBtn = itemView.findViewById(R.id.cart_item_delete);
            cartHolder = itemView.findViewById(R.id.cart_item_holder);
            incrementBtn = itemView.findViewById(R.id.cart_item_increment);
            decrementBtn = itemView.findViewById(R.id.cart_item_decrement);
        }
    }

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    // Update quantity
    private void updateQuantity(int position, ProductItem productItem, int change) {
        int newQuantity = productItem.getQuantity() + change;
        if (newQuantity > 0) {
            CartManager.getInstance(context).updateQuantity(productItem.getProduct(), newQuantity);
            productItem.setQuantity(newQuantity);
            notifyItemChanged(position);
//        } else {
//            removeItem(position);
        }
        cartUpdateListener.onCartUpdated();
    }

    // Remove item from cart
    private void removeItem(int position) {
        CartManager.getInstance(context).removeProduct(productItems.get(position).getProduct().getId());
        Log.i("MyMedsCartAdapter", "CartItems" + productItems.size());
        //cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productItems.size());
        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
        cartUpdateListener.onCartUpdated();
    }

    // Show input dialog for quantity change
    private void showQuantityDialog(ProductItem productItem, CartViewHolder holder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Quantity");

        final EditText input = new EditText(context);
        input.setText(String.valueOf(productItem.getQuantity()));
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            try {
                int newQuantity = Integer.parseInt(input.getText().toString());
                updateQuantity(position, productItem, newQuantity - productItem.getQuantity());
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}
