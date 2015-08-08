package com.example.hippy.chatapp.custom;

import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;

/**
 * Created by Truong Giang on 8/8/2015.
 */
public class MyNavDrawerAdapter extends RecyclerView.Adapter<MyNavDrawerAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private int profile;
    private String email;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        int holderId;

        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView name;
        TextView email;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if(viewType == TYPE_ITEM){
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = 1;
            } else{
                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                holderId = 0;
            }
        }
    }


    MyNavDrawerAdapter(String Titles[], int Icons[], String Name, String Email, int Profile){
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;
    }

    @Override
    public MyNavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ITEM){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_row, viewGroup, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;
        } else if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_header, viewGroup, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType);
            return vhHeader;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(MyNavDrawerAdapter.ViewHolder viewHolder, int position) {
        if(viewHolder.holderId == 1){
            viewHolder.textView.setText(mNavTitles[position-1]);
            viewHolder.imageView.setImageResource(mIcons[position-1]);
        } else {
            viewHolder.profile.setImageResource(profile);
            viewHolder.name.setText(name);
            viewHolder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position){
        return position == 0;
    }
}
