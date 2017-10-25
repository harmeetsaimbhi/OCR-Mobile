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

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private String[] data;
    private String[] name;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public LazyAdapter(Activity a, String[] d, String[] t) {
        activity = a;
        data=d;
        name = t;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_single, null);

        TextView text=(TextView)vi.findViewById(R.id.txt);;
        ImageView image=(ImageView)vi.findViewById(R.id.img);
        text.setText(name[position]);
        imageLoader.DisplayImage(data[position], image);
        return vi;
    }
}