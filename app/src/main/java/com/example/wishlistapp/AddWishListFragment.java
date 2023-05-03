package com.example.wishlistapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wishlistapp.adapters.Item_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;
import com.example.wishlistapp.models.Wishlist;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddWishListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddWishListFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Item_RecyclerViewAdapter adapter;
    ArrayList<Item> itemsF = new ArrayList<>();
    RecyclerView recyclerView;
    Slider seekbar;
    TextInputEditText reserve_count_lable;
    TextInputEditText wl_name;
    TextInputEditText wl_description;
    Switch switch_public;
    ShapeableImageView set_wishlist_image;
    Uri wislistImage;
    ProgressDialog loadingBar;
    final private StorageReference storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
    final private StorageReference storageRefItems = FirebaseStorage.getInstance().getReference("ImageItemsDB");;
    final private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    Button btn_save;
    Uri upload_uri;
    UploadTask up;
    Integer itemPosition;
    Integer imageItemPosition;
    //Stack<Pair<Integer, Stack<Integer>>> itemStack;

    public AddWishListFragment() {
        // Required empty public constructor
    }


    public static AddWishListFragment newInstance(String param1, String param2) {
        AddWishListFragment fragment = new AddWishListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_wish_list, container, false);
//        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
//        database = FirebaseDatabase.getInstance();
//        ref = database.getReference();
        TextView add_item = view.findViewById(R.id.btn_add_item);
        add_item.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recycler_view_items_add);
        adapter = new Item_RecyclerViewAdapter(getActivity(), itemsF);
        seekbar = view.findViewById(R.id.seekbar_add_wl);
        reserve_count_lable = view.findViewById(R.id.reserve_count_lable);
        wl_name = view.findViewById(R.id.wl_name);
        wl_description = view.findViewById(R.id.wl_description);
        switch_public = view.findViewById((R.id.switch_public));
        set_wishlist_image = view.findViewById(R.id.add_wl_image);
        btn_save = view.findViewById(R.id.btn_save);
        loadingBar = new ProgressDialog(getContext());
        switch_public.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    seekbar.setEnabled(true);
                }
                else{
                    seekbar.setEnabled(false);
                    seekbar.setValue(0f);
                }
            }
        });
        seekbar.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                double number = (double) value;
                reserve_count_lable.setText(String.valueOf((long) number));
            }
        });

        set_wishlist_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);}
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(wl_name.getText().toString()))
                {
                    Toast.makeText( getActivity(), "Fill in the field \"Name\"!", Toast.LENGTH_LONG).show();
                }
                else{
                if(itemsF.size()>0){
                    hideKeyboardFrom(getActivity(), getView());
                loadingBar.setTitle("Create a wishlist");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                //createListItem();
                uploadToFirebase();
                }
                else{
                    Toast.makeText(getActivity(), "Add at least one item", Toast.LENGTH_LONG).show();
                }
            }}
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onClick(View v) {
        // Получаем объект FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Создаем новый фрагмент
        AddItemFragment addItemFragment = new AddItemFragment();

        // Выполняем транзакцию для добавления фрагмента в контейнер
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayoutNewWishlist, addItemFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void setItems(ArrayList<Item> items) {
        this.itemsF = items;

    }

    protected void updateContent() {
    Slider seekbar = getView().findViewById(R.id.seekbar_add_wl);
        if(itemsF.size()!=0){
            //seekbar.setEnabled(true);
            seekbar.setValueFrom(0f);
            seekbar.setValueTo((float)itemsF.size());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContent(); //новое максимальное значение
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK){

                wislistImage = (data.getData());
                set_wishlist_image.setImageURI(wislistImage);
        }
    }

    private void uploadToFirebase(){
        if(wislistImage != null){
        Uri uri = wislistImage;
        final StorageReference imageReference = storageRef.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Wishlist wishlist = new Wishlist(user.getUid(), wl_name.getText().toString(), wl_description.getText().toString(), uri.toString(), switch_public.isChecked(), (double) seekbar.getValue(), itemsF);
                            String key = ref.child("Wishlists").push().getKey();
                            ref.child("Wishlists").child(key).setValue(wishlist);
                            loadingBar.dismiss();
                            Toast.makeText(getActivity(), "New wishlist saved!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), HomePage.class);
                            startActivity(intent);
                            //Toast.makeText( getActivity(), "Ok wl!", Toast.LENGTH_LONG).show();

                            ////////---------------Реализация с рандомным раскидыванием картинок по айтемсам
//                            itemStack = new Stack<>(); //стек позиций
//                            //заполняем стек
//
//                            for (int i = itemsF.size()-1; i >= 0; i--){
//                                Pair<Integer, Stack<Integer>> itemsPair;
//                                Item item = itemsF.get(i);
//                                Stack <Integer> imgNumber = new Stack<>();
//                                for (int j=item.getImage().length-1;j>=0; j--){
//                                    imgNumber.push(j);
//                                }
//                                itemsPair = new Pair<>(i, imgNumber);
//                                itemStack.push(itemsPair);
//                            }
//
//                            //заливаем позиции и изобажения в бд
//                            for (int i = 0; i < itemsF.size(); i++){
//                                Item item = new Item(itemsF.get(i));  //
//                                itemsF.get(i).deleteArray();
//                                ref.child("Wishlists").child(key).child("items").child(Integer.toString(i)).setValue(itemsF.get(i));
//                                int x = item.getImage().length;
//                                for (int j = 0; j < x; j++){
//                                Uri uriItem = item.getImage(j);
//                                final StorageReference imageReferenceItem = storageRefItems.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
//                                imageReferenceItem.putFile(uriItem).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        imageReferenceItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                            @Override
//                                            public void onSuccess(Uri uri) {
//                                                String item_number = Integer.toString(itemStack.peek().first);
//                                                String image_number = Integer.toString(itemStack.peek().second.pop());
//                                                ref.child("Wishlists").child(key).child("items").child(item_number).child("image_uri").child(image_number).setValue(uri.toString());
//                                                if (
//                                                        itemStack.peek().second.isEmpty()
//                                                ){itemStack.pop();
//                                                    if (
//                                                            itemStack.isEmpty()
//                                                    ) {
//                                                        loadingBar.dismiss();
//                                                        Toast.makeText(getActivity(), "New wishlist saved!", Toast.LENGTH_LONG).show();
//                                                        Intent intent = new Intent(getActivity(), HomePage.class);
//                                                        startActivity(intent);
//                                                    }
//
//                                                }
//
//                                            }
//                                        });
//                                    }
//                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText( getActivity(), "Fail item", Toast.LENGTH_LONG).show();
//
//                                    }
//                                });
//
//
//                            }
//
//
//                        }
                            ////////--------------- Конец Реализация с рандомным раскидыванием картинок по айтемсам

                    }
                }});}

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //Toast.makeText( getActivity(), "Ok progress wl!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText( getActivity(), "Fail wl", Toast.LENGTH_LONG).show();
            }
        });
        }
        else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Wishlist wishlist = new Wishlist(user.getUid(), wl_name.getText().toString(), wl_description.getText().toString(), null, switch_public.isChecked(), (double) seekbar.getValue(), itemsF);
                ref.child("Wishlists").child(ref.child("Wishlists").push().getKey()).setValue(wishlist);
                loadingBar.dismiss();
                Toast.makeText(getActivity(), "New wishlist saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), HomePage.class);
                startActivity(intent);
            }
        }


}

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
//    private void createListItem(){
//
//        for (int i=0; i< itemsF.size(); i++){
//            Item item = itemsF.get(i);
//            if (item.getImage() != null){
//            List<String> image_uri_list = new ArrayList<>(item.getImage().length);
//            addUrl(item, image_uri_list, 0);
//            item.setImage_uri(image_uri_list);
//            item.deleteArray();
//            itemsF.set(i, item);
////            for(int j = 0; j < item.getImage().length; j++){
////                Uri uri = item.getImage(j);
////
////                final StorageReference imageReferenceItem = storageRefItems.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
////                imageReferenceItem.putFile(uri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                    @Override
////                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                        imageReferenceItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                            @Override
////                            public void onSuccess(Uri uri) {
////                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////                                if (user != null){
////
////                                    image_uri_list.add(uri.toString());
////
////                                }
////
////                            }
////                        });
////                    }
////                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
////                    @Override
////                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
////                    }
////                }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        Toast.makeText( getActivity(), "Fail item", Toast.LENGTH_LONG).show();
////                    }
////                });
////
////            }
////            item.setImage_uri(image_uri_list);
////            item.deleteArray();
////            itemsF.set(i, item);
//            }
//
//        }
//
//    }

//    private void addUrl(Item item, List<String> image_uri_list, Integer j){
//            Uri uri = item.getImage(j);
//            final StorageReference imageReferenceItem = storageRefItems.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
//            imageReferenceItem.putFile(uri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    imageReferenceItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            if (user != null){
//
//                                image_uri_list.add(uri.toString());
//                                if (j+1<item.getImage().length){
//                                addUrl(item, image_uri_list, j+1);}
//                                else {
//                                    return;
//                                };
//
//
//                            }
//
//                        }
//                    });
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText( getActivity(), "Fail item", Toast.LENGTH_LONG).show();
//                    return;
//
//                }
//            });
//        //while (j<item.getImage().length) {}
//
//    }


}