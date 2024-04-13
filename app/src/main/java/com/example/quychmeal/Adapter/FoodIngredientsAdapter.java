package com.example.quychmeal.Adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quychmeal.Models.FoodIngredient;
import com.example.quychmeal.Models.Ingredient;
import com.example.quychmeal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FoodIngredientsAdapter extends BaseAdapter {
    private ArrayList<FoodIngredient> foodIngredientArrayList;
    private final Context context;
    LayoutInflater layoutInflater;

    public FoodIngredientsAdapter(ArrayList<FoodIngredient> foodIngredientArrayList, Context context) {
        this.foodIngredientArrayList = foodIngredientArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return foodIngredientArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodIngredientArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.food_ingredient_item, null);
        }

        TextView nameIngredient = convertView.findViewById(R.id.nameFoodIngredient);
        TextView quantityIngredient = convertView.findViewById(R.id.quantityIngredient);

        nameIngredient.setText(foodIngredientArrayList.get(position).getName());
        quantityIngredient.setText(foodIngredientArrayList.get(position).getQuantity());

        return convertView;
    }




}
