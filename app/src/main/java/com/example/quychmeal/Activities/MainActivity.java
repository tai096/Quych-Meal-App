package com.example.quychmeal.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.quychmeal.Activities.Fragments.HomeScreenFragment;
import com.example.quychmeal.Activities.Fragments.OwnScreenFragment;
import com.example.quychmeal.Activities.Fragments.ProfileScreenFragment;
import com.example.quychmeal.Activities.Fragments.TodayMenuScreenFragment;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

//        getAllDocuments();
    }

    private void getAllDocuments() {
        firestoreDB.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("MyDebug", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("MyDebug", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}