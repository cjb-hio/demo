package com.xyw.smartlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.Audit;
import com.xyw.smartlock.utils.SLImageLoader;

import java.util.List;

/**
 * Created by 19428 on 2016/11/5.
 */

public class AuditGridViewAdapter extends BaseAdapter {
    private List<Audit> mList;
    private Context context;
    private ViewHolder holder = null;



    public AuditGridViewAdapter(Context context, List<Audit> list) {
        this.context = context;
        this.mList = list;
    }

    //获取图片有多少个
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.audit_picture_gridview_item, null);
            holder = new ViewHolder();
            holder.Audit_picture_GridView_item = (ImageView) convertView.findViewById(R.id.Audit_picture_GridView_item);

            convertView.setTag(holder);// 如果convertView为空就 把holder赋值进去
        } else {
            holder = (ViewHolder) convertView.getTag();// 如果convertView不为空，那么就在convertView中getTag()拿出来
        }
        String str1 = mList.get(position).getSIGNIMG();
        if (!str1.equals("")) {
            loadHeadPixImage(str1);
        }


        return convertView;
    }

    static class ViewHolder {
        public ImageView Audit_picture_GridView_item;

    }

    private void loadHeadPixImage(String str1) {
        String url = HttpServerAddress.UPLOADS + str1;
        SLImageLoader.getInstance().loadImagePix(url, holder.Audit_picture_GridView_item);

    }
}
