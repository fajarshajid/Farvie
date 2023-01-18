package com.fajar.movie.Activity;

import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fajar.movie.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity implements ComponentCallbacks2 {

    final Fragment fragmentHome = new FragmentHome();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            fm.beginTransaction().add(R.id.container, fragmentHome, "Home").commit();
            active = fragmentHome;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }



    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
    }

    @Override
    public void onPause() {
        // Cancel the timer if the game is paused.
        super.onPause();
    }

    //
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetView;

    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_COLLAPSED:

                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:

                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:

                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            bottomSheetDialog.dismiss();
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:

                            break;
                        default:

                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };




}