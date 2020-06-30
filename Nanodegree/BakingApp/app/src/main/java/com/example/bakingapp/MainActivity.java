package com.example.bakingapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MessageDelayer.DelayerCallback {
    private SimpleIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.init();

        MessageDelayer.processMessage(idlingResource);
        RecyclerView recyclerView = findViewById(R.id.main_list_rv);
        List<Recipe> recipes = viewModel.getRecipeData();
        RecipeAdapter adapter = new RecipeAdapter(recipes, this);
        recyclerView.setAdapter(adapter);
        if (!getResources().getBoolean(R.bool.isTablet)) {
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
            recyclerView.setLayoutManager(manager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(manager);
        }
    }

    @Override
    public void onDone() {
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
    }
}