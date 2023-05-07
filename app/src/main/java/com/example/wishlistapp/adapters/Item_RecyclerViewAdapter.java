package com.example.wishlistapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.AddItemFragment;
import com.example.wishlistapp.Add_Wish_Interface;
import com.example.wishlistapp.HomePage;
import com.example.wishlistapp.NewWishlistActivity;
import com.example.wishlistapp.R;
import com.example.wishlistapp.models.Item;
import com.example.wishlistapp.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Item_RecyclerViewAdapter extends RecyclerView.Adapter<Item_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Item> items;
    String paren_key;

    public Item_RecyclerViewAdapter(Context context, ArrayList<Item> items, String paren_key) {
        this.context = context;
        this.items = items;
        this.paren_key = paren_key;
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
        //Uri[] arr1 = items.get(position).getImage();
        if (items.get(position).getImage_uri() != null) {
            Glide.with(holder.imageView.getContext()).load(items.get(position).getImage_uri(0)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(holder.imageView);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.get(holder.getAdapterPosition()).getImage_uri() != null) {
                    List<String> uri = items.get(holder.getAdapterPosition()).getImage_uri();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    for (String imageUrl : uri) {
                        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                    }
                }

                if (!paren_key.isEmpty()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wishlists").child(paren_key).child("items");
                    String x = Integer.toString(holder.getAdapterPosition());

                    for (int i=holder.getAdapterPosition()+1; i< items.size(); i++){
                        Item item = new Item(items.get(i));
                        reference.child(Integer.toString(i-1)).setValue(item);
                    }
                    reference.child(Integer.toString(items.size()-1)).removeValue();
                }
                items.remove(items.get(holder.getAdapterPosition()));
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((NewWishlistActivity) context).getSupportFragmentManager();
                //AddItemFragment editItem = new AddItemFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("Key", items.get(holder.getAdapterPosition()).getKey());
//                bundle.putString("Name", items.get(holder.getAdapterPosition()).getName());
//                bundle.putString("Annotation", items.get(holder.getAdapterPosition()).getAnnotation());
//                bundle.putString("Links", items.get(holder.getAdapterPosition()).getLinks());
//                editItem.setArguments(bundle);
                // Выполняем транзакцию для добавления фрагмента в контейнер
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Item x = items.get(holder.getAdapterPosition());
                ((Add_Wish_Interface)context).editItem(items.get(holder.getAdapterPosition()), holder.getAdapterPosition(), paren_key, transaction, fragmentManager);
//                transaction.replace(R.id.frameLayoutNewWishlist, editItem);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

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
