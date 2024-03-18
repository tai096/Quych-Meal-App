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
import com.example.quychmeal.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.viewholder> {
    ArrayList<Category> categories;
    Context context;

    public CategoriesAdapter(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoriesAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_categories, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.viewholder holder, int position) {
        holder.categoryName.setText(categories.get(position).getName());
        Glide.with(context).load(categories.get(position).getImage()).transform(new CenterCrop(), new RoundedCorners(30)).into(holder.categoryImg);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImg;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryName);
            categoryImg = itemView.findViewById(R.id.categoryImg);
        }
    }
}
