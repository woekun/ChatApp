package com.example.hippy.chatapp.Activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.CustomActivity;
import com.example.hippy.chatapp.utils.Const;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class Action_Call extends CustomActivity {

    private Call call;
    private String buddy;
    private String user;
    private SinchClient sinchClient;
    private Button btnCall;
    private TextView callState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        btnCall = (Button) findViewById(R.id.btnCall);
        callState = (TextView) findViewById(R.id.callState);

        setTouchNClick(R.id.btnCall);

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        user = UserList.user.getUsername();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(user)
                .applicationKey("59ecf280-0506-4d4b-bfa0-4b4ca98d1019")
                .applicationSecret("qd4AsZncokSj+UNrSdD5TA==")
                .environmentHost("sandbox.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.btnCall) {
            if (call == null) {
                call = sinchClient.getCallClient().callUser(buddy);
                call.addCallListener(new SinchCallListener());
                btnCall.setText("HANG UP");
            } else {
                call.hangup();
            }
        }

    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            btnCall.setText("CALL");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            //startActivity(new Intent(Action_Call.this, Chat.class));
            //finish();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            call.answer();
            call.addCallListener(new SinchCallListener());
            btnCall.setText("HANG UP");
        }

    }


}




