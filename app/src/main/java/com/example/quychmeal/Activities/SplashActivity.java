package com.example.quychmeal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.quychmeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::navigateActivity, 2000);
    }

    private void navigateActivity() {
        String currentUserId = pref.getString("userId", "");

        Log.d("debug", "Pref"+ currentUserId);
        // check isLogIn???
        if(currentUserId.isEmpty()) {
            startActivity(new Intent(this, LogInActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    };
}