package com.xyw.smartlock.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xyw.smartlock.R;

import java.util.List;

/**
 * Created by acer on 2016/6/16.
 */
public class BTAdapter extends BaseAdapter {
    Context context;
    List<BluetoothDevice> devices;
    LayoutInflater inflater;

    public BTAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.devices = devices;
    }
    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = inflater.inflate(R.layout.device_element, null);
        }
        BluetoothDevice device = devices.get(position);
        final TextView tvadd = ((TextView) convertView.findViewById(R.id.address));
        final TextView tvname = ((TextView) convertView.findViewById(R.id.name));
        final TextView tvpaired = (TextView) convertView.findViewById(R.id.paired);
        final TextView tvrssi = (TextView) convertView.findViewById(R.id.rssi);
        tvrssi.setVisibility(View.VISIBLE);

        return convertView;
    }
}
