package com.example.quychmeal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.quychmeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateActivity();
            }
        }, 2000); // 3000 milliseconds = 3 seconds
    }

    private void navigateActivity() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // check isLogIn???
        if(currentUser == null) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    };
}