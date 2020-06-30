package com.example.bakingapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonInstance {
    private static Gson gson;

    private GsonInstance() {
    }

    public static Gson getInstance() {
        if (gson == null)
            gson = new GsonBuilder().create();
        return gson;
    }
}