package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.custom.UserAdapter;
import com.example.hippy.chatapp.utils.CallService;
import com.example.hippy.chatapp.utils.Const;
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


                            uList = new ArrayList(list);
                            ListView listView = (ListView) findViewById(R.id.list);
                            listView.setAdapter(new UserAdapter(UserList.this, uList));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    startActivity(new Intent(UserList.this, Chat.class)
                                            .putExtra(Const.EXTRA_DATA, uList.get(position)
                                                    .getUsername()));
                                }
                            });
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
        stopService(new Intent(this, CallService.class));
        super.onDestroy();
    }
}
