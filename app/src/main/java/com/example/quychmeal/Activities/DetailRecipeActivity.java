package com.example.quychmeal.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.quychmeal.R;
import com.example.quychmeal.databinding.ActivityMainBinding;

public class DetailRecipeActivity extends RootActivity {
    private String foodName, foodImage, foodDes, foodCreator, foodLevel, foodServing, foodPrep, foodCookTime, foodId;
TextView txtFoodName, txtFoodDesc,txtCreator, txtServings, txtPreptime, txtCookingTime;
ImageView foodImgView;
RatingBar cookingLevelBar;
ImageButton detailBackBtn;
Button btnCook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodDesc = findViewById(R.id.txtFoodDesc);
        txtCreator = findViewById(R.id.txtCreator);
        txtServings = findViewById(R.id.txtServings);
        txtPreptime = findViewById(R.id.txtPreptime);
        txtCookingTime = findViewById(R.id.txtCookingTime);
        cookingLevelBar = findViewById(R.id.cookingLevelBar);
        foodImgView = findViewById(R.id.foodImgView);
        detailBackBtn = findViewById(R.id.detailBackBtn);
        btnCook = findViewById(R.id.btnCook);

        Intent intent = getIntent();
        if (intent != null) {
            foodName = intent.getStringExtra("foodName");
            foodImage = intent.getStringExtra("foodImage");
            foodDes = intent.getStringExtra("foodDes");
            foodCreator = intent.getStringExtra("foodCreator");
            foodLevel = intent.getStringExtra("foodLevel");
            foodServing = intent.getStringExtra("foodServing");
            foodPrep = intent.getStringExtra("foodPrep");
            foodCookTime = intent.getStringExtra("foodCookTime");
            foodId = intent.getStringExtra("foodId");

            getFoodDetail();
            handleCook(foodId);
        }

        handleGoBack();
    }

    private void handleGoBack() {
        detailBackBtn.setOnClickListener(v -> {
            DetailRecipeActivity.this.finish();
        });
    }

    private void handleCook(String foodId){
    btnCook.setOnClickListener(v -> {
        Intent intent = new Intent(this, CookingActivity.class);
        intent.putExtra("foodId", foodId);
        this.startActivity(intent);
    });
    }

    private void getFoodDetail() {
        txtFoodName.setText(foodName);
        txtFoodDesc.setText(foodDes);
        txtCreator.setText(foodCreator);
        txtServings.setText(foodServing + "p");
        txtPreptime.setText(foodPrep + "m");
        txtCookingTime.setText(foodCookTime + "m");
        cookingLevelBar.setRating(Float.parseFloat(foodLevel));
        Glide.with(this).load(foodImage).transform(new CenterCrop()).into(foodImgView);
    }
}
