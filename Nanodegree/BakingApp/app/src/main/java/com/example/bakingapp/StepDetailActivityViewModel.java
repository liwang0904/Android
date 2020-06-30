package com.example.bakingapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class StepDetailActivityViewModel extends AndroidViewModel {
    private Recipe recipe;
    private int position;
    private int previousPosition = -1;
    private int index = 0;
    private long playBackPosition = 0;
    private boolean playWhenReady = true;
    private boolean isLandscape = false;

    public StepDetailActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(int previousPosition) {
        this.previousPosition = previousPosition;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getPlayBackPosition() {
        return playBackPosition;
    }

    public void setPlayBackPosition(long playBackPosition) {
        this.playBackPosition = playBackPosition;
    }

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public void setLandscape(boolean landscape) {
        isLandscape = landscape;
    }
}
