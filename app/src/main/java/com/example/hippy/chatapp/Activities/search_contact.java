package com.example.hippy.chatapp.Activities;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.hippy.chatapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class search_contact extends NavigationDrawer {

    AutoCompleteTextView actSearch;
    ArrayList<String> contactsList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

        contactsList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item);
        actSearch = (AutoCompleteTextView) findViewById(R.id.actSearch);
        actSearch.setThreshold(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ParseUser.getQuery()
                .whereNotEqualTo("username", UserList.user.getUsername())
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (list != null && list.size() > 0) {
                            for (ParseUser pu : list)
                                contactsList.add(pu.getUsername());

                            arrayAdapter.addAll(contactsList);
                            actSearch.setAdapter(arrayAdapter);

                        }
                    }
                });

    }
}
