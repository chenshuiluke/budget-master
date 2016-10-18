package com.lukechenshui.budgetmaster;

import android.Manifest;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lukechenshui.budgetmaster.adapters.ItemRecyclerViewAdapter;
import com.lukechenshui.budgetmaster.shopping.Item;
import com.lukechenshui.budgetmaster.utilities.CurrencyUtility;
import com.lukechenshui.budgetmaster.utilities.ImageUtility;
import com.ritaja.xchangerate.util.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

public class MainActivity extends AppCompatActivity {

    private Spinner currencySpinner;
    private ExpandableRelativeLayout expandableLayout;
    private ToggleButton toggleButton;
    private EditText newItemName;
    private EditText newItemPrice;
    private String currencyName;
    private RecyclerView itemRecyclerView;
    private LinearLayoutManager layoutManager;
    private ImageButton newItemImageButton;
    private Uri pictureUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Currency", "Available currencies: " + CurrencyUtility.getCurrencies().toString());
        currencySpinner = (Spinner) findViewById(R.id.mainCurrencySelectionSpinner);
        expandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);
        toggleButton = (ToggleButton) findViewById(R.id.toggleAddItem);
        newItemName = (EditText) findViewById(R.id.itemName);
        newItemPrice = (EditText) findViewById(R.id.itemPrice);
        newItemImageButton = (ImageButton) findViewById(R.id.itemImageButton);
        itemRecyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(layoutManager);
        setSpinnerSelectionItems(getSelectedCurrency());
        setSpinnerOnItemSelected();
        setToggleButtonOnToggle();
        expandableLayout.setClosePosition(0);
        expandableLayout.collapse();

        populateItemRecyclerView();
    }

    private void populateItemRecyclerView() {
        ArrayList<Item> items = CurrencyUtility.getExistingItems();
        Log.d("ShoppingCart", "Existing items:" + items.toString());
        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(items);
        itemRecyclerView.setAdapter(adapter);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void saveItem(View view) {
        String name = newItemName.getText().toString();
        String strPrice = newItemPrice.getText().toString();
        byte[] picture = ImageUtility.getBytes(this, pictureUri);
        if (name.length() == 0) {
            makeToast("Please enter the item's name");
        } else if (strPrice.length() == 0) {
            makeToast("Please enter the item's price");
        } else {
            BigDecimal price = new BigDecimal(strPrice);
            Currency currency = CurrencyUtility.getCurrencyMap().get(currencyName);
            Item item = new Item(currency, price, name, picture);
            Log.d("Item", "Item to save:" + item.toString());
            item.save();
            clearNewItemInformation();
            expandableLayout.collapse();
            populateItemRecyclerView();
            toggleButton.setChecked(false);
        }

    }

    private void clearNewItemInformation() {
        newItemName.setText("");
        newItemPrice.setText("");
        expandableLayout.collapse();
        toggleButton.setChecked(false);
        pictureUri = null;
    }

    private void setToggleButtonOnToggle() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expandableLayout.expand();
                    newItemName.setFocusable(true);
                    newItemPrice.setFocusable(true);
                    newItemPrice.setFocusableInTouchMode(true);
                    newItemName.setFocusableInTouchMode(true);
                    newItemName.requestFocus();
                    Log.i("ExpandableLayout", "Expanding layout");
                } else {
                    clearNewItemInformation();
                    Log.i("ExpandableLayout", "Collapsing layout");
                    newItemName.setFocusable(false);
                    newItemPrice.setFocusable(false);
                    newItemPrice.setFocusableInTouchMode(false);
                    newItemName.setFocusableInTouchMode(false);
                }
            }
        });
    }

    private void setSpinnerOnItemSelected() {
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (currencySpinner != null) {
                    TextView item = (TextView) selectedItemView;
                    if (item != null) {
                        currencyName = item.getText().toString();
                        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_name), 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("selectedCurrencyPos", position);
                        editor.putString("selectedCurrencyName", currencyName);
                        Log.i("Currency", "Changed currency to " + currencyName);
                        editor.commit();

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String finalCurrencyName = currencyName;
                                ArrayList<Item> items = CurrencyUtility.getExistingItems();
                                for (Item tempItem : items) {
                                    CurrencyUtility.convertItemCurrency(tempItem, finalCurrencyName);
                                    tempItem.save();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        populateItemRecyclerView();
                                    }
                                });

                            }
                        });
                        thread.start();

                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private int getSelectedCurrency() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_name), 0);
        ArrayList<String> currencies = CurrencyUtility.getCurrencies();
        int pos = settings.getInt("selectedCurrencyPos", currencies.indexOf("USD"));
        String name = settings.getString("selectedCurrencyName", "USD");
        currencyName = name;
        Log.i("Currency", "Initially selected curreny:" + name);
        return pos;
    }

    private void setSpinnerSelectionItems(int pos) {

        ArrayList<String> currencies = CurrencyUtility.getCurrencies();
        Log.d("Currency", "Position of USD currency in list:" + pos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
                currencies);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setSelection(pos);
    }

    public void selectImages(View view){

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getApplicationContext())
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                newItemImageButton.setImageURI(uri);
                                pictureUri = uri;
                            }
                        })
                        .create();

                tedBottomPicker.show(getSupportFragmentManager());
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject these permissions, you can't set item photos.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }
}
