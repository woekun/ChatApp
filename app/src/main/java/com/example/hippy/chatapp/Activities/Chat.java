package com.example.hippy.chatapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.custom.BaseConnection;
import com.example.hippy.chatapp.custom.ChatAdapter;
import com.example.hippy.chatapp.models.Conversation;
import com.example.hippy.chatapp.utils.Const;
import com.example.hippy.chatapp.utils.FileChooser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Chat extends BaseConnection {

    public static boolean isRunning;

    private static Chat inst;
    private ChatAdapter chatAdapter;
    private EditText edtMess;
    private String buddy;
    private ListView list_chat;
    private String currentUser;
    private Call call;


    public static Chat getInstance() {
        return inst;
    }

    public String getBuddy() {
        return buddy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        currentUser = UserList.user.getUsername();
        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
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
    protected void onServiceConnected() {
//        MessageClient messageClient= getSinchServiceInterface().getMessageClient();
//        messageClient.addMessageClientListener(new MyMessageClientListener());
    }

    @Override
    protected void onServiceDisconnected() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        inst = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btnSend) {
            sendMessages();
//            getSinchServiceInterface().callUser(buddy);

        }
    }

    private void sendMessages() {
        if (edtMess.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtMess.getWindowToken(), 0);

        String mess = edtMess.getText().toString();
        getSinchServiceInterface().sendMessage(buddy, mess);
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

    Dialog dialog;
    String path;

    public void OpenDrawing(View view){
        Intent intent = new Intent(Chat.this,search_contact.class);
        startActivity(intent);
    }

    public void onbtnSendFileClicked(View view){
        final FileChooser fileChooser = new FileChooser(this);
        dialog = fileChooser.getDialog();

        fileChooser.setActionAfterChoose(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Upload file code here.
                path = fileChooser.getPath();
                EditText editText = (EditText) dialog.findViewById(R.id.editText);
                File file = new File(path);
                Toast.makeText(Chat.this, "Uploaded", Toast.LENGTH_SHORT).show();
                byte[] data = new byte[(int) file.length()];
                FileInputStream fis;

                try {
                    fis = new FileInputStream(file);
                    fis.read(data);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ParseFile parseFile = new ParseFile(file.getName(), data);
                parseFile.saveInBackground();

                ParseObject parseObject = new ParseObject("fileupload");
                parseObject.put("FileName", "abc");
                parseObject.put("File", parseFile);
                parseObject.saveInBackground();

                dialog.dismiss();
            }


        });
    }

    public void onIncommingMessage(String message, Date time, String sender){
        chatAdapter.addMessage(new Conversation(message, time, sender));
    }

    public void onMessageSent(String message, Date time, String recipientId){
        ParseObject parseObject = new ParseObject("Chat");
        parseObject.put("sender", currentUser);
        parseObject.put("receiver", recipientId);
        parseObject.put("message",message);
        parseObject.saveInBackground();

        chatAdapter.addMessage(new Conversation(message, time, currentUser));
    }

    public void onMessageFailed(String messageFailureInfo) {
        Toast.makeText(Chat.this, messageFailureInfo, Toast.LENGTH_LONG).show();
    }

    public void onMessageDelivered() {

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
