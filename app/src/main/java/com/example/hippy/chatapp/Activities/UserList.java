package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.custom.UserAdapter;
import com.example.hippy.chatapp.utils.Const;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends CustomActivity {

    public static ParseUser user;
    private ArrayList<ParseUser> uList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        //getActionBar().setDisplayHomeAsUpEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadContacts() {
        final ProgressDialog dia = ProgressDialog.show(this, null, "loading...");

        ParseUser.getQuery().whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername())
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        dia.dismiss();
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
}
