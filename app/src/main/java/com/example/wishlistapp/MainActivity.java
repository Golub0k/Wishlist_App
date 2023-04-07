package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // в дальнейшем здесь будет находиться обработчик на первичный вход пользователя в систему, чтобы показать ему приветственные слайды
//        Intent intent = new Intent(this, Onboarding.class);
//        startActivity(intent);
//        finish();
    }
}