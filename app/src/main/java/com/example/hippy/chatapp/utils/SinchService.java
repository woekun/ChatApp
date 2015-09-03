package com.example.hippy.chatapp.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

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
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

public class SinchService extends Service implements SinchClientListener {

    private static final String APP_KEY = "59ecf280-0506-4d4b-bfa0-4b4ca98d1019";
    private static final String APP_SECRET = "qd4AsZncokSj+UNrSdD5TA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private Intent broadcastIntent = new Intent("com.example.hippy.chatapp.Activities.UserList");

    private final CallServiceInterface serviceInterface = new CallServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;
    private Call call;
    private LocalBroadcastManager broadcaster;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentUser = ParseUser.getCurrentUser().getUsername();

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
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

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

    // Call
    public void startCall(String recipientUserId) {
        if (callClient != null) {
            callClient.callUser(recipientUserId);
        }
    }

    public void endCall(String recipientUserId) {
        call = callClient.getCall(recipientUserId);
        if (call != null)
            call.hangup();
    }


    //Message
    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    // CallListener
    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
//            show custom dialog(accept or decline)
//            if accept ---> answer else hangup
//                call.answer();
        }
    }

    //    MessageListener
    public void addMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.removeMessageClientListener(listener);
        }
    }


    //     Binder fof CallService
    public class CallServiceInterface extends Binder {
        public void startCall(String recipientUserId) {
            SinchService.this.startCall(recipientUserId);
        }

        public void endCall(String recipientUserId) {
            SinchService.this.endCall(recipientUserId);
        }

        public boolean isSinchClientStarted() {
            return SinchService.this.isSinchClientStarted();
        }
    }

    //    Binder for MessageService
    public class MessageServiceInterface extends Binder {
        public void sendMessage(String recipientUserId, String textBody) {
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            SinchService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            SinchService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return SinchService.this.isSinchClientStarted();
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }
}


