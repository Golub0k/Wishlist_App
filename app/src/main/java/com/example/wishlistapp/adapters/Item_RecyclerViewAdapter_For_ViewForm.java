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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.AddItemFragment;
import com.example.wishlistapp.R;
import com.example.wishlistapp.ViewItemFragment;
import com.example.wishlistapp.ViewWishlistActivity;
import com.example.wishlistapp.ViewWishlistFragment;
import com.example.wishlistapp.models.Item;
import com.example.wishlistapp.models.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class Item_RecyclerViewAdapter_For_ViewForm extends RecyclerView.Adapter<MyViewHolderItems>{

    Context context;
    private List<Item> items;
    private String wishlist_key;
    private Integer user_reserve;

    public Item_RecyclerViewAdapter_For_ViewForm(Context context, List<Item> items, String wishlist_key, Integer user_reserve) {
        this.context = context;
        this.items = items;
        this.wishlist_key = wishlist_key;
        this.user_reserve = user_reserve;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderItems holder, int position) {

        holder.name.setText(items.get(position).getName());
        holder.delete.setVisibility(View.INVISIBLE);
        holder.edit.setVisibility(View.INVISIBLE);
        Uri[] arr1 = items.get(position).getImage();
        if(items.get(position).getImage_uri()!= null){
        Glide.with(holder.imageView.getContext()).load(items.get(position).getImage_uri(0)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(holder.imageView);}

        holder.card_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((ViewWishlistActivity)context).getSupportFragmentManager();

                // Создаем новый фрагмент
                ViewItemFragment viewItemFragment = new ViewItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString("item_key", items.get(holder.getAdapterPosition()).getKey());
                bundle.putString("wishlist_key", wishlist_key);
                bundle.putInt("user_reserve", user_reserve);
                viewItemFragment.setArguments(bundle);
                // Выполняем транзакцию для добавления фрагмента в контейнер
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.FrameLayoutViewWishlist, viewItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();
//                Intent intent = new Intent(context, ViewWishlistActivity.class);
//                intent.putExtra("Image", wishlists.get(holder.getAdapterPosition()).getUri());
//                intent.putExtra("Description", wishlists.get(holder.getAdapterPosition()).getDescription());
//                intent.putExtra("Name", wishlists.get(holder.getAdapterPosition()).getName());
//                intent.putExtra("Key",wishlists.get(holder.getAdapterPosition()).getKey());
//                //intent.putExtra("Items", wishlists.get(holder.getAdapterPosition()).getItems());
//                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolderItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item, parent, false);
        return new MyViewHolderItems(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setUser_reserve(Integer user_reserve){
        this.user_reserve = user_reserve;
    }

}
class MyViewHolderItems extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView name;
    ImageButton edit;
    ImageButton delete;
    CardView card_item;

    public MyViewHolderItems(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.card_item_image);
        name = itemView.findViewById(R.id.card_item_name);
        edit = itemView.findViewById(R.id.btn_edit_item);
        delete = itemView.findViewById(R.id.btn_delete_item);
        card_item = itemView.findViewById(R.id.card_item);
//          itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Wishlist wishlist = holder.get(getLayoutPosition());
//                    onUserClickListener.onUserClick(user);
//                }
//            });
    }


}

