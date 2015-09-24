package com.example.hippy.chatapp.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Information extends NavigationDrawer {

    ImageView imgView;
    TextView txtName, txtNick, txtSdt;
    EditText edtEmail, edtOldPw, edtNewPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        imgView = (ImageView) findViewById(R.id.imgAvatar);
        txtName = (TextView) findViewById(R.id.txtName);
        txtNick = (TextView) findViewById(R.id.txtNick);
        txtSdt = (TextView) findViewById(R.id.txtSdt);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtOldPw = (EditText) findViewById(R.id.edtOldPw);
        edtNewPw = (EditText) findViewById(R.id.edtNewPw);

        loadInformation(UserList.user.getUsername());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_information, menu);
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

    public void loadInformation(String username){
        //Create query (username) -> ParseUser
        ParseQuery<ParseUser> query  = ParseQuery.getQuery("User");
        //query.whereEqualTo("username", "1");
        //OK -> Info
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                //Show

                txtName.setText(parseUser.getUsername());
                txtNick.setText(parseUser.getString("nickname"));
                txtSdt.setText(parseUser.getString("phonenumber"));

                edtEmail.setText(parseUser.getString("email"));
            }
        });


    }
}
