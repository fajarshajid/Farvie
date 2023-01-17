package com.fajar.movie.MainActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.fajar.movie.Splash;

public class MyApplication extends Application {
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        Intent in = new Intent(this, Splash.class);
        startActivity(in);
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static MyApplication getInstance() {
        return instance;
    }
}
