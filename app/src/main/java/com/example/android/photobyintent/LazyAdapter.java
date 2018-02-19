package com.example.android.photobyintent;

/**
 * Created by harmeetsaimbhi on 2017-10-24.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    Context mContext ;
    LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private List<Combination> comboList = null;
    private ArrayList<Combination> arraylist;

    public LazyAdapter(Context context, Activity a, List<Combination> comboList) {
        mContext = context;
        activity = a;
        inflater = LayoutInflater.from(mContext);
        imageLoader = new ImageLoader(activity.getApplicationContext());
        this.comboList = comboList;
        this.arraylist = new ArrayList<Combination>();
        this.arraylist.addAll(comboList);
    }

    public class ViewHolder {
        TextView text;
        ImageView image;
    }



    @Override
    public int getCount() {
        return comboList.size();
    }

    @Override
    public Combination getItem(int position) {
        return comboList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_single, null);
            holder.text = (TextView) convertView.findViewById(R.id.txt);
            holder.image = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.text.setText(comboList.get(position).getFilePathStrings());
        imageLoader.DisplayImage(comboList.get(position).getFileNameStrings(), holder.image);
        return convertView;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        comboList.clear();
        if (charText.length() == 0) {
            comboList.addAll(arraylist);
        } else {
            for (Combination wp : arraylist) {
                if (wp.getFileNameStrings().substring((wp.getFileNameStrings().length() - 3), (wp.getFileNameStrings().length())).equals("txt")) {
                    Log.d("TESTING", "The content is:" + wp.getText(wp.getFileNameStrings()));
                    if (wp.getText(wp.getFileNameStrings())
                            .contains(charText)) {
                        comboList.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}