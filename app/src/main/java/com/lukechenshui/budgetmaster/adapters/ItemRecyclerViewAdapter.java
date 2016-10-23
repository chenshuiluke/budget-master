package com.lukechenshui.budgetmaster.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lukechenshui.budgetmaster.R;
import com.lukechenshui.budgetmaster.shopping.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

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
        private EditText editName;
        private EditText editPrice;
        private ImageView itemPicture;
        private ImageButton deleteCard;
        private ImageButton editCard;
        private ImageButton acceptEdit;
        private ImageButton cancelEdit;
        private CardView card;

        public ItemHolder(View view) {
            super(view);
            card = (CardView) view.findViewById(R.id.itemCard);
            itemName = (TextView) view.findViewById(R.id.itemName);
            itemPrice = (TextView) view.findViewById(R.id.itemPrice);
            itemPicture = (ImageView) view.findViewById(R.id.itemImage);
            deleteCard = (ImageButton) view.findViewById(R.id.deleteCard);
            editName = (EditText) view.findViewById(R.id.editName);
            editPrice = (EditText) view.findViewById(R.id.editPrice);
            editCard = (ImageButton) view.findViewById(R.id.editCard);
            acceptEdit = (ImageButton) view.findViewById(R.id.acceptEdit);
            cancelEdit = (ImageButton) view.findViewById(R.id.cancelEdit);
            deleteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(getAdapterPosition());
                }
            });
            editCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemName.setVisibility(GONE);
                    itemPrice.setVisibility(GONE);
                    editCard.setVisibility(GONE);
                    deleteCard.setVisibility(GONE);
                    editName.setText(itemName.getText());
                    editPrice.setText(itemPrice.getText());
                    editName.setVisibility(View.VISIBLE);
                    editPrice.setVisibility(View.VISIBLE);
                    acceptEdit.setVisibility(View.VISIBLE);
                    cancelEdit.setVisibility(View.VISIBLE);
                }
            });
            acceptEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = getItem(getAdapterPosition());
                    String newName = editName.getText().toString();
                    String newPrice = editPrice.getText().toString();
                    Log.d("Item", "new name:" + newName + " new price:" + newPrice);
                    newPrice = newPrice.replaceAll(",", "");
                    item.setPrice(new BigDecimal(newPrice));
                    item.setName(newName);
                    Log.d("Item", "Item after editing:" + item);
                    item.save();
                    notifyItemChanged(getAdapterPosition());

                    resetViews();
                }
            });
            cancelEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetViews();
                }
            });
        }

        private void resetViews() {
            itemName.setVisibility(View.VISIBLE);
            itemPrice.setVisibility(View.VISIBLE);
            editCard.setVisibility(View.VISIBLE);
            deleteCard.setVisibility(View.VISIBLE);
            editName.setText("");
            editPrice.setText("");
            editName.setVisibility(GONE);
            editPrice.setVisibility(GONE);
            acceptEdit.setVisibility(GONE);
            cancelEdit.setVisibility(GONE);
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
