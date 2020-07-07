package com.example.bakingapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RecipeRepository {
    private static RecipeRepository repository;
    private List<Recipe> recipes;

    private RecipeRepository() {}

    public static RecipeRepository getInstance() {
        if (repository == null)
            repository = new RecipeRepository();
        return repository;
    }

    public void getRecipeData(final Context context) throws IOException {
        AndroidNetworking.initialize(context);
        AndroidNetworking.get(context.getResources().getString(R.string.url)).setPriority(Priority.HIGH).build().getAsObjectList(Recipe.class, new ParsedRequestListener<List<Recipe>>() {
            @Override
            public void onResponse(List<Recipe> recipes) {
                System.out.println("********" + recipes);
                setValue(recipes);
                storeData(context, recipes);
            }

            @Override
            public void onError(ANError anError) {
            }
        });
    }

    public void setValue(List<Recipe> recipesList) {
        this.recipes = recipesList;
    }

    public Set<String> getRecipeSet(Context context) {
        Set<String> set;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.DATA_PREFERENCE_FILE), Context.MODE_PRIVATE);
        set = sharedPreferences.getStringSet(context.getString(R.string.recipe_ingredients), null);
        return set;
    }

    private void storeData(Context context, List<Recipe> recipes) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.DATA_PREFERENCE_FILE), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            String json = GsonInstance.getInstance().toJson(recipe);
            set.add(json);
        }
        editor.putStringSet(context.getString(R.string.recipe_ingredients), set);
        editor.apply();
    }

    public String getRecipe(int position, Context context) {
        Set<String> set;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.DATA_PREFERENCE_FILE), Context.MODE_PRIVATE);
        set = sharedPreferences.getStringSet(context.getString(R.string.recipe_ingredients), null);
        Iterator<String> iterator;
        String response = null;
        int i = 1;
        if (set != null) {
            iterator = set.iterator();
            while (iterator.hasNext()) {
                response = iterator.next();
                if (i == position)
                    break;
                i++;
            }
        }
        return response;
    }
}