package com.example.wishlistapp.models;

import android.net.Uri;

public class Item {
    String name;
    String annotation;
    String links;
    Uri[] image;

    public Item(String name, String annotation, String links, Uri[] image) {
        this.name = name;
        this.annotation = annotation;
        this.links = links;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getLinks() {
        return links;
    }

    public Uri[] getImage() {
        return image;
    }
    public Uri getImage(int position) {
        return image[position];
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public void setImage(Uri[] image) {
        this.image = image;
    }
}
