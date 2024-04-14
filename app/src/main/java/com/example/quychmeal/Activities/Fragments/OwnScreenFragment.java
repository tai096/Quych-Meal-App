package com.example.quychmeal.Activities.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.quychmeal.Adapter.OwnFoodAdapter;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;
import com.example.quychmeal.Models.FoodIngredient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OwnScreenFragment extends Fragment {
    private String mParam1;
    private String mParam2;
    private List<Food> foodList = new ArrayList<>();
    private List<String> cateNames = new ArrayList<>();
    private List<String> ingredients = new ArrayList<>();
    private HashMap<String, String> ingredientMap = new HashMap<>();
    private HashMap<String, String> categoryMap = new HashMap<>();
    private ArrayAdapter<String> spinnerAdapter;
    private OwnFoodAdapter foodListAdapter;
    private Context mContext;
    SharedPreferences pref;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURI;
    private ImageView selectedImage;
LinearLayout notFoundRecipeView;
    public OwnScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_own_screen, container, false);
        mContext = getContext();

        pref = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // RecyclerView (Own Recipe List)
        RecyclerView foodRecyclerView = view.findViewById(R.id.ownFoodRecyclerView);
      notFoundRecipeView = view.findViewById(R.id.notFoundRecipeView);
      notFoundRecipeView.setVisibility(View.GONE);
        foodRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
        foodListAdapter = new OwnFoodAdapter(foodList, new OwnFoodAdapter.OnItemClickListener() {
          @Override
          public void onItemClick(int position) {
            Food food = foodList.get(position);
            showFoodDetail(food);
          }

          @Override
          public void onDeleteClick(int position) {
            deleteFood(position);
          }
        });
        foodRecyclerView.setAdapter(foodListAdapter);

        // Add new recipe
        ImageView createRecipe = view.findViewById(R.id.createRecipeBtn);
        createRecipe.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showDialog();
          }
        });


        getRecipe();
        return view;
    }

    // Method to get recipe list
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
            int prepTime = foodSnapshot.child("prepTime").getValue(Integer.class);
            int serving = foodSnapshot.child("serving").getValue(Integer.class);
            String description = foodSnapshot.child("description").getValue(String.class);
            int id = foodSnapshot.child("id").getValue(Integer.class);
            String image = foodSnapshot.child("image").getValue(String.class);
            int level = foodSnapshot.child("level").getValue(Integer.class);
            String method = foodSnapshot.child("method").getValue(String.class);
            String foodName = foodSnapshot.child("name").getValue(String.class);

            String videoEmbedded = foodSnapshot.child("video").getValue(String.class);
            String videoId = extractVideoId(videoEmbedded);
            String video = "https://www.youtube.com/watch?v=" + videoId;

            // Retrieve ingredients
            List<FoodIngredient> ingredientList = new ArrayList<>();
            DataSnapshot ingredients = foodSnapshot.child("ingredients");
            for (DataSnapshot ingredient : ingredients.getChildren()) {
              String ingredientId = ingredient.getKey();
              ingredientList.add(new FoodIngredient(Integer.parseInt(ingredientId), "", ""));
            }

            Food food = new Food(id, cateId, cookTime, currentUserId, description, image, ingredientList, level, method, foodName, prepTime, serving, video);
            foodList.add(food);
          }
          foodListAdapter.notifyDataSetChanged();

          if (foodList.isEmpty()) {
            notFoundRecipeView.setVisibility(View.VISIBLE);
          } else {
            notFoundRecipeView.setVisibility(View.GONE);

          }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
      });
    }

    // Method to setup and show Dialog
    private void showDialog() {
      View foodDialog = getLayoutInflater().inflate(R.layout.dialog_food, null);
      Spinner cateSpinner = foodDialog.findViewById(R.id.categoryInputValue);
      LinearLayout ingredientContainer = foodDialog.findViewById(R.id.ingredientContainer);
      Button addIngredientBtn = foodDialog.findViewById(R.id.addIngredientBtn);

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

      addIngredientBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_input_layout, null);
          Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientSpinner);
          EditText quantityEditText = ingredientView.findViewById(R.id.quantityEditText);
          Button deleteIngredient = ingredientView.findViewById(R.id.deleteIngredientBtn);

          // Populate data into categorySpinner
          DatabaseReference ingredientRef = FirebaseDatabase.getInstance().getReference("ingredients");
          ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              ingredients.clear();
              for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                String ingredientId = ingredientSnapshot.getKey();
                String ingredientName = ingredientSnapshot.child("name").getValue(String.class);
                if (ingredientName != null) {
                  ingredients.add(ingredientName);
                  ingredientMap.put(ingredientName, ingredientId);
                }
              }
              spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_ingredient_layout, ingredients);
              spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              ingredientSpinner.setAdapter(spinnerAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
          });

          // Add listener for delete ingredient
          deleteIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ingredientContainer.removeView(ingredientView);
            }
          });
          ingredientContainer.addView(ingredientView);
        }
      });

      // Add image
      Button addImageBtn = foodDialog.findViewById(R.id.imageAddBtn);
      selectedImage = foodDialog.findViewById(R.id.imageInputValue);
      addImageBtn.setOnClickListener(v -> {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
      });

      // Show the Dialog
      AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
      builder.setView(foodDialog);
      builder.setPositiveButton("Save", (dialog, which) -> {
        // Retrieve input data from user
        TextView nameInput = foodDialog.findViewById(R.id.nameInputValue);
        Spinner cateInput = foodDialog.findViewById(R.id.categoryInputValue);
        TextView aboutInput = foodDialog.findViewById(R.id.descriptionInputValue);
        TextView methodInput = foodDialog.findViewById(R.id.methodInputValue);
        TextView servingInput = foodDialog.findViewById(R.id.servingInputValue);
        TextView prepTimeInput = foodDialog.findViewById(R.id.prepTimeInputValue);
        TextView cookTimeInput = foodDialog.findViewById(R.id.cookTimeInputValue);
        TextView videoURL = foodDialog.findViewById(R.id.videoInputValue);
        TextView levelInput = foodDialog.findViewById(R.id.levelInputValue);

        // Get selected data
        String saveName = nameInput.getText().toString();
        String saveAbout = aboutInput.getText().toString();
        String saveMethod = methodInput.getText().toString();
        int saveLevel = Integer.parseInt(levelInput.getText().toString());
        int saveServing = Integer.parseInt(servingInput.getText().toString());
        int savePrep = Integer.parseInt(prepTimeInput.getText().toString());
        int saveCook = Integer.parseInt(cookTimeInput.getText().toString());
        String saveVideo = videoURL.getText().toString();

        if (saveLevel > 5) {
          saveLevel = 5;
        } else if (saveLevel < 1) {
          saveLevel = 1;
        }

        // Get input ingredients
        List<FoodIngredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < ingredientContainer.getChildCount(); i++) {
          View ingredientView = ingredientContainer.getChildAt(i);
          Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientSpinner);
          EditText quantityEditText = ingredientView.findViewById(R.id.quantityEditText);

          // Get selected ingredients
          String ingredientName = ingredientSpinner.getSelectedItem().toString();
          String quantity = quantityEditText.getText().toString();

          // Add FoodIngredient to ingredientList
          if (ingredientMap.containsKey(ingredientName)) {
            String ingredientId = ingredientMap.get(ingredientName);
            FoodIngredient foodIngredient = new FoodIngredient(Integer.parseInt(ingredientId), quantity, ingredientName);
            ingredientList.add(foodIngredient);
          }
        }

        // Set Category
        String selectedCategoryName = cateInput.getSelectedItem().toString();
        int saveCate = Integer.parseInt(categoryMap.get(selectedCategoryName));

        // Set VideoURL
        String youtubeEmbedUrl = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/";
        String videoId = saveVideo.substring(saveVideo.lastIndexOf("=") + 1);
        String formattedVideoURL = youtubeEmbedUrl + videoId + "?si=yrKhxfGi4NHzaPrd\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";

        // ADD DATA
        uploadData(saveCate, saveCook, saveAbout, imageURI, ingredientList, saveLevel, saveMethod, saveName, savePrep, saveServing, formattedVideoURL);
      });
      builder.setNegativeButton("Cancel", (dialog, which) -> {
        Log.d("Count Ingredient", String.valueOf(ingredientContainer.getChildCount()));
        cateNames.clear();
        dialog.dismiss();
      });
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
    }

    // Method to show dialog with food details
    private void showFoodDetail(Food food) {
      View dialogView = getLayoutInflater().inflate(R.layout.dialog_food, null);

      // Initialize views
      TextView nameInputValue = dialogView.findViewById(R.id.nameInputValue);
      TextView descriptionInputValue = dialogView.findViewById(R.id.descriptionInputValue);
      TextView methodInputValue = dialogView.findViewById(R.id.methodInputValue);
      TextView servingInputValue = dialogView.findViewById(R.id.servingInputValue);
      TextView prepTimeInputValue = dialogView.findViewById(R.id.prepTimeInputValue);
      TextView cookTimeInputValue = dialogView.findViewById(R.id.cookTimeInputValue);
      TextView levelInputValue = dialogView.findViewById(R.id.levelInputValue);
      TextView videoInputValue = dialogView.findViewById(R.id.videoInputValue);
      ImageView imageInputValue = dialogView.findViewById(R.id.imageInputValue);
      Spinner cateInputValue = dialogView.findViewById(R.id.categoryInputValue);

      // Set value
      nameInputValue.setText(food.getName());
      descriptionInputValue.setText(food.getDescription());
      methodInputValue.setText(food.getMethod());
      servingInputValue.setText(String.valueOf(food.getServing()));
      prepTimeInputValue.setText(String.valueOf(food.getPrepTime()));
      cookTimeInputValue.setText(String.valueOf(food.getCookTime()));
      levelInputValue.setText(String.valueOf(food.getLevel()));
      videoInputValue.setText(food.getVideo());

      // Set image
      Glide.with(requireContext()).load(food.getImage()).into(imageInputValue);
      Glide.with(requireContext()).downloadOnly().load(food.getImage()).listener(new RequestListener<File>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<File> target, boolean isFirstResource) {
          return false;
        }

        @Override
        public boolean onResourceReady(@NonNull File resource, @NonNull Object model, Target<File> target, @NonNull DataSource dataSource, boolean isFirstResource) {
          imageURI = Uri.fromFile(resource);
          selectedImage.setImageURI(imageURI);
          return true;
        }
      }).submit();
      selectedImage = dialogView.findViewById(R.id.imageInputValue);
      Button addImgBtn = dialogView.findViewById(R.id.imageAddBtn);
      addImgBtn.setText("Change Image");
      addImgBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/**");
          startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
      });

      // Set ingredients
      LinearLayout ingredientContainer = dialogView.findViewById(R.id.ingredientContainer);
      DatabaseReference ingredientRef = FirebaseDatabase.getInstance().getReference("foods").child(String.valueOf(food.getId())).child("ingredients");
      ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          for (DataSnapshot ingredientSnapShot : snapshot.getChildren()) {
            String ingredientName = ingredientSnapShot.child("name").getValue(String.class);
            String quantity = ingredientSnapShot.child("quantity").getValue(String.class);

            View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_input_layout, null);
            Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientSpinner);
            EditText quantityEditText = ingredientView.findViewById(R.id.quantityEditText);
            Button deleteIngredientBtn = ingredientView.findViewById(R.id.deleteIngredientBtn);

            // Setter
            ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Arrays.asList(ingredientName));
            ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ingredientSpinner.setAdapter(ingredientAdapter);
            quantityEditText.setText(quantity);

            deleteIngredientBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                ingredientContainer.removeView(ingredientView);
              }
            });
            ingredientContainer.addView(ingredientView);
          }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e("Error", String.valueOf(error));
        }
      });
      Button addIngredientBtn = dialogView.findViewById(R.id.addIngredientBtn);
      addIngredientBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_input_layout, null);
          Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientSpinner);
          EditText quantityEditText = ingredientView.findViewById(R.id.quantityEditText);
          Button deleteIngredient = ingredientView.findViewById(R.id.deleteIngredientBtn);

          // Populate data into categorySpinner
          DatabaseReference ingredientRef = FirebaseDatabase.getInstance().getReference("ingredients");
          ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              ingredients.clear();
              for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                String ingredientId = ingredientSnapshot.getKey();
                String ingredientName = ingredientSnapshot.child("name").getValue(String.class);
                if (ingredientName != null) {
                  ingredients.add(ingredientName);
                  ingredientMap.put(ingredientName, ingredientId);
                }
              }
              spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_ingredient_layout, ingredients);
              spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              ingredientSpinner.setAdapter(spinnerAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
          });

          // Add listener for delete ingredient
          deleteIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ingredientContainer.removeView(ingredientView);
            }
          });
          ingredientContainer.addView(ingredientView);
        }
      });

      // Set category
      DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");
      categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          List<String> cateNames = new ArrayList<>();
          for (DataSnapshot cateSnapshot : snapshot.getChildren()) {
            String cateName = cateSnapshot.child("name").getValue(String.class);
            String cateId = cateSnapshot.getKey();
            cateNames.add(cateName);
            categoryMap.put(cateName, cateId);
          }
          ArrayAdapter<String> cateAdapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_item, cateNames);
          cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          cateInputValue.setAdapter(cateAdapter);

          // Set default
          cateInputValue.setSelection(food.getCategoryId());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });

      AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
      builder.setView(dialogView);
      builder.setPositiveButton("Save", (dialog, which) -> {
        // Retrieve input data from user
        TextView nameInput = dialogView.findViewById(R.id.nameInputValue);
        Spinner cateInput = dialogView.findViewById(R.id.categoryInputValue);
        TextView aboutInput = dialogView.findViewById(R.id.descriptionInputValue);
        TextView levelInput = dialogView.findViewById(R.id.levelInputValue);
        TextView methodInput = dialogView.findViewById(R.id.methodInputValue);
        TextView servingInput = dialogView.findViewById(R.id.servingInputValue);
        TextView prepTimeInput = dialogView.findViewById(R.id.prepTimeInputValue);
        TextView cookTimeInput = dialogView.findViewById(R.id.cookTimeInputValue);
        TextView videoURL = dialogView.findViewById(R.id.videoInputValue);
        ImageView image = dialogView.findViewById(R.id.imageInputValue);

        // Get selected data
        String saveName = nameInput.getText().toString();
        String saveAbout = aboutInput.getText().toString();
        String saveMethod = methodInput.getText().toString();
        int saveLevel = Integer.parseInt(levelInput.getText().toString());
        int saveServing = Integer.parseInt(servingInput.getText().toString());
        int savePrep = Integer.parseInt(prepTimeInput.getText().toString());
        int saveCook = Integer.parseInt(cookTimeInput.getText().toString());
        String saveVideo = videoURL.getText().toString();

        if (saveLevel > 5) {
          saveLevel = 5;
        } else if (saveLevel < 1) {
          saveLevel = 1;
        }

        // Get input ingredients
        List<FoodIngredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < ingredientContainer.getChildCount(); i++) {
          View ingredientView = ingredientContainer.getChildAt(i);
          Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientSpinner);
          EditText quantityEditText = ingredientView.findViewById(R.id.quantityEditText);

          // Get selected ingredients
          String ingredientName = ingredientSpinner.getSelectedItem().toString();
          String quantity = quantityEditText.getText().toString();

          // Add FoodIngredient to ingredientList
          if (ingredientMap.containsKey(ingredientName)) {
            String ingredientId = ingredientMap.get(ingredientName);
            FoodIngredient foodIngredient = new FoodIngredient(Integer.parseInt(ingredientId), quantity, ingredientName);
            ingredientList.add(foodIngredient);
          }
        }

        // Set Category
        String selectedCategoryName = cateInput.getSelectedItem().toString();
        int saveCate = Integer.parseInt(categoryMap.get(selectedCategoryName));

        // Set VideoURL
        String youtubeEmbedUrl = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/";
        String videoId = saveVideo.substring(saveVideo.lastIndexOf("=") + 1);
        String formattedVideoURL = youtubeEmbedUrl + videoId + "?si=yrKhxfGi4NHzaPrd\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";

        // UPDATE DATA
        updateData(food, saveCate, saveCook, saveAbout, imageURI, ingredientList, saveLevel, saveMethod, saveName, savePrep, saveServing, formattedVideoURL);

      });
      builder.setNegativeButton("Cancel", (dialog, which) -> {
        dialog.dismiss();
      });
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
        imageURI = data.getData();
        if (selectedImage != null) {
          selectedImage.setImageURI(imageURI);
        }
        else {
          Log.e("OwnScreenFragment", "selectedImage is null");
        }
      }
    }

    // Methods to upload
    private void uploadData(int saveCate, int saveCookTime, String saveAbout,  Uri imageURI, List<FoodIngredient> ingredientList, int saveLevel, String saveMethod, String saveName, int savePrepTime, int saveServing, String formattedVideoURL) {
      String randomKey = UUID.randomUUID().toString();
      final ProgressDialog progressDialog = new ProgressDialog(mContext);
      progressDialog.setTitle("Uploading Food...");
      progressDialog.show();
      StorageReference reference = FirebaseStorage.getInstance().getReference().child("food/" + randomKey);

      reference.putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
        progressDialog.dismiss();
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
          String imageURL = uri.toString();
          String currentUserId = pref.getString("userId", null);
          DatabaseReference foodListReference = FirebaseDatabase.getInstance().getReference("foods");
          foodListReference.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String idReceived = "0";
              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                idReceived = snapshot.getKey();
              }
              int id = Integer.parseInt(idReceived);
              int newId = id + 1;

              foodListReference.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                  Food newFood = new Food(newId, saveCate, saveCookTime, currentUserId, saveAbout, imageURL, ingredientList, saveLevel, saveMethod, saveName, savePrepTime, saveServing, formattedVideoURL);
                  currentData.child(String.valueOf(newId)).setValue(newFood);
                  return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                  if (committed) {
                    Toast.makeText(getActivity(), "Recipe has been added", Toast.LENGTH_SHORT).show();
                    foodListAdapter.notifyDataSetChanged();
                    cateNames.clear();
                    reloadFragment();
                  }
                }
              });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              Log.d("Error", String.valueOf(error));
            }
          });
        });
      });
    }

    // Method to update
    private void updateData(Food food, int saveCate, int saveCookTime, String saveAbout,  Uri imageURI, List<FoodIngredient> ingredientList, int saveLevel, String saveMethod, String saveName, int savePrepTime, int saveServing, String formattedVideoURL) {
      String randomKey = UUID.randomUUID().toString();
      final ProgressDialog progressDialog = new ProgressDialog(mContext);
      progressDialog.setTitle("Updating Food...");
      progressDialog.show();
      StorageReference reference = FirebaseStorage.getInstance().getReference().child("food/" + randomKey);

      reference.putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
        progressDialog.dismiss();
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
          String imageURL = uri.toString();
          String currentUserId = pref.getString("userId", null);
          DatabaseReference foodListReference = FirebaseDatabase.getInstance().getReference("foods");
          foodListReference.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              int currentId = food.getId();
              foodListReference.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                  Food newFood = new Food(currentId, saveCate, saveCookTime, currentUserId, saveAbout, imageURL, ingredientList, saveLevel, saveMethod, saveName, savePrepTime, saveServing, formattedVideoURL);
                  currentData.child(String.valueOf(currentId)).setValue(newFood);
                  return Transaction.success(currentData);
                }
                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                  if (committed) {
                    Toast.makeText(getActivity(), "Recipe has been updated", Toast.LENGTH_SHORT).show();
                    foodListAdapter.notifyDataSetChanged();
                    cateNames.clear();
                    reloadFragment();
                  }
                }
              });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              Log.d("Error", String.valueOf(error));
            }
          });
        });
      });
    }

    // Method to extract Video's id
    private String extractVideoId(String url) {
      String videoId = null;
      String pattern = "(?<=embed\\/|watch\\?v=|\\/videos\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\%2Fvideos\\%2F|youtu.be%2F|v%2F)[^#\\&\\?\\n]*";
      Pattern compiledPattern = Pattern.compile(pattern);
      Matcher matcher = compiledPattern.matcher(url);
      if (matcher.find()) {
        videoId = matcher.group();
      }
      return videoId;
    }

    // Method to reload whenever saved
    private void reloadFragment() {
      foodList.clear();
      foodListAdapter.notifyDataSetChanged();
      OwnScreenFragment fragment = new OwnScreenFragment();
      requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.own_screen_fragment, fragment).commit();
    }

    // Method to delete food
    private void deleteFood(int pos) {
      Food deleteFood = foodList.get(pos);
      String currentUserId = pref.getString("userId", null);
      DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods");
      foodRef.orderByChild("id").equalTo(deleteFood.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            String createdby = dataSnapshot.child("createdBy").getValue(String.class);
            if (createdby != null && createdby.equals(currentUserId)) {
              dataSnapshot.getRef().removeValue();
              foodList.remove(pos);
              foodListAdapter.notifyItemRemoved(pos);
              Toast.makeText(mContext, "Food deleted successfully!", Toast.LENGTH_SHORT).show();
            }
            else {
              Toast.makeText(mContext, "You don't have permission to delete this food", Toast.LENGTH_SHORT).show();
            }
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.d("Error", String.valueOf(error));
        }
      });
    }
}
