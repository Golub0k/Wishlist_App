package com.example.wishlistapp.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.AddItemFragment;
import com.example.wishlistapp.HomePage;
import com.example.wishlistapp.MyWishlistsFragment;
import com.example.wishlistapp.R;
import com.example.wishlistapp.adapters.Wishlist_RecyclerViewAdapter;
import com.example.wishlistapp.friends.MyFriendsFragment;
import com.example.wishlistapp.models.Request;
import com.example.wishlistapp.models.User;
import com.example.wishlistapp.models.Wishlist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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


public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    Wishlist_RecyclerViewAdapter adapter;
    List<Wishlist> wishlists;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ValueEventListener eventListenerUser;
    ShapeableImageView profile_image;
    FirebaseUser currentUser;
    TextView text_no_public_wl;
    User user;
    TextView name;
    TextView date_hb;
    ImageButton btn_left;
    ImageButton back;
    TextView header;
    String requestId;

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
        btn_left = view.findViewById(R.id.settingButton);
        profile_image = view.findViewById(R.id.profile_image);

        if (getArguments() == null || getArguments().getString("Key").equals(currentUser.getUid().toString())) {
            currentUserProfile(view);
        } else {
            if (getArguments().getString("RequestId") != null) {
                requestId = getArguments().getString("RequestId");
            }
            otherUser(view);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (getArguments() == null) {
                    MyWishlistsFragment fragment = new MyWishlistsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit();
                    BottomNavigationView bottomNavigationView;
                    bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.setSelectedItemId(R.id.menu_home);
                } else {
                    MyFriendsFragment fragment = new MyFriendsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit();
                    BottomNavigationView bottomNavigationView;
                    bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.setSelectedItemId(R.id.menu_friends);;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }


    public void currentUserProfile(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wishlists");
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(getActivity()).load(currentUser.getPhotoUrl()).into(profile_image);
        }
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlists.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Wishlist wishlist = itemSnapshot.getValue(Wishlist.class);
                    if (wishlist.getCreator_id().equals(currentUser.getUid()) && wishlist.getPublic_flag().equals(true)) {
                        wishlist.setKey(itemSnapshot.getKey());
                        wishlists.add(wishlist);
                    }
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
                if (user.getProfileImage() != null) {
                    Glide.with(getActivity()).load(user.getProfileImage()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ups", Toast.LENGTH_LONG).show();
            }
        });

        btn_left.setOnClickListener(new View.OnClickListener() {
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
    }

    public void otherUser(View view) {
        text_no_public_wl = view.findViewById(R.id.text_no_public_wl);
//        TextView text_not_friend = view.findViewById(R.id.text_not_friend);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wishlists");
        if (getArguments().getString("Image") != null) {
            Glide.with(getActivity()).load(getArguments().getString("Image")).into(profile_image);
        }
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlists.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Wishlist wishlist = itemSnapshot.getValue(Wishlist.class);
                    if (getArguments().getString("Friend_current").equals("true") && wishlist.getCreator_id().equals(getArguments().getString("Key")) && wishlist.getPublic_flag().equals(true)) {
                        wishlist.setKey(itemSnapshot.getKey());
                        wishlists.add(wishlist);
                    }
                }
                if (wishlists.size() == 0) {
                    text_no_public_wl.setVisibility(View.VISIBLE);
                } else {
                    text_no_public_wl.setVisibility(View.GONE);
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
        back = view.findViewById(R.id.btn_back_from_friend_profile);
        header = view.findViewById(R.id.profile_header);
        name.setText(getArguments().getString("First_name") + " " + getArguments().getString("Last_name"));
        date_hb.setText(getArguments().getString("Birthday"));
        header.setText(getArguments().getString("First_name") + " " + getArguments().getString("Last_name"));
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
        if (getArguments().getString("Friend_current").equals("true")) {
            btn_left.setImageDrawable(getResources().getDrawable(R.drawable.delete_friends));
            btn_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriend(view);
                }
            });
        } else {
            btn_left.setImageDrawable(getResources().getDrawable(R.drawable.add_friends));
            if (getArguments().getString("RequestAdded").equals("true")) {
                text_no_public_wl.setText(getResources().getText(R.string.not_friend_request));
                btn_left.setEnabled(false);
                btn_left.setColorFilter(getResources().getColor(R.color.base_400));
            } else {
                text_no_public_wl.setText(getResources().getText(R.string.not_friend));
                btn_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestId.isEmpty()) {
                            confirmDialogAddRequest(view);
                        } else {
                            confirmDialogAcceptRequest(view);
                        }
                    }
                });
            }
        }
    }

    public void confirmDialogAddRequest(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(getActivity()).inflate(R.layout.layout_add_new_friend_dialog,
                (ConstraintLayout) view.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.confirm_title_add_friend));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.confirm_text_add_friend));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests");
                Request request = new Request(currentUser.getUid().toString(), getArguments().getString("Key"), "New");
                reference.child(reference.push().getKey()).setValue(request);
                btn_left.setEnabled(false);
                btn_left.setColorFilter(getResources().getColor(R.color.base_400));
                text_no_public_wl.setText(getResources().getText(R.string.not_friend_request));
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

    public void confirmDialogAcceptRequest(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(getActivity()).inflate(R.layout.layout_add_new_friend_dialog,
                (ConstraintLayout) view.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.confirm_title_add_friend));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.confirm_text_add_friend_approve));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.approve));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.reject));

        final AlertDialog alertDialog = builder.create();
        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Requests").child(requestId).child("state").setValue("Accept");
                reference.child("Users").child(getArguments().getString("Key")).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            Integer x = 0;
                            for (DataSnapshot friend : task.getResult().getChildren()) {
                                x++;
                            }
                            reference.child("Users").child(getArguments().getString("Key")).child("friends").child(((Integer) x).toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());


                        }

                    }
                });
                reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            Integer x = 0;
                            for (DataSnapshot friend : task.getResult().getChildren()) {
                                x++;
                            }
                            reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child(((Integer) x).toString()).setValue(getArguments().getString("Key"));
                        }


                    }
                });
                requestId = "";
                btn_left.setImageDrawable(getResources().getDrawable(R.drawable.delete_friends));
                btn_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriend(view);
                    }
                });
                Toast.makeText(getActivity(), "User added to friends list", Toast.LENGTH_SHORT).show();
            }
        });

        viewD.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Requests").child(requestId).child("state").setValue("Rejected");
                requestId = "";
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void deleteFriend(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(getActivity()).inflate(R.layout.layout_warning_dialog,
                (ConstraintLayout) view.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.warning_title_delete_friend));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.warning_text_delete_friend));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                reference.child(getArguments().getString("Key")).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            List<String> friends = new ArrayList<>();
                            for (DataSnapshot friend : task.getResult().getChildren()) {
                                if (!friend.getValue(String.class).equals(currentUser.getUid().toString()))
                                    friends.add(friend.getValue(String.class));
                            }
                            reference.child(getArguments().getString("Key")).child("friends").setValue(friends);

                        }

                    }
                });
                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            List<String> friends = new ArrayList<>();
                            for (DataSnapshot friend : task.getResult().getChildren()) {
                                if (!friend.getValue(String.class).equals(getArguments().getString("Key")))
                                    friends.add(friend.getValue(String.class));
                            }
                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").setValue(friends);

                        }
                    }
                });

//                    reference.child(currentUser.getUid().toString()).child("friends").child(getArguments().getString("Key")).removeValue();
//                    reference.child(getArguments().getString("Key")).child("friends").child(currentUser.getUid().toString()).removeValue();
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                btn_left.setImageDrawable(getResources().getDrawable(R.drawable.add_friends));
                text_no_public_wl.setText(getResources().getText(R.string.not_friend));
                btn_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestId.isEmpty()) {
                            confirmDialogAddRequest(view);
                        } else {
                            confirmDialogAcceptRequest(view);
                        }
                    }
                });
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