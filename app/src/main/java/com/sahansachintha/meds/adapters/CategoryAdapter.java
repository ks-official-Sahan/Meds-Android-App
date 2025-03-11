package com.sahansachintha.meds.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
import com.sahansachintha.meds.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final Context context;

    private int selectedItem = -1;

    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, Context context, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.context = context;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryTitle.setText(category.getName());
        if (category.getImgId() > 0 && category.getImage() == null) {
            holder.categoryImage.setImageResource(category.getImgId());
        } else if (category.getImage() != null) {
            Glide.with(context)
                    .load(category.getImage())
                    .placeholder(R.drawable.ic_medication)
                    .centerCrop()
                    //.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.categoryImage);
        }

        if (position == selectedItem) {
            holder.categoryCard.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).start();
            holder.categoryContainer.setBackgroundResource(R.color.primary_400);
        } else {
            holder.categoryCard.animate().scaleX(1f).scaleY(1f).setDuration(500).start();
            holder.categoryContainer.setBackgroundResource(R.color.transparent);
        }

        holder.categoryCard.setOnClickListener(v -> {
            setSelectedItem(position);
            //setSelectedItem(holder.getAdapterPosition());
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        ImageView categoryImage;
        MaterialCardView categoryCard;
        View categoryContainer;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_card_title);
            categoryImage = itemView.findViewById(R.id.category_card_img);
            categoryContainer = itemView.findViewById(R.id.category_card_container);

            categoryCard = itemView.findViewById(R.id.category_card_holder);
        }
    }
}
