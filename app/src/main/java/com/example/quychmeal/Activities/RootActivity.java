package com.example.quychmeal.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.quychmeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class RootActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase realtimeDB;
    FirebaseFirestore firestoreDB;
    private boolean darkMode;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        firestoreDB = FirebaseFirestore.getInstance();
        realtimeDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        darkMode = sharedPreferences.getBoolean("darkMode", false);

        loadLocale();

        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    public void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor prefEditor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        prefEditor.putString("language", langCode);
        prefEditor.apply();
    }

    public void loadLocale () {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("language","");
        setLocale(language);
    }

}

