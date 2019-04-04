package com.xyw.smartlock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.OnItemClickListener;
import com.xyw.smartlock.utils.PersonMainBean;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Created by HP on 2017/7/14.
 */

public class UnitSwipeAdapter extends SwipeMenuAdapter<UnitSwipeAdapter.DefaultViewHolder> {

    private List<PersonMainBean> mList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public UnitSwipeAdapter(Context context, List<PersonMainBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_production_item, parent, false);
        return view;
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        PersonMainBean bean = mList.get(position);
        holder.production_item_TextView1.setText(bean.getPersonMainStr());
        holder.production_item_TextView2.setText(bean.getPersonMainID());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView production_item_TextView1, production_item_TextView2;
        public OnItemClickListener onItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            production_item_TextView1 = (TextView) itemView.findViewById(R.id.production_item_TextView1);
            production_item_TextView2 = (TextView) itemView.findViewById(R.id.production_item_TextView2);
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
