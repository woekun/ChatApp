package com.example.hippy.chatapp.custom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hippy.chatapp.Activities.Chat;
import com.example.hippy.chatapp.R;
import com.example.hippy.chatapp.utils.Const;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private RoundImage roundedImage;
    private ArrayList<ParseUser> uList;
    private LayoutInflater layoutInflater;

    public UserAdapter(Activity activity, ArrayList<ParseUser> uList) {
        this.layoutInflater = activity.getLayoutInflater();
        this.uList = uList;
    }



    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View convertView = layoutInflater.inflate(R.layout.item_list, null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return new ViewHolder(convertView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ParseUser parseUser = uList.get((int) getItemId(position));
        Bitmap bm = BitmapFactory.decodeResource(layoutInflater.getContext().getResources(), R.drawable.image);
        roundedImage = new RoundImage(bm);

        holder.avatar.setImageDrawable(roundedImage);
        holder.contactName.setText(parseUser.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), uList.get(holder.getPosition()).getUsername(), Toast.LENGTH_SHORT).show();

                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), Chat.class)
                        .putExtra(Const.EXTRA_DATA, uList.get(holder.getPosition()).getUsername()));
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        return uList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contactName;
        private ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.list_item);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);

        }

    }
}
