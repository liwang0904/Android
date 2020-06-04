package com.abhiandroid.quizgameapp.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Developed by AbhiAndroid.com
 */

public class Questions_Pojo extends JSONObject{


    public Data data;

    public class Options {

        public String id,question_id,optn,optn_name;
    }
    public class Data {
        public int isquiz_delete;
        public String question_time,life,quiz_image,best_score,quiz_name,followstatus,quiz_id;
        public List<Question> question;
    }

    public class Question {
        public int id;
        public String  title,question_id,answer,type,image;
        public List<Options> options;
    }

}


