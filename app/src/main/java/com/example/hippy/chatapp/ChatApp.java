package com.example.hippy.chatapp;

import android.app.Application;

import com.parse.Parse;

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "EqJPz7hBmv0VIV42otb71Uvi51oH3zKidng0ZnNo", "glJmZyqdDmeAhNQ8axbClNUt3oQCiK45fKWzibHz");
    }
}
