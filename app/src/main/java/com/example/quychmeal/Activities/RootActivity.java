package com.example.quychmeal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;

import com.example.quychmeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RootActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase realtimeDB;
    FirebaseFirestore firestoreDB;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        firestoreDB = FirebaseFirestore.getInstance();
        realtimeDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pref = getPreferences(MODE_PRIVATE);
    }

}
