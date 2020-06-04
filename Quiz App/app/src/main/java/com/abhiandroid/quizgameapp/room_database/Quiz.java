package com.abhiandroid.quizgameapp.room_database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;

import java.util.List;

/**
 * Developed by AbhiAndroid.com
 */

@Entity
public class Quiz {
    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "question_id")
    private String question_id;

    @ColumnInfo(name = "answer")
    private String answer;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "image")
    private String image;

    public byte[] getImage_array() {
        return image_array;
    }

    public void setImage_array(byte[] image_array) {
        this.image_array = image_array;
    }

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image_array;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @ColumnInfo(name = "options")
    public String options;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

}

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.

/**
 * Developed by AbhiAndroid.com
 */