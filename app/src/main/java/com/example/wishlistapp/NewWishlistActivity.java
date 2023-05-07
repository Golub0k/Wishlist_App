package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.wishlistapp.models.Item;

import java.util.ArrayList;

public class NewWishlistActivity extends AppCompatActivity implements Add_Wish_Interface {

    ArrayList<Item> items = new ArrayList<>();
    //ActivityNewWishlistBinding binding;
    AddWishListFragment addWishListFragment;
    AddItemFragment addItemFragment;

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
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frameLayoutNewWishlist, addWishListFragment);
//        transaction.commit();

    }

    @Override
    public void editItem(Item item, Integer position, String parent_key, FragmentTransaction transaction, FragmentManager fragmentManager) {
        addItemFragment = new AddItemFragment();

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayoutNewWishlist, addItemFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        addItemFragment.setItem(item, position, parent_key);
    }

    @Override
    public void updateItem(Item item, Integer position, FragmentTransaction transaction, FragmentManager fragmentManager) {
        //добавляем новый item в лист items
        //items.set(position, item);
        addWishListFragment.updateItems (item, position);
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frameLayoutNewWishlist, addWishListFragment);
//        transaction.commit();

    }
}