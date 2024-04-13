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
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quychmeal.Adapter.OwnFoodAdapter;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;
import com.example.quychmeal.Models.FoodIngredient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private HashMap<String, String> ingredientMap = new HashMap<>();
    private HashMap<String, String> categoryMap = new HashMap<>();
    private ArrayAdapter<String> spinnerAdapter;
    private OwnFoodAdapter foodListAdapter;
    private Context mContext;
    SharedPreferences pref;
  private static final String SHARED_PREF_NAME = "mypref";

    public OwnScreenFragment() {
        // Required empty public constructor
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
        mContext = getContext();

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
            String cateId = cateSnapshot.getKey();
            if (cateName != null && cateId != null) {
              cateNames.add(cateName);
              categoryMap.put(cateName, cateId);
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

      // Add content to ingredientSpinner
      DatabaseReference ingredientRef = FirebaseDatabase.getInstance().getReference("ingredients");
      ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
            String ingredientId = ingredientSnapshot.getKey();
            String ingredientName = ingredientSnapshot.child("name").getValue(String.class);
            if (ingredientName != null) {
              ingredientMap.put(ingredientName, ingredientId);
            }
          }
          MultiAutoCompleteTextView ingredientTemp = foodDialog.findViewById(R.id.ingredientInputValue);
          List<String> ingredientList = new ArrayList<>(ingredientMap.keySet());
          ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,  android.R.layout.simple_dropdown_item_1line, ingredientList);
          ingredientTemp.setAdapter(adapter);
          ingredientTemp.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.d("Error", String.valueOf(error));
        }
      });

      // Show the Dialog
      AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
      builder.setView(foodDialog);
      builder.setPositiveButton("Save", (dialog, which) -> {
        // Retrieve the input data from user
        TextView nameInput = foodDialog.findViewById(R.id.nameInputValue);
        MultiAutoCompleteTextView ingredientInput = foodDialog.findViewById(R.id.ingredientInputValue);
        Spinner cateInput = foodDialog.findViewById(R.id.categoryInputValue);
        TextView aboutInput = foodDialog.findViewById(R.id.descriptionInputValue);
        TextView methodInput = foodDialog.findViewById(R.id.nameInputValue);
        TextView servingInput = foodDialog.findViewById(R.id.servingInputValue);
        TextView prepTimeInput = foodDialog.findViewById(R.id.prepTimeInputValue);
        TextView cookTimeInput = foodDialog.findViewById(R.id.cookTimeInputValue);
        TextView videoURL = foodDialog.findViewById(R.id.videoInputValue);

        // Get selected
        String saveName = nameInput.getText().toString();
        String saveIngredients = ingredientInput.getText().toString();
        String saveAbout = aboutInput.getText().toString();
        String saveMethod = methodInput.getText().toString();
        int saveServing = Integer.parseInt(servingInput.getText().toString());
        int savePrep = Integer.parseInt(prepTimeInput.getText().toString());
        int saveCook = Integer.parseInt(cookTimeInput.getText().toString());
        String saveVideo = videoURL.getText().toString();

        // Convert Ingredient to ID
        List<String> ingredientNames = Arrays.asList(saveIngredients.split("\\s*,\\s*"));
        List<FoodIngredient> ingredientList = new ArrayList<>();
        for (String ingredientName : ingredientNames) {
          if (ingredientMap.containsKey(ingredientName)) {
            String ingredientId = ingredientMap.get(ingredientName);
            FoodIngredient foodIngredient = new FoodIngredient(Integer.parseInt(ingredientId), "");
            ingredientList.add(foodIngredient);
          }
        }

        for (FoodIngredient food : ingredientList) {
          int id = food.getIngredientId();
          Log.d("Ingredients ID", String.valueOf(id));
        }

        // Convert Category
        // Get selected category name
        String selectedCategoryName = cateInput.getSelectedItem().toString();
        int saveCate = Integer.parseInt(categoryMap.get(selectedCategoryName));

        // Add to Firebase
        String currentUserId = pref.getString("userId", null);
        DatabaseReference foodListReference = FirebaseDatabase.getInstance().getReference("foods");
        foodListReference.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int id = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              id = snapshot.child("id").getValue(Integer.class);
            }
            Food newFood = new Food(id++, saveCate, saveCook, currentUserId, saveAbout, null, ingredientList, 1, saveMethod, saveName, savePrep, saveServing, null);
            Log.d("NewFood", String.valueOf(newFood));
            foodListReference.child(String.valueOf(id)).setValue(newFood);
            Toast.makeText(getActivity(), "Recipe has been added", Toast.LENGTH_SHORT).show();
            foodListAdapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
        });
      });
      builder.setNegativeButton("Cancel", (dialog, which) -> {
        cateNames.clear();
        dialog.dismiss();
      });
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
    }
}
