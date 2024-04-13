package com.example.quychmeal.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;

import com.example.quychmeal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddNewRecipeActivity extends RootActivity {
  private Button btnSave;
  public Uri imageURI;
  private EditText editTxtName, editTxtMethod, editTxtAbout, editTxtServing, editTxtCook, editTxtPrep;
  private Spinner cateSpinner;
  SharedPreferences pref;
  private static final String SHARED_PREF_NAME = "mypref";
  DatabaseReference refDB;
  String currentUserId;
  private ActivityResultLauncher<Intent> mActivityResultLauncher;
  private FirebaseStorage firebaseStorage;
  private StorageReference storageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.dialog_food);

    pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
    refDB = FirebaseDatabase.getInstance().getReference("foods");
    firebaseStorage = FirebaseStorage.getInstance();
    storageReference = firebaseStorage.getReference();
  }
}
