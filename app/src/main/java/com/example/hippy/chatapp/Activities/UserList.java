package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.UserAdapter;
import com.example.hippy.chatapp.utils.SinchService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends NavigationDrawer {

    public static ParseUser user;
    private ArrayList<ParseUser> uList;
    private BroadcastReceiver receiver = null;
    private ProgressDialog progressDialog;
    private RecyclerView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.user_list);
        showSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listView = (RecyclerView) findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(UserList.this));
        loadContacts();


    }

    private void loadContacts() {

        ParseUser.getQuery().whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername())
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {

                        if (list != null) {
                            if (list.size() == 0)
                                Toast.makeText(UserList.this, "No user found!!", Toast.LENGTH_SHORT).show();


                            uList = new ArrayList<>(list);
                            listView.setAdapter(new UserAdapter(UserList.this, uList));


                        } else {
                            Toast.makeText(UserList.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Call service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.example.hippy.chatapp.Activities.UserList"));
    }



    @Override
    public void onDestroy() {
        stopService(new Intent(this, SinchService.class));
        super.onDestroy();
    }
}