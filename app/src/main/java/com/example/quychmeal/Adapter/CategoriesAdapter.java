package com.example.quychmeal.Adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quychmeal.Models.Category;
import com.example.quychmeal.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {


    public interface CategoryClickListener {
        void onCategoryClick(Category category);
    }

    private Context context;
    private List<Category> categoryList;
    private CategoryClickListener listener;
    private int selectedPosition = -1; // To keep track of the selected category position

    public CategoriesAdapter(Context context, List<Category> categoryList, CategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
        this.selectedPosition = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public int getSelectedCategory() {
        return selectedPosition;
    }


    public void setSelectedCategory(Category category) {
        // Find the position of the selected category in the list
        int position = categoryList.indexOf(category);
        if (position != -1) {
            // Update selected position
            int previousSelectedPosition = selectedPosition;
            selectedPosition = position;
            // Notify item changes to update UI
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImg;
        LinearLayout cardCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImg = itemView.findViewById(R.id.categoryImg);
            categoryName = itemView.findViewById(R.id.categoryName);
            cardCategory = itemView.findViewById(R.id.cardCategory);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categoryList.get(position));
                }
            });
        }

        public void bind(Category category, int position) {
            categoryName.setText(category.getName());
            Glide.with(context)
                    .load(category.getImage())
                    .into(categoryImg);

            // setTextColor based on selected position
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (position == selectedPosition) {
                cardCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));

            } else {
                if(nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
                    cardCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.lightCard));
                }else {
                    cardCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.darkCard));
                }
            }
        }
    }
}
