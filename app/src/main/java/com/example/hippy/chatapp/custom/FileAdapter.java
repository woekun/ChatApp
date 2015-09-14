package com.example.hippy.chatapp.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hippy.chatapp.R;

import java.io.File;

public class FileAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater=null;
        private Context context;
        private File[] arr;
        public FileAdapter(Context context, File[] objects) {

            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arr= objects;

        }

        public class Holder{
            TextView tv;
            ImageView img;
        }

        @Override
        public int getCount() {
            return arr==null?0:arr.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



    @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Holder holder = new Holder();
            convertView =layoutInflater.inflate(R.layout.item,null);

            holder.tv = (TextView) convertView.findViewById(R.id.Dir);
            holder.tv.setText(arr[position].getName());



            return convertView;
        }





    }