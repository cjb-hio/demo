package com.xyw.smartlock.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MyAdapter extends PagerAdapter {
	private int imgs[];
	private Context context;

	public MyAdapter(Context context,int imgs[]) {
		// TODO Auto-generated constructor stub
		this.imgs = imgs;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imgs.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		ImageView iv = new ImageView(context);
		iv.setImageResource(imgs[position]);
		iv.setScaleType(ScaleType.FIT_XY);
		container.addView(iv);
		return iv;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}

}