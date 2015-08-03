package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
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
    private CheckBox chkRemember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Parse.initialize(this, "79sEAU8hFXbMAVU3LOVv71g2UtdF6d44t937SC12", "8qIzVeg5CFKzA8mfOeaxOZrnAUiv5w0SuMHMhFtY");

        setTouchNClick(R.id.btnLogin);
        setTouchNClick(R.id.btnReg);

        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPass = (EditText) findViewById(R.id.edtPass);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);

        loadSavedPreferences();
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
            final ProgressDialog dialog = ProgressDialog.show(this, null, "Wait.....");

            ParseUser.logInInBackground(user, pass, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    dialog.dismiss();
                    if (parseUser != null) {
                        UserList.user = parseUser;
                        if(chkRemember.isChecked()){
                            savePreferences("sp_user","sp_pass", user, pass);
                        }
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

    // =================== Shared Preferences Example ================
    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

		/*
		 * Lấy giá trị SharePreferences với key là sp_checkbox, nếu ko có thì
		 * mặc định là false
		 */
        //boolean checkBoxValue = sharedPreferences.getBoolean("sp_checkbox",false);

		/*
		 * Lấy giá trị SharePreferences với key là sp_name, nếu ko có thì mặc
		 * định là null
		 */
        String user = sharedPreferences.getString("sp_user", "");
        String pass = sharedPreferences.getString("sp_pass", "");

        if (user.length() == 0 || pass.length() == 0) {
            return;
        } else {
            ParseUser.logInInBackground(user, pass);
            startActivity(new Intent(Login.this, UserList.class));
            finish();
        }
    }

    // Lưu giá trị Checkbox
    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // Lưu giá trị Edittext
    private void savePreferences(String key, String key2, String value, String value2) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.putString(key2, value2);
        editor.commit();
    }
}
