package com.example.wishlistapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wishlistapp.adapters.Image_Item_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddItemFragment extends Fragment implements View.OnClickListener, Add_Wish_Interface, Image_Item_RecyclerViewAdapter.itemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final int PICK_IMAGE = 1;
    ArrayList<Uri> itemImage = new ArrayList<>();
    final private StorageReference storageRefItems = FirebaseStorage.getInstance().getReference("ImageItemsDB");
    Image_Item_RecyclerViewAdapter adapter = new Image_Item_RecyclerViewAdapter(itemImage, this);
    Activity activity;
    TextView header;
    TextInputEditText name;
    TextInputEditText annotation;
    TextInputEditText links;
    List<String> image_uri;
    List<String> image_uri_copy;
    FragmentManager fragmentManager;
    ProgressDialog loadingBar;
    Item item;
    Integer position;
    String parent_key;
    Button addItem;


    public AddItemFragment() {
        // Required empty public constructor
    }

    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.item_recycle_gallery_image);
        TextView addImageBtn = view.findViewById(R.id.btn_add_image_item);
        header = view.findViewById(R.id.header_add_or_edit_item);
        name = view.findViewById(R.id.edit_item_name);
        annotation = view.findViewById(R.id.edit_item_annotation);
        links = view.findViewById(R.id.edit_item_links);
        addItem = view.findViewById(R.id.btn_add_item_add_wl);
        image_uri = new ArrayList<>();
        if (item != null) {
            header.setText("Edit item");
            name.setText(item.getName());
            annotation.setText(item.getAnnotation());
            links.setText(item.getLinks());
            if (item.getImage_uri() != null) {
                image_uri = item.getImage_uri();
                image_uri_copy = new ArrayList<>(image_uri);
                adapter = new Image_Item_RecyclerViewAdapter(image_uri, itemImage, this);
            }
            addItem.setText("Save");
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        addImageBtn.setOnClickListener(this);

        addItem.setOnClickListener(this);
        ImageButton back = view.findViewById(R.id.button_back_add_wishlist);
        back.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        // Получаем объект FragmentManager
        switch (v.getId()) {

            case R.id.button_back_add_wishlist:
                fragmentManager = getActivity().getSupportFragmentManager();

                // Создаем новый фрагмент
                AddWishListFragment addWishListFragment = new AddWishListFragment();

                // Выполняем транзакцию для добавления фрагмента в контейнер
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayoutNewWishlist, addWishListFragment);
                transaction.commit();
                break;

            case R.id.btn_add_image_item:
                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE);
                break;

            case R.id.btn_add_item_add_wl:

                if (TextUtils.isEmpty(name.getText().toString())) {
                    Toast.makeText(getActivity(), "Fill in the field \"Name\"!", Toast.LENGTH_LONG).show();
                } else {
                    hideKeyboardFrom(getActivity(), getView());
                    loadingBar = new ProgressDialog(getContext());
                    loadingBar.setTitle("Adding an item to the wishlist");
                    loadingBar.setMessage("One second...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    //Uri[] imgArr = new Uri[itemImage.size()];
                    //Если это добавление позиции (а не редактирование)
                    if (image_uri_copy != null) {
                        deleteStorage();
                    }
                    if (itemImage.size() != 0) {
                        Integer image_uri_before = image_uri.size();
                        for (int i = 0; i < itemImage.size(); i++) {
                            Uri uriItem = itemImage.get(i);
                            final StorageReference imageReferenceItem = storageRefItems.child(System.currentTimeMillis() + "." + getFileExtension(uriItem));
                            imageReferenceItem.putFile(uriItem).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageReferenceItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            image_uri.add(uri.toString());
                                            if ((image_uri.size() - image_uri_before) == itemImage.size()) {
                                                Item item_new = new Item(name.getText().toString(), annotation.getText().toString(), links.getText().toString(), image_uri);
                                                fragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                                                FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                                                if (item == null) { //если это добавление новой позиции
                                                    ((Add_Wish_Interface) activity).addItem(item_new, transaction2, fragmentManager2);
                                                } else {
                                                    if (parent_key.isEmpty()) { //если это редактирование на форме создания вишлиста
                                                        ((Add_Wish_Interface) activity).updateItem(item_new, position, transaction2, fragmentManager2);
                                                    } else {//если это редактирование позиции уже существующего вишлиста
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                        ref.child("Wishlists").child(parent_key).child("items").child(position.toString()).setValue(item_new);
                                                        ((Add_Wish_Interface) activity).updateItem(item_new, position, transaction2, fragmentManager2);
                                                    }
                                                }
                                                loadingBar.dismiss();
                                                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                                                    getParentFragmentManager().popBackStack();
                                                }
                                            }

                                        }
                                    });
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Fail add image in db", Toast.LENGTH_LONG).show();

                                }
                            });


                        }


                        //imgArr = itemImage.toArray(imgArr);}
                        //else imgArr = null;
                    } else {
                        Item item_new = new Item(name.getText().toString(), annotation.getText().toString(), links.getText().toString(), image_uri);
                        fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                        if (item == null) {
                            ((Add_Wish_Interface) activity).addItem(item_new, transaction2, fragmentManager2);
                        }else {
                            if (parent_key.isEmpty()) { //если это редактирование на форме создания вишлиста
                                ((Add_Wish_Interface) activity).updateItem(item_new, position, transaction2, fragmentManager2);
                            } else {//если это редактирование позиции уже существующего вишлиста
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("Wishlists").child(parent_key).child("items").child(position.toString()).setValue(item_new);
                                ((Add_Wish_Interface) activity).updateItem(item_new, position, transaction2, fragmentManager2);
                            }
                        }

                        loadingBar.dismiss();
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();
                        }
                    }
                }

                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();

                for (int i = 0; i < x; i++) {
                    if (itemImage.size() < 10) {
                        itemImage.add(data.getClipData().getItemAt(i).getUri());
                    } else {
                        Toast.makeText(getActivity(), "You can choose up to 10 photos", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                adapter.notifyDataSetChanged();

            } else if (data.getData() != null) {
                //String imageUrL = data.getData();
                if (itemImage.size() < 10) {
                    itemImage.add(data.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "You can choose up to 10 photos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void addItem(Item item, FragmentTransaction transaction, FragmentManager fragmentManager) {
    }

    @Override
    public void editItem(Item item, Integer position, String parent_key, FragmentTransaction transaction, FragmentManager fragmentManager) {

    }

    @Override
    public void updateItem(Item item, Integer position, FragmentTransaction transaction, FragmentManager fragmentManager) {

    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void itemClick(int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.zoom_image);
        ImageView zoom_image = dialog.findViewById(R.id.zoomImage);
        if (position < image_uri.size()) {
            Glide.with(getActivity()).load(image_uri.get(position)).apply(new RequestOptions().override(1000, 1000)).placeholder(R.drawable.gallery).error(R.drawable.min_logo).into(zoom_image);
        } else {
            zoom_image.setImageURI(itemImage.get(position));
        }
        ImageView close_image = dialog.findViewById(R.id.close_image);
        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setItem(Item item, Integer position, String parent_key) {
        this.item = item;
        this.position = position;
        this.parent_key = parent_key;
    }

    public void deleteStorage() {
        if (image_uri.size() < image_uri_copy.size()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            if (image_uri.size() == 0) {
                for (int i = 0; i < image_uri_copy.size(); i++) {
                    StorageReference storageReference = storage.getReferenceFromUrl(image_uri_copy.get(i));
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    });
                }
            } else {
                int j = 0;
                for (int i = 0; i < image_uri.size(); ) {
                    if (image_uri.get(i).equals(image_uri_copy.get(j))) {
                        i++;
                        j++;
                    } else {
                        StorageReference storageReference = storage.getReferenceFromUrl(image_uri_copy.get(j));
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                        j++;
                    }
                }
            }
        }

    }
}
