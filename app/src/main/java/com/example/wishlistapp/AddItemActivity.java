package com.example.wishlistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.wishlistapp.adapters.Image_Item_RecyclerViewAdapter;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView addImageBtn;
    ArrayList<Uri> itemImage = new ArrayList<>();
    Image_Item_RecyclerViewAdapter adapter;

    private static final int Read_permission = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        recyclerView = findViewById(R.id.item_recycle_gallery_image);
        addImageBtn = findViewById(R.id.btn_add_image_item);

        adapter = new Image_Item_RecyclerViewAdapter(itemImage);
        recyclerView.setLayoutManager(new GridLayoutManager(AddItemActivity.this, 2));
        recyclerView.setAdapter(adapter);

//        if(ContextCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(AddItemActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_permission);
//        }

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);}
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            if (data.getClipData()!= null){
                int x = data.getClipData().getItemCount();

                for (int i=0; i<x; i++){
                    itemImage.add(data.getClipData().getItemAt(i).getUri());
                }
                adapter.notifyDataSetChanged();

            }else if (data.getData()!= null){
                String imageUrL = data.getData().getPath();
                itemImage.add(Uri.parse(imageUrL));
            }
        }
    }
}