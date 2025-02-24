package com.sahansachintha.meds.helper.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sahansachintha.meds.model.Product;
import com.sahansachintha.meds.network.ProductApiService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductManager {

    private static volatile ProductManager instance;
    private final List<Product> products;
    private final ProductApiService apiService;
    private final Gson gson;

    private ProductManager() {
        this.products = new CopyOnWriteArrayList<>();
        this.apiService = new ProductApiService();
        this.gson = new Gson();
        initializeSampleData(); // Optional: local fallback/sample data.
        updateProductListFromServer(); // Load products from backend on initialization.
    }

    public static ProductManager getInstance() {
        if (instance == null) {
            synchronized (ProductManager.class) {
                if (instance == null) {
                    instance = new ProductManager();
                }
            }
        }
        return instance;
    }

    // Local in-memory add product methods:
    public boolean addProduct(String name, String shortDesc, String longDesc, String imageUrl,
                              String category, String dosage, String price, int stock) {
        return addProduct(name, shortDesc, longDesc, imageUrl, category, dosage, price, stock, false);
    }

    public boolean addProduct(String name, String shortDesc, String longDesc, String imageUrl,
                              String category, String dosage, String price, int stock, boolean isAnotherLeft) {
        Product product = new Product(null, name, shortDesc, longDesc, imageUrl, category, dosage, price, stock);
        return addProduct(product, isAnotherLeft);
    }

    public boolean addProduct(Product product, boolean isAnotherLeft) {
        boolean saved = saveProductToBackend(product);
        if (!isAnotherLeft) {
            new Thread(this::updateProductListFromServer).start();
        }
        return saved;
    }

    public boolean updateProduct(String id, String name, String shortDesc, String longDesc, String imageUrl,
                                 String category, String dosage, String price, int stock) {
        Optional<Product> optionalProduct = getProductById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(name);
            product.setTitle(shortDesc);
            product.setDescription(longDesc);
            product.setImage(imageUrl);
            product.setCategoryName(category);
            product.setDosage(dosage);
            product.setPrice(price);
            product.setQuantity(stock);
            updateProductToBackend(product);
            return true;
        }
        return false; // Product not found
    }

    public boolean removeProduct(String id) {
        boolean removed = products.removeIf(product -> product.getId().equalsIgnoreCase(id));
        // Optionally, call a delete endpoint on the backend if available.
        return removed;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return products.stream()
                .filter(product -> product.getCategoryName().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());
    }

    private Optional<Product> getProductById(String id) {
        return products.stream().filter(product -> product.getId().equalsIgnoreCase(id)).findFirst();
    }

    private void updateProductListFromServer() {
        loadProductsFromBackend(new LoadProductsCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                Log.i("MyMedsProducts", "Loaded products from backend successfully.");
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("MyMedsProducts", "Error loading products from backend: " + errorMessage);
            }
        });
    }

    // Load products from the backend and update the local list.
    public void loadProductsFromBackend(final LoadProductsCallback callback) {
        apiService.getAllProducts(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();
                    Type productListType = new TypeToken<List<Product>>() {}.getType();
                    List<Product> backendProducts = gson.fromJson(jsonResponse, productListType);
                    products.clear();
                    products.addAll(backendProducts);

                    if (backendProducts.isEmpty()) {
                        seedProducts();
                    }

                    callback.onSuccess(new ArrayList<>(products));
                } else {
                    callback.onFailure("Error: " + response.code());
                }
                response.close();
            }
        });
    }

    // Save a product to the backend.
    private boolean saveProductToBackend(Product product) {
        final boolean[] saved = {false};
        apiService.createProduct(product, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MyMedsProducts", "Error saving product: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();
                    Product createdProduct = gson.fromJson(jsonResponse, Product.class);
                    Log.i("MyMedsProducts", "Product saved to backend successfully. ID: " + createdProduct.getId());
                    saved[0] = true;
                } else {
                    Log.e("MyMedsProducts", "Error saving product, HTTP code: " + response.code());
                }
                response.close();
            }
        });
        return saved[0];
    }

    // Update a product on the backend.
    private void updateProductToBackend(Product product) {
        apiService.updateProduct(product, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MyMedsProducts", "Error updating product: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("MyMedsProducts", "Product updated on backend successfully.");
                } else {
                    Log.e("MyMedsProducts", "Error updating product, HTTP code: " + response.code());
                }
                response.close();
            }
        });
    }

    // Callback interface for loading products.
    public interface LoadProductsCallback {
        void onSuccess(List<Product> products);
        void onFailure(String errorMessage);
    }

    public interface AddProductCallback {
        void onSuccess(Product product);
        void onFailure(String errorMessage);
    }

    // ---------- Sample Data & Seeding ----------

    // Sample data list (with IDs as strings for local fallback).
    List<Product> sampleDataList = new ArrayList<Product>() {{
        add(new Product("1",
                "Paracetamol",
                "Paracetamol 500mg Tablets – Pain & Fever Relief",
                "A widely used pain reliever and fever reducer. Effective for headaches, muscle pain, colds, and flu. Safe for most ages when taken as directed.",
                "https://picsum.photos/600/800?random=1",
                "Pain Killer",
                "500mg",
                "320",
                100));
        add(new Product("2",
                "Aspirin",
                "Aspirin 75mg – Pain Relief & Heart Health",
                "Aspirin is used for pain relief, fever reduction, and blood-thinning to support heart health. Consult a doctor before long-term use.",
                "https://picsum.photos/600/800?random=2",
                "Pain Killer",
                "75mg",
                "280",
                75));
        add(new Product("3",
                "Amoxicillin",
                "Amoxicillin 500mg Capsules – Antibiotic for Infections",
                "A broad-spectrum antibiotic used for respiratory, urinary, and skin infections. Must be taken as prescribed by a doctor.",
                "https://picsum.photos/600/800?random=3",
                "Antibiotic",
                "500mg",
                "540",
                50));
        add(new Product("4",
                "Cetirizine",
                "Cetirizine 10mg Tablets – Allergy Relief",
                "An effective antihistamine that relieves sneezing, runny nose, itchy eyes, and skin allergies. Provides 24-hour relief.",
                "https://picsum.photos/600/800?random=4",
                "Allergy Relief",
                "10mg",
                "250",
                80));
        add(new Product("5",
                "Omeprazole",
                "Omeprazole 20mg – Acid Reflux & Ulcer Treatment",
                "A highly effective acid reducer used for GERD, stomach ulcers, and heartburn. Best taken before meals.",
                "https://picsum.photos/600/800?random=5",
                "Digestive Health",
                "20mg",
                "460",
                60));
        add(new Product("6",
                "Metformin",
                "Metformin 500mg – Blood Sugar Control",
                "An essential medication for type 2 diabetes, helping regulate blood sugar and improve insulin response.",
                "https://picsum.photos/600/800?random=6",
                "Diabetes Care",
                "500mg",
                "620",
                100));
        add(new Product("7",
                "Ibuprofen",
                "Ibuprofen 400mg Tablets – Pain & Inflammation Relief",
                "A powerful pain reliever and anti-inflammatory medication for headaches, arthritis, and muscle pain. Take with food.",
                "https://picsum.photos/600/800?random=7",
                "Pain Killer",
                "400mg",
                "350",
                90));
        add(new Product("8",
                "Losartan",
                "Losartan 50mg – Blood Pressure Control",
                "Used to manage high blood pressure and reduce stroke risk. May also help kidney protection in diabetics.",
                "https://picsum.photos/600/800?random=8",
                "Heart Health",
                "50mg",
                "540",
                40));
        add(new Product("9",
                "Atorvastatin",
                "Atorvastatin 10mg – Cholesterol Control",
                "A statin that lowers cholesterol and reduces the risk of heart disease. Best taken at night.",
                "https://picsum.photos/600/800?random=9",
                "Heart Health",
                "10mg",
                "850",
                70));
        add(new Product("10",
                "Salbutamol",
                "Salbutamol 100mcg Inhaler – Fast Asthma Relief",
                "A bronchodilator that quickly relieves asthma and COPD symptoms like shortness of breath and wheezing.",
                "https://picsum.photos/600/800?random=10",
                "Respiratory Care",
                "100mcg per dose",
                "1200",
                30));
    }};

    // Add the sample data to the local list.
    private void initializeSampleData() {
        products.addAll(sampleDataList);
    }

    // ---------- Seeding Sample Data to the Backend ----------

    public interface SeedProductsCallback {
        void onSuccess(List<Product> seededProducts);
        void onFailure(String errorMessage);
    }

    public void seedProducts() {
        seedProductsToDatabase(new SeedProductsCallback() {
            @Override
            public void onSuccess(List<Product> seededProducts) {
                Log.i("MyMedsProducts", "Products seeded successfully.");
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("MyMedsProducts", "Error seeding products: " + errorMessage);
            }
        });
    }

    public void seedProductsToDatabase(final SeedProductsCallback callback) {
        final int totalProducts = sampleDataList.size();
        final AtomicInteger counter = new AtomicInteger(0);
        final List<Product> seededProducts = new ArrayList<>();

        // Iterate over each sample product.
        for (final Product sampleProduct : sampleDataList) {
            // Create a new product instance with id = null so the backend can generate it.
            Product productToSeed = new Product(
                    null,
                    sampleProduct.getName(),
                    sampleProduct.getTitle(),
                    sampleProduct.getDescription(),
                    sampleProduct.getImage(),
                    sampleProduct.getCategoryName(),
                    sampleProduct.getDosage(),
                    sampleProduct.getPrice(),
                    sampleProduct.getQuantity()
            );

            // Call the API to create the product.
            apiService.createProduct(productToSeed, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("MyMedsProducts", "Error seeding product: " + e.getMessage());
                    if (counter.incrementAndGet() == totalProducts) {
                        if (seededProducts.isEmpty()) {
                            callback.onFailure("Seeding failed for all products.");
                        } else {
                            callback.onSuccess(seededProducts);
                        }
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Product createdProduct = gson.fromJson(jsonResponse, Product.class);
                        seededProducts.add(createdProduct);
                        Log.i("MyMedsProducts", "Product seeded successfully. ID: " + createdProduct.getId());
                    } else {
                        Log.e("MyMedsProducts", "Error seeding product, HTTP code: " + response.code());
                    }
                    if (counter.incrementAndGet() == totalProducts) {
                        products.clear();
                        products.addAll(seededProducts);
                        callback.onSuccess(seededProducts);
                    }
                    response.close();
                }
            });
        }
    }
}
