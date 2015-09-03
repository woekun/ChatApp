package com.example.hippy.chatapp.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.utils.SinchService;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Register extends CustomActivity {

    private EditText edtRUser;
    private EditText edtRPass;
    private EditText edtEmail;
    private Intent serviceIntent;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        setTouchNClick(R.id.btnReg);

        intent = new Intent(Register.this, UserList.class);
        serviceIntent = new Intent(getApplicationContext(), SinchService.class);

        edtRUser = (EditText) findViewById(R.id.user);
        edtRPass = (EditText) findViewById(R.id.pwd);
        edtEmail = (EditText) findViewById(R.id.email);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        final String user = edtRUser.getText().toString();
        final String pass = edtRPass.getText().toString();
        final String email = edtEmail.getText().toString();

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
//                    Helper.savePreferences("user", "pass", user, pass, Register.this);
                    startActivity(intent);
                    startService(serviceIntent);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(Register.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
