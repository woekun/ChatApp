package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        getActionBar().setDisplayHomeAsUpEnabled(false);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
