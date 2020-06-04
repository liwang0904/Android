package com.abhiandroid.quizgameapp.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Developed by AbhiAndroid.com
 */

public class Favourite_Pojo extends JSONObject{

    public List<Data> data;

    public class Data {
        public int id;
        public String  quiz_name,cat_id,cat_name,subcat_id,subcat_name,quiz_image,quiz_id;

    }

}


