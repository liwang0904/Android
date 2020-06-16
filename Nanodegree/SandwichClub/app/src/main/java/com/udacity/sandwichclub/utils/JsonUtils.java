package com.udacity.sandwichclub.utils;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {
        Sandwich sandwich = new Sandwich();

        String name;
        String placeOfOrigin;
        String description;
        String image;

        ArrayList<String> ingredients_list = new ArrayList<>();
        ArrayList<String> alsoKnownAs_list = new ArrayList<>();

        if (json != null) {
            try {
                JSONObject sandwichJSON = new JSONObject(json);
                JSONObject objectForName = sandwichJSON.getJSONObject("name");

                name = objectForName.getString("mainName");
                placeOfOrigin = sandwichJSON.getString("placeOfOrigin");
                description = sandwichJSON.getString("description");
                image = sandwichJSON.getString("image");

                JSONArray alsoKnownAs = objectForName.getJSONArray("alsoKnownAs");
                for (int i = 0; i < alsoKnownAs.length(); i++) {
                    alsoKnownAs_list.add(alsoKnownAs.getString(i));
                }

                JSONArray ingredients = sandwichJSON.getJSONArray("ingredients");
                for (int i = 0; i < ingredients.length(); i++) {
                    ingredients_list.add(ingredients.getString(i));
                }

                sandwich.setMainName(name);
                sandwich.setPlaceOfOrigin(placeOfOrigin);
                sandwich.setDescription(description);
                sandwich.setImage(image);
                sandwich.setAlsoKnownAs(alsoKnownAs_list);
                sandwich.setIngredients(ingredients_list);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sandwich;
    }
}
