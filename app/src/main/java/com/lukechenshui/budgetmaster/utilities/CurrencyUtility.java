package com.lukechenshui.budgetmaster.utilities;

import android.util.Log;

import com.activeandroid.query.Select;
import com.google.common.collect.HashBiMap;
import com.lukechenshui.budgetmaster.shopping.Item;
import com.ritaja.xchangerate.api.CurrencyConverter;
import com.ritaja.xchangerate.api.CurrencyConverterBuilder;
import com.ritaja.xchangerate.util.Currency;
import com.ritaja.xchangerate.util.Strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 10/15/16.
 */

public abstract class CurrencyUtility {
    private static CurrencyConverter converter = new CurrencyConverterBuilder()
            .strategy(Strategy.CURRENCY_LAYER_FILESTORE)
            .accessKey("98bb09677965b8e86b9a4ba5e34f9fb9")
            .buildConverter();
    public static ArrayList<String> getCurrencies(){
        final ArrayList<String> list = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Currency[] arr = Currency.values();
                for (Currency currency : arr) {
                    try {
                        converter.convertCurrency(new BigDecimal("100"), Currency.USD, currency);
                        converter.convertCurrency(new BigDecimal("100"), currency, Currency.USD);
                        list.add(currency.name());
                    } catch (Exception exc) {
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception exc) {

        }

        return list;
    }

    public static HashBiMap<String, Currency> getCurrencyMap() {
        HashBiMap<String, Currency> map = HashBiMap.create();
        Currency[] arr = Currency.values();
        for (Currency currency : arr) {
            map.put(currency.name(), currency);
            Log.i("Currency", "Adding " + currency + " to map with key: " + currency.name());
        }
        return map;
    }

    public static ArrayList<Item> getExistingItems() {
        try {
            List<Item> items = new Select().from(Item.class).execute();
            ArrayList<Item> itemList = new ArrayList<>();
            itemList.addAll(items);
            return itemList;
        } catch (NullPointerException exc) {
            Log.d("Item", "Error occurred while getting list of items", exc);
            return new ArrayList<Item>();
        }

    }

    public static void convertItemCurrency(Item item, Currency currency) {
        try {
            Log.d("CurrencyConversion", " old currency: " + item.getCurrency() + " new currency: " + currency);
            BigDecimal newPrice = converter.convertCurrency(item.getPrice(), item.getCurrency(), currency);
            Log.d("CurrencyConversion", "old price:" + item.getPrice() + " new price: " + newPrice);
            item.setPrice(newPrice);
            item.setCurrency(currency);
        } catch (Exception exc) {
            Log.d("CurrencyConversion", exc.toString(), exc);
        }
    }

    public static void convertItemCurrency(Item item, String currencyName) {
        Currency currency = getCurrencyMap().get(currencyName);
        convertItemCurrency(item, currency);
    }

    public static BigDecimal convertCurrency(Currency old, Currency newCurrency, BigDecimal oldValue) {
        try {
            return converter.convertCurrency(oldValue, old, newCurrency);
        } catch (Exception exc) {
            Log.d("CurrencyConversion", "Error converting currency", exc);
            return null;
        }
    }
}
