package com.example.hippy.chatapp.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;


public class Chat extends NavigationDrawer {

    private ChatAdapter chatAdapter;
    private EditText edtMess;
    private String buddy;
    private boolean isRunning;
    private ListView list_chat;
    private String currentUser;

    private SinchService.ServiceInterface messageService;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MessageClientListener messageClientListener = new MyMessageClientListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        bindService(new Intent(this, SinchService.class), serviceConnection, BIND_AUTO_CREATE);

        currentUser = UserList.user.getUsername();
        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(buddy);

        list_chat = (ListView) findViewById(R.id.list_chat);
        chatAdapter = new ChatAdapter(Chat.this);
        list_chat.setAdapter(chatAdapter);
        list_chat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list_chat.setStackFromBottom(true);
        loadConversation();

        setTouchNClick(R.id.btnSend);

        edtMess = (EditText) findViewById(R.id.edtMessage);
        edtMess.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

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

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtMess.getWindowToken(), 0);

        String mess = edtMess.getText().toString();
        messageService.sendMessage(buddy, mess);
        edtMess.setText(null);

    }

    private void loadConversation() {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Chat");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(buddy);

        arrayList.add(UserList.user.getUsername());
        parseQuery.whereContainedIn("sender", arrayList);
        parseQuery.whereContainedIn("receiver", arrayList);
        parseQuery.orderByDescending("createdAt");
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
                        chatAdapter.addMessage(conversation);
                    }
                }
            }
        });
    }


    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = (SinchService.ServiceInterface) iBinder;
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
            if (message.getSenderId().equals(buddy)) {
                chatAdapter.addMessage(new Conversation(message.getTextBody(), message.getTimestamp(), buddy));
            }
        }

        @Override
        public void onMessageSent(MessageClient messageClient, Message message, String recipientId) {
            ParseObject parseObject = new ParseObject("Chat");
            parseObject.put("sender", currentUser);
            parseObject.put("receiver", recipientId);
            parseObject.put("message", message.getTextBody());
            parseObject.saveInBackground();

            chatAdapter.addMessage(new Conversation(message.getTextBody(), message.getTimestamp(), currentUser));
        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
            Toast.makeText(Chat.this, messageFailureInfo.getSinchError().getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

        }
    }

}
