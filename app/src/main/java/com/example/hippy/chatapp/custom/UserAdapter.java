package com.example.hippy.chatapp.custom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private RoundImage roundedImage;
    private ArrayList<ParseUser> uList;
    private ArrayList<String> abc;
    private LayoutInflater layoutInflater;

//    public UserAdapter(Activity activity, ArrayList<ParseUser> uList) {
//        this.layoutInflater = activity.getLayoutInflater();
//        this.uList = uList;
//    }

    public UserAdapter(ArrayList<ParseUser> uList) {
        this.uList = uList;
    }

//    @Override
//    public int getCount() {
//        return uList.size();
//    }

//    @Override
//    public ParseUser getItem(int position) {
//        return uList.get(position);
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        String a = abc.get(i);
        ParseUser parseUser = uList.get(i);
//        Bitmap bm = BitmapFactory.decodeResource(layoutInflater.getContext().getResources(), R.drawable.image);
//        roundedImage = new RoundImage(bm);
//
//        viewHolder.avatar.setImageDrawable(roundedImage);
        viewHolder.contactName.setText(parseUser.getUsername());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return abc.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.item_list, null);
//
//            // setup viewHolder
//            viewHolder = new ViewHolder();
//            viewHolder.contactName = (TextView) convertView.findViewById(R.id.list_item);
//            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        //bind data
//        ParseUser parseUser = getItem(position);
//        Bitmap bm = BitmapFactory.decodeResource(layoutInflater.getContext().getResources(), R.drawable.image);
//        roundedImage = new RoundImage(bm);
//
//        viewHolder.avatar.setImageDrawable(roundedImage);
//        viewHolder.contactName.setText(parseUser.getUsername());
//
//        return convertView;
//    }

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
