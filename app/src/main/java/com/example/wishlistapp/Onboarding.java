package com.example.wishlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Onboarding extends AppCompatActivity {

    Integer page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        final Button Next = (Button) findViewById(R.id.next);
        final ImageButton Back = (ImageButton) findViewById(R.id.imageButton2);
        final ImageView OnboarImg1 = (ImageView) findViewById(R.id.OnboarImg1);
        final ImageView OnboarImg2 = (ImageView) findViewById(R.id.OnboarImg2);
        final ImageView OnboarImg3 = (ImageView) findViewById(R.id.OnboarImg3);
        final TextView OnboardHead = (TextView) findViewById(R.id.OnboardHead1);
        final TextView OnboardText = (TextView) findViewById(R.id.OnboardText1);
        final ImageView Count = (ImageView) findViewById(R.id.count);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(page) {
                    case 1:

                        OnboarImg1.setVisibility(View.INVISIBLE);
                        OnboarImg2.setVisibility(View.VISIBLE);
                        Back.setVisibility(View.VISIBLE);
                        OnboardHead.setText(R.string.OnboardHead2);
                        OnboardText.setText(R.string.OnboardText2);
                        Count.setImageResource(R.drawable.onboarding_count_2);
                        break;
                    case 2:
                        OnboarImg2.setVisibility(View.INVISIBLE);
                        OnboarImg3.setVisibility(View.VISIBLE);
                        OnboardHead.setText(R.string.OnboardHead3);
                        OnboardText.setText(R.string.OnboardText3);
                        Next.setText(R.string.ButtonStart);
                        Count.setImageResource(R.drawable.onboarding_count_3);
                        break;
                    case 3:
                        Intent intent = new Intent(Onboarding.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                }
                page+=1;

            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(page) {
                    case 2:

                        OnboarImg1.setVisibility(View.VISIBLE);
                        OnboarImg2.setVisibility(View.INVISIBLE);
                        Back.setVisibility(View.INVISIBLE);
                        OnboardHead.setText(R.string.OnboardHead1);
                        OnboardText.setText(R.string.OnboardText1);
                        Count.setImageResource(R.drawable.onboarding_count_1);
                        break;
                    case 3:

                        OnboarImg2.setVisibility(View.VISIBLE);
                        OnboarImg3.setVisibility(View.INVISIBLE);
                        OnboardHead.setText(R.string.OnboardHead2);
                        OnboardText.setText(R.string.OnboardText2);
                        Next.setText(R.string.ButtonNext);
                        Count.setImageResource(R.drawable.onboarding_count_2);
                        break;

                }
                page-=1;
            }
        });

    }

}