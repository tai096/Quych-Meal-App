package com.example.quychmeal.Activities.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
    SearchView searchView;
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
        searchView = view.findViewById(R.id.searchIngredientView);

        ingredientArrayList = new ArrayList<>();
        ingredientsAdapter = new IngredientsAdapter(ingredientArrayList, getContext());
        gridView.setAdapter(ingredientsAdapter);

        getIngredients();
        handleClickIngredient();
        handleClickGetRecipe();
        handleSearchIngredients();
        updateButtonState();

        return view;
    }

    private void handleClickGetRecipe() {
        btnGetRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeneratedRecipeActivity.class);
            intent.putIntegerArrayListExtra("selectedIngredients", selectedIngredients);
            startActivity(intent);
        });
    }

    private void updateButtonState() {
        if (selectedIngredients.isEmpty()) {
            btnGetRecipe.setEnabled(false);
        } else {
            btnGetRecipe.setEnabled(true);
        }
    }

    private void getIngredients() {
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

    private void handleClickIngredient() {
        gridView.setOnItemClickListener((parent, view, position, id) -> {
//            Ingredient ingredient = ingredientArrayList.get(position);
//            Integer ingredientId = ingredient.getId();

            Ingredient ingredient = (Ingredient) view.getTag();
            Integer ingredientId = ingredient.getId();
            String ingredientName = ingredient.getName();

            TextView nameIngredient = view.findViewById(R.id.nameIngredient);

            // Toggle selection
            if (selectedIngredients.contains(ingredientId)) {
                selectedIngredients.remove(ingredientId);
                if(ingredientName == nameIngredient.getText()){
                    nameIngredient.setBackgroundResource(R.drawable.custom_button);
                    nameIngredient.setTextColor(getResources().getColor(R.color.darkMode));
                }

            } else {
                selectedIngredients.add(ingredientId);
                if(ingredientName == nameIngredient.getText()){
                    nameIngredient.setBackgroundResource(R.drawable.custom_selected_item);
                    nameIngredient.setTextColor(getResources().getColor(R.color.white));
                }
            }
            updateButtonState();
        });

    }

    private void handleSearchIngredients() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterIngredients(newText);

                return true;
            }
        });
    }

    private void filterIngredients(String searchText) {
        ArrayList<Ingredient> filteredList = new ArrayList<>();

        for (Ingredient ingredient : ingredientArrayList) {
            int position = ingredientArrayList.indexOf(ingredient);
            // Get the corresponding view in the GridView
            View view = gridView.getChildAt(position);

            if (ingredient.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(ingredient);
            }
        }

        ingredientsAdapter.filterList(filteredList);
    }

}