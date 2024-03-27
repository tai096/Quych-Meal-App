package com.example.quychmeal.Activities.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
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

import java.util.Arrays;

public class ProfileScreenFragment extends Fragment {
    private EditText editTextName, editTextEmail, editTextAge;
    private Spinner sexSpinner;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        pref = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        editTextName = view.findViewById(R.id.nameEditText);
        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextAge = view.findViewById(R.id.ageEditText);
        sexSpinner = view.findViewById(R.id.sexSpinner);

        getProfile();
        return view;
    }

    private void getProfile() {
        String currentUserId = pref.getString("userId", null);

        assert currentUserId != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        String[] sexOptions = getResources().getStringArray(R.array.sex_options);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                editTextName.setText(userProfile.getUsername());
                editTextEmail.setText(userProfile.getEmail());
                editTextAge.setText(userProfile.getAge());

                String sex = userProfile.getSex();

                int sexIndex = Arrays.asList(sexOptions).indexOf(sex);
                if (sexIndex != -1) {
                    sexSpinner.setSelection(sexIndex);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}