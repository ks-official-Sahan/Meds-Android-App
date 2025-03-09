package com.sahansachintha.meds.fragment.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.CategoryAdapter;
import com.sahansachintha.meds.adapters.ProductAdapter;
import com.sahansachintha.meds.helper.data.CategoryManager;
import com.sahansachintha.meds.helper.data.ProductManager;
import com.sahansachintha.meds.model.Category;
import com.sahansachintha.meds.model.Product;

import java.util.List;

public class StoreFragment extends Fragment implements SensorEventListener {

    private RecyclerView categoryRecycler, productRecycler;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Product> productData;
    private List<Category> categoryData;
    private View view;
    private EditText search;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final int SHAKE_THRESHOLD = 200;
    private static final int SHAKE_TIME_MS = 500;
    private long lastUpdate;
    private float lastX, lastY, lastZ;
    private boolean isRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store, container, false);

        view.findViewById(R.id.store_container).setAlpha(0);
        view.findViewById(R.id.store_container).setScaleX(0.1f);
        view.findViewById(R.id.store_container).setScaleY(0.1f);
        view.findViewById(R.id.store_container).animate().alpha(1f).scaleY(1f).scaleX(1f).setDuration(1500).start();

        initUI();
        setupSearchListener();
        setupSensor();

        //new Handler().postDelayed(this::viewAll, 2000);

        return view;
    }

    private void initUI() {
        search = view.findViewById(R.id.store_search);
        categoryRecycler = view.findViewById(R.id.store_category_recycler);
        productRecycler = view.findViewById(R.id.store_products_recycler);

        initCategoryRecycler();
        initProductRecycler();

        view.findViewById(R.id.products_view_all_store).setOnClickListener(v -> viewAll());
        view.findViewById(R.id.products_refresh_store).setOnClickListener(v -> viewAll());
    }

    private void setupSearchListener() {
        search.setOnFocusChangeListener((v, hasFocus) -> toggleCategorySection(hasFocus));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    updateProducts(s.toString());
                } else {
                    viewAll();
                    search.clearFocus();
                    toggleCategorySection(search.hasFocus());

                    view.findViewById(R.id.store_category_text).requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void toggleCategorySection(boolean hasFocus) {
        int visibility = hasFocus ? View.GONE : View.VISIBLE;
        categoryRecycler.setVisibility(visibility);
        view.findViewById(R.id.store_category_text).setVisibility(visibility);
        if (!hasFocus) {
            hideKeyboard();
            view.findViewById(R.id.store_category_text).requestFocus();
        }
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void setupSensor() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void viewAll() {
        updateProducts(ProductManager.getInstance().getAllProducts());
        updateCategories(CategoryManager.getInstance().getAllCategories());
        categoryAdapter.setSelectedItem(-1);
        search.clearFocus();
    }

    private void setClickListener(int viewId, Runnable action) {
        view.findViewById(viewId).setOnClickListener(v -> action.run());
    }

    private void initCategoryRecycler() {
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryData = CategoryManager.getInstance().getAllCategories();
        categoryAdapter = new CategoryAdapter(categoryData, getContext(), this::updateProducts);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private void initProductRecycler() {
        productRecycler.setLayoutManager(new GridLayoutManager(getContext(), getSpanCountBasedOnScreenWidth()));

        productData = ProductManager.getInstance().getAllProducts();
        productAdapter = new ProductAdapter(productData, getContext());
        productRecycler.setAdapter(productAdapter);

        view.findViewById(R.id.products_empty_view).setVisibility(productData.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateProducts(Category category) {
        if (category != null) {
            Log.d("MyMedsStore", "Updating products for category: " + category.getName());
            updateProducts(ProductManager.getInstance().getProductsByCategory(category.getName()));
        }
    }

    private void updateProducts(String name) {
        Log.d("MyMedsStore", "Updating products for name: " + name);
        updateProducts(ProductManager.getInstance().getProductsByName(name));
        categoryAdapter.setSelectedItem(-1);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateProducts(List<Product> products) {
        if (products != null) {
            Log.d("MyMedsStore", "Updating UI with " + products.size() + " products.");
            productData.clear();
            productData.addAll(products);
            productAdapter.notifyDataSetChanged();

            //view.findViewById(R.id.products_empty_view).setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            View emptyView = view.findViewById(R.id.products_empty_view);
            if (emptyView != null) {
                emptyView.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateCategories(List<Category> categories) {
        if (categories != null) {
            Log.d("MyMedsStore", "Updating UI with " + categories.size() + " categories.");
            categoryData.clear();
            categoryData.addAll(categories);
            categoryAdapter.notifyDataSetChanged();
        }
    }

    private int getSpanCountBasedOnScreenWidth() {
        int screenWidthDp = getResources().getConfiguration().screenWidthDp;
        return (screenWidthDp >= 1200) ? 4 : (screenWidthDp >= 800) ? 3 : 2;
    }

    /* Sensor Handling */
    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null && sensorManager != null) {
            if (!isRegistered) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                isRegistered = true;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > SHAKE_TIME_MS) {
            long diffTime = curTime - lastUpdate;
            lastUpdate = curTime;

            //double acceleration = Math.sqrt(x * x + y * y + z * z);
            //float acceleration = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;
            float acceleration = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000;

            //Log.d("SensorTest", "Acceleration: " + acceleration);

            if (acceleration > SHAKE_THRESHOLD) {
                Log.d("SensorTest", "Shake detected! Speed: " + acceleration);
                if (search.getText().length() > 0) {
                    search.setText("");
                    updateProducts(ProductManager.getInstance().getAllProducts());
                    search.clearFocus();
                    hideKeyboard();
                } else {
                    //if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}