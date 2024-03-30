package com.example.quychmeal.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quychmeal.Models.User;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class EditProfileActivity extends RootActivity {
    private EditText editTxtName, editTxtAge;
    private Spinner spinnerSex;
    private Button btnSave;
    private ImageButton imageBtnGoBack;
    private ImageView avatarView;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";
    DatabaseReference refDB;
    String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        pref = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        refDB = FirebaseDatabase.getInstance().getReference("users");

        editTxtName = findViewById(R.id.editProfileName);
        editTxtAge = findViewById(R.id.editProfileAge);
        spinnerSex = findViewById(R.id.spinnerSex);
        btnSave = findViewById(R.id.btnSave);
        imageBtnGoBack = findViewById(R.id.imageBtnGoBack);
        avatarView = findViewById(R.id.avatarView);

        currentUserId = pref.getString("userId", null);

        getProfile();
        handleUpdateProfile();
        handleGoBack();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

private void handleGoBack (){
    imageBtnGoBack.setOnClickListener(v -> {
        EditProfileActivity.this.finish();
    });
}

    private void getProfile() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                editTxtName.setText(userProfile.getUsername());
                editTxtAge.setText(userProfile.getAge());
                String profileImageUrl = userProfile.getAvatar();

                if (!isFinishing() && profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(EditProfileActivity.this)
                            .load(profileImageUrl)
                            .into(avatarView);
                }

                String sex = userProfile.getSex();
                String[] sexOptions = getResources().getStringArray(R.array.sex_options);

                int sexIndex = Arrays.asList(sexOptions).indexOf(sex);

                if (sexIndex != -1) {
                    spinnerSex.setSelection(sexIndex);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });}


    private void handleUpdateProfile() {
        btnSave.setOnClickListener(v -> {
            // Show loading indicator
            ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
            progressDialog.setMessage("Updating profile...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String newName = editTxtName.getText().toString();
            int newAge = Integer.parseInt(editTxtAge.getText().toString());
            String newSex = spinnerSex.getSelectedItem().toString(); // Get selected sex from the spinner

            refDB.child(currentUserId).child("username").setValue(newName);
            refDB.child(currentUserId).child("age").setValue(newAge);
            refDB.child(currentUserId).child("sex").setValue(newSex)
                    .addOnCompleteListener(task -> {
                        // Dismiss loading indicator
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Show success toast
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Show failure toast
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

}

