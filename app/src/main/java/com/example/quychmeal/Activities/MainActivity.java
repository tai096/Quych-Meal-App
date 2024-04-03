package com.example.quychmeal.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quychmeal.Activities.Fragments.HomeScreenFragment;
import com.example.quychmeal.Activities.Fragments.OwnScreenFragment;
import com.example.quychmeal.Activities.Fragments.ProfileScreenFragment;
import com.example.quychmeal.Activities.Fragments.TodayMenuScreenFragment;
import com.example.quychmeal.Adapter.CategoriesAdapter;
import com.example.quychmeal.Models.Category;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivityMainBinding;
import com.example.quychmeal.databinding.FragmentHomeScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RootActivity {
    ActivityMainBinding binding;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        pref = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

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