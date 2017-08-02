package com.example.android.myinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventory.Data.ProductContract;

import static com.example.android.myinventory.Data.ProductContract.ProductInput.product_image;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private TextView mQuantityTextView;
    private EditText mPriceEditText;
    private ImageView mProductImageview;
    String mProductImageString = null;
    private static final int SELECT_PICTURE = 100;
    private boolean productChanged = false;
    Uri imageUri;
    Button orderButton;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        orderButton = (Button) findViewById(R.id.order_button);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle("Add New Product ");
            invalidateOptionsMenu();
            orderButton.setVisibility(View.INVISIBLE);
        } else {
            setTitle("Edit Product");
            orderButton.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText= (EditText) findViewById(R.id.edit_product_name);
        mQuantityTextView = (TextView)findViewById(R.id.display_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mProductImageview = (ImageView) findViewById(R.id.product_image);

        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mProductImageview.setOnTouchListener(mTouchListener);

        Button Plus = (Button) findViewById(R.id.add_quantity);
        Plus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int q = Integer.parseInt(mQuantityTextView.getText().toString());
                if(q==10)
                {
                    Toast.makeText(getApplicationContext(),"Product not greater the 10 ",Toast.LENGTH_SHORT);
                }
                else
                {
                    q++;
                }
                mQuantityTextView.setText(String.valueOf(q));
            }
        });
        Button Minus = (Button) findViewById(R.id.sub_quantity);
        Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = Integer.parseInt(mQuantityTextView.getText().toString());
                if(q==0)
                {
                    Toast.makeText(getApplicationContext(),"Already Zero",Toast.LENGTH_SHORT);
                }
                else
                {
                    q--;
                }
                mQuantityTextView.setText(String.valueOf(q));
            }
        });
        Button Photo = (Button) findViewById(R.id.photo_select);
        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_PICTURE);
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email_intent = new Intent(android.content.Intent.ACTION_SEND);
                email_intent.setType("plain/text");
                email_intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"123random@gmail.com" });
                email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Product Order ");
                email_intent.putExtra(android.content.Intent.EXTRA_TEXT," Product Name "+mNameEditText.getText()+"\nProduct Quantity "+mQuantityTextView.getText()+"\nProduct Price "+mPriceEditText.getText());
                startActivity(Intent.createChooser(email_intent, "Email"));
            }
        });
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productChanged = true;
            return false;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                imageUri = data.getData();
                mProductImageString = imageUri.toString();
                mProductImageview.setImageURI(imageUri);
            }
        }
    }

    private void saveProduct() {
        String productImage = "";
        if (imageUri != null) {
            productImage = imageUri.toString();
            flag = 1;
        }
        else
        {
            flag = 0;
        }
        String productName = mNameEditText.getText().toString().trim();
        String productQuantity = mQuantityTextView.getText().toString().trim();
        String productPrice = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productName) ||
                TextUtils.isEmpty(productQuantity) || TextUtils.isEmpty(productPrice)) {
            Toast.makeText(getApplicationContext(), "All Fields are Necessary", Toast.LENGTH_LONG).show();
            return;
        }
        if (mCurrentProductUri == null && TextUtils.isEmpty(productName) &&
                TextUtils.isEmpty(productQuantity) && TextUtils.isEmpty(productPrice)) {
            Toast.makeText(getApplicationContext(), "All Fields are necessary Otherwise product is not saved ", Toast.LENGTH_LONG).show();
            return;
        }
        if(flag==0) {
            Toast.makeText(getApplicationContext(), "Image is not added ", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductInput.product_name, productName);
        values.put(ProductContract.ProductInput.product_quantity, productQuantity);
        values.put(ProductContract.ProductInput.product_price, productPrice);
        if(flag==1) {
            values.put(ProductContract.ProductInput.product_image, productImage);
        }
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductInput.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error in saving the Product ",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved ",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating Product",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Saved ",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!productChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductInput._ID,
                ProductContract.ProductInput.product_name,
                ProductContract.ProductInput.product_quantity,
                ProductContract.ProductInput.product_price,
                ProductContract.ProductInput.product_image,
        };
        return new android.content.CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int name = data.getColumnIndex(ProductContract.ProductInput.product_name);
            int quantity = data.getColumnIndex(ProductContract.ProductInput.product_quantity);
            int price = data.getColumnIndex(ProductContract.ProductInput.product_price);
            int image = data.getColumnIndex(product_image);

            String productName = data.getString(name);
            int productQuantity = data.getInt(quantity);
            int productPrice = data.getInt(price);
            String productImage = data.getString(image);

            mNameEditText.setText(productName);
            mQuantityTextView.setText(Integer.toString(productQuantity));
            mPriceEditText.setText(Integer.toString(productPrice));
            mProductImageview.setImageURI(Uri.parse(productImage));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityTextView.setText("");
        mPriceEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(" Discard your changes and quit editing?");
        builder.setPositiveButton("Discard your changes and quit editing?", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are You sure you want to delete the product? ");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct(){
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with Deleting the product ",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product Deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
