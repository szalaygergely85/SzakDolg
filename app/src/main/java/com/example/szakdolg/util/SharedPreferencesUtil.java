package com.example.szakdolg.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

   private Context context;
   public SharedPreferencesUtil(Context context) {
      this.context = context;
   }

   private final String PREF_NAME = "MyAppPrefs";

   public String getStringPreference(String key) {
      SharedPreferences sharedPreferences = context.getSharedPreferences(
         PREF_NAME,
         MODE_PRIVATE
      );
      return sharedPreferences.getString(key, null);
   }

   public void setStringPreference(
      String key,
      String value
   ) {
      SharedPreferences sharedPreferences = context.getSharedPreferences(
         PREF_NAME,
         Context.MODE_PRIVATE
      );
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(key, value);
      editor.apply();
   }

   public void deleteStringPreference(String key) {
      SharedPreferences sharedPreferences = context.getSharedPreferences(
         PREF_NAME,
         Context.MODE_PRIVATE
      );
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.remove(key);
      editor.apply();
   }

   public long getLongPreference(String key) {
      SharedPreferences preferences = context.getSharedPreferences(
         PREF_NAME,
         Context.MODE_PRIVATE
      );
      return preferences.getLong(key, -1);
   }

   public void setLongPreference(
      String key,
      long value
   ) {
      SharedPreferences preferences = context.getSharedPreferences(
         PREF_NAME,
         Context.MODE_PRIVATE
      );
      SharedPreferences.Editor editor = preferences.edit();
      editor.putLong(key, value);
      editor.apply();
   }
}
