package com.example.hippy.chatapp.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.utils.CallService;
import com.example.hippy.chatapp.utils.Const;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

public class Action_Call extends CustomActivity {

    private String buddy;
    private TextView callState;
    private CallService.CallServiceInterface callService;
    private ServiceConnection serviceConnection = new MyServiceConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        bindService(new Intent(this, CallService.class), serviceConnection, BIND_AUTO_CREATE);

        callState = (TextView) findViewById(R.id.callState);
        setTouchNClick(R.id.btnCall);

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        callService.startCall(buddy);

    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            callService = (CallService.CallServiceInterface) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            callService = null;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.btnCall) {
            callService.endCall(buddy);
        }
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}




