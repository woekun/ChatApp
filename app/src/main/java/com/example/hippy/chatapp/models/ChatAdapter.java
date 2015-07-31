package com.example.hippy.chatapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hippy.chatapp.R;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    private ArrayList<Conversation> convList;
    private LayoutInflater mLayoutInflater;

    public ChatAdapter(Context context, ArrayList<Conversation> convList) {
        this.mLayoutInflater = LayoutInflater.from(context);
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
        final ViewHolder viewHolder;


        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_chat, null);

            // setup viewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.item_chat);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //bind data
        Conversation conversation = getItem(position);
        viewHolder.tvMessage.setText(conversation.getMessage());

        return convertView;
    }

    static class ViewHolder {
        private TextView tvMessage;
    }
}
