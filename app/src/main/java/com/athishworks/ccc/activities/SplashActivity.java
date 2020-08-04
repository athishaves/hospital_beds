package com.athishworks.ccc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.athishworks.ccc.R;

public class SplashActivity extends AppCompatActivity {

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        count = 1;

        Log.i("SplashScreen", "Activated");

        ImageView imageView = findViewById(R.id.splash_icon);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageView.getLayoutParams().height = displayMetrics.widthPixels/2;
        imageView.getLayoutParams().width = displayMetrics.widthPixels/2;

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        imageView.startAnimation(animFadeIn);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SplashScreen", "Count " + count);
                count++;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("SplashScreen", "Handler run");
                if (count>=5)
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else startActivity(new Intent(SplashActivity.this, LocateCenter.class));

                finish();
            }
        }, 2500);
    }

}