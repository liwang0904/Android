package com.abhiandroid.quizgameapp.room_database;

import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Developed by AbhiAndroid.com
 */

public class Converters {
    @TypeConverter
    public static ArrayList<Options> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Options>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Options> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
