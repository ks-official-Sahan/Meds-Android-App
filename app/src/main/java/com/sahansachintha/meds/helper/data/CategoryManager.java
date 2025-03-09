package com.sahansachintha.meds.helper.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sahansachintha.meds.model.Category;
import com.sahansachintha.meds.network.CategoryApiService;

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

public class CategoryManager {

    private static volatile CategoryManager instance;
    private final List<Category> categories;
    private final CategoryApiService apiService;
    private final Gson gson;

    // Sample data list for categories.
    private final List<Category> sampleDataList = new ArrayList<Category>() {{
        add(new Category("1", "Pain Killer", "https://picsum.photos/200/200?random=1"));
        add(new Category("2", "Antibiotic", "https://picsum.photos/200/200?random=2"));
        add(new Category("3", "Allergy Relief", "https://picsum.photos/200/200?random=3"));
        add(new Category("4", "Digestive Health", "https://picsum.photos/200/200?random=4"));
        add(new Category("5", "Diabetes Care", "https://picsum.photos/200/200?random=5"));
        add(new Category("6", "Heart Health", "https://picsum.photos/200/200?random=6"));
        add(new Category("7", "Respiratory Care", "https://picsum.photos/200/200?random=7"));
    }};

    private CategoryManager() {
        this.categories = new CopyOnWriteArrayList<>();
        this.apiService = new CategoryApiService();
        this.gson = new Gson();
        //initializeSampleData(); // Local fallback/sample data.
        updateCategoryListFromServer(); // Synchronize with backend.
    }

    public static CategoryManager getInstance() {
        if (instance == null) {
            synchronized (CategoryManager.class) {
                if (instance == null) {
                    instance = new CategoryManager();
                }
            }
        }
        return instance;
    }

    // Initialize local sample data.
    private void initializeSampleData() {
        categories.addAll(sampleDataList);
    }

    // Save sample data to the backend (seeding).
    public interface SeedCategoriesCallback {
        void onSuccess(List<Category> seededCategories);
        void onFailure(String errorMessage);
    }

    public void seedCategoriesToDatabase(final SeedCategoriesCallback callback) {
        final int totalCategories = sampleDataList.size();
        final AtomicInteger counter = new AtomicInteger(0);
        final List<Category> seededCategories = new ArrayList<>();

        for (final Category sampleCategory : sampleDataList) {
            // Create a new category with id null to let backend generate it.
            Category categoryToSeed = new Category(null, sampleCategory.getName(), sampleCategory.getImage());

            apiService.createCategory(categoryToSeed, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("MyMedsCategories", "Error seeding category: " + e.getMessage());
                    if (counter.incrementAndGet() == totalCategories) {
                        if (seededCategories.isEmpty()) {
                            callback.onFailure("Seeding failed for all categories.");
                        } else {
                            callback.onSuccess(seededCategories);
                        }
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Category createdCategory = gson.fromJson(jsonResponse, Category.class);
                        seededCategories.add(createdCategory);
                        Log.i("MyMedsCategories", "Category seeded successfully. ID: " + createdCategory.getId());
                    } else {
                        Log.e("MyMedsCategories", "Error seeding category, HTTP code: " + response.code());
                    }
                    if (counter.incrementAndGet() == totalCategories) {
                        categories.clear();
                        categories.addAll(seededCategories);
                        callback.onSuccess(seededCategories);
                    }
                    response.close();
                }
            });
        }
    }

    public void seedCategories() {
        seedCategoriesToDatabase(new SeedCategoriesCallback() {
            @Override
            public void onSuccess(List<Category> seededCategories) {
                Log.i("MyMedsCategories", "Categories seeded successfully.");
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("MyMedsCategories", "Error seeding categories: " + errorMessage);
            }
        });
    }

    // Get all categories.
    public List<Category> getAllCategories() {
        if (categories.isEmpty()) {
            updateCategoryListFromServer();
        }
        return new ArrayList<>(categories);
    }

    // Update a category in the local list and backend.
    public boolean updateCategory(String id, String name, String image) {
        Optional<Category> optionalCategory = getCategoryById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(name);
            category.setImage(image);
            updateCategoryToBackend(category);
            return true;
        }
        return false;
    }

    public boolean removeCategory(String id) {
        boolean removed = categories.removeIf(category -> category.getId().equalsIgnoreCase(id));
        // Optionally call a delete endpoint on the backend.
        return removed;
    }

    private Optional<Category> getCategoryById(String id) {
        return categories.stream()
                .filter(category -> category.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public void updateCategoryListFromServer() {
        updateCategoryListFromServer(() -> {});
    }

    public void updateCategoryListFromServer(Runnable callback) {
        loadCategoriesFromBackend(new LoadCategoriesCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                Log.i("MyMedsCategories", "Loaded categories from backend successfully.");
                callback.run();
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("MyMedsCategories", "Error loading categories: " + errorMessage);
            }
        });
    }

    // Load categories from the backend.
    public void loadCategoriesFromBackend(final LoadCategoriesCallback callback) {
        apiService.getAllCategories(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();

                    Type categoryListType = new TypeToken<List<Category>>() {}.getType();
                    List<Category> backendCategories = gson.fromJson(jsonResponse, categoryListType);
                    Log.i("MyMedsCategories", "Loaded " + backendCategories.size() + " categories from backend.");

                    categories.clear();
                    categories.addAll(backendCategories);

                    if (backendCategories.isEmpty()) {
                        seedCategories();
                    }
                    //callback.onSuccess(new ArrayList<>(categories));
                    callback.onSuccess(new ArrayList<>(backendCategories));
                } else {
                    callback.onFailure("Error: " + response.code());
                }
                response.close();
            }
        });
    }

    // Save (create) a category on the backend.
    public void saveCategoryToBackend(Category category) {
        apiService.createCategory(category, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MyMedsCategories", "Error saving category: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("MyMedsCategories", "Category saved to backend successfully.");
                } else {
                    Log.e("MyMedsCategories", "Error saving category, HTTP code: " + response.code());
                }
                response.close();
            }
        });
    }

    // Update a category on the backend.
    public void updateCategoryToBackend(Category category) {
        apiService.updateCategory(category, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MyMedsCategories", "Error updating category: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("MyMedsCategories", "Category updated on backend successfully.");
                } else {
                    Log.e("MyMedsCategories", "Error updating category, HTTP code: " + response.code());
                }
                response.close();
            }
        });
    }

    // Callback interface for loading categories.
    public interface LoadCategoriesCallback {
        void onSuccess(List<Category> categories);
        void onFailure(String errorMessage);
    }
}
