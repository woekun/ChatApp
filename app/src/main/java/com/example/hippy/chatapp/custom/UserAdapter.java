package com.example.hippy.chatapp.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private ArrayList<ParseUser> uList;

    public UserAdapter(Context context, ArrayList<ParseUser> uList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.uList = uList;
    }

    @Override
    public int getCount() {
        return uList.size();
    }

    @Override
    public ParseUser getItem(int position) {
        return uList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_list, null);

            // setup viewHolder
            viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.list_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //bind data
        ParseUser c = getItem(position);
        viewHolder.contactName.setText(c.getUsername());

        return convertView;
    }

    static class ViewHolder {
        private TextView contactName;
    }
}
