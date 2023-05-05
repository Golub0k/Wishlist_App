package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.wishlistapp.friends.MyFriendsFragment;
import com.example.wishlistapp.databinding.ActivityHomePageBinding;
import com.example.wishlistapp.profile.ProfileFragment;

public class HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MyWishlistsFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.menu_home:
                    replaceFragment(new MyWishlistsFragment());
                    break;
                case R.id.menu_friends:
                    replaceFragment(new MyFriendsFragment());
                    break;
                case R.id.menu_add:
                    Intent intent = new Intent(HomePage.this, NewWishlistActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.menu_profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}