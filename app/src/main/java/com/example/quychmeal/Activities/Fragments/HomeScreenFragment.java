// HomeScreenFragment.java

package com.example.quychmeal.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quychmeal.Activities.DetailRecipeActivity;
import com.example.quychmeal.Activities.GeneratedRecipeActivity;
import com.example.quychmeal.Adapter.CategoriesAdapter;
import com.example.quychmeal.Adapter.ReipeAdapter;
import com.example.quychmeal.Models.Category;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment implements CategoriesAdapter.CategoryClickListener {
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categoryList;
    private ProgressBar progressBarCategory;
    private SearchView searchView;
    private Context context;
    private GridView gridView;
    private ArrayList<Food> recipeArrayList;
    private ReipeAdapter reipeAdapter;
    LinearLayout notFoundFoods;

    @Override
    public void onCategoryClick(Category category) {
        getFoods(category.getId(), "");
        categoriesAdapter.setSelectedCategory(category); // Highlight the selected category
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any important state information here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        searchView = view.findViewById(R.id.searchFoodView);
        gridView = view.findViewById(R.id.foodGridView);
        notFoundFoods = view.findViewById(R.id.notFoundFoods);
        notFoundFoods.setVisibility(View.GONE);

        view.setFocusableInTouchMode(true);
        view.requestFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleSearch(newText);
                return false;
            }
        });

        // Categories ////////////////
        RecyclerView recyclerViewCategory = view.findViewById(R.id.categoriesRecyclerView);
        progressBarCategory = view.findViewById(R.id.progressBarCategoty);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressBarCategory.setVisibility(View.VISIBLE);

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getContext(), categoryList, this);
        recyclerViewCategory.setAdapter(categoriesAdapter);
        getCategories();

        recipeArrayList = new ArrayList<>();
        reipeAdapter = new ReipeAdapter(recipeArrayList, getContext());
        gridView.setAdapter(reipeAdapter);

        getFoods(0, "");

        ///////////////////////////////////////
        return view;
    }

    private void handleSearch(String searchText) {
        int categoryId = categoriesAdapter.getSelectedCategory();
        getFoods(categoryId, searchText);
    }


    private void getCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }

                categoriesAdapter.notifyDataSetChanged();
                progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void getFoods(int categoryId, String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("foods");
        Query query = reference.orderByChild("categoryId").equalTo(categoryId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeArrayList.clear(); // Clear previous data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    // Filter food by name and add to the list
                    if (food.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        recipeArrayList.add(food);
                    }
                }

                if (recipeArrayList.isEmpty()) {
                    notFoundFoods.setVisibility(View.VISIBLE);
                } else {
                    notFoundFoods.setVisibility(View.GONE);
                }

                reipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
