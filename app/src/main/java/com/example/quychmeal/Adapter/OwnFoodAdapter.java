package com.example.quychmeal.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;

import java.util.List;

public class OwnFoodAdapter extends RecyclerView.Adapter<OwnFoodAdapter.ViewHolder> {
  private List<Food> foodList;

  // Constructor
  public OwnFoodAdapter(List<Food> foodList) {
    this.foodList = foodList;
  }

  // ViewHolder class
  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView foodName;
    ImageView foodImg;
    TextView foodServing;
    TextView foodPrepTime;
    TextView foodCookTime;
    TextView description;
    TextView cookMethod;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      foodImg = itemView.findViewById(R.id.foodImg);
      foodName = itemView.findViewById(R.id.foodName);
      foodServing = itemView.findViewById(R.id.servingValue);
      foodPrepTime = itemView.findViewById(R.id.prepValue);
      foodCookTime = itemView.findViewById(R.id.cookValue);
    }
  }

  @NonNull
  @Override
  public OwnFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_user_recipe, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull OwnFoodAdapter.ViewHolder holder, int position) {
    Food food = foodList.get(position);
    holder.foodName.setText(food.getName());

    holder.foodPrepTime.setText(food.getPrepTime() + "hrs");
    holder.foodServing.setText(food.getServing() + "pp");
    holder.foodCookTime.setText(food.getCookTime() + "hrs");
  }

  @Override
  public int getItemCount() {
    return foodList.size();
  }
}
