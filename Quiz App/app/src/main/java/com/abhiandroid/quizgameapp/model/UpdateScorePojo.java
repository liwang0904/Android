package com.abhiandroid.quizgameapp.model;

import org.json.JSONObject;

/**
 * Developed by AbhiAndroid.com
 */

public class UpdateScorePojo extends JSONObject{

    public String  message;
    public  Data  data;
    class Data extends JSONObject{
        public String student_id;
    }

}


