package com.sahansachintha.meds.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.ProductAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.CartManager;
import com.sahansachintha.meds.helper.data.ProductManager;
import com.sahansachintha.meds.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductViewActivity extends AppCompatActivity {

    /* Recycler View */
    private RecyclerView productRecycler;
    private List<Product> productData;
    private ProductAdapter productAdapter;
    private LinearLayoutManager productLayoutManager;

    /* Product */
    private Product product;
    private int maxQuantity = -1;
    private int id;

    /* Fields */
    private TextView title;
    private TextView price;
    private TextView dosage;
    private TextView description;
    private ImageView image;
    private EditText quantityField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_view_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        setProduct();
        setupQuantity();

        findViewById(R.id.product_view_buy_btn).setOnClickListener(v -> {
            if (product != null) {
                int quantity = Integer.parseInt(quantityField.getText().toString());
                CartManager.addProduct(product, quantity);
                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        setUpToolbar();

        initProductRecycler();
    }

    private void setUpToolbar() {
        findViewById(R.id.product_view_back_btn).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.product_view_profile_btn).setOnClickListener(v -> NavigationHelper.getInstance().viewProfile(this));
    }

    private void setProduct() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            product = getIntent().getSerializableExtra("product", Product.class);
        } else {
            product = (Product) getIntent().getSerializableExtra("product");
        }
        if (product != null) {
            //Toast.makeText(this, product.getTitle(), Toast.LENGTH_SHORT).show();
            title.setText(product.getTitle());
            price.setText(product.getPrice());
            dosage.setText(product.getDosage());
            description.setText(product.getDescription());
            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.med_asset_05) // Optional: Add a placeholder image
                    .error(R.drawable.error_image) // Optional: Handle errors
                    .into(image);

            maxQuantity = product.getQuantity();
        }
    }

    private void initViews() {
        productRecycler = findViewById(R.id.product_view_recycler);

        title = findViewById(R.id.product_view_title);
        price = findViewById(R.id.product_view_price);
        dosage = findViewById(R.id.product_view_dosage);
        description = findViewById(R.id.product_view_description);
        image = findViewById(R.id.product_view_img);
    }

    private void initProductRecycler() {
        //productRecycler = findViewById(R.id.product_view_recycler);

        productLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        productRecycler.setLayoutManager(productLayoutManager);

        productData = getProductData();

        productAdapter = new ProductAdapter(productData, this);
        productRecycler.setAdapter(productAdapter);
    }

    private List<Product> getProductData() {
        List<Product> productList = new ArrayList<>(ProductManager.getInstance().getSampleData());

        if (product != null) {
            productList.removeIf(p -> p.getId() == product.getId());
        }

        return productList;
    }

    private void setupQuantity() {
        ImageView decrement = findViewById(R.id.cart_item_decrement);
        ImageView increment = findViewById(R.id.cart_item_increment);
        quantityField = findViewById(R.id.cart_item_quantity);

        quantityField.setText(String.valueOf(1));
        //maxQuantity = getIntent().getIntExtra("maxQuantity", -1);

        decrement.setOnClickListener(v -> {
            int quantity = Integer.parseInt(this.quantityField.getText().toString());
            if (quantity > 1) {
                quantity--;
                this.quantityField.setText(String.valueOf(quantity));
            }
        });

        increment.setOnClickListener(v -> {
            int quantity = Integer.parseInt(this.quantityField.getText().toString());
            if (maxQuantity != -1) {
                if (maxQuantity >= quantity) {
                    quantity++;
                    this.quantityField.setText(String.valueOf(quantity));
                }
            } else {
                quantity++;
                this.quantityField.setText(String.valueOf(quantity));
            }
        });
    }
}