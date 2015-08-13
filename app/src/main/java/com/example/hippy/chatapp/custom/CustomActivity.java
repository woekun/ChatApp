package com.example.hippy.chatapp.custom;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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

        TypedValue colorDarkPrimary = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, colorDarkPrimary, true);

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(colorDarkPrimary.data);
        }


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
