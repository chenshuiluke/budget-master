package com.lukechenshui.shoppingcart.utilities;

import com.activeandroid.query.Select;
import com.google.common.collect.HashBiMap;
import com.lukechenshui.shoppingcart.shopping.Item;
import com.ritaja.xchangerate.util.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 10/15/16.
 */

public abstract class CurrencyUtility {

    public static ArrayList<String> getCurrencies(){
        ArrayList<String>list = new ArrayList<>();
        Currency[] arr = Currency.values();
        for(Currency currency : arr){
            list.add(currency.name());
        }
        return list;
    }

    public static HashBiMap<String, Currency> getCurrencyMap() {
        HashBiMap<String, Currency> map = HashBiMap.create();
        Currency[] arr = Currency.values();
        for (Currency currency : arr) {
            map.put(currency.name(), currency);
        }
        return map;
    }

    public static ArrayList<Item> getExistingItems() {
        List<Item> items = new Select().from(Item.class).execute();
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.addAll(items);
        return itemList;
    }

}
