package com.example.quychmeal.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.quychmeal.Models.User;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUpActivity extends RootActivity {
    ActivitySignUpBinding binding;
    String uniqueID = UUID.randomUUID().toString();
    String defaultAvatar = "https://www.zooniverse.org/assets/simple-avatar.png";
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setVariable();
    }

    private void setVariable() {
        binding.btnSignUp.setOnClickListener(view -> {
            String email = binding.editTxtEmail.getText().toString();
            String username = binding.editTxtName.getText().toString();
            String password = binding.editTxtPassword.getText().toString();
            String age = binding.editTxtAge.getText().toString();
            String sex = binding.editTxtSex.getText().toString();

            if (username.length() < 1 || age.length() < 1 || sex.length() < 1) {
                Toast.makeText(SignUpActivity.this, "This fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Your password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getUser().getUid();

                    User user = new User(userId, email, username, password, Integer.parseInt(age), sex, defaultAvatar);

                    reference.child(userId).setValue(user);

                    Toast.makeText(SignUpActivity.this, "Welcome To Quych Meal!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show();
                    return;
                }
            });
        });

        binding.btnNavigateToLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
        });
    }
}