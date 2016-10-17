package com.lukechenshui.budgetmaster.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukechenshui.budgetmaster.R;
import com.lukechenshui.budgetmaster.shopping.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 10/16/16.
 */

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemHolder> {
    private ArrayList<Item> items;

    public ItemRecyclerViewAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_view_row, parent, false);
        return new ItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Item item = items.get(position);
        holder.bindItem(item);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemName;
        private TextView itemPrice;
        private TextView itemCurrency;

        public ItemHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
            itemPrice = (TextView) view.findViewById(R.id.itemPrice);
            itemCurrency = (TextView) view.findViewById(R.id.itemCurrency);
        }

        @Override
        public void onClick(View v) {

        }

        public void bindItem(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText(item.getPrice().toString());
            itemCurrency.setText(item.getCurrency().name());
        }
    }
}
