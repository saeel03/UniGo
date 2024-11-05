package com.example.ridesharing;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Reference to the logo ImageView
        ImageView logo = findViewById(R.id.splash_logo);

        // Load the animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in); // Change to fade_in for fade effect
        logo.startAnimation(animation);

        // Delay for 2 seconds and transition to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // Adjust delay as needed
    }
}
