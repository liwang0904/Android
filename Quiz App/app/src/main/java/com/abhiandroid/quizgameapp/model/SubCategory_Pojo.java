package com.abhiandroid.quizgameapp.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Developed by AbhiAndroid.com
 */

public class SubCategory_Pojo extends JSONObject{

    public List<Data> data;
    public Meta meta;
    public Links links;

    public class Links {

        public String next;
    }
    public class Meta {
        public int total,current_page,last_page,from,per_page;


    }
    public class Data {
        public int id;
        public String  subcat_name,subcat_image,cat_id,subcat_id,cat_name;

    }

}


