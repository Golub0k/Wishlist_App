package com.example.wishlistapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wishlistapp.adapters.Wishlist_RecyclerViewAdapter;
import com.example.wishlistapp.models.Wishlist;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyWishlistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MyWishlistsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    Wishlist_RecyclerViewAdapter adapter;
    List<Wishlist> wishlists;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    TextInputEditText search_input;

    public static MyWishlistsFragment newInstance(String param1, String param2) {
        MyWishlistsFragment fragment = new MyWishlistsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyWishlistsFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_my_wishlists, container, false);
        recyclerView = view.findViewById(R.id.wishlist_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        search_input = view.findViewById(R.id.search_input);
        search_input.clearFocus();
//        FirebaseRecyclerOptions<Wishlist> options =
//                new FirebaseRecyclerOptions.Builder<Wishlist>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Wishlists").orderByChild("creator_id").equalTo(currentUserId), Wishlist.class)
//                        .build();

//        Wishlist_RecyclerViewAdapter.OnWishlistClickListener onWishlistClickListener = new Wishlist_RecyclerViewAdapter.OnWishlistClickListener() {
//            @Override
//            public void onWishlistClick(Wishlist wishlist) {
//                Toast.makeText(getActivity(), "wishlist " + wishlist.getName(), Toast.LENGTH_SHORT).show();
//            }
//        };
        wishlists = new ArrayList<>();
        adapter = new Wishlist_RecyclerViewAdapter(getActivity(), wishlists);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wishlists");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlists.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Wishlist wishlist = itemSnapshot.getValue(Wishlist.class);
                    if (wishlist.getCreator_id().equals(currentUserId)){
                    wishlist.setKey(itemSnapshot.getKey());
                    wishlists.add(wishlist);}
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchList(s.toString());
            }
        });

        return view;
    }

    public void searchList(String text){
        ArrayList<Wishlist> searchList = new ArrayList<>();
        for (Wishlist wishlist: wishlists){
            if (wishlist.getName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(wishlist);
            }
        }
        adapter.searchDataList(searchList);
    }

//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        adapter.startListening();
//    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
//    @Override
//    public void onStop()
//    {
//        super.onStop();
//        adapter.stopListening();
//    }
}