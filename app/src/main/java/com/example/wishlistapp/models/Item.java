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

    public List<String> getImage_uri() {
        return image_uri;
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
