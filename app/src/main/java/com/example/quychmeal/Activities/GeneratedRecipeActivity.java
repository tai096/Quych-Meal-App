package com.example.quychmeal.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quychmeal.Adapter.FoodsAdapter;
import com.example.quychmeal.Adapter.IngredientsAdapter;
import com.example.quychmeal.Adapter.ReipeAdapter;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.Models.FoodIngredient;
import com.example.quychmeal.Models.Ingredient;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GeneratedRecipeActivity extends RootActivity {
    ArrayList<Integer> selectedIngredients;
    GridView gridView;
    ArrayList<Food> recipeArrayList;
    ReipeAdapter reipeAdapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("foods");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_generated_recipe);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedIngredients = getIntent().getIntegerArrayListExtra("selectedIngredients");


        gridView = findViewById(R.id.recipeGridView);
        recipeArrayList = new ArrayList<>();
        reipeAdapter = new ReipeAdapter( recipeArrayList, GeneratedRecipeActivity.this);
        gridView.setAdapter(reipeAdapter);

        getFoods();
    }

    private void getFoods() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (foodContainsSelectedIngredients(food)) {
                        recipeArrayList.add(food);
                    }
                }
                reipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getFoods", "Failed to retrieve foods from database: " + error.getMessage());
            }
        });
    }

    private boolean foodContainsSelectedIngredients(Food food) {
        if (food == null || food.getIngredients() == null) {
            return false; // Handle null objects gracefully
        }

        for (Integer selectedIngredientId : selectedIngredients) {
            boolean found = false;
            for (FoodIngredient ingredient : food.getIngredients()) {
                if (ingredient != null && ingredient.getIngredientId() != null && ingredient.getIngredientId() == selectedIngredientId) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false; // Food does not contain one of the selected ingredients
            }
        }
        return true; // Food contains all selected ingredients
    }



}