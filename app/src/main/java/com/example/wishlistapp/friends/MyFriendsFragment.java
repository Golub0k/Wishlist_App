package com.example.wishlistapp.friends;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.HomePage;
import com.example.wishlistapp.MyWishlistsFragment;
import com.example.wishlistapp.R;
import com.example.wishlistapp.adapters.Friend_RecyclerViewAdapter;
import com.example.wishlistapp.adapters.Wishlist_RecyclerViewAdapter;
import com.example.wishlistapp.models.Request;
import com.example.wishlistapp.models.User;
import com.example.wishlistapp.models.Wishlist;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyFriendsFragment extends Fragment {

    RecyclerView recyclerView;
    Friend_RecyclerViewAdapter adapter;
    List<User> friends;
    List<Request> requests;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ValueEventListener eventListenerRequests;
    TextInputEditText search_input;
    ImageButton add_new_friend;
    String currentUserId;
    TextView friend_list_header;
    ImageButton btn_back_from_add_friend;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    public static MyFriendsFragment newInstance(String param1, String param2) {
        MyFriendsFragment fragment = new MyFriendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);
        recyclerView = view.findViewById(R.id.friends_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        search_input = view.findViewById(R.id.search_friends_input);
        add_new_friend = view.findViewById(R.id.add_new_friend);
        friend_list_header = view.findViewById(R.id.friend_list_header);
        btn_back_from_add_friend = view.findViewById(R.id.btn_back_from_add_friend);
        search_input.clearFocus();

        friends = new ArrayList<>();
        requests = new ArrayList<>();


        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventListenerRequests = databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Request request = itemSnapshot.getValue(Request.class);
                    if ((request.getRecipientId().equals(currentUserId) || request.getSenderId().equals(currentUserId)) && request.getState().equals("New")) {
                        request.setKey(itemSnapshot.getKey());
                        requests.add(request);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        eventListener = databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    User friend = itemSnapshot.getValue(User.class);
                    friend.setKey(itemSnapshot.getKey());
                    if (friend.getFriends() != null) {
                        if (friend.getFriends().contains(currentUserId)) {

                            friends.add(friend);
                        } else {
                            if (requests != null) {
                                for (int i = 0; i < requests.size(); i++) {
                                    if (requests.get(i).getSenderId().equals(friend.getKey()) && !friend.getKey().equals(currentUserId)) {
                                        friends.add(friend);
                                    }
                                }
                            }
                        }
                    } else {
                        if (requests != null) {
                            for (int i = 0; i < requests.size(); i++) {
                                if (requests.get(i).getSenderId().equals(friend.getKey()) && !friend.getKey().equals(currentUserId)) {
                                    friends.add(friend);
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });

        adapter = new Friend_RecyclerViewAdapter(getActivity(), friends, requests /*, currentUserId*/);
        recyclerView.setAdapter(adapter);
        add_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNewFriends();
            }
        });

        btn_back_from_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFriends();
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (friend_list_header.getText().toString().equals(getResources().getString(R.string.friends))) {
                    MyWishlistsFragment fragment = new MyWishlistsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit();
                    BottomNavigationView bottomNavigationView;
                    bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.setSelectedItemId(R.id.menu_home);
                    //(HomePage)getActivity().bottomNavigationView.setSelectedItemId(R.id.menu_item_id)
                } else {
                    myFriends();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

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

    void findNewFriends() {
        eventListener = databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    User friend = itemSnapshot.getValue(User.class);

                    friend.setKey(itemSnapshot.getKey());
                    friends.add(friend);


                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });

        friend_list_header.setText("All users");
        btn_back_from_add_friend.setVisibility(View.VISIBLE);
        add_new_friend.setVisibility(View.GONE);

    }

    public void myFriends() {
        friend_list_header.setText(getResources().getString(R.string.friends));
        btn_back_from_add_friend.setVisibility(View.GONE);
        add_new_friend.setVisibility(View.VISIBLE);
        eventListener = databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    User friend = itemSnapshot.getValue(User.class);
                    friend.setKey(itemSnapshot.getKey());
                    if (friend.getFriends() != null) {
                        if (friend.getFriends().contains(currentUserId)) {

                            friends.add(friend);
                        } else {
                            if (requests != null) {
                                for (int i = 0; i < requests.size(); i++) {
                                    if (requests.get(i).getSenderId().equals(friend.getKey()) && !friend.getKey().equals(currentUserId)) {
                                        friends.add(friend);
                                    }
                                }
                            }
                        }
                    } else {
                        if (requests != null) {
                            for (int i = 0; i < requests.size(); i++) {
                                if (requests.get(i).getSenderId().equals(friend.getKey()) && !friend.getKey().equals(currentUserId)) {
                                    friends.add(friend);
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void searchList(String text) {
        ArrayList<User> searchList = new ArrayList<>();
        for (User friend : friends) {
            if (friend.getFirstName().toLowerCase().contains(text.toLowerCase()) || friend.getLastName().toLowerCase().contains(text.toLowerCase()) || friend.getEmail().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(friend);
            }
        }
        adapter.searchDataList(searchList);
    }

}