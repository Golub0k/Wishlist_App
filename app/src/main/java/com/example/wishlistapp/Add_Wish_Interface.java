package com.example.wishlistapp;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wishlistapp.models.Item;

import java.util.ArrayList;

public interface Add_Wish_Interface {

    public void addItem(Item item, FragmentTransaction transaction, FragmentManager fragmentManager);
}
