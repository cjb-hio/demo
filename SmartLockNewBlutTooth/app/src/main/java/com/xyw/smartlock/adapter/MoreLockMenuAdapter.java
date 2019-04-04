package com.xyw.smartlock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.BluetoothDeviceBean;
import com.xyw.smartlock.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Created by YOLANDA on 2016/7/22.
 */
public class MoreLockMenuAdapter extends SwipeMenuAdapter<MoreLockMenuAdapter.MoreLockViewHolder> {

    private List<BluetoothDeviceBean> titles;

    private OnItemClickListener mOnItemClickListener;

    public MoreLockMenuAdapter(List<BluetoothDeviceBean> titles) {
        this.titles = titles;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.more_lock_item, parent, false);
    }

    @Override
    public MoreLockViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        MoreLockViewHolder viewHolder = new MoreLockViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoreLockViewHolder holder, int position) {
        holder.setData(titles.get(position));
    }

    static class MoreLockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_more_device_name, tv_more_device_mac, tv_more_lock_state;
        OnItemClickListener mOnItemClickListener;

        public MoreLockViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_more_device_name = (TextView) itemView.findViewById(R.id.tv_more_device_name);
            tv_more_device_mac = (TextView) itemView.findViewById(R.id.tv_more_device_mac);
            tv_more_lock_state = (TextView) itemView.findViewById(R.id.tv_more_lock_state);
        }

        public void setData(BluetoothDeviceBean deviceBean) {
            tv_more_device_name.setText(deviceBean.getName());
            tv_more_device_mac.setText(deviceBean.getMac());
            tv_more_lock_state.setText(deviceBean.getState());
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
