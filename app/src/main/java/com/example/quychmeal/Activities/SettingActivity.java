package com.example.quychmeal.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quychmeal.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public class SettingActivity extends RootActivity {
    private ImageButton btnSettingGoBack;
    private Switch switchTheme;
    private boolean darkMode;
    SharedPreferences.Editor editor;
    private Spinner langSpinner;
    public static final String[] languages = {"Select Language", "English", "Vietnamese", "Japanese"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_setting);


        btnSettingGoBack = findViewById(R.id.btnSettingGoBack);
        switchTheme = findViewById(R.id.swichTheme);

        langSpinner = findViewById(R.id.languageSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
        langSpinner.setSelection(0);


        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        darkMode = sharedPreferences.getBoolean("darkMode", false);

        if (darkMode) {
            switchTheme.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switchTheme.setOnClickListener(view -> {
            if (darkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("darkMode", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("darkMode", true);
            }
            editor.apply();
        });

        handleGoBack();
        handleSelectLanguage();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleGoBack() {
        btnSettingGoBack.setOnClickListener(v -> {
            SettingActivity.this.finish();
        });
    }

    private void handleSelectLanguage() {
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLang = adapterView.getItemAtPosition(i).toString();
                switch (selectedLang) {
                    case "English":
                        setLocale("en");
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        break;
                    case "Vietnamese":
                        setLocale("vi");
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        break;
                    case "Japanese":
                        setLocale("ja");
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

}