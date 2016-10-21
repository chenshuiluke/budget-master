package com.lukechenshui.budgetmaster;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ProgressBar;
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
import java.text.DecimalFormat;
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
    private EditText budgetText;
    private Uri pictureUri;
    private ToggleButton toggleBudgetSet;
    private BigDecimal budgetNum;
    private TextView currentCost;
    private DecimalFormat df = new DecimalFormat("#,###.00");
    private ProgressBar progressBar;
    //private ScrollView scrollView;
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
        budgetText = (EditText) findViewById(R.id.budgetText);
        toggleBudgetSet = (ToggleButton) findViewById(R.id.toggleButton);
        layoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) findViewById(R.id.costToBudgetProgressBar);
        currentCost = (TextView) findViewById(R.id.currentCostText);
        //scrollView = (ScrollView) findViewById(R.id.scrollView);
        getBudgetFromSettings();
        setSpinnerSelectionItems(getSelectedCurrency());
        setSpinnerOnItemSelected();
        setToggleButtonOnToggle();
        setBudgetToggleOnToggle();
        expandableLayout.setClosePosition(0);
        expandableLayout.collapse();

        populateItemRecyclerView();
    }

    private void getBudgetFromSettings() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_name), 0);
        String budget = settings.getString("budget", "");
        if (budget.length() > 0) {
            toggleBudgetSet.setChecked(true);
            budgetText.setFocusable(false);
            budgetText.setText(budget);
            budgetNum = new BigDecimal(budget);
        }
    }

    private void setBudgetText() {
        String budget = budgetText.getText().toString();
        if (budget.length() > 0) {
            SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("budget", budget);
            Log.i("Budget", "Changed budget to " + budget);
            editor.commit();
            budgetText.setFocusable(false);
            budgetNum = new BigDecimal(df.format(new BigDecimal(budget)));
            budgetText.setText(budgetNum.toString());
            compareTotalCostToBudget();
        } else {
            makeToast("Please enter a budget and try again.");
            toggleBudgetSet.setChecked(false);
        }
    }

    private void setBudgetToggleOnToggle() {
        toggleBudgetSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setBudgetText();
                    Log.d("Budget", "Setting budget text to unfocusable");
                } else {
                    budgetText.setFocusable(true);
                    budgetText.setFocusableInTouchMode(true);
                    budgetText.requestFocus();
                    Log.d("Budget", "Setting budget text to focusable");
                }
            }
        });
    }

    private void compareTotalCostToBudget() {
        Log.d("Budget", "Checking to see if the total cost exceeds the budget");
        ArrayList<Item> items = CurrencyUtility.getExistingItems();
        BigDecimal totalCost = new BigDecimal(0);
        for (Item item : items) {
            BigDecimal price = item.getPrice();
            totalCost = totalCost.add(price);
        }
        if (budgetNum != null && totalCost.compareTo(budgetNum) > 0) {
            makeToast("The total cost exceeds your budget!");
            budgetText.setBackgroundColor(Color.parseColor("#e3655b"));
        } else {
            budgetText.setBackgroundColor(Color.WHITE);
        }
        progressBar.setMax(budgetNum.intValue());
        progressBar.setProgress(totalCost.intValue());
        currentCost.setText("   " + totalCost.toString());
    }

    private void populateItemRecyclerView() {
        ArrayList<Item> items = CurrencyUtility.getExistingItems();
        compareTotalCostToBudget();
        Log.d("ShoppingCart", "Existing items:" + items.toString());
        final ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(items);
        itemRecyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                populateItemRecyclerView();
                Log.d("CardView", "Registered that an item was deleted.");
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);

                populateItemRecyclerView();
                Log.d("CardView", "Registered that an item changed.");
            }


        });
        //scrollView.fullScroll(ScrollView.FOCUS_UP);
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
        Uri uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/drawable/ic_camera_enhance_black_24dp");
        newItemImageButton.setImageURI(uri);
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
                    // scrollView.fullScroll(ScrollView.FOCUS_UP);
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
                                Currency currency = CurrencyUtility.getCurrencyMap().get(currencyName);
                                ArrayList<Item> items = CurrencyUtility.getExistingItems();
                                String budget = budgetText.getText().toString();
                                if (items != null && items.size() > 0 && budget != null) {
                                    budgetNum = CurrencyUtility.convertCurrency(items.get(0).getCurrency(), currency, new BigDecimal(budget));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            budgetText.setText(budgetNum.toString());
                                        }
                                    });

                                }

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
