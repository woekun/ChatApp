package com.example.hippy.chatapp.custom;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> dataHeader;
    private LayoutInflater layoutInflater;
    private HashMap<String, List<String>> collections;

    public UserAdapter(Activity activity, ArrayList<String> dataHeader, HashMap<String, List<String>> collections) {
        this.layoutInflater = activity.getLayoutInflater();
        this.collections = collections;
        this.dataHeader = dataHeader;
    }

    @Override
    public int getGroupCount() {
        return dataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return collections.get(dataHeader.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return dataHeader.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return collections.get(dataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupHeader = getGroup(groupPosition);
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.item_group, parent,false);
        TextView header = (TextView)convertView.findViewById(R.id.group_header);
        header.setTypeface(null, Typeface.BOLD);
        header.setText(groupHeader);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String contact = getChild(groupPosition,childPosition);
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.item_list, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.contactName = (TextView) convertView.findViewById(R.id.list_item);
        viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);

        viewHolder.contactName.setText(contact);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {
        private TextView contactName;
        private ImageView avatar;

    }
}
