package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.Service.SinchService;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class Login extends CustomActivity {

    private EditText edtUser;
    private EditText edtPass;
    private CheckBox chkRemember;
    private Intent intent;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setTouchNClick(R.id.btnLogin);
        setTouchNClick(R.id.btnReg);

        intent = new Intent(getApplicationContext(), UserList.class);
        serviceIntent = new Intent(getApplicationContext(), SinchService.class);

        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPass = (EditText) findViewById(R.id.edtPass);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            UserList.user = currentUser;
            startActivity(intent);
            startService(serviceIntent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.btnLogin) {
            final String user = edtUser.getText().toString();
            final String pass = edtPass.getText().toString();

            if (user.length() == 0 || pass.length() == 0) {
                Toast.makeText(Login.this, "Please fill all the fields. ", Toast.LENGTH_LONG).show();
                return;
            }
            final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...");

            ParseUser.logInInBackground(user, pass, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    dialog.dismiss();
                    if (parseUser != null) {
                        UserList.user = parseUser;
                        startActivity(intent);
                        startService(serviceIntent);
                        finish();
                    } else
                        Toast.makeText(Login.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (view.getId() == R.id.btnReg) {
            startActivityForResult(new Intent(this, Register.class), RESULT_OK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK)
            finish();
    }
}
