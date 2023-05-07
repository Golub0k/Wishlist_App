package com.example.wishlistapp;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wishlistapp.adapters.Image_Item_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;
import com.example.wishlistapp.models.Wishlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ViewItemFragment extends Fragment implements Image_Item_RecyclerViewAdapter.itemClickListener {

    private String mParam1;
    private String mParam2;
    TextView item_name;
    TextView item_annotation;
    TextView item_links;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    Item item;
    FirebaseUser currentUser;
    String creatorId;
    RecyclerView recyclerView;
    Button book;
    Integer max_reserve;
    List<String> image_uri = new ArrayList<>();
    Image_Item_RecyclerViewAdapter adapter;
    Integer user_reserve;
    String itemKey;

    public ViewItemFragment() {
        // Required empty public constructor
    }

    public static ViewItemFragment newInstance(String param1, String param2) {
        ViewItemFragment fragment = new ViewItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_item, container, false);
        recyclerView = view.findViewById(R.id.item_view_recycle_gallery_image);
        item_name = view.findViewById(R.id.item_view_name);
        item_annotation = view.findViewById(R.id.item_view_annotation);
        item_links = view.findViewById(R.id.item_view_links);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        book = view.findViewById(R.id.btn_book);
        max_reserve = 0;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        user_reserve = 0;

        itemKey = getArguments().getString("item_key");
        String wishlistKey = getArguments().getString("wishlist_key");
        user_reserve = getArguments().getInt("user_reserve");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wishlists").child(wishlistKey);
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                for (DataSnapshot uriSnapshot: snapshot.getChildren()){
//
//                    image_uri.add(uriSnapshot.child("image_uri").getValue(String.class));
//                }
//                    String name = snapshot.child("name").getValue(String.class);
//                    String annotation = snapshot.child("annotation").getValue(String.class);
//                    String links = snapshot.child("links").getValue(String.class);
                max_reserve = snapshot.child("max_reserve").getValue(Integer.class);
                creatorId = snapshot.child("creator_id").getValue(String.class);
                item = snapshot.child("items").child(itemKey).getValue(Item.class);
                updateInfo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

    public void updateInfo() {

        if (!item.getName().isEmpty()) {
            item_name.setText(item.getName());
        }

        if (!item.getAnnotation().isEmpty()) {
            item_annotation.setText(item.getAnnotation());
        }

        if (!item.getLinks().isEmpty()) {
            item_links.setText(item.getLinks());
        }
        if (item.getImage_uri() != null) {
            adapter = new Image_Item_RecyclerViewAdapter(item.getImage_uri(), this);
            recyclerView.setAdapter(adapter);
        }
        if (max_reserve == 0) {
            book.setVisibility(View.GONE);
        } else {
//            if (currentUser.getUid() != creatorId) {
//                book.setVisibility(View.VISIBLE);
                if (item.getBooked_person()!= null && item.getBooked_person().equals(currentUser.getUid())){
                    book.setText("Cancel your booking");
                    book.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            user_reserve--;
                            book.setText("Book");
                            databaseReference.child("items").child(itemKey).child("booked_person").setValue("");
                        }
                    });
                }
                else{
                    if (user_reserve<max_reserve && item.getBooked_person().isEmpty()){
                        book.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseReference.child("items").child(itemKey).child("booked_person").setValue(currentUser.getUid());
                            }
                        });
                    }
                    else{
                        book.setEnabled(false);
                    }
                }
            }
//        }
    }

    @Override
    public void itemClick(int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.zoom_image);
        ImageView zoom_image = dialog.findViewById(R.id.zoomImage);
        ImageView close_image = dialog.findViewById(R.id.close_image);
        Glide.with(getActivity()).load(item.getImage_uri().get(position)).apply(new RequestOptions().override(1000, 1000)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(zoom_image);
        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}