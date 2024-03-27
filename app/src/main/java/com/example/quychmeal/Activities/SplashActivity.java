package com.example.quychmeal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.quychmeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends RootActivity {
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        new Handler().postDelayed(this::navigateActivity, 2000);

    }

    private void navigateActivity() {
        String currentUserId = pref.getString("userId", null);

        // check isLogIn???
        if(currentUserId == null) {
            startActivity(new Intent(this, LogInActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    };
}