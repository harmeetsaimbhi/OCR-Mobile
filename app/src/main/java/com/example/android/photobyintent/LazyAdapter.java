package com.example.android.photobyintent;

/**
 * Created by harmeetsaimbhi on 2017-10-24.
 */

import android.app.Activity;
import android.content.Context;
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
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    private List<Combination> comboList = null;
    private ArrayList<Combination> arraylist;

    public LazyAdapter(Activity a, List<Combination> comboList) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
        this.comboList = comboList;
        this.arraylist = new ArrayList<Combination>();
        this.arraylist.addAll(comboList);
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
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_single, null);

        TextView text=(TextView)vi.findViewById(R.id.txt);;
        ImageView image=(ImageView)vi.findViewById(R.id.img);
        text.setText(this.arraylist.get(position).getFilePathStrings());
        imageLoader.DisplayImage(this.arraylist.get(position).getFileNameStrings(), image);
        return vi;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        comboList.clear();
        if (charText.length() == 0) {
            comboList.addAll(arraylist);
        } else {
            for (Combination wp : arraylist) {
                if (wp.getFilePathStrings().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    comboList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}