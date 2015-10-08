package com.example.hippy.chatapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

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
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;

public class SinchService extends Service implements SinchClientListener {

    private Intent broadcastIntent = new Intent(Const.ACTION_SINCH_SERVICE);

    private final ServiceInterface serviceInterface = new ServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;
    private LocalBroadcastManager broadcaster;

    private MessageClientListener messListener = new DefaultMessageListener();
    private CallClientListener callClientListener = new DefaultCallListener();

    private CallListener callListener = new MyCallListener();

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

    public CallClient getCallClient() {
        return callClient;
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
        callClient.addCallClientListener(callClientListener);

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
        if (call != null) {
            call.answer();
        }
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
            call.addCallListener(callListener);
            Chat.instance().setCall(call);
            Intent intent = new Intent(Const.ACTION_SINCH_CALL);
            String s = call.getRemoteUserId();
            intent.putExtra(Const.SENDER, s);
            intent.putExtra(Const.TYPE, "calling");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    private class MyCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onCallEstablished(Call call) {
//            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallEnded(Call endedCall) {
            Chat.instance().setCall(endedCall);
//            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            Chat.instance().setCallInterface(View.INVISIBLE);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }



}


