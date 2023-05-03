package com.example.wishlistapp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.R;

import java.util.ArrayList;
import java.util.List;

public class Image_Item_RecyclerViewAdapter extends RecyclerView.Adapter<Image_Item_RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private List<String> stringArrayList;

    public Image_Item_RecyclerViewAdapter(ArrayList<Uri> uriArrayList) {
        this.uriArrayList = uriArrayList;
    }
    public Image_Item_RecyclerViewAdapter(List<String> stringArrayList) {
        this.stringArrayList = stringArrayList;

    }

    @NonNull
    @Override
    public Image_Item_RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Image_Item_RecyclerViewAdapter.ViewHolder holder, int position) {
        if (stringArrayList == null){
        holder.imageView.setImageURI(uriArrayList.get(position));}
        else{
            Glide.with(holder.imageView.getContext()).load(stringArrayList.get(position)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (stringArrayList == null){
        return uriArrayList.size();
        }
        else{
            return stringArrayList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_single);
        }
    }
}
