package com.example.hippy.chatapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hippy.chatapp.Activities.UserList;
import com.example.hippy.chatapp.utils.Const;
import com.parse.ParseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.WritableMessage;

public class SinchService extends Service implements SinchClientListener {

    private Intent broadcastIntent = new Intent(Const.ACTION_SINCH_SERVICE);

    private final ServiceInterface serviceInterface = new ServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;
    private LocalBroadcastManager broadcaster;

    private CallClientListener callClientListener = new DefaultCallListener();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentUser = ParseUser.getCurrentUser().getUsername();
        if (currentUser != null && !isSinchClientStarted()) {
            startSinchClient(currentUser);
        }
        broadcaster = LocalBroadcastManager.getInstance(this);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(username)
                .applicationKey(Const.SINCH_APP_KEY)
                .applicationSecret(Const.SINCH_APP_SECRET)
                .environmentHost(Const.SINCH_ENVIRONMENT)
                .build();

        sinchClient.addSinchClientListener(this);

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.checkManifest();
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
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
        callClient.addCallClientListener(callClientListener);

        messageClient = client.getMessageClient();
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

    // Message
    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    //     Binder fof Service
    public class ServiceInterface extends Binder {


        public void sendMessage(String recipientUserId, String textBody) {
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public Call callUser(String recipientUserId) {
            return sinchClient.getCallClient().callUser(recipientUserId);
        }

        public Call getCall(String recipientUserId){
            return sinchClient.getCallClient().getCall(recipientUserId);
        }

        public MessageClient getMessageClient(){
            return messageClient;
        }

        public boolean isSinchClientStarted() {
            return SinchService.this.isSinchClientStarted();
        }
    }

    private class DefaultCallListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
//            Intent intent = new Intent(SinchService.this, CallScreen.class);
//            intent.putExtra(Const.SENDER, call.getCallId());
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            SinchService.this.startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        if (isSinchClientStarted()) {
            sinchClient.stopListeningOnActiveConnection();
            sinchClient.terminate();
        }
    }
}


