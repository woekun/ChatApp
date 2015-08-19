package com.example.hippy.chatapp.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.ChatAdapter;
import com.example.hippy.chatapp.models.Conversation;
import com.example.hippy.chatapp.utils.Const;
import com.example.hippy.chatapp.utils.SinchService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat extends NavigationDrawer {

    private static Handler handler;
    private ArrayList<Conversation> convList;
    private ChatAdapter chatAdapter;
    private EditText edtMess;
    private String buddy;
    private Date lastMsgDate;
    private boolean isRunning;
    private ListView list_chat;

    private SinchService.MessageServiceInterface messageService;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MessageClientListener messageClientListener = new MyMessageClientListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        bindService(new Intent(this, SinchService.class), serviceConnection, BIND_AUTO_CREATE);

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        //getSupportActionBar().setTitle(buddy);

        convList = new ArrayList<>();
        list_chat = (ListView) findViewById(R.id.list_chat);
        chatAdapter = new ChatAdapter(Chat.this, convList);
        list_chat.setAdapter(chatAdapter);
        list_chat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list_chat.setStackFromBottom(true);

        setTouchNClick(R.id.btnSend);

        edtMess = (EditText) findViewById(R.id.edtMessage);
        edtMess.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        loadConversation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        messageService.removeMessageClientListener(messageClientListener);
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btnSend) {
            sendMessages();
        }
    }

    private void sendMessages() {
        if (edtMess.length() == 0)
            return;
        //
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtMess.getWindowToken(), 0);

        String mess = edtMess.getText().toString();
        final Conversation conversation = new Conversation(mess, new Date(), UserList.user.getUsername());
        convList.add(conversation);
        chatAdapter.notifyDataSetChanged();
        edtMess.setText(null);

        ParseObject parseObject = new ParseObject("Chat");
        parseObject.put("sender", UserList.user.getUsername());
        parseObject.put("receiver", buddy);
        parseObject.put("message", mess);

        parseObject.saveEventually();

    }

    private void loadConversation() {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Chat");
        if (convList.size() == 0) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(buddy);

            arrayList.add(UserList.user.getUsername());
            parseQuery.whereContainedIn("sender", arrayList);
            parseQuery.whereContainedIn("receiver", arrayList);
        } else {
            if (lastMsgDate != null)
                parseQuery.whereGreaterThan("createdAt", lastMsgDate);

            parseQuery.whereEqualTo("sender", buddy);
            parseQuery.whereEqualTo("receiver", UserList.user.getUsername());
        }

        parseQuery.orderByDescending("createdAt");
        parseQuery.setLimit(30);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (int i = list.size() - 1; i >= 0; i--) {
                        ParseObject parseObject = list.get(i);
                        Conversation conversation = new Conversation(
                                parseObject.getString("message"),
                                parseObject.getCreatedAt(),
                                parseObject.getString("sender"));
                        convList.add(conversation);

                        if (lastMsgDate == null || lastMsgDate.before(conversation.getDate()))
                            lastMsgDate = conversation.getDate();

                        chatAdapter.notifyDataSetChanged();
                    }
                }
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (isRunning)
                            loadConversation();
                    }
                }, 1000);
            }
        });
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = (SinchService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {

        @Override
        public void onIncomingMessage(MessageClient messageClient, Message message) {

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

}
