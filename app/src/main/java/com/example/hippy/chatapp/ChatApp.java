package com.example.hippy.chatapp;

import android.app.Application;

import com.example.hippy.chatapp.utils.Const;
import com.parse.Parse;

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, Const.USER_CODE, Const.APP_CODE);
    }
}
