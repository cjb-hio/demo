/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyw.smartlock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.listener.OnItemClickListener;
import com.xyw.smartlock.utils.JurisdictBean;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;


/**
 * Created by YOLANDA on 2016/7/22.
 */
public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    private List<JurisdictBean> jurisdictBeans;

    private OnItemClickListener mOnItemClickListener;

    public MenuAdapter(List<JurisdictBean> titles) {
        this.jurisdictBeans = titles;
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
        return jurisdictBeans == null ? 0 : jurisdictBeans.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_jurisdoiction_item, parent, false);
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.setData(jurisdictBeans.get(position));
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView jurisdiction_image;
        TextView name;
        TextView jurisdiction_area;
        TextView phone;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            jurisdiction_image = (ImageView) itemView.findViewById(R.id.jurisdiction_image);
            name = (TextView) itemView.findViewById(R.id.jurisdiction_name);
            jurisdiction_area = (TextView) itemView.findViewById(R.id.jurisdiction_area);
            phone = (TextView) itemView.findViewById(R.id.jurisdiction_phone);
        }

        public void setData(JurisdictBean jurisdictBean) {
            this.name.setText(jurisdictBean.getName().trim());
            this.phone.setText(jurisdictBean.getOp_No());
            this.jurisdiction_area.setText(jurisdictBean.getZONE_NAME());
            if (jurisdictBean.getRole_Id().equals("1")) {
                this.jurisdiction_image.setImageResource(R.mipmap.jurisd03);
            } else if (jurisdictBean.getRole_Id().equals("2")) {
                this.jurisdiction_image.setImageResource(R.mipmap.jurisd02);
            } else if (jurisdictBean.getRole_Id().equals("3")) {
                this.jurisdiction_image.setImageResource(R.mipmap.jurisd01);
            } else if (jurisdictBean.getRole_Id().equals("4")) {
                this.jurisdiction_image.setImageResource(R.mipmap.jurisd04);
            } else {
                this.jurisdiction_image.setImageResource(R.mipmap.jurisd05);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
