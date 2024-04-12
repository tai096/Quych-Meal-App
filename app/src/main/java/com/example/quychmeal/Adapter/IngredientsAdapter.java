package com.example.quychmeal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quychmeal.Models.Ingredient;
import com.example.quychmeal.R;

import java.util.ArrayList;

public class IngredientsAdapter extends BaseAdapter {
    private ArrayList<Ingredient> ingredientArrayList;
    private Context context;
    LayoutInflater layoutInflater;

    public IngredientsAdapter(ArrayList<Ingredient> ingredientArrayList, Context context) {
        this.ingredientArrayList = ingredientArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ingredientArrayList.size();
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
            convertView = layoutInflater.inflate(R.layout.ingredient_item, null);

        }

        // Get the current ingredient
        Ingredient ingredient = ingredientArrayList.get(position);

        // Set the tag for the view
        convertView.setTag(ingredient);

        TextView nameIngredient = convertView.findViewById(R.id.nameIngredient);
        nameIngredient.setText(ingredientArrayList.get(position).getName());


        return convertView;
    }

    public void filterList(ArrayList<Ingredient> filteredList) {
        ingredientArrayList = filteredList;
        notifyDataSetChanged();
    }

}
