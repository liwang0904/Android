package com.abhiandroid.quizgameapp.room_database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Developed by AbhiAndroid.com
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM Quiz")
    List<Quiz> getAll();

    @Query("SELECT * FROM Quiz WHERE uid IN (:userIds)")
    List<Quiz> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Quiz WHERE title LIKE :first AND "
            + "answer LIKE :last LIMIT 1")
    Quiz findByName(String first, String last);

    @Insert
    void insertAll(Quiz... quizzes);

    @Delete
    void delete(Quiz quiz);


    @Query("DELETE FROM Quiz")
    void delete();
}
