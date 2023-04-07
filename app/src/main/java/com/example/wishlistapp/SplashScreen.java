package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View.OnClickListener;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    // в дальнейшем тут будет переход на страницу регистрации/авторизации
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
        Intent intent = new Intent(this, Onboarding.class);
        startActivity(intent);
        finish();
    }

}