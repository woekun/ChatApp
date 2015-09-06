package com.example.hippy.chatapp.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;

public class Notifications {
//    private static int initialX;
//    private static int initialY;
//    private static float initialTouchX;
//    private static float initialTouchY;

//    public static void createChatHead(Context context, String mess) {
//
//        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        final View chatHead = LayoutInflater.from(context).inflate(R.layout.floating_notification, null);
//
//        final TextView textView = (TextView) chatHead.findViewById(R.id.noti_mess);
//        textView.setText(mess);
//
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.x = 0;
//        params.y = 100;
//
//        chatHead.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        windowManager.updateViewLayout(chatHead, params);
//                        return true;
//                }
//                return false;
//            }
//        });
//
//        chatHead.findViewById(R.id.noti_ava)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        windowManager.removeView(chatHead);
//                    }
//                });
//
//        windowManager.addView(chatHead, params);
//    }
}

