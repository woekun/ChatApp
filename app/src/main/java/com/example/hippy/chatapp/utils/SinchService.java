package com.example.hippy.chatapp.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hippy.chatapp.Activities.UserList;
import com.example.hippy.chatapp.R;
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

    private final ServiceInterface serviceInterface = new ServiceInterface();
    private SinchClient sinchClient = null;
    private CallClient callClient = null;
    private MessageClient messageClient = null;
    private String currentUser;
    private Call call;
    private LocalBroadcastManager broadcaster;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private WindowManager windowManager;
    private View chatHead;
    private TextView textView;
    private WindowManager.LayoutParams params;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentUser = UserList.user.getUsername();

        if (currentUser != null && !isSinchClientStarted()) {
            startSinchClient(currentUser);
        }

        broadcaster = LocalBroadcastManager.getInstance(this);
//        Notifications.createChatHead(this);
        createChatHead();

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

//        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        sinchClient.checkManifest();
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

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

//        public void addView(String mess) {
//            SinchService.this.setChatHead(mess);
//        }

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

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }



    public void setChatHead(String mess){
        chatHead = LayoutInflater.from(this).inflate(R.layout.floating_notification, null);
        textView = (TextView) chatHead.findViewById(R.id.noti_mess);
        textView.setText(mess);

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return false;
            }
        });

        chatHead.findViewById(R.id.noti_ava)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        windowManager.removeView(chatHead);
                    }
                });



        windowManager.addView(chatHead,params);

    }
    public void createChatHead() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

    }
}


