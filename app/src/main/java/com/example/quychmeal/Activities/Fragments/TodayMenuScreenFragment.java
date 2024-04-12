package com.example.quychmeal.Activities.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.quychmeal.Activities.FeedbackActivity;
import com.example.quychmeal.Activities.GeneratedRecipeActivity;
import com.example.quychmeal.Adapter.IngredientsAdapter;
import com.example.quychmeal.Models.Ingredient;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TodayMenuScreenFragment extends Fragment {

    GridView gridView;
    ArrayList<Ingredient> ingredientArrayList;
    IngredientsAdapter ingredientsAdapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ingredients");
    ArrayList<Integer> selectedIngredients = new ArrayList<>();
    private Button btnGetRecipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_menu_screen, container, false);

        btnGetRecipe = view.findViewById(R.id.btnGetRecipe);
        gridView = view.findViewById(R.id.ingredientGridView);

        ingredientArrayList = new ArrayList<>();
        ingredientsAdapter = new IngredientsAdapter(ingredientArrayList, getContext());
        gridView.setAdapter(ingredientsAdapter);

        getIngredients();
        handleClickIngredient();
        handleClickGetRecipe();
        return view;
    }

    private void handleClickGetRecipe() {
        btnGetRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeneratedRecipeActivity.class);
            intent.putIntegerArrayListExtra("selectedIngredients", selectedIngredients);
            startActivity(intent);
        });


    }


    private void getIngredients (){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Ingredient ingredient = dataSnapshot.getValue(Ingredient.class);
                    ingredientArrayList.add(ingredient);
                }
                ingredientsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleClickIngredient(){
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Ingredient ingredient = ingredientArrayList.get(position);
            Integer ingredientId = ingredient.getId();

            TextView nameIngredient = view.findViewById(R.id.nameIngredient);

            // Toggle selection
            if (selectedIngredients.contains(ingredientId)) {
                selectedIngredients.remove(ingredientId);
                nameIngredient.setBackgroundResource(R.drawable.custom_button);
                nameIngredient.setTextColor(getResources().getColor(R.color.darkMode));
            } else {
                selectedIngredients.add(ingredientId);
                nameIngredient.setBackgroundResource(R.drawable.custom_selected_item);
                nameIngredient.setTextColor(getResources().getColor(R.color.white));
            }
        });

    }
}