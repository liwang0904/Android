package com.abhiandroid.quizgameapp.room_database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Developed by AbhiAndroid.com
 */


public  class Options{

    public int option_id;

    public String id;

    public String question_id;

    public int getOption_id() {
        return option_id;
    }

    public void setOption_id(int option_id) {
        this.option_id = option_id;
    }


    public String optn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getOptn() {
        return optn;
    }

    public void setOptn(String optn) {
        this.optn = optn;
    }

    public String getOptn_name() {
        return optn_name;
    }

    public void setOptn_name(String optn_name) {
        this.optn_name = optn_name;
    }

    public String optn_name;
}

