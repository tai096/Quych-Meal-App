package com.example.quychmeal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.quychmeal.Activities.DetailRecipeActivity;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.Models.Ingredient;
import com.example.quychmeal.R;

import java.util.ArrayList;

public class ReipeAdapter extends BaseAdapter {
    private ArrayList<Food> recipeList;
    private Context context;
    LayoutInflater layoutInflater;

    public ReipeAdapter(ArrayList<Food> recipeList, Context context) {
        this.recipeList = recipeList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.food_item, null);

        }

        TextView foodName;
        ImageView foodImg;
        TextView foodServing;
        TextView foodPrepTime;
        TextView foodCookTime;
        Button tryNowBtn;

        foodName = convertView.findViewById(R.id.foodName);
        foodName.setText(recipeList.get(position).getName());

        foodServing = convertView.findViewById(R.id.servingValue);
        foodServing.setText(String.valueOf(recipeList.get(position).getServing()) + "p");

        foodPrepTime = convertView.findViewById(R.id.prepValue);
        foodPrepTime.setText(String.valueOf(recipeList.get(position).getPrepTime()) + "m");

        foodCookTime = convertView.findViewById(R.id.cookValue);
        foodCookTime.setText(String.valueOf (recipeList.get(position).getCookTime()) + "m");

        foodImg = convertView.findViewById(R.id.foodImg);
        Glide.with(context).load(recipeList.get(position).getImage()).transform(new CenterCrop(), new RoundedCorners(30)).into(foodImg);

        tryNowBtn = convertView.findViewById(R.id.tryNowBtn); // Added
        // Added
        tryNowBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailRecipeActivity.class);
            intent.putExtra("foodName", recipeList.get(position).getName());
            intent.putExtra("foodImage", recipeList.get(position).getImage());
            intent.putExtra("foodDes", recipeList.get(position).getDescription());
            intent.putExtra("foodCreator", recipeList.get(position).getCreatedBy());
            intent.putExtra("foodLevel",  String.valueOf(recipeList.get(position).getLevel()));
            intent.putExtra("foodServing", String.valueOf(recipeList.get(position).getServing()));
            intent.putExtra("foodPrep", String.valueOf(recipeList.get(position).getPrepTime()));
            intent.putExtra("foodCookTime", String.valueOf(recipeList.get(position).getCookTime()));
            intent.putExtra("foodId", String.valueOf(recipeList.get(position).getId()));

            // Add more data as needed
            // Start the activity
            context.startActivity(intent);
        });

        return convertView;
    }
}
