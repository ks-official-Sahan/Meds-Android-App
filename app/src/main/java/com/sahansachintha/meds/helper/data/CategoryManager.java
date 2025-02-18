package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class CategoryManager {

    private static volatile CategoryManager instance;
    private final List<Category> categories;

    private CategoryManager() {
        this.categories = new CopyOnWriteArrayList<>();
        initializeSampleData();
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

    private void initializeSampleData() {
        addCategory(1, "Pain Killer", R.drawable.ic_medicine);
        addCategory(2, "Antibiotic", R.drawable.ic_medication);
        addCategory(3, "Allergy Relief", R.drawable.ic_medicine);
        addCategory(4, "Digestive Health", R.drawable.ic_medicine);
        addCategory(5, "Diabetes Care", R.drawable.ic_medicine);
        addCategory(6, "Heart Health", R.drawable.ic_medication);
        addCategory(7, "Respiratory Care", R.drawable.ic_medication);
    }

    public boolean addCategory(int id, String name, int iconResId) {
        if (getCategoryById(id).isPresent()) {
            return false; // Prevent duplicate IDs
        }
        return categories.add(new Category(id, name, iconResId));
    }

    public boolean updateCategory(int id, String name, int iconResId) {
        Optional<Category> optionalCategory = getCategoryById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(name);
            category.setImgId(iconResId);
            return true;
        }
        return false; // Category not found
    }

    public boolean removeCategory(int id) {
        return categories.removeIf(category -> category.getId() == id);
    }

    public List<Category> getAllCategories() {
        //return Collections.unmodifiableList(categories);
        return new ArrayList<>(categories);
    }

    private Optional<Category> getCategoryById(int id) {
        return categories.stream().filter(category -> category.getId() == id).findFirst();
    }
}
