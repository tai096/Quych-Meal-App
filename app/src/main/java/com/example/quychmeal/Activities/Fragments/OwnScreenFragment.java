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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

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

    public OwnScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

//            Food food = foodSnapshot.getValue(Food.class);
//            foodList.add(food);

            String videoEmbedded = foodSnapshot.child("video").getValue(String.class);
            String videoId = extractVideoId(videoEmbedded);
            String video = "https://www.youtube.com/watch?v=" + videoId;

            // Retrieve ingredients
            List<FoodIngredient> ingredientList = new ArrayList<>();
            DataSnapshot ingredients = foodSnapshot.child("ingredients");
            for (DataSnapshot ingredient : ingredients.getChildren()) {
              String ingredientId = ingredient.getKey();
              ingredientList.add(new FoodIngredient(Integer.parseInt(ingredientId), ""));
            }

            Food food = new Food(id, cateId, cookTime, currentUserId, description, image, ingredientList, level, method, foodName, prepTime, serving, video);
            foodList.add(food);
          }
          foodListAdapter.notifyDataSetChanged();
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

      // Add image
      Button addImageBtn = foodDialog.findViewById(R.id.imageAddBtn);
      selectedImage = foodDialog.findViewById(R.id.imageInputValue);
      addImageBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/*");
          startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
        TextView methodInput = foodDialog.findViewById(R.id.methodInputValue);
        TextView servingInput = foodDialog.findViewById(R.id.servingInputValue);
        TextView prepTimeInput = foodDialog.findViewById(R.id.prepTimeInputValue);
        TextView cookTimeInput = foodDialog.findViewById(R.id.cookTimeInputValue);
        TextView videoURL = foodDialog.findViewById(R.id.videoInputValue);

        // Get selected data
        String saveName = nameInput.getText().toString();
        String saveIngredients = ingredientInput.getText().toString();
        String saveAbout = aboutInput.getText().toString();
        String saveMethod = methodInput.getText().toString();
        int saveServing = Integer.parseInt(servingInput.getText().toString());
        int savePrep = Integer.parseInt(prepTimeInput.getText().toString());
        int saveCook = Integer.parseInt(cookTimeInput.getText().toString());
        String saveVideo = videoURL.getText().toString();

        // Set Ingredient ID
        List<String> ingredientNames = Arrays.asList(saveIngredients.split("\\s*,\\s*"));
        List<FoodIngredient> ingredientList = new ArrayList<>();
        for (String ingredientName : ingredientNames) {
          if (ingredientMap.containsKey(ingredientName)) {
            String ingredientId = ingredientMap.get(ingredientName);
            FoodIngredient foodIngredient = new FoodIngredient(Integer.parseInt(ingredientId), "");
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
        uploadImage(saveCate, saveCook, saveAbout, imageURI, ingredientList, 1, saveMethod, saveName, savePrep, saveServing, formattedVideoURL);
      });
      builder.setNegativeButton("Cancel", (dialog, which) -> {
        cateNames.clear();
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
        selectedImage.setImageURI(imageURI);
      }
    }

    // Methods to upload food's data to Firebase
    private void uploadImage(int saveCate, int saveCook, String saveAbout,  Uri imageURI, List<FoodIngredient> ingredientList, int level, String saveMethod, String saveName, int savePrep, int saveServing, String formattedVideoURL) {
      String randomKey = UUID.randomUUID().toString();
      final ProgressDialog progressDialog = new ProgressDialog(mContext);
      progressDialog.setTitle("Uploading Food...");
      progressDialog.show();

      StorageReference reference = FirebaseStorage.getInstance().getReference().child("food/" + randomKey);

      reference.putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
        progressDialog.dismiss();
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
          String imageURL = uri.toString();
          addFoodToDatabase(saveCate, saveCook, saveAbout, imageURL, ingredientList, level, saveMethod, saveName, savePrep, saveServing, formattedVideoURL);
        });
      });
    }

    private void addFoodToDatabase(int saveCate, int saveCookTime, String saveAbout,  String imageURL, List<FoodIngredient> ingredientList, int level, String saveMethod, String saveName, int savePrepTime, int saveServing, String formattedVideoURL) {
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
              Food newFood = new Food(newId, saveCate, saveCookTime, currentUserId, saveAbout, imageURL, ingredientList, 1, saveMethod, saveName, savePrepTime, saveServing, formattedVideoURL);
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
}
