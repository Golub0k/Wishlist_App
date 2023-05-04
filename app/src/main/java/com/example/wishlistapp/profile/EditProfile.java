package com.example.wishlistapp.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wishlistapp.Add_Wish_Interface;
import com.example.wishlistapp.HomePage;
import com.example.wishlistapp.R;
import com.example.wishlistapp.RegisterActivity;
import com.example.wishlistapp.models.User;
import com.example.wishlistapp.models.Wishlist;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;

public class EditProfile extends Fragment {

    FloatingActionButton editImage;
    ShapeableImageView profileImage;
    Uri uri;
    ProgressDialog loadingBar;
    TextInputEditText first_name;
    TextInputEditText last_name;
    TextInputEditText birthday;
    TextInputEditText password;
    TextInputEditText email;
    Button save;
    String first_name_old;
    String last_name_old;
    String birthday_old;
    String password_old;
    String email_old;
    final private StorageReference storageRef = FirebaseStorage.getInstance().getReference("UsersDB");
    final private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public EditProfile() {
        // Required empty public constructor
    }

    public static EditProfile newInstance(String param1, String param2) {
        EditProfile fragment = new EditProfile();
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editImage = view.findViewById(R.id.edit_profile_image_btn);
        profileImage = view.findViewById(R.id.profile_image_edit);
        first_name = view.findViewById(R.id.edit_profile_first_name);
        last_name = view.findViewById(R.id.edit_profile_last_name);
        birthday = view.findViewById(R.id.edit_profile_birthday);
        email = view.findViewById(R.id.edit_profile_email);
        password = view.findViewById(R.id.edit_profile_password);
        save = view.findViewById(R.id.button_save_change_profile);
        first_name_old = getArguments().getString("first_name");
        last_name_old = getArguments().getString("last_name");
        birthday_old = getArguments().getString("birthday");
        password_old = getArguments().getString("password");
        email_old = getArguments().getString("email");
        first_name.setText(getArguments().getString("first_name"));
        last_name.setText(getArguments().getString("last_name"));
        birthday.setText(getArguments().getString("birthday"));
        email.setText(getArguments().getString("email"));
        password.setText(getArguments().getString("password"));
        if (getArguments().getString("profileImage") != null){
            Glide.with(getActivity()).load(getArguments().getString("profileImage")).into(profileImage);}

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfile.this)
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


        Locale.setDefault(Locale.ENGLISH);
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getActivity().getSupportFragmentManager(), "MaterialDatePicker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        birthday.setText(datePicker.getHeaderText());
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((TextUtils.isEmpty(first_name.getText().toString())) || (TextUtils.isEmpty(last_name.getText().toString())) || (TextUtils.isEmpty(birthday.getText().toString())) || (TextUtils.isEmpty(password.getText().toString())) || (TextUtils.isEmpty(email.getText().toString()))){
                    Toast.makeText(getActivity(), "Fill in all the fields!", Toast.LENGTH_LONG).show();
                }
                else{
                    if (password.getText().toString().trim().length() < 6){
                        Toast.makeText(getActivity(), "Minimum password length - 6 characters!", Toast.LENGTH_LONG).show();
                    }else {
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                            Toast.makeText(getActivity(), "Incorrect email...", Toast.LENGTH_LONG).show();
                        }
                        else {
                            loadingBar = new ProgressDialog(getContext());
                            loadingBar.setTitle("Update profile");
                            loadingBar.setMessage("Please wait...");
                            loadingBar.setCanceledOnTouchOutside(false);
                            loadingBar.show();
                            if (uri != null) {
                                final StorageReference imageReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
                                imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uriDB) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                UserProfileChangeRequest profileUpdates;
                                                if (!last_name.getText().toString().equals(last_name_old) || !first_name.getText().toString().equals(first_name_old)) {
                                                    profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(first_name.getText().toString() + " " + last_name.getText().toString())
                                                            .setPhotoUri(uriDB)
                                                            .build();
                                                } else {
                                                    profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setPhotoUri(uriDB)
                                                            .build();
                                                }

                                                user.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                }
                                                            }
                                                        });
                                                if (!email.getText().toString().equals(email_old)) {
                                                    user.updateEmail(email.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                    }
                                                                }
                                                            });
                                                }

                                                if (!password.getText().toString().equals(password_old)) {
                                                    user.updatePassword(password.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                    }
                                                                }
                                                            });
                                                }

                                                User updateUser = new User();
                                                updateUser.setEmail(email.getText().toString());
                                                updateUser.setPassword(password.getText().toString());
                                                updateUser.setFirstName(first_name.getText().toString());
                                                updateUser.setLastName(last_name.getText().toString());
                                                updateUser.setBirthday(birthday.getText().toString());
                                                updateUser.setProfileImage(uriDB.toString());
                                                ref.child("Users").child(user.getUid()).setValue(updateUser);

                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                                ProfileFragment profileFragment = new ProfileFragment();
                                                transaction.replace(R.id.frameLayout, profileFragment);
                                                transaction.commit();
                                                loadingBar.dismiss();
                                                Toast.makeText(getActivity(), "Profile changes saved!", Toast.LENGTH_LONG).show();


                                            }
                                        });
                                    }

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
                            } else {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates;
                                if (!last_name.getText().toString().equals(last_name_old) || !first_name.getText().toString().equals(first_name_old)) {
                                    profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(first_name.getText().toString() + " " + last_name.getText().toString())
                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                }

                                if (!email.getText().toString().equals(email_old)) {
                                    user.updateEmail(email.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                }

                                if (!password.getText().toString().equals(password_old)) {
                                    user.updatePassword(password.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                }

                                User updateUser = new User();
                                updateUser.setEmail(email.getText().toString());
                                updateUser.setPassword(password.getText().toString());
                                updateUser.setFirstName(first_name.getText().toString());
                                updateUser.setLastName(last_name.getText().toString());
                                updateUser.setBirthday(birthday.getText().toString());
                                if (user.getPhotoUrl() != null) {
                                    updateUser.setProfileImage(user.getPhotoUrl().toString());
                                }
                                ref.child("Users").child(user.getUid()).setValue(updateUser);

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                ProfileFragment profileFragment = new ProfileFragment();
                                transaction.replace(R.id.frameLayout, profileFragment);
                                transaction.commit();
                                loadingBar.dismiss();
                                Toast.makeText(getActivity(), "Profile changes saved!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        profileImage.setImageURI(uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            uri = data.getData();
            profileImage.setImageURI(uri);}
        else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();}
        else {
            Toast.makeText(getActivity(), "Sorry, something went wrong. Please try again later", Toast.LENGTH_SHORT).show();}


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
}