package com.example.android.photobyintent;

/**
 * Created by harmeetsaimbhi on 2017-10-24.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LazyAdapter extends BaseAdapter {

    public static final int BUFFER_COUNT = 10;
    public static final String OCCURENCE_TEXT = "Number of occurences: ";
    public static final String ELLIPSES = "...";

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
        Log.d("FILTER", "THE SIZE OF COMBOLIST IS:" + this.getCount());
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


    public void filter(String charText) throws IOException {
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
                        List<Integer> indices = getCount(charText,desc);
                        comboList.add(wp);

                        SpannableStringBuilder sb = new SpannableStringBuilder(OCCURENCE_TEXT + indices.size());
                        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE); //specify color here
                        BackgroundColorSpan bcs = new BackgroundColorSpan(Color.GRAY);
                        int index = indices.get(0);

                        // Starting
                        sb.append("\n" + ELLIPSES + desc.substring(index > BUFFER_COUNT ? index - BUFFER_COUNT : 0, index));

                        // Match Text
                        SpannableString text = new SpannableString(charText);
                        text.setSpan(fcs, 0, charText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        text.setSpan(bcs, 0, charText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        sb.append(text);

                        // Ending
                        sb.append(desc.substring(index + charText.length(), index + charText.length() +  BUFFER_COUNT < desc.length() ?
                                index + charText.length() + BUFFER_COUNT : desc.length()) + ELLIPSES);

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

    public List getCount (String searchText, String data) throws IOException {

        Pattern nom = Pattern.compile(searchText);
        List list = new ArrayList<Integer>();

        Matcher matcher = nom.matcher(data);
        while (matcher.find()) {
            list.add(matcher.start());
        }

        Log.d("FILTER", searchText  + " locations : " + list);
        return list;
    }

}