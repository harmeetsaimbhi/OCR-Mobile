package com.example.android.photobyintent;

/**
 * Created by harmeetsaimbhi on 2017-10-24.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

    public static final int BUFFER_COUNT = 10;

    private Activity activity;
    Context mContext;
    LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private List<Combination> comboList = null;
    private ArrayList<Combination> arraylist;
    boolean filterTextAdded;

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
        TextView name;
        ImageView image;
        TextView content;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.list_single, null);
        holder.name = (TextView) convertView.findViewById(R.id.txt);
        holder.image = (ImageView) convertView.findViewById(R.id.img);
        holder.content = (TextView) convertView.findViewById(R.id.searchText);

        holder.name.setText(comboList.get(position).getFileName());
        imageLoader.displayImage(comboList.get(position).getFilePath(), holder.image);

        if(filterTextAdded) {
            holder.content.setText(comboList.get(position).getFilterText());
        }

        return convertView;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        comboList.clear();

        if (charText.length() == 0) {
            filterTextAdded = false;
            comboList.addAll(arraylist);
        } else {
            filterTextAdded = true;
            for (Combination wp : arraylist) {
                //  filter text files
                if (wp.getFileName().substring((wp.getFileName().length() - 3), (wp.getFileName().length())).equals("txt")) {
                    String origDesc = wp.getText(wp.getFilePath());
                    String desc = origDesc.toLowerCase();

                    if ((wp.getFileName()).contains(charText)) {
                        comboList.add(wp);
//                        SpannableStringBuilder sb = new SpannableStringBuilder(desc);
                    } else if(desc.contains(charText)) {
                        comboList.add(wp);
                        SpannableStringBuilder sb = new SpannableStringBuilder(origDesc);
                        int index = desc.indexOf(charText);

                        if(index >= 0) {
                            //TODO handle ending array out of bounds
//                            sb = new SpannableStringBuilder("..." + sb.subSequence(index > 2 ? index - 3: index, index +  charText.length() + 3) +"...");
                            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158)); //specify color here
                            sb.setSpan(fcs, index, index + charText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            if (index + charText.length() < origDesc.length() - BUFFER_COUNT) {
                                sb.replace(index + charText.length() + BUFFER_COUNT, origDesc.length(), "...");
                            }

                            if (index > BUFFER_COUNT) {
                                sb.replace(0, index - BUFFER_COUNT, "...");
                            }

                        }
                        wp.setFilterText(sb);
                    }
                } else {
                    if ((wp.getFileName()).contains(charText)) {
                        comboList.add(wp);
                    }

                }
            }
        }
        notifyDataSetChanged();
    }

}