package com.xyw.smartlock.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.activity.TaskListActivityNew;
import com.xyw.smartlock.bean.TaskBean;
import com.xyw.smartlock.utils.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;


public class TaskListAdapter extends SwipeMenuAdapter<TaskListAdapter.DefaultViewHolder> {
    private List<TaskBean> mList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public TaskListAdapter(TaskListActivityNew context, List<TaskBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return view;
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, final int position) {
        final TaskBean taskBean = mList.get(position);
        holder.setOnItemClickListener(onItemClickListener);
        if (taskBean.getHAVE_INFO().equals("0")) {
            holder.tv_time.setText(taskBean.getStartTime());
            holder.tv_area.setText(taskBean.getArea());
            holder.bt_ret.setText(taskBean.getRET_V());
            holder.tv_myname.setText(taskBean.getMyName());
            holder.tv_op_no.setText(taskBean.getR_op_no());
            if (taskBean.getRET_V().equals("审核中")) {
                holder.bt_ret.setText("审核中");
                holder.rl_color.setBackgroundResource(R.drawable.tk_ing_bg);
                setTextColor(holder.tv_time, holder.tv_area, holder.bt_ret, holder.tv_myname, holder.tv_op_no, mContext.getResources().getColor(R.color.black));
            } else if (taskBean.getRET_V().equals("审核失败")) {
                holder.bt_ret.setText("申请失败");
                holder.rl_color.setBackgroundResource(R.drawable.tk_false_bg);
                setTextColor(holder.tv_time, holder.tv_area, holder.bt_ret, holder.tv_myname, holder.tv_op_no, mContext.getResources().getColor(R.color.white));
            } else {
                holder.bt_ret.setText("审核通过");
                holder.rl_color.setBackgroundResource(R.drawable.tk_true_bg);
                setTextColor(holder.tv_time, holder.tv_area, holder.bt_ret, holder.tv_myname, holder.tv_op_no, mContext.getResources().getColor(R.color.result_view));
            }
        } else {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    private void setTextColor(TextView tv1, TextView tv2, TextView tv3, TextView tv4, TextView tv5, int color) {
        tv1.setTextColor(color);
        tv2.setTextColor(color);
        tv3.setTextColor(color);
        tv4.setTextColor(color);
        tv5.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_time;
        public TextView tv_area;
        public TextView bt_ret;
        public TextView tv_op_no;
        public TextView tv_myname;
        public RelativeLayout rl_color;
        private OnItemClickListener onItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tv_time = (TextView) itemView.findViewById(R.id.tv_time_list);
            tv_area = (TextView) itemView.findViewById(R.id.tv_area_list);
            bt_ret = (TextView) itemView.findViewById(R.id.bt_ret_list);
            tv_op_no = (TextView) itemView.findViewById(R.id.tv_op_no);
            tv_myname = (TextView) itemView.findViewById(R.id.tv_myname);
            rl_color = (RelativeLayout) itemView.findViewById(R.id.rl_color);


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
