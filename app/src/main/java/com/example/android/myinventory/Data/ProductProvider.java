package com.example.android.myinventory.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.myinventory.Data.ProductContract.ProductInput;
import static com.example.android.myinventory.Data.ProductDBHelper.LOG_TAG;

public class ProductProvider extends ContentProvider {

    public static final String Log_TAG = ProductProvider.class.getSimpleName();
    private static final int Product = 1;
    private static final int Product_id = 2;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, Product);
        matcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", Product_id);
    }

    private ProductDBHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = matcher.match(uri);

        switch (match) {
            case Product:
                cursor = database.query(ProductInput.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case Product_id:
                selection = ProductInput._ID + " =?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(ProductInput.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues value) {

        final int match = matcher.match(uri);

        switch (match) {
            case Product:
                return insertProduct(uri, value);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues value) {
        String productName = value.getAsString(ProductContract.ProductInput.product_name);
        if (productName == null) {
            throw new IllegalArgumentException("Product Name is required");
            //Toast.makeText(getContext(),"Please Enter product name",Toast.LENGTH_SHORT).show();
        }
        Integer quantity = value.getAsInteger(ProductContract.ProductInput.product_quantity);
        if (quantity == 0) {
            throw new IllegalArgumentException("Product quantity is required");
            //Toast.makeText(getContext(),"Please Enter product quantity",Toast.LENGTH_SHORT).show();
        }
        Integer price = value.getAsInteger(ProductContract.ProductInput.product_price);
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid Price");
            //Toast.makeText(getContext(),"Please Enter product price",Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ProductContract.ProductInput.TABLE_NAME, null, value);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues value, String selection,
                      String[] selectionArgs) {

        final int match = matcher.match(uri);

        switch (match) {
            case Product:
                return updateProduct(uri, value, selection, selectionArgs);
            case Product_id:
                selection = ProductInput._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, value, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductInput.product_name)) {
            String name = values.getAsString(ProductInput.product_name);
            if (name == null) {
                throw new IllegalArgumentException("Product Name is Required");
            }
        }
        if (values.containsKey(ProductInput.product_quantity)) {
            Integer quantity = values.getAsInteger(ProductContract.ProductInput.product_quantity);
            if (quantity < 0) {
                throw new IllegalArgumentException("Product Quantity is required");
            }
        }
        if (values.containsKey(ProductInput.product_price)) {
            Integer price = values.getAsInteger(ProductInput.product_price);
            if (price <= 0) {
                throw new IllegalArgumentException("Invalid Price");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductInput.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = matcher.match(uri);

        switch (match) {
            case Product:
                rowsDeleted = database.delete(ProductContract.ProductInput.TABLE_NAME, selection, selectionArgs);
                break;
            case Product_id:
                selection = ProductContract.ProductInput._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductContract.ProductInput.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);

        switch (match) {
            case Product:
                return ProductContract.ProductInput.CONTENT_LIST_TYPE;
            case Product_id:
                return ProductContract.ProductInput.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}