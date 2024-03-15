package com.example.quychmeal.Activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.quychmeal.Activities.Fragments.HomeScreenFragment;
import com.example.quychmeal.Activities.Fragments.OwnScreenFragment;
import com.example.quychmeal.Activities.Fragments.ProfileScreenFragment;
import com.example.quychmeal.Activities.Fragments.TodayMenuScreenFragment;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivityMainBinding;

public class MainActivity extends RootActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeScreenFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if(itemId == R.id.home){
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
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}