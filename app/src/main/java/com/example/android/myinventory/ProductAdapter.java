package com.example.android.myinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventory.Data.ProductContract;
import com.example.android.myinventory.Data.ProductContract.ProductInput;

public class ProductAdapter extends CursorAdapter {

    public ProductAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);
        ImageView cart = (ImageView) view.findViewById(R.id.cart);
        ImageView imageView = (ImageView) view.findViewById(R.id.product_image);

        int name = cursor.getColumnIndex(ProductInput.product_name);
        int quantity = cursor.getColumnIndex(ProductInput.product_quantity);
        final String quantity_string = cursor.getString(quantity);
        int price = cursor.getColumnIndex(ProductInput.product_price);
        int id = cursor.getColumnIndex(ProductInput._ID);
        final String id_string = cursor.getString(id);
        int image = cursor.getColumnIndex(ProductInput.product_image);
        String imageStr = cursor.getString(image);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = Integer.parseInt(quantity_string);
                if (q <= 0) {
                    Toast.makeText(context, "Cannot be zero", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    q--;
                    ContentValues value = new ContentValues();
                    value.put(ProductContract.ProductInput.product_quantity, q);
                    Uri uri = ContentUris.withAppendedId(ProductInput.CONTENT_URI, Long.parseLong(id_string));
                    context.getContentResolver().update(uri, value, null, null);
                }
            }
        });

        Uri imageUri = Uri.parse(imageStr);
        String productName = cursor.getString(name);
        String productquantity = cursor.getString(quantity);
        String productprice = cursor.getString(price);

        nameTextView.setText(productName);
        quantityTextView.setText(productquantity);
        priceTextView.setText(productprice);
        imageView.setImageURI(imageUri);
    }
}

