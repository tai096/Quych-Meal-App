package com.example.quychmeal.Activities.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.quychmeal.Adapter.CategoriesAdapter;
import com.example.quychmeal.Models.Category;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.FragmentHomeScreenBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categoryList;
    private ProgressBar progressBarCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        recyclerView = view.findViewById(R.id.categoriesRecyclerView);
        progressBarCategory = view.findViewById(R.id.progressBarCategoty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        progressBarCategory.setVisibility(View.VISIBLE);

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getContext(), categoryList);
        recyclerView.setAdapter(categoriesAdapter);

        getCategories();
        return view;
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

            }
        });
    }
}