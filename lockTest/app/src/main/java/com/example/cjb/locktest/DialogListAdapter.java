package com.example.cjb.locktest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DialogListAdapter extends ArrayAdapter<BluetoothDevice> {

    List devices;
    Context context;

    private View.OnClickListener onClickListener;
    public DialogListAdapter(@NonNull Context context, int resource, @NonNull List<BluetoothDevice> objects, View.OnClickListener itemListener) {
        super(context, resource, objects);
        this.context=context;
        this.devices=objects;
        this.onClickListener=itemListener;
    }

    class ViewHolder {
        public TextView title;
        public TextView des;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothDevice device = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) { //若没有缓存布局，则加载
            //首先获取布局填充器，然后使用布局填充器填充布局文件
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_device, null);
            view.setOnClickListener(onClickListener);
            viewHolder = new ViewHolder();
            //存储子项布局中子控件对象
            viewHolder.title = (TextView) view.findViewById(R.id.item_title);
            viewHolder.des = (TextView) view.findViewById(R.id.item_description);
            // 将内部类对象存储到View对象中
            view.setTag(viewHolder);
        } else { //若有缓存布局，则直接用缓存（利用的是缓存的布局，利用的不是缓存布局中的数据）
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setText(device.getAddress());
        viewHolder.des.setText(device.getName());
        return view;
    }
}
