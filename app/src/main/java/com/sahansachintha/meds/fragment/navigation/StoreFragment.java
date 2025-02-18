package com.sahansachintha.meds.fragment.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.CategoryAdapter;
import com.sahansachintha.meds.adapters.ProductAdapter;
import com.sahansachintha.meds.helper.data.CategoryManager;
import com.sahansachintha.meds.helper.data.ProductManager;
import com.sahansachintha.meds.model.Category;
import com.sahansachintha.meds.model.Product;

import java.util.List;

public class StoreFragment extends Fragment {

    private RecyclerView categoryRecycler, productRecycler;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Product> productData;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store, container, false);

        initCategoryRecycler();
        initProductRecycler();

        setClickListener(R.id.products_view_all_store, this::viewAll);
        setClickListener(R.id.products_refresh_store, this::viewAll);

        return view;
    }

    private void viewAll() {
        updateProducts(ProductManager.getInstance().getAllProducts());
        categoryAdapter.setSelectedItem(-1);
    }

    private void setClickListener(int viewId, Runnable action) {
        view.findViewById(viewId).setOnClickListener(v -> action.run());
    }

    private void initCategoryRecycler() {
        categoryRecycler = view.findViewById(R.id.store_category_recycler);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryAdapter = new CategoryAdapter(CategoryManager.getInstance().getAllCategories(), getContext(), this::updateProducts);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private void initProductRecycler() {
        productRecycler = view.findViewById(R.id.store_products_recycler);
        productRecycler.setLayoutManager(new GridLayoutManager(getContext(), getSpanCountBasedOnScreenWidth()));

        productData = ProductManager.getInstance().getAllProducts();
        productAdapter = new ProductAdapter(productData, getContext());
        productRecycler.setAdapter(productAdapter);
    }

    private void updateProducts(Category category) {
        //Toast.makeText(getContext(), category.getName(), Toast.LENGTH_SHORT).show();
        Log.d("MyMedsStore", "Updating products for category: " + category.getName());
        updateProducts(ProductManager.getInstance().getProductsByCategory(category.getName()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateProducts(List<Product> products) {
        Log.d("MyMedsStore", "Updating UI with " + products.size() + " products.");

        productData.clear();
        productData.addAll(products);
        productAdapter.notifyDataSetChanged();
    }

    private int getSpanCountBasedOnScreenWidth() {
        int screenWidthDp = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        return (screenWidthDp >= 1200) ? 4 : (screenWidthDp >= 800) ? 3 : 2;
    }
}