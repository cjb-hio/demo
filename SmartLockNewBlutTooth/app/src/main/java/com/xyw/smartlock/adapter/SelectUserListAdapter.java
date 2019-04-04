package com.xyw.smartlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.UserBean;

import java.util.List;

/**
 * Created by acer on 2016/6/4.
 */
public class SelectUserListAdapter extends BaseAdapter {
    List<UserBean> mlist;
    Context mContext;
    public SelectUserListAdapter(Context context,List<UserBean> list){
        mlist = list;
        mContext = context;
    }
    public void upData(List<UserBean> data) {
        mlist = data;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_user, null);
        }
        TextView tv_user = (TextView) convertView.findViewById(R.id.tv_select_user1);
        tv_user.setText(mlist.get(position).getName());
        return convertView;
    }
}
