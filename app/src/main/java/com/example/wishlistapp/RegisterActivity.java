package com.example.wishlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wishlistapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText birthdayInput;
    TextInputEditText firstnameInput;
    TextInputEditText lastnameInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    ProgressDialog loadingBar;
    TextView signIn;
    Button createAcc;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        birthdayInput = (TextInputEditText) findViewById(R.id.register_birthday);
        firstnameInput = (TextInputEditText) findViewById(R.id.register_first_name);
        lastnameInput = (TextInputEditText) findViewById(R.id.register_last_name);
        emailInput = (TextInputEditText) findViewById(R.id.register_email);
        passwordInput = (TextInputEditText) findViewById(R.id._register_password);
        signIn = (TextView) findViewById(R.id.register_sign_in);
        createAcc = (Button) findViewById(R.id.button_create_account);
        loadingBar = new ProgressDialog(this);

        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        birthdayInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "MaterialDatePicker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        birthdayInput.setText(datePicker.getHeaderText());
                    }
                });
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount(){
        String firstName = firstnameInput.getText().toString();
        String lastName = lastnameInput.getText().toString();
        String birthday = birthdayInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if ((TextUtils.isEmpty(firstName)) || (TextUtils.isEmpty(lastName)) || (TextUtils.isEmpty(birthday)) || (TextUtils.isEmpty(password)) || (TextUtils.isEmpty(email))){
            Toast.makeText(RegisterActivity.this, "Fill in all the fields!", Toast.LENGTH_LONG).show();
        }
        else{
            if (password.trim().length() < 6){
                Toast.makeText(RegisterActivity.this, "Minimum password length - 6 characters!", Toast.LENGTH_LONG).show();
            }else{
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this, "Incorrect email...", Toast.LENGTH_LONG).show();
                }
                else{
            Toast.makeText(RegisterActivity.this, "Everything is fine! Account is being created...", Toast.LENGTH_SHORT).show();
            loadingBar.setTitle("Create an account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateLogin(email, password, birthday, firstName, lastName);}}
        }
    }

    private void ValidateLogin(String email, String password, String birthday, String firstName, String lastName) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
//                            ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(email);
//                            ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("password").setValue(password);
//                            ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("firstName").setValue(firstName);
//                            ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("lastName").setValue(lastName);
//                            ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("birthday").setValue(birthday);

                            User user = new User();
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setBirthday(birthday);
                            ref.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user);

                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration completed successfully! Please sign in", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "You have some errors", Toast.LENGTH_LONG).show();
                        }
                    }
                });
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference RootRef = database.getReference();
//        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!(snapshot.child("Users").child(login).exists())){
//                    HashMap<String,Object> userDataMap = new HashMap<>();
//                    userDataMap.put("firstName", firstName);
//                    userDataMap.put("lastName", lastName);
//                    userDataMap.put("birthday", birthday);
//                    userDataMap.put("login", login);
//                    userDataMap.put("password", password);
//
//                    RootRef.child("Users").child(login).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                loadingBar.dismiss();
//                                Toast.makeText(RegisterActivity.this, "User with this login already exists", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            }
//                            else{
//                                loadingBar.dismiss();
//                                Toast.makeText(RegisterActivity.this, "Error :(", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//
//                }
//                else{
//                    loadingBar.dismiss();
//                    Toast.makeText(RegisterActivity.this, "Registration completed successfully! Please sign in", Toast.LENGTH_LONG).show();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                loadingBar.dismiss();
//                Toast.makeText(RegisterActivity.this, "Error :(", Toast.LENGTH_LONG).show();
//            }
//        });
    }
}