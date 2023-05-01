package com.example.wishlistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wishlistapp.adapters.Image_Item_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

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
    Image_Item_RecyclerViewAdapter adapter = new Image_Item_RecyclerViewAdapter(itemImage);
    Activity activity;
    TextInputEditText name;
    TextInputEditText annotation;
    TextInputEditText links;

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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

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
                Uri[] imgArr = new Uri[itemImage.size()];
                if (itemImage.size()!=0){
                imgArr = itemImage.toArray(imgArr);}
                else imgArr = null;
                Item item = new Item(name.getText().toString(), annotation.getText().toString(), links.getText().toString(), imgArr);
                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                ((Add_Wish_Interface)activity).addItem(item, transaction2, fragmentManager2);}

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
}