package com.example.szakdolg.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static final String PREF_NAME = "MyAppPrefs";
    public static String getStringPreference(Context context, String key){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void setStringPreference(Context context, String key, String value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void deleteStringPreference(Context context, String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
