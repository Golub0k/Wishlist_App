package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import com.example.wishlistapp.databinding.ActivityNewWishlistBinding;
import com.example.wishlistapp.models.Item;

import java.util.ArrayList;

public class NewWishlistActivity extends AppCompatActivity implements Add_Wish_Interface {

    ArrayList<Item> items = new ArrayList<>();
    //ActivityNewWishlistBinding binding;
    AddWishListFragment addWishListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wishlist);

        //binding = ActivityNewWishlistBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        addWishListFragment = new AddWishListFragment();
        replaceFragment(addWishListFragment);


    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutNewWishlist,fragment);
        fragmentTransaction.commit();
    }
//    @Override
//    public void onBackPressed() {
//        // super.onBackPressed();
//        Intent intent = new Intent(NewWishlistActivity.this, HomePage.class);
//        startActivity(intent);
//        finish();
//    }
    @Override
    public void addItem(Item item, FragmentTransaction transaction, FragmentManager fragmentManager) {
        //добавляем новый item в лист items
        items.add(item);
        addWishListFragment.setItems (items);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayoutNewWishlist, addWishListFragment);
        transaction.commit();

    }

}