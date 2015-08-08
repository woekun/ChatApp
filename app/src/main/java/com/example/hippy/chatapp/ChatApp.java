package com.example.hippy.chatapp;

import android.app.Application;

import com.parse.Parse;

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "79sEAU8hFXbMAVU3LOVv71g2UtdF6d44t937SC12", "8qIzVeg5CFKzA8mfOeaxOZrnAUiv5w0SuMHMhFtY");
    }
}
