package com.example.hippy.chatapp.custom;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.hippy.chatapp.utils.TouchEffect;

public class CustomActivity extends FragmentActivity implements View.OnClickListener {

    public static final TouchEffect TOUCH = new TouchEffect();


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupActionBar();
    }

    protected void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null)
            return;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
