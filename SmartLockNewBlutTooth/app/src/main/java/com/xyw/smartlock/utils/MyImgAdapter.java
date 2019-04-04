package com.xyw.smartlock.utils;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;

import java.util.List;

/**
 * Created by 19428 on 2016/7/4.
 */
public class MyImgAdapter extends BaseAdapter {
    private List<SearchListObj> myListOBJ;
    private Context mContext;

    /*
     * 构造函数
     */
    public MyImgAdapter(Context context, List<SearchListObj> list) {
        this.mContext = context;
        this.myListOBJ = list;
    }

    @Override
    public int getCount() {
        return myListOBJ.size();// 返回数组的长度
    }

    @Override
    public Object getItem(int position) {
        return myListOBJ.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 书中详细解释该方法
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_search_item, null);
            holder = new Holder();
            holder.used = (TextView) convertView.findViewById(R.id.search_used);
            holder.dateTime = (TextView) convertView.findViewById(R.id.search_DateTime);
            holder.lockID = (TextView) convertView.findViewById(R.id.search_lockID);
            holder.result = (TextView) convertView.findViewById(R.id.search_result);
            holder.areaName = (TextView) convertView.findViewById(R.id.search_areaName);
            holder.search_LinearLayout = (LinearLayout) convertView.findViewById(R.id.search_linearlayout);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        /** 设置TextView显示的内容，即我们存放在动态数组中的数据 */
        SearchListObj searchListObj = myListOBJ.get(position);
        holder.dateTime.setText(searchListObj.getDateTime());
        holder.lockID.setText(searchListObj.getLockID());
        String itemresult = searchListObj.getResult();
        holder.result.setText(itemresult);

        holder.areaName.setText(searchListObj.getSreaName());
        String itemUsed = searchListObj.getUsed();
        holder.used.setText(itemUsed);
        if (itemUsed.equals("1")) {
            Log.e("MyImagAdapter", "getView: position = " + position);
//            holder.search_LinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.gray_text));
            holder.search_LinearLayout.setBackgroundResource(R.drawable.device_bg_unuse);
            holder.result.setBackgroundColor(mContext.getResources().getColor(R.color.gray_text));
        } else if (itemUsed.equals("0")){
            if (itemresult.equals("未下装")) {
                holder.result.setBackgroundColor(Color.RED);
            } else if (itemresult.equals("下装成功")) {
                holder.result.setBackgroundColor(Color.GREEN);
            }
//            holder.search_LinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.search_LinearLayout.setBackgroundResource(R.drawable.device_bg_use);
        }
        return convertView;
    }

    private class Holder{
        TextView used;
        TextView dateTime;
        TextView lockID;
        TextView result;
        TextView areaName;
        LinearLayout search_LinearLayout;
    }
}
