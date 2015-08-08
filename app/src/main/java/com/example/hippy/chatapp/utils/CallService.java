package com.example.hippy.chatapp.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hippy.chatapp.Activities.UserList;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class CallService extends Service implements SinchClientListener {

    private static final String APP_KEY = "59ecf280-0506-4d4b-bfa0-4b4ca98d1019";
    private static final String APP_SECRET = "qd4AsZncokSj+UNrSdD5TA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private final CallServiceInterface serviceInterface = new CallServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private String currentUser;
    private LocalBroadcastManager broadcaster;
    private Intent broadcastIntent = new Intent("com.example.hippy.chatapp.Acivities.UserList");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentUser = UserList.user.getUsername();

        if (currentUser != null && !isSinchClientStarted()) {
            startSinchClient(currentUser);
        }

        broadcaster = LocalBroadcastManager.getInstance(this);

        return super.onStartCommand(intent, flags, startId);
    }

    public void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(username)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.addSinchClientListener(this);

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.checkManifest();
        sinchClient.start();
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        broadcastIntent.putExtra("success", false);
        broadcaster.sendBroadcast(broadcastIntent);

        sinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        broadcastIntent.putExtra("success", true);
        broadcaster.sendBroadcast(broadcastIntent);

        client.startListeningOnActiveConnection();
        callClient = client.getCallClient();
    }

    @Override
    public void onClientStopped(SinchClient client) {
        sinchClient = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    @Override
    public void onLogMessage(int level, String area, String message) {
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
    }

    public void sendCall(String recipientUserId, String textBody) {
        if (callClient != null) {
            //something
        }
    }

    public void addCallClientListener(CallClientListener listener) {
        if (callClient != null) {
            callClient.addCallClientListener(listener);
        }
    }

    public void removeCallClientListener(CallClientListener listener) {
        if (callClient != null) {
            callClient.removeCallClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    public class CallServiceInterface extends Binder {
        public void sendCall(String recipientUserId, String textBody) {
            CallService.this.sendCall(recipientUserId, textBody);
        }

        public void addCallClientListener(CallClientListener listener) {
            CallService.this.addCallClientListener(listener);
        }

        public void removeCallClientListener(CallClientListener listener) {
            CallService.this.removeCallClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return CallService.this.isSinchClientStarted();
        }
    }
}

