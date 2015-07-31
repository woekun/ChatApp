package com.example.hippy.chatapp.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Register extends CustomActivity {

    private EditText edtRUser;
    private EditText edtRPass;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        setTouchNClick(R.id.btnReg_OK);

        edtRUser = (EditText) findViewById(R.id.edtRUser);
        edtRPass = (EditText) findViewById(R.id.edtRPass);
        edtEmail = (EditText) findViewById(R.id.edtREmail);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        String user = edtRUser.getText().toString();
        String pass = edtRPass.getText().toString();
        String email = edtEmail.getText().toString();

        if (user.length() == 0 || pass.length() == 0 || email.length() == 0) {
            Toast.makeText(Register.this, "Please fill all the fields. ", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "wait....");

        final ParseUser parseUser = new ParseUser();
        parseUser.setEmail(email);
        parseUser.setUsername(user);
        parseUser.setPassword(pass);

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    UserList.user = parseUser;
                    startActivity(new Intent(Register.this, UserList.class));
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
