package com.example.hippy.chatapp.custom;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.models.Conversation;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    private RoundImage roundedImage;
    private ArrayList<Conversation> convList;
    private LayoutInflater layoutInflater;

    public ChatAdapter(Activity activity, ArrayList<Conversation> convList) {
        this.layoutInflater = activity.getLayoutInflater();
        this.convList = convList;
    }

    @Override
    public int getCount() {
        return convList.size();
    }

    @Override
    public Conversation getItem(int position) {
        return convList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Conversation conversation = getItem(position);

        if (conversation.isSent()) {
            convertView = layoutInflater.inflate(R.layout.item_chat_sent, null);
        } else {
            convertView = layoutInflater.inflate(R.layout.item_chat_recv, null);
        }

        // setup viewHolder
        viewHolder = new ViewHolder();
        viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
        convertView.setTag(viewHolder);

        viewHolder = (ViewHolder) convertView.getTag();

        //bind data
        Bitmap bm = BitmapFactory.decodeResource(layoutInflater.getContext().getResources(), R.drawable.image);
        roundedImage = new RoundImage(bm);
        viewHolder.ivAvatar.setImageDrawable(roundedImage);
        viewHolder.tvMessage.setText(conversation.getMessage());
        return convertView;
    }

    static class ViewHolder {
        private TextView tvMessage;
        private ImageView ivAvatar;
    }
}
