package com.xyw.smartlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.SelectionListBean;

import java.util.List;

/**
 * Created by 19428 on 2016/10/18.
 */
public class SelectionListAdapter extends BaseAdapter {
    private List<SelectionListBean> mList;
    private Context context;

    public SelectionListAdapter(Context context, List<SelectionListBean> list) {
        this.context = context;
        this.mList = list;

    }

    @Override
    public int getCount() {
        return mList.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_user, null);
        }
        TextView tv_user1 = (TextView) convertView.findViewById(R.id.tv_select_user1);
        TextView tv_user2 = (TextView) convertView.findViewById(R.id.tv_select_user2);
        tv_user1.setText(mList.get(position).getSelectionBean1());
        tv_user2.setText(mList.get(position).getSelectionBean2());
        return convertView;
    }

}
