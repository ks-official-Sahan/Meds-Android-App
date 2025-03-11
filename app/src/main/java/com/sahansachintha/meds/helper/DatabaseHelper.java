package com.sahansachintha.meds.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.sahansachintha.meds.MyMeds;
import com.sahansachintha.meds.model.Cart;
import com.sahansachintha.meds.model.Product;
import com.sahansachintha.meds.model.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static SQLiteHelper sqLiteHelper;
    private static Gson gson = new Gson();

    // Initialize the SQLiteHelper with application context.
    public static void init(Context context) {
        if (sqLiteHelper == null) {
            sqLiteHelper = new SQLiteHelper(context.getApplicationContext());
        }
    }

    public static void init() {
        if (sqLiteHelper == null) {
            sqLiteHelper = new SQLiteHelper(MyMeds.getInstance().getApplicationContext());
        }
    }

    // Save the given cart by replacing the current items in the local DB.
    public static void updateCart(Cart cart) {
        if (sqLiteHelper == null) {
            throw new IllegalStateException("DatabaseHelper not initialized. Call init() first.");
        }
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        try {
            // Clear current cart items
            db.delete(SQLiteHelper.TABLE_CART_ITEMS, null, null);

            List<ProductItem> items = cart.getCartItems();
            if (items == null) {
                items = new ArrayList<>();
            }

            // Insert each ProductItem into the table.
            for (ProductItem item : items) {
                ContentValues values = new ContentValues();
                values.put(SQLiteHelper.COLUMN_PRODUCT_ID, item.getProduct().getId());
                values.put(SQLiteHelper.COLUMN_QUANTITY, item.getQuantity());
                // Optionally store product details as JSON.
                String productJson = gson.toJson(item.getProduct());
                values.put(SQLiteHelper.COLUMN_PRODUCT_JSON, productJson);
                db.insert(SQLiteHelper.TABLE_CART_ITEMS, null, values);
            }
        } finally {
            db.close();
        }
    }

    // Load the cart from the local DB and return a Cart instance.
    public static Cart loadCart() {
        if (sqLiteHelper == null) {
            throw new IllegalStateException("DatabaseHelper not initialized. Call init() first.");
        }
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        List<ProductItem> productItems = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(SQLiteHelper.TABLE_CART_ITEMS,
                    new String[]{SQLiteHelper.COLUMN_PRODUCT_ID, SQLiteHelper.COLUMN_QUANTITY, SQLiteHelper.COLUMN_PRODUCT_JSON},
                    null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String productId = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PRODUCT_ID));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_QUANTITY));
                    String productJson = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PRODUCT_JSON));

                    // Convert the JSON to a Product instance.
                    Product product = gson.fromJson(productJson, Product.class);
                    ProductItem item = new ProductItem(product, quantity);
                    productItems.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return new Cart.Builder().setCartItems(productItems).build();
    }

    // Delete all cart items.
    public static void deleteCart() {
        if (sqLiteHelper == null) {
            throw new IllegalStateException("DatabaseHelper not initialized. Call init() first.");
        }
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        try {
            db.delete(SQLiteHelper.TABLE_CART_ITEMS, null, null);
        } finally {
            db.close();
        }
    }
}
