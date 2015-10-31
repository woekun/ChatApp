package com.example.hippy.chatapp.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.hippy.chatapp.Activities.Chat;
import com.example.hippy.chatapp.utils.Const;

import java.util.Date;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String sender_messager = intent.getStringExtra("sender_message");
        String sender_calling = intent.getStringExtra("sender_calling");
        Date time = new Date(intent.getLongExtra("time",-1));
        String message = intent.getStringExtra("message");

        switch (intent.getAction()){
            case Const.RECEIVER_CALL :
//                Intent callScreen = new Intent(context,CallingScreen.class);
//                callScreen.putExtra("sender",sender_calling);
//                context.startActivity(callScreen);
                break;

            case Const.RECEIVER_MESSAGE:
                if(Chat.isRunning && Chat.getInstance().getBuddy().equals(sender_messager))
                    Chat.getInstance().onIncommingMessage(message,time,sender_messager);
                else
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
