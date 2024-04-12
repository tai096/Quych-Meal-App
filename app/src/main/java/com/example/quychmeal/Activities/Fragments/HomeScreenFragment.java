package com.example.quychmeal.Activities.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quychmeal.Adapter.CategoriesAdapter;
import com.example.quychmeal.Adapter.FoodsAdapter;
import com.example.quychmeal.Models.Category;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.FragmentHomeScreenBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment implements CategoriesAdapter.CategoryClickListener {
    private String isCategoriesText;
    private TextView catergoriesText;
    private CategoriesAdapter categoriesAdapter;
    private FoodsAdapter foodsAdapter;
    private List<Category> categoryList;
    private List<Food> foodList;
    private ProgressBar progressBarCategory;
    private ProgressBar progressBarFood;
    private SearchView searchView;
    private Context context;

    @Override
    public void onCategoryClick(Category category) {
        getFoods(category.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        searchView = view.findViewById(R.id.searchFoodView);
        catergoriesText = view.findViewById(R.id.catergoriesText);

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

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        progressBarCategory.setVisibility(View.VISIBLE);

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getContext(), categoryList, this);
        recyclerViewCategory.setAdapter(categoriesAdapter);

        getCategories();
        /////////////////////////////

        // Foods ///////////////////////////////
        RecyclerView recyclerViewFood = view.findViewById(R.id.foodsRecyclerView);
        progressBarFood = view.findViewById(R.id.progressBarFood);

        recyclerViewFood.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progressBarFood.setVisibility(View.VISIBLE);

        foodList = new ArrayList<>();
        foodsAdapter = new FoodsAdapter(getContext(), foodList);
        recyclerViewFood.setAdapter(foodsAdapter);

        getFoods(0);

        ///////////////////////////////////////
        return view;
    }

    private void handleSearch(String searchText) {
        List<Food> filteredList = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(food);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "No recipe found", Toast.LENGTH_SHORT).show();
        } else {
            foodsAdapter.setfilteredList(filteredList);
        }
    }

    private void getCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categories");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                    isCategoriesText = dataSnapshot.child("name").getValue().toString();
                }
                catergoriesText.setText(isCategoriesText);

                categoriesAdapter.notifyDataSetChanged();
                progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFoods(int categoryId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("foods");
        Query query = reference.orderByChild("categoryId").equalTo(categoryId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    foodList.add(food);
                }

                foodsAdapter.notifyDataSetChanged();
                progressBarFood.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }


}