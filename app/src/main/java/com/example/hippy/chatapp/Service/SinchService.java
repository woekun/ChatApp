package com.example.hippy.chatapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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

public class SinchService extends Service {

    private Intent broadcastIntent = new Intent(Const.ACTION_SINCH_SERVICE);

    private final ServiceInterface serviceInterface = new ServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;

    private CallClientListener callClientListener = new DefaultCallListener();

    @Override
    public void onCreate() {
        super.onCreate();
        currentUser = ParseUser.getCurrentUser().getUsername();
        if (currentUser != null && !isSinchClientStarted()) {
            startSinchClient(currentUser);
        }
    }

    private void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(username)
                .applicationKey(Const.SINCH_APP_KEY)
                .applicationSecret(Const.SINCH_APP_SECRET)
                .environmentHost(Const.SINCH_ENVIRONMENT)
                .build();

        sinchClient.addSinchClientListener(new ClientListener());

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.checkManifest();
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
    }

    private class ClientListener implements SinchClientListener{

        @Override
        public void onClientStarted(SinchClient client) {
            broadcastIntent.putExtra("success", true);
            sendBroadcast(broadcastIntent);

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
        public void onClientFailed(SinchClient client, SinchError sinchError) {
            broadcastIntent.putExtra("success", false);
            sendBroadcast(broadcastIntent);
            sinchClient = null;
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

        }

        @Override
        public void onLogMessage(int i, String s, String s1) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        broadcastIntent.putExtra("success", true);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null)
            messageClient.send(new WritableMessage(recipientUserId, textBody));
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

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


