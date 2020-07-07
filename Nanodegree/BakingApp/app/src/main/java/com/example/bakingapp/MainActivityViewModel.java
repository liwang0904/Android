package com.example.bakingapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.IOException;
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
            try {
                repository.getRecipeData(getApplication().getApplicationContext());

                Set<String> set = repository.getRecipeSet(getApplication().getApplicationContext());
                if(set != null) {
                    for (String data : set) {
                        recipes.add(GsonInstance.getInstance().fromJson(data, Recipe.class));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (recipes == null)
                Log.e("MainActivityViewModel", "Empty!");
        }
        return recipes;
    }
}