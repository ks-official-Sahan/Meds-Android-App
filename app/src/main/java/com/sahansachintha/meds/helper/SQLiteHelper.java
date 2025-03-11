package com.sahansachintha.meds.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyMeds.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CART_ITEMS = "cart_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_JSON = "product_json"; // optional, for storing product details

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table for cart items
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART_ITEMS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_ID + " TEXT NOT NULL UNIQUE, " // UNIQUE so that one row per product.
                + COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_JSON + " TEXT"
                + ")";
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity, drop the old table and create a new one.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        onCreate(db);
    }

}