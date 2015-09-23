package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import android.view.View;

import android.widget.ExpandableListView;

import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.UserAdapter;
import com.example.hippy.chatapp.utils.Const;
import com.example.hippy.chatapp.utils.SinchService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserList extends NavigationDrawer {

    public static ParseUser user;
    private ArrayList<String> conList;
    private ArrayList<String> dataHeader; //data header
    private HashMap<String, List<String>> collections; // data child

    private BroadcastReceiver receiver = null;
    private ProgressDialog progressDialog;
    private ExpandableListView expandableListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        showSpinner();

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

        ParseUser.getQuery().whereEqualTo("username", ParseUser.getCurrentUser().getUsername())
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {

                        if (list != null) {
                            if (list.size() == 0)
                                Toast.makeText(UserList.this, "No user found!!", Toast.LENGTH_SHORT).show();

                            final ArrayList<String> listGroup =(ArrayList<String>) list.get(0).get("Groups");
                            final ArrayList<String> listContacts =(ArrayList<String>) list.get(0).get("Contacts");

                            collections.put(dataHeader.get(0), listGroup);
                            collections.put(dataHeader.get(1), listContacts);
                            expandableListView.setAdapter(new UserAdapter(UserList.this, dataHeader, collections));

                            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                    Intent intent = new Intent(getApplicationContext(), Chat.class);
                                    intent.putExtra(Const.EXTRA_DATA, collections.get(dataHeader.get(groupPosition)).get(childPosition));
                                    startActivity(intent);
                                    return false;
                                }
                            });
                        } else Toast.makeText(UserList.this, e.toString(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Sinch service failed to start", Toast.LENGTH_LONG).show();
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