package com.example.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<String> list;
    private final Context context;
    private int recipeNumber;
    private Recipe recipe;

    public ListProvider(Context context1, Intent intent) {
        if(intent.hasExtra("RECIPE NUMBER"))
            recipeNumber = intent.getIntExtra("RECIPE NUMBER", 0);
        context = context1;
    }

    private void setUp() {
        for(Ingredient ingredient : recipe.getIngredients()) {
            StringBuilder builder = new StringBuilder();
            builder.append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append(" of ")
                    .append(ingredient.getIngredient())
                    .append("\n");
            list.add(new String(builder));
        }
    }

    @Override
    public void onCreate() {
        list = new ArrayList<>();
        recipe = GsonInstance.getInstance().fromJson(RecipeRepository.getInstance().getRecipe(recipeNumber, context), Recipe.class);
        setUp();
    }

    @Override
    public void onDataSetChanged() {
        setUp();
    }

    @Override
    public void onDestroy() {
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setTextViewText(R.id.widget_single_text, list.get(i));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
