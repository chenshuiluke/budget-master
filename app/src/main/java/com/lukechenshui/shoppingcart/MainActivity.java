package com.lukechenshui.shoppingcart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lukechenshui.shoppingcart.utilities.CurrencyUtility;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner currencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Currency", "Available currencies: " + CurrencyUtility.getCurrencies().toString());
        currencySpinner = (Spinner) findViewById(R.id.mainCurrencySelectionSpinner);
        setSpinnerSelectionItems(getSelectedCurrency());
        setSpinnerOnItemSelected();
    }

    private void setSpinnerOnItemSelected() {
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                TextView item = (TextView) selectedItemView;
                String currencyName = item.getText().toString();
                SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_name), 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("selectedCurrencyPos", position);
                editor.putString("selectedCurrencyName", currencyName);
                Log.i("Currency", "Changed currency to " + currencyName);
                editor.commit();
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
        Log.i("Currency", "Initially selected curreny:" + name);
        return pos;
    }

    private void setSpinnerSelectionItems(int pos) {

        ArrayList<String> currencies = CurrencyUtility.getCurrencies();
        Log.d("Currency", "Position of USD currency in list:" + pos);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                currencies);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setSelection(pos);
    }
}
