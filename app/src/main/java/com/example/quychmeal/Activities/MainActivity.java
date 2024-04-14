package com.example.quychmeal.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quychmeal.Activities.Fragments.HomeScreenFragment;
import com.example.quychmeal.Activities.Fragments.OwnScreenFragment;
import com.example.quychmeal.Activities.Fragments.ProfileScreenFragment;
import com.example.quychmeal.Activities.Fragments.TodayMenuScreenFragment;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivityMainBinding;


public class MainActivity extends RootActivity {
    ActivityMainBinding binding;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeScreenFragment());
            } else if (itemId == R.id.today_menu) {
                replaceFragment(new TodayMenuScreenFragment());
            } else if (itemId == R.id.own) {
                replaceFragment(new OwnScreenFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileScreenFragment());
            }

            return true;
        });

        // Only add the initial fragment if it's the first creation of the activity
        if (savedInstanceState == null) {
            replaceFragment(new HomeScreenFragment());
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


}