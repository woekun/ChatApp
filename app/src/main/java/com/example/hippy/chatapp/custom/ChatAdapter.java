package com.example.hippy.chatapp.custom;

import android.app.Activity;
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

    private ArrayList<Conversation> convList;
    private LayoutInflater layoutInflater;

    public ChatAdapter(Activity activity) {
        this.layoutInflater = activity.getLayoutInflater();
        convList = new ArrayList<>();
    }

    public void addMessage(Conversation conversation) {
        convList.add(conversation);
        notifyDataSetChanged();
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
            convertView = layoutInflater.inflate(R.layout.item_chat_sent, parent, false);
        } else {
            convertView = layoutInflater.inflate(R.layout.item_chat_recv, parent, false);
        }

        // setup viewHolder
        viewHolder = new ViewHolder();
        viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
        convertView.setTag(viewHolder);

        viewHolder = (ViewHolder) convertView.getTag();

        //bind data
        viewHolder.tvMessage.setText(conversation.getMessage());

        return convertView;
    }

    static class ViewHolder {
        private TextView tvMessage;
        private ImageView ivAvatar;
    }
}

