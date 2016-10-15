package com.lukechenshui.shoppingcart.utilities;

import com.ritaja.xchangerate.util.Currency;

import java.util.ArrayList;

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


}
