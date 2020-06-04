package com.abhiandroid.quizgameapp.utils;

/**
 * Developed by AbhiAndroid.com
 */

public class TimeTracker {

    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    public  void main(String[] args) {

        int seconds = 50391;

        System.out.println(timeConversion(seconds));

    }

    public   String timeConversion(int totalSeconds) {
        int hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        int minutes = (totalSeconds - (hoursToSeconds(hours)))
                / SECONDS_IN_A_MINUTE;
        int seconds = totalSeconds
                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

        //return hours + " hours " + minutes + " minutes " + seconds + " seconds";
        return  minutes + ":" + seconds ;
    }

    private  int hoursToSeconds(int hours) {
        return hours * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE;
    }

    private  int minutesToSeconds(int minutes) {
        return minutes * SECONDS_IN_A_MINUTE;
    }
}

/**
 * Developed by AbhiAndroid.com
 */