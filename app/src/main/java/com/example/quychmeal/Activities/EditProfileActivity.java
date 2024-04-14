package com.example.quychmeal.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quychmeal.Models.User;
import com.example.quychmeal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.UUID;

public class EditProfileActivity extends RootActivity {
    public Uri imageURI;
    private EditText editTxtName, editTxtAge;
    private Spinner spinnerSex;
    private Button btnSave;
    private ImageButton imageBtnGoBack;
    private ImageView avatarView;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";
    DatabaseReference refDB;
    String currentUserId;
    private ActivityResultLauncher<Intent> mActivityResultLauncher;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        refDB = FirebaseDatabase.getInstance().getReference("users");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                imageURI = result.getData().getData();
                avatarView.setImageURI(imageURI);

                if (imageURI != null) {
                    uploadImage(imageURI);
                }
            }
        });

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
        handleUploadAvatar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleUploadAvatar() {
        avatarView.setOnClickListener(v -> chooseImage());
    }

    private void chooseImage() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(intent);


    }

    private void uploadImage(Uri imageURI) {
        String randomKey = UUID.randomUUID().toString();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        StorageReference reference = storageReference.child("users/" + randomKey);

        reference.putFile(imageURI)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        refDB.child(currentUserId).child("avatar").setValue(uri.toString());
                        Toast.makeText(EditProfileActivity.this, "Avatar Uploaded!", Toast.LENGTH_LONG).show();
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Failed To Upload!", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                });
    }


    private void handleGoBack() {
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
                editTxtAge.setText(String.valueOf(userProfile.getAge()));
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

        });
    }


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

