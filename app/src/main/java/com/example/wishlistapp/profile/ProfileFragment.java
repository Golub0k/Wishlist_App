package com.example.wishlistapp.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.AddItemFragment;
import com.example.wishlistapp.R;
import com.example.wishlistapp.adapters.Wishlist_RecyclerViewAdapter;
import com.example.wishlistapp.models.User;
import com.example.wishlistapp.models.Wishlist;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    Wishlist_RecyclerViewAdapter adapter;
    List<Wishlist> wishlists;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ValueEventListener eventListenerUser;
    ShapeableImageView profile_image;
    FirebaseUser currentUser;
    User user;
    TextView name;
    TextView date_hb;
    ImageButton edit_profile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.profile_wishlists_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wishlists = new ArrayList<>();
        adapter = new Wishlist_RecyclerViewAdapter(getActivity(), wishlists);
        recyclerView.setAdapter(adapter);
        edit_profile = view.findViewById(R.id.settingButton);
        profile_image = view.findViewById(R.id.profile_image);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wishlists");
        if (currentUser.getPhotoUrl() != null){
        Glide.with(getActivity()).load(currentUser.getPhotoUrl()).into(profile_image);}
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlists.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Wishlist wishlist = itemSnapshot.getValue(Wishlist.class);
                    if (wishlist.getCreator_id().equals(currentUser.getUid()) && wishlist.getPublic_flag().equals(true)){
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
        name = view.findViewById(R.id.person_name);
        date_hb = view.findViewById(R.id.date_hb);
        DatabaseReference dbRefUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        eventListenerUser = dbRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    user = snapshot.getValue(User.class);
                    name.setText(user.getFirstName() + " " + user.getLastName());
                    date_hb.setText(user.getBirthday());
                if (user.getProfileImage() != null){
                    Glide.with(getActivity()).load(user.getProfileImage()).into(profile_image);}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Создаем новый фрагмент
                EditProfile editProfile = new EditProfile();
                Bundle bundle = new Bundle();
                bundle.putString("key", currentUser.getUid());
                bundle.putString("first_name", user.getFirstName());
                bundle.putString("last_name", user.getLastName());
                bundle.putString("birthday", user.getBirthday());
                bundle.putString("email", user.getEmail());
                bundle.putString("profileImage", user.getProfileImage());
                bundle.putString("password", user.getPassword());

                editProfile.setArguments(bundle);
                // Выполняем транзакцию для добавления фрагмента в контейнер
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, editProfile);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }


}