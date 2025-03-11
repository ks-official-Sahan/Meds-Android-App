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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public ProductAdapter(List<Product> productList, Context context, OnProductClickListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.productCategory.setText(product.getCategoryName());

        Glide.with(context)
                .load(product.getImage())
                //.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .placeholder(R.drawable.med_asset_05) // Optional: Add a placeholder image
                .error(R.drawable.error_image) // Optional: Handle errors
                .into(holder.productImage);

        holder.productCard.setOnClickListener(v -> {
            //Toast.makeText(context, product.getTitle(), Toast.LENGTH_SHORT).show();
            //NavigationHelper.getInstance().openIntent(context, ProductViewActivity.class);
            NavigationHelper.getInstance().viewProduct(context, product);
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle;
        TextView productPrice;
        TextView productCategory;
        ImageView productImage;
        MaterialCardView productCard;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.cart_item_title);
            productPrice = itemView.findViewById(R.id.cart_item_price);
            productCategory = itemView.findViewById(R.id.cart_item_category);
            productImage = itemView.findViewById(R.id.cart_item_img);
            productCard = itemView.findViewById(R.id.cart_item__holder);
        }
    }
}
