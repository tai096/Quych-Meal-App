package com.example.quychmeal.Activities.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.quychmeal.Models.Category;
import com.example.quychmeal.Models.User;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileScreenFragment extends Fragment {
    private EditText editTextName, editTextEmail, editTextAge;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        editTextName = view.findViewById(R.id.nameEditText);
        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextAge = view.findViewById(R.id.ageEditText);

        getProfile();
        return view;
    }

    private void getProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child("4JUxNW1zs5SEpMbXnQ18nvzqUxq1");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                editTextName.setText(userProfile.getUsername());
                editTextEmail.setText(userProfile.getEmail());
                editTextAge.setText(userProfile.getAge());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}