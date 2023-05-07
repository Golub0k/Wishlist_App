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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Image_Item_RecyclerViewAdapter extends RecyclerView.Adapter<Image_Item_RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private List<String> stringArrayList;
    private itemClickListener itemClickListener;
    private Boolean edit_flag = false;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public Image_Item_RecyclerViewAdapter(ArrayList<Uri> uriArrayList, itemClickListener itemClickListener) {
        this.uriArrayList = uriArrayList;
        this.itemClickListener = itemClickListener;
    }

    public Image_Item_RecyclerViewAdapter(List<String> stringArrayList, itemClickListener itemClickListener) {
        this.stringArrayList = stringArrayList;
        this.itemClickListener = itemClickListener;
    }

    public Image_Item_RecyclerViewAdapter(List<String> stringArrayList, ArrayList<Uri> uriArrayList, itemClickListener itemClickListener) {
        this.stringArrayList = stringArrayList;
        this.uriArrayList = uriArrayList;
        this.itemClickListener = itemClickListener;
        this.edit_flag = true;
    }


    @NonNull
    @Override
    public Image_Item_RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Image_Item_RecyclerViewAdapter.ViewHolder holder, int position) {
        if (stringArrayList == null) {
            holder.imageView.setImageURI(uriArrayList.get(position));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uriArrayList.remove(uriArrayList.get(holder.getAdapterPosition()));
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                }
            });
        } else {
            if (edit_flag == true) {
                if (position >= stringArrayList.size() && uriArrayList != null) {
                    holder.imageView.setImageURI(uriArrayList.get(position - stringArrayList.size()));
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uriArrayList.remove(uriArrayList.get(holder.getAdapterPosition() - stringArrayList.size()));
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                        }
                    });
                } else {
                    Glide.with(holder.imageView.getContext()).load(stringArrayList.get(position)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(holder.imageView);
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            StorageReference storageReference = storage.getReferenceFromUrl(stringArrayList.get(holder.getAdapterPosition()));
//                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    stringArrayList.remove(stringArrayList.get(holder.getAdapterPosition()));
//                                    notifyItemRemoved(holder.getAdapterPosition());
//                                    notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
//                                }
//                            });
                            stringArrayList.remove(stringArrayList.get(holder.getAdapterPosition()));
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                        }
                    });
                }
            } else {
                holder.delete.setVisibility(View.INVISIBLE);
                Glide.with(holder.imageView.getContext()).load(stringArrayList.get(position)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(holder.imageView);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (stringArrayList == null) {
            return uriArrayList.size();
        } else {
            if (stringArrayList != null && uriArrayList == null) {
                return stringArrayList.size();
            } else {
                return stringArrayList.size() + uriArrayList.size();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView, delete;
        itemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView, itemClickListener itemClickListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_single);
            delete = itemView.findViewById(R.id.delete_image);
            this.itemClickListener = itemClickListener;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.itemClick(getAdapterPosition());
            }
        }
    }

    public interface itemClickListener {
        void itemClick(int position);
    }
}
