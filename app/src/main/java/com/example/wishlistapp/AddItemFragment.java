package com.example.wishlistapp;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.wishlistapp.adapters.Image_Item_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment implements View.OnClickListener, Add_Wish_Interface{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Uri> itemImage = new ArrayList<>();
    final private StorageReference storageRefItems = FirebaseStorage.getInstance().getReference("ImageItemsDB");
    Image_Item_RecyclerViewAdapter adapter = new Image_Item_RecyclerViewAdapter(itemImage);
    Activity activity;
    TextInputEditText name;
    TextInputEditText annotation;
    TextInputEditText links;
    List<String> image_uri;
    FragmentManager fragmentManager;
    ProgressDialog loadingBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.item_recycle_gallery_image);
        TextView addImageBtn = view.findViewById(R.id.btn_add_image_item);
        name = view.findViewById(R.id.edit_item_name);
        annotation = view.findViewById(R.id.edit_item_annotation);
        links = view.findViewById(R.id.edit_item_links);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        addImageBtn.setOnClickListener(this);
        Button addItem = view.findViewById(R.id.btn_add_item_add_wl);
        addItem.setOnClickListener(this);
        ImageButton back = view.findViewById(R.id.button_back_add_wishlist);
        back.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        // Получаем объект FragmentManager
        switch(v.getId()){

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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);}
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
                break;

            case R.id.btn_add_item_add_wl:

                if (TextUtils.isEmpty(name.getText().toString()))
                {
                    Toast.makeText( getActivity(), "Fill in the field \"Name\"!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    hideKeyboardFrom(getActivity(), getView());
                    loadingBar = new ProgressDialog(getContext());
                    loadingBar.setTitle("Adding an item to the wishlist");
                    loadingBar.setMessage("One second...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                //Uri[] imgArr = new Uri[itemImage.size()];
                    image_uri = new ArrayList<>();
                if (itemImage.size()!=0){

                    for (int i = 0; i < itemImage.size(); i++){
                        Uri uriItem = itemImage.get(i);
                        final StorageReference imageReferenceItem = storageRefItems.child(System.currentTimeMillis()+"."+ getFileExtension(uriItem));
                                imageReferenceItem.putFile(uriItem).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imageReferenceItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                image_uri.add(uri.toString());
                                                if (image_uri.size()==itemImage.size()){
                                                    Item item = new Item(name.getText().toString(), annotation.getText().toString(), links.getText().toString(), image_uri);
                                                    fragmentManager = getActivity().getSupportFragmentManager();
                                                    FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                                                    FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                                                    ((Add_Wish_Interface)activity).addItem(item, transaction2, fragmentManager2);
                                                    loadingBar.dismiss();
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
                                        Toast.makeText( getActivity(), "Fail add image in db", Toast.LENGTH_LONG).show();

                                    }
                                });


                            }


                //imgArr = itemImage.toArray(imgArr);}
                //else imgArr = null;
                }
                else{
                    Item item = new Item(name.getText().toString(), annotation.getText().toString(), links.getText().toString(), image_uri);
                    fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                    ((Add_Wish_Interface)activity).addItem(item, transaction2, fragmentManager2);
                    loadingBar.dismiss();
                }
                }

                break;
    }}

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        if (context instanceof Activity){
            activity = (Activity) context;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK){
            if (data.getClipData()!= null){
                int x = data.getClipData().getItemCount();

                for (int i=0; i<x; i++){
                    itemImage.add(data.getClipData().getItemAt(i).getUri());
                }
                adapter.notifyDataSetChanged();

            }else if (data.getData()!= null){
                //String imageUrL = data.getData();
                itemImage.add(data.getData());
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void addItem(Item item, FragmentTransaction transaction, FragmentManager fragmentManager) {}
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}