package com.example.android.myinventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.myinventory.Data.ProductContract.ProductInput;

public class ProductDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductInput.TABLE_NAME + " ("
                + ProductInput._ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, "
                + ProductInput.product_name + " TEXT NOT NULL, "
                + ProductInput.product_quantity + " INTEGER NOT NULL DEFAULT 0, "
                + ProductInput.product_price + " INTEGER NOT NULL DEFAULT 0,"
                + ProductInput.product_image + " TEXT );";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * This is called when database needs to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
