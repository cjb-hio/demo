package com.xyw.smartlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xyw.smartlock.R;

import java.util.List;

/**
 * Created by acer on 2016/5/5.
 */
public class MyRecordAdapter extends BaseAdapter {
    Context mContext;
    List<String> mList;
    public MyRecordAdapter(Context context, List<String> list){
        mContext = context;
        mList = list;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_voicelist, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_armName);
        int num = position+1;
        tv.setText("消息"+num);
        return convertView;
    }
}
