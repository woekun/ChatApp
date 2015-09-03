package com.example.hippy.chatapp.custom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hippy.chatapp.Activities.Chat;
import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.utils.Const;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    private ArrayList<ParseUser> uList;
    private LayoutInflater layoutInflater;

    public UserAdapter(Activity activity, ArrayList<ParseUser> uList) {
        this.layoutInflater = activity.getLayoutInflater();
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

        ParseUser parseUser = getItem(position);

        convertView = layoutInflater.inflate(R.layout.item_list, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        TextView contactName = (TextView) convertView.findViewById(R.id.list_item);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);

        contactName.setText(parseUser.getUsername());

        return convertView;
    }

    static class ViewHolder {
        private TextView contactName;
        private ImageView avatar;

    }
}
