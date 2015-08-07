package com.example.hippy.chatapp.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.utils.CallService;
import com.example.hippy.chatapp.utils.Const;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

public class Action_Call extends CustomActivity {

    private Call call;
    private String buddy;
    private String user;
    private SinchClient sinchClient;
    private Button btnCall;
    private TextView callState;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        btnCall = (Button) findViewById(R.id.btnCall);
        callState = (TextView) findViewById(R.id.callState);

        setTouchNClick(R.id.btnCall);

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        user = UserList.user.getUsername();

        showSpinner();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.btnCall) {
            if (call != null) {
                call.hangup();
                finish();
            }
        }

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, CallService.class));
        super.onDestroy();
    }

    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(".Activities.Action_Call"));
    }
}




