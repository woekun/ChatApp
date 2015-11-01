package com.example.hippy.chatapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.hippy.chatapp.Activities.Chat;
import com.example.hippy.chatapp.utils.Const;
import com.parse.ParseUser;
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
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

public class SinchService extends Service {

    private Intent broadcastIntent = new Intent(Const.ACTION_SINCH_SERVICE);

    private final ServiceInterface serviceInterface = new ServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;

    private CallClientListener callClientListener = new DefaultCallListener();
    private MessageClientListener messageClientListener = new DefaultMessageListener();

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
            messageClient.addMessageClientListener(messageClientListener);
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
        broadcastIntent.putExtra("success", true);
        sendBroadcast(broadcastIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        currentUser = ParseUser.getCurrentUser().getUsername();
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

        public VideoController getVideoController() {
            return sinchClient.getVideoController();
        }
    }

    private class DefaultCallListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Intent intent = new Intent(Const.RECEIVER_CALL);
            intent.putExtra("sender_calling", call.getRemoteUserId());
            sendBroadcast(intent);
        }
    }

    private class DefaultMessageListener implements MessageClientListener{
        @Override
        public void onIncomingMessage(MessageClient messageClient, Message message) {
            Intent intent = new Intent(Const.RECEIVER_MESSAGE);
            String s = message.getSenderId();
            intent.putExtra("sender_message", s);
            intent.putExtra("message", message.getTextBody());
            intent.putExtra("time", message.getTimestamp());
            sendBroadcast(intent);
        }

        @Override
        public void onMessageSent(MessageClient messageClient, Message message, String s) {
            if(Chat.getInstance()!=null && Chat.isRunning)
                Chat.getInstance().onMessageSent(message.getTextBody(), message.getTimestamp(),
                        message.getRecipientIds().get(0));
        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
            Chat.getInstance().onMessageFailed(messageFailureInfo.toString());
        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

        }
    }

    @Override
    public void onDestroy() {
        if (isSinchClientStarted()) {
            sinchClient.terminateGracefully();
        }
        super.onDestroy();
    }


}


