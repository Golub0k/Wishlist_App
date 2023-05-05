package com.example.wishlistapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.HomePage;
import com.example.wishlistapp.R;
import com.example.wishlistapp.ViewWishlistActivity;
import com.example.wishlistapp.models.Request;
import com.example.wishlistapp.models.User;
import com.example.wishlistapp.models.Wishlist;
import com.example.wishlistapp.profile.EditProfile;
import com.example.wishlistapp.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class Friend_RecyclerViewAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    Context context;
    private List<User> friends;
    private List<Request> requests;
    //User currentUser;

    public Friend_RecyclerViewAdapter(Context context, List<User> friends, List<Request> requests /*, String currentUserId*/) {
        this.friends = friends;
        this.context = context;
        this.requests = requests;

//        DatabaseReference dbRefUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
//        ValueEventListener eventListenerUser = dbRefUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                currentUser = snapshot.getValue(User.class);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {

        holder.name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
        holder.birthday.setText(friends.get(position).getBirthday());
//        if (currentUser!=null && (friends.get(position).getFriends()== null || !friends.get(position).getFriends().contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
        if (friends.get(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.delete_or_add.setVisibility(View.INVISIBLE);
        } else {
            boolean isRequestExists = false;

            for (Request request : requests) {
                if (request.getSenderId().equals(friends.get(position).getKey())) {
                    isRequestExists = true;
                    break;
                }
            }
            if (isRequestExists == true) {
                holder.delete_or_add.setImageDrawable(context.getResources().getDrawable(R.drawable.new_request));
                holder.delete_or_add.setVisibility(View.VISIBLE);
                holder.delete_or_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.confirmDialog(friends.get(holder.getAdapterPosition()).getKey(), requests);
                    }
                });
            } else {
                if (friends.get(position).getFriends() == null || !friends.get(position).getFriends().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    String requestId = "";
                    for (Request request : requests) {
                        if (request.getRecipientId().equals(friends.get(holder.getAdapterPosition()).getKey()) && request.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            requestId = request.getKey();
                            break;
                        }
                    }
                    if (!requestId.isEmpty()) {
                        holder.delete_or_add.setImageDrawable(context.getResources().getDrawable(R.drawable.addfriend));
                        holder.delete_or_add.setVisibility(View.VISIBLE);
                        holder.delete_or_add.setEnabled(false);
                        holder.delete_or_add.setColorFilter(context.getResources().getColor(R.color.base_400));
                    } else {
                        holder.delete_or_add.setImageDrawable(context.getResources().getDrawable(R.drawable.addfriend));
                        holder.delete_or_add.setVisibility(View.VISIBLE);
                        holder.delete_or_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.confirmDialogAddRequest(friends.get(holder.getAdapterPosition()).getKey());
                            }
                        });
                    }
                } else {

                    holder.delete_or_add.setImageDrawable(context.getResources().getDrawable(R.drawable.delete));
                    holder.delete_or_add.setVisibility(View.VISIBLE);
                    holder.delete_or_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.deleteFriend(friends.get(holder.getAdapterPosition()).getKey());
                        }
                    });
                }
            }

        }

        if (friends.get(position).getProfileImage() != null) {
            Glide.with(holder.imageView.getContext()).load(friends.get(position).getProfileImage()).placeholder(R.drawable.gallery).error(R.drawable.friend_avatar).into(holder.imageView);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.friend_avatar));
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        holder.card_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((HomePage) context).getSupportFragmentManager();
                String requestId = "";
                String requestAdded = "false";
                for (Request request : requests) {
                    if (request.getSenderId().equals(friends.get(holder.getAdapterPosition()).getKey()) && request.getRecipientId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        requestId = request.getKey();
                    }
                    if (request.getRecipientId().equals(friends.get(holder.getAdapterPosition()).getKey()) && request.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        requestAdded = "true";
                    }
                }
                // Создаем новый фрагмент
                ProfileFragment friendProfile = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Image", friends.get(holder.getAdapterPosition()).getProfileImage());
                bundle.putString("First_name", friends.get(holder.getAdapterPosition()).getFirstName());
                bundle.putString("Last_name", friends.get(holder.getAdapterPosition()).getLastName());
                bundle.putString("Key", friends.get(holder.getAdapterPosition()).getKey());
                bundle.putString("Birthday", friends.get(holder.getAdapterPosition()).getBirthday());
                bundle.putString("RequestId", requestId);
                bundle.putString("RequestAdded", requestAdded);
                if (friends.get(holder.getAdapterPosition()).getFriends() == null || !friends.get(holder.getAdapterPosition()).getFriends().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    bundle.putString("Friend_current", "false");
                } else {
                    bundle.putString("Friend_current", "true");
                }

                friendProfile.setArguments(bundle);
                // Выполняем транзакцию для добавления фрагмента в контейнер
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, friendProfile);
                transaction.commit();
            }
        });
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void searchDataList(ArrayList<User> searchList) {
        friends = searchList;
        notifyDataSetChanged();
    }


}

class FriendViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView name;
    TextView birthday;
    ImageView delete_or_add;
    CardView card_friend;


    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.friend_image);
        name = itemView.findViewById(R.id.card_friend_name);
        birthday = itemView.findViewById(R.id.card_friend_birthday);
        delete_or_add = itemView.findViewById(R.id.btn_delete_friend);
        card_friend = itemView.findViewById(R.id.card_friend);
    }

    public void confirmDialog(String senderKey, List<Request> requests) {
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_add_new_friend_dialog,
                (ConstraintLayout) itemView.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(itemView.getContext().getResources().getString(R.string.confirm_title_add_friend));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(itemView.getContext().getResources().getString(R.string.confirm_text_add_friend_approve));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(itemView.getContext().getResources().getString(R.string.approve));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(itemView.getContext().getResources().getString(R.string.reject));

        final AlertDialog alertDialog = builder.create();
        Integer x = 0;
        for (Request requestItem : requests) {
            if (requestItem.getSenderId().equals(senderKey)) {
                break;
            }
            x++;
        }
        Request request = requests.get(x);
        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                request.setState("Approved");
                reference.child("Requests").child(request.getKey()).setValue(request);
                reference.child("Users").child(senderKey).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            Integer x = 0;
                            for (DataSnapshot friend : task.getResult().getChildren()) {
                                x++;
                            }
                            reference.child("Users").child(senderKey).child("friends").child(((Integer) x).toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());


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
                            reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child(((Integer) x).toString()).setValue(senderKey);
                        }


                    }
                });
            }
        });

        viewD.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                request.setState("Rejected");
                reference.child("Requests").child(request.getKey()).setValue(request);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void deleteFriend(String senderKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_warning_dialog,
                (ConstraintLayout) itemView.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(itemView.getContext().getResources().getString(R.string.warning_title_delete_friend));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(itemView.getContext().getResources().getString(R.string.warning_text_delete_friend));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(itemView.getContext().getResources().getString(R.string.yes));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(itemView.getContext().getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                reference.child(senderKey).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            List<String> friends = new ArrayList<>();
                            for (DataSnapshot friend : task.getResult().getChildren()) {
                                if (!friend.getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    friends.add(friend.getValue(String.class));
                            }
                            reference.child(senderKey).child("friends").setValue(friends);

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
                                if (!friend.getValue(String.class).equals(senderKey))
                                    friends.add(friend.getValue(String.class));
                            }
                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").setValue(friends);

                        }
                    }
                });

//                    reference.child(currentUser.getUid().toString()).child("friends").child(getArguments().getString("Key")).removeValue();
//                    reference.child(getArguments().getString("Key")).child("friends").child(currentUser.getUid().toString()).removeValue();

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

    public void confirmDialogAddRequest(String recipientKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), R.style.AlertDialogTheme);
        View viewD = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_add_new_friend_dialog,
                (ConstraintLayout) itemView.findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(viewD);
        ((TextView) viewD.findViewById(R.id.textTitle)).setText(itemView.getContext().getResources().getString(R.string.confirm_title_add_friend));
        ((TextView) viewD.findViewById(R.id.textMessage)).setText(itemView.getContext().getResources().getString(R.string.confirm_text_add_friend));
        ((Button) viewD.findViewById(R.id.buttonYes)).setText(itemView.getContext().getResources().getString(R.string.yes));
        ((Button) viewD.findViewById(R.id.buttonNo)).setText(itemView.getContext().getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        viewD.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests");
                Request request = new Request(FirebaseAuth.getInstance().getCurrentUser().getUid(), recipientKey, "New");
                reference.child(reference.push().getKey()).setValue(request);
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
