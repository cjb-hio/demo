package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.xyw.smartlock.R;
import com.xyw.smartlock.adapter.MyAdapter;


/**
 * 导航界面
 * @author Mr zhong
 *
 */
public class NavigationActivity extends AppCompatActivity implements
		OnPageChangeListener, OnClickListener {
	private int imgs[] = { R.mipmap.nav1, R.mipmap.nav2,
			R.mipmap.nav3, R.mipmap.nav4,
			R.mipmap.nav5 };
	private Button button;
	private ViewPager viewPager;
	private LinearLayout layout;
	private int prePos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		getSupportActionBar().hide();
		button = (Button) findViewById(R.id.button);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		MyAdapter adapter = new MyAdapter(this, imgs);
		viewPager.setAdapter(adapter);

//		viewPager.setOnPageChangeListener(this);
		viewPager.addOnPageChangeListener(this);
		setPagerDian();
		button.setOnClickListener(this);
	}

	private void setPagerDian() {
		// TODO Auto-generated method stub

		layout = (LinearLayout) findViewById(R.id.layout);
		for (int i = 0; i < imgs.length; i++) {
			View v = new View(this);
			v.setEnabled(false);
			LayoutParams params = new LayoutParams(10, 10);
			params.leftMargin = 10;
			v.setLayoutParams(params);
			v.setBackgroundResource(R.drawable.selector);
			layout.addView(v);
		}
		layout.getChildAt(0).setEnabled(true);
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		if (position == imgs.length - 1) {
			button.setVisibility(View.VISIBLE);
		} else {
			button.setVisibility(View.GONE);
		}
		layout.getChildAt(position).setEnabled(true);
		layout.getChildAt(prePos).setEnabled(false);
		prePos = position;

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		//ToastUtil.MyToast(this, "hello");
		finish();
	}
	
}
