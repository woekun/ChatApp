package com.example.hippy.chatapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hippy.chatapp.Activities.Chat;
import com.example.hippy.chatapp.Activities.UserList;
import com.example.hippy.chatapp.utils.Const;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;

public class SinchService extends Service implements SinchClientListener {

    private static final String APP_KEY = "59ecf280-0506-4d4b-bfa0-4b4ca98d1019";
    private static final String APP_SECRET = "qd4AsZncokSj+UNrSdD5TA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private Intent broadcastIntent = new Intent("com.example.hippy.chatapp.Activities.UserList");

    private final ServiceInterface serviceInterface = new ServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;
    private LocalBroadcastManager broadcaster;

    private MessageClientListener messListener = new DefaultMessageListener();
    private CallClientListener callListener = new DefaultCallListener();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentUser = UserList.user.getUsername();
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
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.addSinchClientListener(this);

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.checkManifest();
        sinchClient.startListeningOnActiveConnection();
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
        callClient.addCallClientListener(callListener);

        messageClient = client.getMessageClient();
        messageClient.addMessageClientListener(messListener);
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
        if (callClient != null)
            callClient.callUser(recipientUserId);
    }

    public void answerCall(String recipientUserId, Call call) {
        if (call != null)
            call.answer();
    }

    public void endCall(String recipientUserId, Call call) {
        if (call != null) {
            call.hangup();
        }
    }

    // Message
    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    // MessageListener
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

    // CallListener
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

    //     Binder fof Service
    public class ServiceInterface extends Binder {
        public void sendMessage(String recipientUserId, String textBody) {
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            SinchService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            SinchService.this.removeMessageClientListener(listener);
        }

        public void addCalleClientListener(CallClientListener listener) {
            SinchService.this.addCallClientListener(listener);
        }

        public void removeCallClientListener(CallClientListener listener) {
            SinchService.this.removeCallClientListener(listener);
        }

        public void startCall(String recipientUserId) {
            SinchService.this.startCall(recipientUserId);
        }

        public void answerCall(String recipientUserId, Call call) {
            SinchService.this.answerCall(recipientUserId, call);
        }

        public void endCall(String recipientUserId, Call call) {
            SinchService.this.endCall(recipientUserId, call);
        }

        public boolean isSinchClientStarted() {
            return SinchService.this.isSinchClientStarted();
        }
    }

    // Default Listener

    private class DefaultMessageListener implements MessageClientListener {
        @Override
        public void onIncomingMessage(MessageClient messageClient, Message message) {
            if (!Chat.isRunning) {
                //TODO: show chathead
                Intent intent = new Intent(SinchService.this, Chat.class);
                intent.putExtra(Const.EXTRA_DATA, message.getSenderId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        public void onMessageSent(MessageClient messageClient, Message message, String s) {

        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {

        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

        }

    }

    private class DefaultCallListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            if (!Chat.isRunning) {
                Intent intent = new Intent(SinchService.this, Chat.class);
                intent.putExtra(Const.EXTRA_DATA, call.getRemoteUserId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }



}


