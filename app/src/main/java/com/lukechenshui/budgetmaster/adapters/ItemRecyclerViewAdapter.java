package com.lukechenshui.budgetmaster.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    public Item getItem(int pos) {
        return items.get(pos);
    }

    public void delete(int position) {
        Item item = items.get(position);
        item.delete();
        items.remove(position);
        notifyItemRemoved(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemName;
        private TextView itemPrice;
        private ImageView itemPicture;
        private ImageButton deleteCard;
        public ItemHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
            itemPrice = (TextView) view.findViewById(R.id.itemPrice);
            itemPicture = (ImageView) view.findViewById(R.id.itemImage);
            deleteCard = (ImageButton) view.findViewById(R.id.deleteCard);
            deleteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {

        }

        public void bindItem(Item item) {
            Log.i("BudgetMaster", item.toString());
            itemName.setText(item.getName());
            itemPrice.setText(item.getPrice().toString());
            Log.d("Image", "item image: " + item.getPicture());
            if(item.getPicture() != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(item.getPicture(), 0, item.getPicture().length);
                itemPicture.setImageBitmap(bitmap);
            }

        }
    }
}
