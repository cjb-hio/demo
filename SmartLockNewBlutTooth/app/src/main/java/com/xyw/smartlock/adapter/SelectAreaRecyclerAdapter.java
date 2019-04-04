package com.xyw.smartlock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.OnItemClickListener;

import java.util.List;

/**
 * Created by HP on 2017/8/4.
 */

public class SelectAreaRecyclerAdapter extends RecyclerView.Adapter<SelectAreaRecyclerAdapter.SelectAreaRecyclerViewHolder> {

    private List<String> areList;
    private OnItemClickListener onItemClickListener;

    public SelectAreaRecyclerAdapter(List<String> list) {
        this.areList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SelectAreaRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_areamanagement_item, parent, false);
        return new SelectAreaRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectAreaRecyclerViewHolder holder, int position) {
        holder.areamanagement_currentarea.setText(areList.get(position));
        holder.onItemClickListener = this.onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return areList.size();
    }

    public static class SelectAreaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView areamanagement_currentarea;
        OnItemClickListener onItemClickListener;

        public SelectAreaRecyclerViewHolder(View itemView) {
            super(itemView);
            areamanagement_currentarea = (TextView) itemView.findViewById(R.id.areamanagement_currentarea);
            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
