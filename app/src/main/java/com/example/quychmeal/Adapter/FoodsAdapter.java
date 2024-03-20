package com.example.quychmeal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.quychmeal.Models.Category;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;

import java.util.List;

public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder> {

    public Context context;
    public List<Food> foodList;

    public FoodsAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        Glide.with(context).load(food.getImage()).transform(new CenterCrop(), new RoundedCorners(30)).into(holder.foodImg);
        holder.foodName.setText(food.getName());
        holder.foodServing.setText(food.getServing());
        holder.foodCookTime.setText(food.getCookTime());
        holder.foodPrepTime.setText(food.getPrepTime());
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void filterList(List<Food> filteredList) {
        foodList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        ImageView foodImg;
        TextView foodServing;
        TextView foodPrepTime;
        TextView foodCookTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImg = itemView.findViewById(R.id.foodImg);
            foodName = itemView.findViewById(R.id.foodName);
            foodServing = itemView.findViewById(R.id.servingValue);
            foodPrepTime = itemView.findViewById(R.id.prepValue);
            foodCookTime = itemView.findViewById(R.id.cookValue);
        }
    }
}
