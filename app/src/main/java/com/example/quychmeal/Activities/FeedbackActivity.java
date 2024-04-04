package com.example.quychmeal.Activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quychmeal.R;

public class FeedbackActivity extends RootActivity {
    private ImageButton btnFbGoBack;
    private EditText editTxtFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_feedback);

        btnFbGoBack = findViewById(R.id.btnFbGoBack);
        editTxtFeedback = findViewById(R.id.editTxtFeedback);

        handleGoBack();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleGoBack (){
        btnFbGoBack.setOnClickListener(v -> {
            FeedbackActivity.this.finish();
        });
    }
}