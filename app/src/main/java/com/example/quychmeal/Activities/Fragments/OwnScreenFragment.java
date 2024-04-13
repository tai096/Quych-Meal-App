package com.example.quychmeal.Activities.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.quychmeal.Adapter.OwnFoodAdapter;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnScreenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Food> foodList = new ArrayList<>();
    private List<String> cateNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private OwnFoodAdapter foodListAdapter;
    private Context mContext;
    SharedPreferences pref;
  private static final String SHARED_PREF_NAME = "mypref";

    public OwnScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnScreenFragment newInstance(String param1, String param2) {
        OwnScreenFragment fragment = new OwnScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_own_screen, container, false);

        pref = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // RecyclerView (Own Recipe List)
        RecyclerView foodRecyclerView = view.findViewById(R.id.ownFoodRecyclerView);
        foodRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
        foodListAdapter = new OwnFoodAdapter(foodList);
        foodRecyclerView.setAdapter(foodListAdapter);

        // Add new recipe
        ImageView createRecipe = view.findViewById(R.id.createRecipeBtn);
        createRecipe.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showDialog();
          }
        });

        // Get Recipes from Firebase Realtime DB
        getRecipe();
        return view;
    }

    private void getRecipe() {
      String currentUserId = pref.getString("userId", null);
      assert currentUserId != null;
      DatabaseReference foodListReference = FirebaseDatabase.getInstance().getReference("foods");
      foodListReference.orderByChild("createdBy").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
          for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
            int cateId = foodSnapshot.child("categoryId").getValue(Integer.class);
            int cookTime = foodSnapshot.child("cookTime").getValue(Integer.class);
            String description = foodSnapshot.child("description").getValue(String.class);
            int id = foodSnapshot.child("id").getValue(Integer.class);
            String image = foodSnapshot.child("image").getValue(String.class);
            int level = foodSnapshot.child("level").getValue(Integer.class);
            String method = foodSnapshot.child("method").getValue(String.class);
            String foodName = foodSnapshot.child("name").getValue(String.class);
            int prepTime = foodSnapshot.child("prepTime").getValue(Integer.class);
            int serving = foodSnapshot.child("serving").getValue(Integer.class);

            Food food = new Food(id, cateId, cookTime, currentUserId, description, image, null, level, method, foodName, prepTime, serving, null);
            foodList.add(food);
          }
          foodListAdapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }

    private void showDialog() {
      View foodDialog = getLayoutInflater().inflate(R.layout.dialog_food, null);
      Spinner cateSpinner = foodDialog.findViewById(R.id.categoryInputValue);

      // Add content to cateSpinner
      DatabaseReference cateListReference = FirebaseDatabase.getInstance().getReference("categories");
      cateListReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          for (DataSnapshot cateSnapshot : snapshot.getChildren()) {
            String cateName = cateSnapshot.child("name").getValue(String.class);
            if (cateName != null) {
              cateNames.add(cateName);
            }
          }
          spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cateNames);
          spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          cateSpinner.setAdapter(spinnerAdapter);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
      });

      // Show the Dialog
      AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
      builder.setView(foodDialog);
      builder.setPositiveButton("Save", (dialog, which) -> {

      });
      builder.setNegativeButton("Cancel", (dialog, which) -> {
        dialog.dismiss();
      });
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
    }
}
