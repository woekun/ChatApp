package com.example.hippy.chatapp.custom;

import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.example.hippy.chatapp.utils.TouchEffect;

public class CustomActivity extends ActionBarActivity implements View.OnClickListener {

    public static final TouchEffect TOUCH = new TouchEffect();


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupActionBar();
    }

    protected void setupActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        //actionBar.setLogo();
        //actionBar.setBackgroundDrawable(getResources().getDrawable());

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public View setTouchNClick(int id) {

        View v = setClick(id);
        if (v != null)
            v.setOnTouchListener(TOUCH);
        return v;
    }

    public View setClick(int id) {

        View v = findViewById(id);
        if (v != null)
            v.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {

    }
}
