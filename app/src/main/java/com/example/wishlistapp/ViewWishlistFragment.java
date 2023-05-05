package com.example.wishlistapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.adapters.Item_RecyclerViewAdapter;
import com.example.wishlistapp.adapters.Item_RecyclerViewAdapter_For_ViewForm;
import com.example.wishlistapp.adapters.Wishlist_RecyclerViewAdapter;
import com.example.wishlistapp.models.Item;
import com.example.wishlistapp.models.Wishlist;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ViewWishlistFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    TextView name;
    TextView description;
    TextView private_public;
    ImageView icon_private_public;
    TextView max_reserve;
    RecyclerView recyclerView;
    String key = "";
    String imageUrl = "";
    ShapeableImageView view_wl_image;
    Item_RecyclerViewAdapter_For_ViewForm adapter;
    List<Item> items;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ImageView delete;
    ImageView edit;

    public ViewWishlistFragment() {
        // Required empty public constructor
    }

    public static ViewWishlistFragment newInstance(String param1, String param2) {
        ViewWishlistFragment fragment = new ViewWishlistFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_wishlist, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name = view.findViewById(R.id.wl_view_name_header);
        description = view.findViewById(R.id.text_description);
        view_wl_image = view.findViewById(R.id.view_wl_image);
        delete = view.findViewById(R.id.delete_wishlist_btn);
        edit = view.findViewById(R.id.edit_wishlist_btn);
        private_public = view.findViewById(R.id.text_private_public);
        max_reserve = view.findViewById(R.id.max_reserve_number);
        icon_private_public = view.findViewById(R.id.icon_private_public_view);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            if (!bundle.getString("Description").isEmpty()) {
                description.setText(bundle.getString("Description"));
            }
            name.setText(bundle.getString("Name"));
            if (!bundle.getString("Creator_id").equals(currentUser.getUid().toString())) {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
            }
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            if (imageUrl != null) {
                Glide.with(getActivity()).load(bundle.getString("Image")).into(view_wl_image);
            } else {
                view_wl_image.setVisibility(View.GONE);
            }

            if (bundle.getBoolean("Public")) {
                private_public.setText(getResources().getString(R.string.public_state));
                icon_private_public.setImageDrawable(getResources().getDrawable(R.drawable.resource_public));
            }

            if (bundle.getDouble("Reserve") > 0) {
                max_reserve.setText(Integer.toString((int) bundle.getDouble("Reserve")));
            }
        }

        recyclerView = view.findViewById(R.id.recycler_view_items_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        items = new ArrayList<>();
        adapter = new Item_RecyclerViewAdapter_For_ViewForm(getActivity(), items, key);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wishlists").child(key).child("items");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    item.setKey(itemSnapshot.getKey());
                    items.add(item);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WarningDialog(view);
            }
        });

        return view;
    }


    public void WarningDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(getActivity()).inflate(R.layout.layout_warning_dialog,
                (ConstraintLayout) view.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.warning_title_delete));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.warning_text_delete));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wishlists");
                if (imageUrl != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(key).removeValue();
                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomePage.class));
                            getActivity().finish();
                        }
                    });
                } else {
                    reference.child(key).removeValue();
                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), HomePage.class));
                    getActivity().finish();
                }

            }
        });

        viewD.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}