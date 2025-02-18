package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ProductManager {

    private static volatile ProductManager instance;
    private final List<Product> products;

    private ProductManager() {
        this.products = new CopyOnWriteArrayList<>();
        initializeSampleData();
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

    private void initializeSampleData() {
        addProduct(1, "Paracetamol", "Paracetamol 500mg Tablets – Pain & Fever Relief",
                "A widely used pain reliever and fever reducer. Effective for headaches, muscle pain, colds, and flu. Safe for most ages when taken as directed.",
                "https://picsum.photos/600/801", "Pain Killer", "500mg", "320", 100);

        addProduct(2, "Aspirin", "Aspirin 75mg – Pain Relief & Heart Health",
                "Aspirin is used for pain relief, fever reduction, and blood-thinning to support heart health. Consult a doctor before long-term use.",
                "https://picsum.photos/600/802", "Pain Killer", "75mg", "280", 75);

        addProduct(3, "Amoxicillin", "Amoxicillin 500mg Capsules – Antibiotic for Infections",
                "A broad-spectrum antibiotic used for respiratory, urinary, and skin infections. Must be taken as prescribed by a doctor.",
                "https://picsum.photos/600/799", "Antibiotic", "500mg", "540", 50);

        addProduct(4, "Cetirizine", "Cetirizine 10mg Tablets – Allergy Relief",
                "An effective antihistamine that relieves sneezing, runny nose, itchy eyes, and skin allergies. Provides 24-hour relief.",
                "https://picsum.photos/600/804", "Allergy Relief", "10mg", "250", 80);

        addProduct(5, "Omeprazole", "Omeprazole 20mg – Acid Reflux & Ulcer Treatment",
                "A highly effective acid reducer used for GERD, stomach ulcers, and heartburn. Best taken before meals.",
                "https://picsum.photos/600/805", "Digestive Health", "20mg", "460", 60);

        addProduct(6, "Metformin", "Metformin 500mg – Blood Sugar Control",
                "An essential medication for type 2 diabetes, helping regulate blood sugar and improve insulin response.",
                "https://picsum.photos/600/806", "Diabetes Care", "500mg", "620", 100);

        addProduct(7, "Ibuprofen", "Ibuprofen 400mg Tablets – Pain & Inflammation Relief",
                "A powerful pain reliever and anti-inflammatory medication for headaches, arthritis, and muscle pain. Take with food.",
                "https://picsum.photos/600/807", "Pain Killer", "400mg", "350", 90);

        addProduct(8, "Losartan", "Losartan 50mg – Blood Pressure Control",
                "Used to manage high blood pressure and reduce stroke risk. May also help kidney protection in diabetics.",
                "https://picsum.photos/600/808", "Heart Health", "50mg", "540", 40);

        addProduct(9, "Atorvastatin", "Atorvastatin 10mg – Cholesterol Control",
                "A statin that lowers cholesterol and reduces the risk of heart disease. Best taken at night.",
                "https://picsum.photos/600/809", "Heart Health", "10mg", "850", 70);

        addProduct(10, "Salbutamol", "Salbutamol 100mcg Inhaler – Fast Asthma Relief",
                "A bronchodilator that quickly relieves asthma and COPD symptoms like shortness of breath and wheezing.",
                "https://picsum.photos/600/810", "Respiratory Care", "100mcg per dose", "1200", 30);
    }

    public boolean addProduct(int id, String name, String shortDesc, String longDesc, String imageUrl,
                              String category, String dosage, String price, int stock) {
        if (getProductById(id).isPresent()) {
            return false; // Prevent duplicate IDs
        }
        return products.add(new Product(id, name, shortDesc, longDesc, imageUrl, category, dosage, price, stock));
    }

    public boolean updateProduct(int id, String name, String shortDesc, String longDesc, String imageUrl,
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
            return true;
        }
        return false; // Product not found
    }

    public boolean removeProduct(int id) {
        return products.removeIf(product -> product.getId() == id);
    }

    public List<Product> getAllProducts() {
        //return Collections.unmodifiableList(products);
        return new ArrayList<>(products);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return products.stream()
                .filter(product -> product.getCategoryName().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());
    }

    private Optional<Product> getProductById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst();
    }
}
