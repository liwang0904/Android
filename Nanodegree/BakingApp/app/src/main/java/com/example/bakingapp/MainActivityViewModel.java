package com.example.bakingapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivityViewModel extends AndroidViewModel {
    private RecipeRepository repository;
    private List<Recipe> recipes;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        repository = RecipeRepository.getInstance();
    }

    public List<Recipe> getRecipeData() {
        if (recipes == null) {
            recipes = new ArrayList<>();
            repository.getRecipeData(getApplication().getApplicationContext());
            Set<String> set = repository.getRecipeSet(getApplication().getApplicationContext());
            for (String recipe: set) {
                recipes.add(GsonInstance.getInstance().fromJson(recipe, Recipe.class));
            }
        }
        return recipes;
    }
}
