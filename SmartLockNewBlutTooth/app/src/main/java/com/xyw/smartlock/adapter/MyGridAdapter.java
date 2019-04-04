package com.xyw.smartlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.common.BaseViewHolder;
import com.xyw.smartlock.utils.DemoApplication;

/**
 * @author http://blog.csdn.net/finddreams
 * @Description:gridview的Adapter
 */
public class MyGridAdapter extends BaseAdapter {
    private Context mContext;
    private DemoApplication demoApplication;

    private String[] img_text;
    //			= { "读ID", "开锁", "落锁", "写ID", "下装", //"彩票",
//			"重写ID","读温度"//, "百度", "机票"
//	};
    private int[] imgs;


    public MyGridAdapter(Context mContext, String[] img_text, int[] imgs) {
        super();
        this.mContext = mContext;
        this.demoApplication = (DemoApplication) mContext;
        this.imgs = imgs;
//		img_text = mContext.getResources().getStringArray(R.array.my_gird_img_text);
        this.img_text = img_text;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);

        tv.setText(img_text[position]);
        return convertView;
    }
}
