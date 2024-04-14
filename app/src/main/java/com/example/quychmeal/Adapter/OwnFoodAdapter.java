package com.example.quychmeal.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.quychmeal.Models.Food;
import com.example.quychmeal.R;

import java.util.List;

public class OwnFoodAdapter extends RecyclerView.Adapter<OwnFoodAdapter.ViewHolder> {
  private List<Food> foodList;
  private OnItemClickListener listener;

  // Click interface
  public interface OnItemClickListener {
    void onItemClick(int position);
    void onDeleteClick(int position);
  }

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
    ImageView binIcon;
    LinearLayout foodBasicInfo;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      foodImg = itemView.findViewById(R.id.foodImg);
      foodName = itemView.findViewById(R.id.foodName);
      foodServing = itemView.findViewById(R.id.servingValue);
      foodPrepTime = itemView.findViewById(R.id.prepValue);
      foodCookTime = itemView.findViewById(R.id.cookValue);
      binIcon = itemView.findViewById(R.id.bin);
      foodBasicInfo = itemView.findViewById(R.id.foodBasicInfo);
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
    Glide.with(holder.itemView.getContext()).load(food.getImage()).transform(new CenterCrop(), new RoundedCorners(10)).into(holder.foodImg);
    holder.foodPrepTime.setText(food.getPrepTime() + "hrs");
    holder.foodServing.setText(food.getServing() + "pp");
    holder.foodCookTime.setText(food.getCookTime() + "hrs");

    // Click listener for delete
    holder.binIcon.setOnClickListener(view -> {
      if (listener != null) {
        int currentPos = holder.getAdapterPosition();
        if (currentPos != RecyclerView.NO_POSITION) {
          listener.onDeleteClick(currentPos);
        }
      }
    });

    // Click listener for food details
    holder.foodBasicInfo.setOnClickListener(view -> {
      if (listener != null) {
        int currentPos = holder.getAdapterPosition();
        if (currentPos != RecyclerView.NO_POSITION) {
          listener.onItemClick(currentPos);
        }
      }
    });

  }

  @Override
  public int getItemCount() {
    return foodList.size();
  }
}
