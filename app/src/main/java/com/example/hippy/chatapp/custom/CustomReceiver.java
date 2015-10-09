package com.example.hippy.chatapp.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.hippy.chatapp.Activities.Chat;
import com.example.hippy.chatapp.utils.Const;

public class CustomReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String name, type;
        switch (intent.getAction()) {
            case Const.ACTION_SINCH_CALL:
                name = intent.getStringExtra(Const.SENDER);
                type = intent.getStringExtra(Const.TYPE);
                if(!Chat.isRunning || !Chat.getBuddy().equals(name))
                    Toast.makeText(context,name+" is "+ type,Toast.LENGTH_LONG).show();
                else
                    Chat.instance().setCallInterface(View.VISIBLE);
                break;

            case Const.ACTION_SINCH_MESSAGE:
                name = intent.getStringExtra(Const.SENDER);
                type = intent.getStringExtra(Const.TYPE);
                if(!Chat.isRunning || !Chat.getBuddy().equals(name))
                    Toast.makeText(context,name+" is "+ type,Toast.LENGTH_LONG).show();

                break;
        }
    }
}
