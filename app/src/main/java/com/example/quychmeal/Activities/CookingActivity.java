package com.example.quychmeal.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.quychmeal.Adapter.FoodIngredientsAdapter;
import com.example.quychmeal.Adapter.IngredientsAdapter;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.Models.FoodIngredient;
import com.example.quychmeal.Models.Ingredient;
import com.example.quychmeal.Models.User;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class CookingActivity extends RootActivity {
    private String foodId;
    GridView foodIngredientGridView;
    TextView foodMethod;
    ImageView foodBackground;
    ArrayList<FoodIngredient> foodIngredients;
    FoodIngredientsAdapter foodIngredientsAdapter;
    WebView video;
ImageButton btnCookingBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cooking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        video = findViewById(R.id.foodVideo);
        foodIngredientGridView = findViewById(R.id.foodIngredientGridView);
        foodMethod = findViewById(R.id.foodMethod);
        foodBackground = findViewById(R.id.foodBackground);

        foodIngredients = new ArrayList<>();
        foodIngredientsAdapter = new FoodIngredientsAdapter(foodIngredients, this);
        foodIngredientGridView.setAdapter(foodIngredientsAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            foodId = intent.getStringExtra("foodId");
            handleGetFood(foodId);

        }

        btnCookingBack.setOnClickListener(v -> {
            CookingActivity.this.finish();
        });
    }


    private void handleGetFood(String foodId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("foods").child(foodId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Food food = snapshot.getValue(Food.class);
                String foodMethodText = food.getMethod();

                foodMethod.setText(foodMethodText);

                String foodImage = food.getImage();
                if (!isFinishing() && foodImage != null && !foodImage.isEmpty()) {
                    Glide.with(CookingActivity.this)
                            .load(foodImage)
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                            .into(foodBackground);
                }
                loadVideo(food.getVideo());

                foodIngredients.clear(); // Clear previous data
                for (FoodIngredient ingredient : food.getIngredients()) {
                    getIngredient(ingredient.getIngredientId().toString(), ingredient.getQuantity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadVideo(String url) {
        video.loadData(url, "text/html", "utf-8");
        video.getSettings().setJavaScriptEnabled(true);
        video.setWebChromeClient(new WebChromeClient());
    }

    private void getIngredient(String id, String quantity) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ingredients").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ingredient ingredient = snapshot.getValue(Ingredient.class);
                if (ingredient != null) {
                    FoodIngredient foodIngredient = new FoodIngredient(ingredient.getId(), quantity, ingredient.getName());
                    foodIngredients.add(foodIngredient);
                    foodIngredientsAdapter.notifyDataSetChanged();
                } else {
                    Log.e("CookingActivity", "Ingredient is null for id: " + id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}