package com.abhiandroid.quizgameapp.model;


/**
 * Developed by AbhiAndroid.com
 */
import com.google.gson.annotations.SerializedName;

public class User {
    public String message;
    public String status;
    public Data data;


    public class Data {
        public int id;
        public String  name,email,logintype,student_id,social_id,profile_pic;
    }
    public class Credentials
    {
        public String email,name,logintype,social_id,image,profile_pic;

    }
}
