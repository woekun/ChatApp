package com.example.hippy.chatapp.custom;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.utils.TouchEffect;

public class CustomActivity extends AppCompatActivity implements View.OnClickListener {

    public static final TouchEffect TOUCH = new TouchEffect();
    Toolbar toolbar;
    ActionBar actionBar;


    @Override
    public void setContentView(int layoutResID) {
        setupActionBar();
        super.setContentView(layoutResID);
    }

    protected void setupActionBar() {
        actionBar = getSupportActionBar();
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
