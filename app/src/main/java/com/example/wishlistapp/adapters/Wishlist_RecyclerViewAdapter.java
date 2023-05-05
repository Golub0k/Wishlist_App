package com.example.wishlistapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.R;
import com.example.wishlistapp.ViewWishlistActivity;
import com.example.wishlistapp.models.Item;
import com.example.wishlistapp.models.Wishlist;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Wishlist_RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    private List<Wishlist> wishlists;

    //  private OnWishlistClickListener onWishlistClickListener;

//    public Wishlist_RecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<Wishlist> options, Context context, OnWishlistClickListener onWishlistClickListener) {
//        super(options);
//        this.context = context;
//        this.onWishlistClickListener = onWishlistClickListener;
//    }

    public Wishlist_RecyclerViewAdapter(Context context, List<Wishlist> wishlists) {
        this.wishlists = wishlists;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(wishlists.get(position).getName());
        if (wishlists.get(position).getMax_reserve() == 0) {
            holder.booking.setVisibility(View.INVISIBLE);
        }
        if (wishlists.get(position).getPublic_flag() == true) {
            holder.iconPrivatePublic.setImageDrawable(context.getResources().getDrawable(R.drawable.resource_public));
        }
        Glide.with(holder.imageView.getContext()).load(wishlists.get(position).getUri()).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(holder.imageView);

        holder.card_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewWishlistActivity.class);
                intent.putExtra("Image", wishlists.get(holder.getAdapterPosition()).getUri());
                intent.putExtra("Description", wishlists.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Name", wishlists.get(holder.getAdapterPosition()).getName());
                intent.putExtra("Key",wishlists.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Public",wishlists.get(holder.getAdapterPosition()).getPublic_flag());
                intent.putExtra("Reserve",wishlists.get(holder.getAdapterPosition()).getMax_reserve());
                intent.putExtra("Creator_id",wishlists.get(holder.getAdapterPosition()).getCreator_id());
                //intent.putExtra("Items", wishlists.get(holder.getAdapterPosition()).getItems());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_wishlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return wishlists.size();
    }
    public void searchDataList(ArrayList<Wishlist> searchList){
        wishlists = searchList;
        notifyDataSetChanged();
    }
}
     class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        ImageView booking;
        ImageView iconPrivatePublic;
        CardView card_wishlist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.card_item_image);
            name = itemView.findViewById(R.id.card_item_name);
            booking = itemView.findViewById(R.id.icon_booking);
            iconPrivatePublic = itemView.findViewById(R.id.icon_private_public);
            card_wishlist = itemView.findViewById(R.id.card_wishlist);
//          itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Wishlist wishlist = holder.get(getLayoutPosition());
//                    onUserClickListener.onUserClick(user);
//                }
//            });
        }


    }
//    public interface OnWishlistClickListener {
//        void onWishlistClick(Wishlist wishlist);
//    }


