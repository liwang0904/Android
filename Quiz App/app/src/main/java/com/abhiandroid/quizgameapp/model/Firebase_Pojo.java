package com.abhiandroid.quizgameapp.model;

import org.json.JSONObject;

/**
 * Developed by AbhiAndroid.com
 */

public class Firebase_Pojo extends JSONObject{

    public String  quiz_name;
    public String  quiz_id;
    public String  quiz_image;
    public String  student_id;
    public String  student_image;
    public String  student_name;
    public int  total_score;

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getQuiz_image() {
        return quiz_image;
    }

    public void setQuiz_image(String quiz_image) {
        this.quiz_image = quiz_image;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_image() {
        return student_image;
    }

    public void setStudent_image(String student_image) {
        this.student_image = student_image;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }

    public String getQuiz_name() {

        return quiz_name;
    }

    public void setQuiz_name(String quiz_name) {
        this.quiz_name = quiz_name;
    }

    public String getQuiz_score() {
        return quiz_score;
    }

    public void setQuiz_score(String quiz_score) {
        this.quiz_score = quiz_score;
    }

    public  String  quiz_score;

}


