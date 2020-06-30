package com.example.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static List<Recipe> recipes;
    private final Context context;

    public RecipeAdapter(List<Recipe> recipeList, Context context) {
        recipes = recipeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_main, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.title.setText(recipes.get(position).getName());
        holder.servings.setText(recipes.get(position).getServings() + " servings");
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView servings;
        final CardView cardView;
        final ConstraintLayout background;

        public RecipeViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.view_holder_title);
            servings = view.findViewById(R.id.view_holder_servings);
            cardView = view.findViewById(R.id.view_holder_cv);
            background = view.findViewById(R.id.view_holder_background);
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    Parcelable wrapped = Parcels.wrap(recipes.get(RecipeViewHolder.this.getAdapterPosition()));
                    intent.putExtra(context.getPackageName(), wrapped);
                    context.startActivity(intent);
                }
            });
        }
    }
}
