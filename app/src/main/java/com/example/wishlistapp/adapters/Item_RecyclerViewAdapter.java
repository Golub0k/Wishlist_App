package com.example.wishlistapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wishlistapp.R;
import com.example.wishlistapp.models.Item;

import java.util.ArrayList;

public class Item_RecyclerViewAdapter extends RecyclerView.Adapter<Item_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Item> items;
    public Item_RecyclerViewAdapter(Context context, ArrayList<Item> items){
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public Item_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row_item, parent, false);
        return new Item_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        Uri[] arr1 = items.get(position).getImage();
        if(items.get(position).getImage()!= null){
        holder.imageView.setImageURI(items.get(position).getImage(0));}

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        ImageButton edit;
        ImageButton delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.card_item_image);
            name = itemView.findViewById(R.id.card_item_name);
            edit = itemView.findViewById(R.id.btn_edit_item);
            delete = itemView.findViewById(R.id.btn_delete_item);
        }
    }


}
