package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wishlistapp.adapters.Item_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;

import java.util.ArrayList;

public class AddWishlistActivity extends AppCompatActivity {
    private ImageButton back;
    private TextView addItem;
    ArrayList<Item> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_items_add);
        Uri[] imgArr = null;
        items.add(new Item("Cake", "taste cake", "", imgArr));

        Item_RecyclerViewAdapter adapter = new Item_RecyclerViewAdapter(this, items);



        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addItem = findViewById(R.id.btn_add_item);
        back = findViewById(R.id.button_back_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddWishlistActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddWishlistActivity.this, AddItemActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}