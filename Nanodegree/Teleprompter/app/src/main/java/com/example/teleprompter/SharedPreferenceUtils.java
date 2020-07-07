package com.example.teleprompter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

public class SharedPreferenceUtils {
    public static void invalidateUserDetails(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(context.getString(R.string.username_key));
        editor.remove(context.getString(R.string.email_key));
        editor.remove(context.getString(R.string.uid_key));
        editor.remove(context.getString(R.string.last_id_key));
        editor.remove(context.getString(R.string.last_cloudid_key));
        editor.remove(context.getString(R.string.pinned_document_key));
        editor.apply();
    }

    public static int getDefaultBackgroundColor(Context context) {
        String key = context.getString(R.string.background_color_key);
        int defaultValue = Color.BLACK;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, defaultValue);
    }

    public static void setDefaultBackgroundColor(Context context, int color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.background_color_key), color);
        editor.apply();
        WidgetService.updateWidget(context);
    }

    public static int getDefaultTextColor(Context context) {
        String key = context.getString(R.string.text_color_key);
        int defaultValue = Color.WHITE;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, defaultValue);
    }

    public static void setDefaultTextColor(Context context, int color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.text_color_key), color);
        editor.apply();
        WidgetService.updateWidget(context);
    }

    public static String getUsername(Context context) {
        String defaultValue = "User";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.username_key), defaultValue);
    }

    public static void setUsername(Context context, String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.username_key), username);
        editor.apply();
    }

    public static String getEmail(Context context) {
        String defaultValue = "User";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.email_key), defaultValue);
    }

    public static void setEmail(Context context, String email) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.email_key), email);
        editor.apply();
    }

    public static int getDefaultFontSize(Context context) {
        String defaultValue = context.getString(R.string.pref_font_size_medium);
        String fontSizeKey = context.getString(R.string.pref_font_size_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = preferences.getString(fontSizeKey, defaultValue);
        return Integer.parseInt(fontSize);
    }

    public static int getDefaultScrollSpeed(Context context) {
        String defaultValue = context.getString(R.string.pref_speed_value_2);
        String speedKey = context.getString(R.string.pref_speed_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = preferences.getString(speedKey, defaultValue);
        return Integer.parseInt(fontSize);
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.uid_key), "-1");
    }

    public static void setUserId(Context context, String uid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.uid_key), uid);
        editor.apply();
    }

    public static int getLastStoredId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(context.getString(R.string.last_id_key), -1);
    }

    public static void setLastStoredId(Context context, int id) {
        String key = context.getString(R.string.last_id_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, id);
        editor.apply();
    }

    public static String getLastStoredCloudId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.last_cloudid_key), "-1");
    }

    public static void setLastStoredCloudId(Context context, String id) {
        String key = context.getString(R.string.last_cloudid_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, id);
        editor.apply();
    }

    public static int getPinnedId(Context context) {
        int defaultValue = -1;
        String key = context.getString(R.string.pinned_document_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, defaultValue);
    }

    public static void setPinnedId(Context context, int id) {
        String key = context.getString(R.string.pinned_document_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, id);
        editor.apply();
    }
}