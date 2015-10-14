package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.Service.SinchService;
import com.example.hippy.chatapp.custom.BaseConnection;
import com.example.hippy.chatapp.custom.UserAdapter;
import com.example.hippy.chatapp.utils.Const;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserList extends BaseConnection {

    public static ParseUser user;
    private ArrayList<String> dataHeader; //data header
    private HashMap<String, List<String>> collections; // data child

    private BroadcastReceiver receiver = null;
    private ProgressDialog progressDialog;
    private ExpandableListView expandableListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        showDialog();

        collections = new HashMap<>();

        dataHeader = new ArrayList<>();
        dataHeader.add("Groups");
        dataHeader.add("Contacts");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        expandableListView = (ExpandableListView) findViewById(R.id.list);

        ParseUser.getQuery().whereEqualTo("username", user.getUsername())
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            List<String> listGroup = list.get(0).getList("Groups");
                            List<String> listContacts = list.get(0).getList("Contacts");

                            if (listContacts.size() > 0 || listContacts.size() > 0) {
                                if (listGroup.size() > 0)
                                    collections.put(dataHeader.get(0), listGroup);
                                if (listContacts.size() > 0)
                                    collections.put(dataHeader.get(1), listContacts);

                                expandableListView.setAdapter(new UserAdapter(UserList.this, dataHeader, collections));
                                expandableListView.expandGroup(0);
                                expandableListView.expandGroup(1);
                                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                    @Override
                                    public boolean onChildClick(ExpandableListView parent, View v,
                                                                int groupPosition, int childPosition, long id) {
                                        Intent intent = new Intent(getApplicationContext(), Chat.class);
                                        intent.putExtra(Const.EXTRA_DATA, collections.get(
                                                dataHeader.get(groupPosition)).get(childPosition));
                                        startActivity(intent);
                                        return false;
                                    }
                                });
                            } else
                                Toast.makeText(UserList.this, "U have no contacts!!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(UserList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //show a loading spinner while the sinch client starts
    private void showDialog() {
        progressDialog = ProgressDialog.show(this, "Loading...", "Please wait...");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Sinch service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Const.ACTION_SINCH_SERVICE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}