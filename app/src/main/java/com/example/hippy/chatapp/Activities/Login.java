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
import com.example.hippy.chatapp.utils.Const;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class Login extends CustomActivity {

    private EditText edtUser;
    private EditText edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Parse.initialize(this, "79sEAU8hFXbMAVU3LOVv71g2UtdF6d44t937SC12", "8qIzVeg5CFKzA8mfOeaxOZrnAUiv5w0SuMHMhFtY");

        setTouchNClick(R.id.btnLogin);
        setTouchNClick(R.id.btnReg);

        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPass = (EditText) findViewById(R.id.edtPass);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.btnLogin) {
            String user = edtUser.getText().toString();
            String pass = edtPass.getText().toString();

            if (user.length() == 0 || pass.length() == 0) {
                Toast.makeText(Login.this, "Please fill all the fields. ", Toast.LENGTH_LONG).show();
                return;
            }
            final ProgressDialog dialog = ProgressDialog.show(this, null, "Wait.....");

            ParseUser.logInInBackground(user, pass, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    dialog.dismiss();
                    if (parseUser != null) {
                        UserList.user = parseUser;
                        startActivity(new Intent(Login.this, UserList.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, e.toString(), Toast.LENGTH_LONG);
                    }
                }
            });
        } else {
            startActivityForResult(new Intent(this, Register.class), Const.REQUEST_CODE);
        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CODE && requestCode == RESULT_OK)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
