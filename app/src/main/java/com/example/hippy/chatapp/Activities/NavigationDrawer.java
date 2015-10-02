package com.example.hippy.chatapp.Activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.Service.SinchService;
import com.example.hippy.chatapp.SettingsActivity;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class NavigationDrawer extends CustomActivity{
    private DrawerLayout drawerLayout;

    @Override
    public void setContentView(int layoutResID) {

        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        LinearLayout linearLayout = (LinearLayout) drawerLayout.findViewById(R.id.customLayout);
        getLayoutInflater().inflate(layoutResID, linearLayout, true);

        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.navigation_view);

        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        super.setContentView(drawerLayout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setElevation(4);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navProfile:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(getApplicationContext(), Information.class);
                                startActivity(intent);
                                return true;
                            case R.id.navGroup:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.navSignout:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                ParseUser.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        stopService(new Intent(getApplicationContext(), SinchService.class));
                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                    }
                                });
                                return true;
                            case R.id.navSetting:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                                return true;
                            case R.id.navHelp:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}