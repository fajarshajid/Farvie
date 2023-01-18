package com.fajar.movie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.fajar.movie.Activity.MainActivity;

public class Splash extends AppCompatActivity {
    Animation anim;
    ImageView lpromo;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        lpromo = findViewById(R.id.lpromo);
        try {
            anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            lpromo.startAnimation(anim);
        } catch (Exception e) {
           e.printStackTrace();
        }


    }
}