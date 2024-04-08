package com.example.quychmeal.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quychmeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RootActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase realtimeDB;
    FirebaseFirestore firestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        firestoreDB = FirebaseFirestore.getInstance();
        realtimeDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
}

