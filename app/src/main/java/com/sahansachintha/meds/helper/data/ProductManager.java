package com.sahansachintha.meds.helper.data;

import com.sahansachintha.meds.model.Product;

import java.util.ArrayList;

public class ProductManager {

        private static ProductManager productManager;

        private ProductManager() {
        }

        public static ProductManager getInstance() {
                if (productManager == null) {
                        productManager = new ProductManager();
                }
                return productManager;
        }

        public ArrayList<Product> getSampleData() {
                ArrayList<Product> productList = new ArrayList<>();

                productList.add(new Product(
                                1,
                                "Paracetamol",
                                "Paracetamol 500mg Tablets – Pain & Fever Relief",
                                "A widely used pain reliever and fever reducer. Effective for headaches, muscle pain, colds, and flu. Safe for most ages when taken as directed.",
                                "https://picsum.photos/600/801",
                                "Pain Killer",
                                "500mg",
                                "320",
                                100));

                productList.add(new Product(
                                2,
                                "Aspirin",
                                "Aspirin 75mg – Pain Relief & Heart Health",
                                "Aspirin is used for pain relief, fever reduction, and blood-thinning to support heart health. Consult a doctor before long-term use.",
                                "https://picsum.photos/600/802",
                                "Pain Killer",
                                "75mg",
                                "280",
                                75));

                productList.add(new Product(
                                3,
                                "Amoxicillin",
                                "Amoxicillin 500mg Capsules – Antibiotic for Infections",
                                "A broad-spectrum antibiotic used for respiratory, urinary, and skin infections. Must be taken as prescribed by a doctor.",
                                "https://picsum.photos/600/799",
                                "Antibiotic",
                                "500mg",
                                "540",
                                50));

                productList.add(new Product(
                                4,
                                "Cetirizine",
                                "Cetirizine 10mg Tablets – Allergy Relief",
                                "An effective antihistamine that relieves sneezing, runny nose, itchy eyes, and skin allergies. Provides 24-hour relief.",
                                "https://picsum.photos/600/804",
                                "Allergy Relief",
                                "10mg",
                                "250",
                                80));

                productList.add(new Product(
                                5,
                                "Omeprazole",
                                "Omeprazole 20mg – Acid Reflux & Ulcer Treatment",
                                "A highly effective acid reducer used for GERD, stomach ulcers, and heartburn. Best taken before meals.",
                                "https://picsum.photos/600/805",
                                "Digestive Health",
                                "20mg",
                                "460",
                                60));

                productList.add(new Product(
                                6,
                                "Metformin",
                                "Metformin 500mg – Blood Sugar Control",
                                "An essential medication for type 2 diabetes, helping regulate blood sugar and improve insulin response.",
                                "https://picsum.photos/600/806",
                                "Diabetes Care",
                                "500mg",
                                "620",
                                100));

                productList.add(new Product(
                                7,
                                "Ibuprofen",
                                "Ibuprofen 400mg Tablets – Pain & Inflammation Relief",
                                "A powerful pain reliever and anti-inflammatory medication for headaches, arthritis, and muscle pain. Take with food.",
                                "https://picsum.photos/600/807",
                                "Pain Killer",
                                "400mg",
                                "350",
                                90));

                productList.add(new Product(
                                8,
                                "Losartan",
                                "Losartan 50mg – Blood Pressure Control",
                                "Used to manage high blood pressure and reduce stroke risk. May also help kidney protection in diabetics.",
                                "https://picsum.photos/600/808",
                                "Heart Health",
                                "50mg",
                                "540",
                                40));

                productList.add(new Product(
                                9,
                                "Atorvastatin",
                                "Atorvastatin 10mg – Cholesterol Control",
                                "A statin that lowers cholesterol and reduces the risk of heart disease. Best taken at night.",
                                "https://picsum.photos/600/809",
                                "Heart Health",
                                "10mg",
                                "850",
                                70));

                productList.add(new Product(
                                10,
                                "Salbutamol",
                                "Salbutamol 100mcg Inhaler – Fast Asthma Relief",
                                "A bronchodilator that quickly relieves asthma and COPD symptoms like shortness of breath and wheezing.",
                                "https://picsum.photos/600/810",
                                "Respiratory Care",
                                "100mcg per dose",
                                "1200",
                                30));

                return productList;
        }
}
