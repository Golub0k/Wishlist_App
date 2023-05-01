package com.example.wishlistapp.models;

import android.net.Uri;

import java.util.List;

public class Wishlist {
    String creator_id;
    String name;
    String description;
    String uri;
    Boolean public_flag;
    Double max_reserve;
    List<Item> items;

    public String getCreator_id() {
        return creator_id;
    }

    public String getName() {
        return name;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublic_flag(Boolean public_flag) {
        this.public_flag = public_flag;
    }

    public void setMax_reserve(Double max_reserve) {
        this.max_reserve = max_reserve;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getPublic_flag() {
        return public_flag;
    }

    public Double getMax_reserve() {
        return max_reserve;
    }

    public List<Item> getItems() {
        return items;
    }


    public Wishlist(String creator_id, String name, String description, String uri, Boolean public_flag, Double max_reserve, List<Item> items) {
        this.creator_id = creator_id;
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.public_flag = public_flag;
        this.max_reserve = max_reserve;
        this.items = items;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Wishlist(String creator_id, String name, String description, Boolean public_flag, Double max_reserve, List<Item> items) {
        this.creator_id = creator_id;
        this.name = name;
        this.description = description;
        this.public_flag = public_flag;
        this.max_reserve = max_reserve;
        this.items = items;
    }
}
