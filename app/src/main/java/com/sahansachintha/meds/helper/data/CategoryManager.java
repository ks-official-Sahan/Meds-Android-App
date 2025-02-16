package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.model.Category;
import com.sahansachintha.meds.model.Product;

import java.util.ArrayList;

public class CategoryManager {

    private static CategoryManager categoryManager;

    private CategoryManager() {
    }

    public static CategoryManager getInstance() {
        if (categoryManager == null) {
            categoryManager = new CategoryManager();
        }
        return categoryManager;
    }

    public ArrayList<Category> getSampleData() {
        ArrayList<Category> categoryList = new ArrayList<>();

        categoryList.add(new Category(1, "Tablets", R.drawable.ic_medicine));
        categoryList.add(new Category(2, "Capsules", R.drawable.ic_medication));
        categoryList.add(new Category(3, "Syrups", R.drawable.ic_medicine));

        return categoryList;
    }
}
