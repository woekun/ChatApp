package com.example.hippy.chatapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.hippy.chatapp.Activities.UserList;
import com.parse.ParseUser;

public class Helper {

    public static void loadSavedPreferences(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        // get SharePreferences with key value is sp_name, if not there default is null

        String user = sharedPreferences.getString("sp_user", "");
        String pass = sharedPreferences.getString("sp_pass", "");

        if (user.length() != 0 || pass.length() != 0) {
            ParseUser.logInInBackground(user, pass);
            Intent intent = new Intent(activity.getApplicationContext(), UserList.class);
            Intent serviceIntent = new Intent(activity.getApplicationContext(), SinchService.class);
            activity.startActivity(intent);
            activity.startService(serviceIntent);
            activity.finish();
        }
    }

    // save valuable Checkbox
    public static void savePreferences(String key, boolean value, Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // save valuable Edittext
    public static void savePreferences(String key, String key2, String value, String value2, Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.putString(key2, value2);
        editor.commit();
    }
}
