package com.example.wishlistapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Item {
    String name;
    String annotation;
    String links;
    Uri[] image;
    List<String> image_uri;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public List<String> getImage_uri() {
        if (image_uri == null || image_uri.size() == 0){
            return null;}
        else return image_uri;
    }

    public String getImage_uri(int position) {
        return image_uri.get(position);
    }

    public  void deleteArray(){

        image = null;
    }

    public void setImage_uri(List<String> image_uri) {

        this.image_uri = image_uri;
    }

    public void addImage_uri(String uri){

        this.image_uri.add(uri);
    }

    public Item(String name, String annotation, String links , List<String> image_uri) {
        this.name = name;
        this.annotation = annotation;
        this.links = links;
        this.image_uri = image_uri;
    }
    public Item(Item item) {
        this.name = item.getName();
        this.annotation = item.getAnnotation();
        this.links = item.getLinks();
        this.image_uri = item.getImage_uri();
    }
    public Item(){}

//    public Item(Item item) {
//        this.name = item.name;
//        this.annotation = item.annotation;
//        this.links = item.links;
//        this.image = item.image;
//        this.image_uri = item.image_uri;
//        this.key = item.key;
//    }

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

        if (image != null){
        return image;}
        else return null;
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

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel dest, int flags) {
//        dest.writeString(name);
//        dest.writeString(());
//    }
}
