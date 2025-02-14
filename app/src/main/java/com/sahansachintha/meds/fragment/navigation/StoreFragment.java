package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.CategoryAdapter;
import com.sahansachintha.meds.adapters.ProductAdapter;
import com.sahansachintha.meds.helper.data.ProductHelper;
import com.sahansachintha.meds.model.Category;
import com.sahansachintha.meds.model.Product;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {

    /* Category */
    private RecyclerView categoryRecycler;
    private List<Category> categoryData;
    private CategoryAdapter categoryAdapter;
    private LinearLayoutManager categoryLayoutManager;

    /* Category */
    private RecyclerView productRecycler;
    private List<Product> productData;
    private ProductAdapter productAdapter;
    private GridLayoutManager productLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        categoryRecycler = view.findViewById(R.id.store_category_recycler);
        initCategoryRecycler();

        productRecycler = view.findViewById(R.id.store_products_recycler);
        initProductRecycler();

        return view;
    }

    private void initCategoryRecycler() {
        categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycler.setLayoutManager(categoryLayoutManager);

        categoryData = getCategoryData();

        categoryAdapter = new CategoryAdapter(categoryData, getContext());
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private List<Category> getCategoryData() {
        List<Category> categoryList = new ArrayList<>();

        categoryList.add(new Category(1, "Tablets", R.drawable.ic_medicine));
        categoryList.add(new Category(2, "Capsules", R.drawable.ic_medication));
        categoryList.add(new Category(3, "Syrups", R.drawable.ic_medicine));

        return categoryList;
    }

    private void initProductRecycler() {
        productLayoutManager = new GridLayoutManager(getContext(), getSpanCountBasedOnScreenWidth(), LinearLayoutManager.VERTICAL, false);
        productRecycler.setLayoutManager(productLayoutManager);

        productData = getProductData();

        productAdapter = new ProductAdapter(productData, getContext());
        productRecycler.setAdapter(productAdapter);
    }

    private List<Product> getProductData() {
        List<Product> productList = new ArrayList<>();

        // Add dummy data to the product list
        productList = ProductHelper.getInstance().getSampledata();

        return productList;
    }

    private int getSpanCountBasedOnScreenWidth() {
        // Get the screen size and density
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidthDp = displayMetrics.widthPixels / displayMetrics.densityDpi;

        // Adjust columns based on screen width in dp
        int spanCount;
        if (screenWidthDp >= 1200) {
            spanCount = 4; // Large screens
        } else if (screenWidthDp >= 800) {
            spanCount = 3; // Medium screens
        } else {
            spanCount = 2; // Small screens
        }

        return spanCount;
    }
}