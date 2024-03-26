package com.example.quychmeal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.quychmeal.Models.User;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivityLogInBinding;
import com.example.quychmeal.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends RootActivity {
    ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setVariable();

    }

    private void setVariable() {
        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.editTxtEmail.getText().toString();
            String password = binding.editTxtPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LogInActivity.this, "This fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(LogInActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LogInActivity.this, task -> {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        pref.edit().putString("userId", user.getUid()).apply();

                        Toast.makeText(LogInActivity.this, "Welcome To Quych Meal!", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    } else {
                        return;
                    }


                }else {
                    String error = task.getException().getMessage();

                    Toast.makeText(LogInActivity.this, error, Toast.LENGTH_LONG).show();
                    return;
                }
            });
        });

        binding.btnNavigateToSignUp.setOnClickListener(view -> {
            startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
        });
    }
}