package com.abhiandroid.quizgameapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Developed by AbhiAndroid.com
 */

@IgnoreExtraProperties
public class Leaderboard_User_Pojo {
    public String student_id,student_name,student_image,quiz_score,quiz_id,quiz_name,quiz_image;
       public int total_score;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Leaderboard_User_Pojo() {
    }

    public Leaderboard_User_Pojo(String student_name, String student_id,String student_image, String quiz_score,int total_score
              ,String quiz_id,String quiz_name,String quiz_image) {
        this.student_name = student_name;
        this.student_id = student_id;
        this.student_image = student_image;
        this.quiz_score = quiz_score;
        this.total_score = total_score;
        this.quiz_id = quiz_id;
        this.quiz_name = quiz_name;
        this.quiz_image = quiz_image;
    }
}
