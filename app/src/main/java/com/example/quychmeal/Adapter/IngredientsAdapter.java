package com.example.quychmeal.Adapter;

import android.content.Context;
import android.content.res.Configuration;
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
    private ArrayList<Integer> selectedIngredients;
    private Context context;
    LayoutInflater layoutInflater;

    public IngredientsAdapter(ArrayList<Ingredient> ingredientArrayList, ArrayList<Integer> selectedIngredients, Context context) {
        this.ingredientArrayList = ingredientArrayList;
        this.selectedIngredients = selectedIngredients;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ingredientArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredientArrayList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.ingredient_item, null);
        }

        // Get the current ingredient
        Ingredient ingredient = ingredientArrayList.get(position);

        // Set the tag for the view
        convertView.setTag(ingredient);

        TextView nameIngredient = convertView.findViewById(R.id.nameIngredient);
        nameIngredient.setText(ingredientArrayList.get(position).getName());

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Check if the ingredient ID is in selectedIngredients
        if (selectedIngredients.contains(ingredient.getId())) {
            // Change the background color of the TextView
            nameIngredient.setBackgroundResource(R.drawable.custom_selected_item);
            nameIngredient.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            // Change the background color back to default
            nameIngredient.setBackgroundResource(R.drawable.custom_button);
            if(nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
                nameIngredient.setTextColor(context.getResources().getColor(R.color.darkMode));
            }
        }

        return convertView;
    }

    public void setSelectedIngredients(ArrayList<Integer> selectedIngredients) {
        this.selectedIngredients = selectedIngredients;
        notifyDataSetChanged();
    }

    public void filterList(ArrayList<Ingredient> filteredList) {
        ingredientArrayList = filteredList;
        notifyDataSetChanged();
    }
}
