package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button singOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // в дальнейшем здесь будет находиться обработчик на первичный вход пользователя в систему, чтобы показать ему приветственные слайды
//        Intent intent = new Intent(this, Onboarding.class);
//        startActivity(intent);
//        finish();
        singOut = (Button) findViewById(R.id.button_sign_out);
        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}