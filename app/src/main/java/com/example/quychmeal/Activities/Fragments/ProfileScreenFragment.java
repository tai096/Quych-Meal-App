package com.example.quychmeal.Activities.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quychmeal.Activities.EditProfileActivity;
import com.example.quychmeal.Activities.FeedbackActivity;
import com.example.quychmeal.Activities.LogInActivity;
import com.example.quychmeal.Activities.MainActivity;
import com.example.quychmeal.Activities.SettingActivity;
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
    private TextView nameLabel;
    private ImageView avatarImageView;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";
    private ConstraintLayout constraintLayoutEdit, constraintLayoutSetting, constraintLayoutFeedback;
    private int originalBackgroundColor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        pref = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        nameLabel = view.findViewById(R.id.nameLabel);
        constraintLayoutEdit= view.findViewById(R.id.constraintLayoutEdit);
        constraintLayoutSetting= view.findViewById(R.id.constraintLayoutSetting);
        constraintLayoutFeedback= view.findViewById(R.id.constraintLayoutFeedback);
        avatarImageView =view.findViewById(R.id.avatarImageView);

        handleNavigate(constraintLayoutEdit, EditProfileActivity.class);
        handleNavigate(constraintLayoutSetting, SettingActivity.class);
        handleNavigate(constraintLayoutFeedback, FeedbackActivity.class);

        getProfile();
        return view;
    }

    private void handleNavigate (ConstraintLayout constraintLayout, Class<?> destinationActivity) {
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change background color when pressed
                        constraintLayout.setBackgroundResource(R.color.hover);
                        break;
                    case MotionEvent.ACTION_UP:
                        /* Revert to original background color when released */
                        constraintLayout.setBackgroundResource(R.color.transparent);
                        // Start the EditProfileActivity
                        startActivity(new Intent(getActivity(),destinationActivity));
                        break;
                }
                return true;
            }
        });
    }

    private void getProfile() {
        String currentUserId = pref.getString("userId", null);

        assert currentUserId != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                nameLabel.setText(userProfile.getUsername());
                String profileImageUrl = userProfile.getAvatar();
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(profileImageUrl)
                            .into(avatarImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}