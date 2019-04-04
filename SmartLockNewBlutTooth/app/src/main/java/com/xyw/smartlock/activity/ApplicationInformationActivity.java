package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.xyw.smartlock.R;


public class ApplicationInformationActivity extends AppCompatActivity {

	private TextView title;
	private ImageView imageback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applicationinformation);
		// 初始化按钮
		initview();
		// 获得从开锁信息界面传过来的值
		intent();

	}

	private void intent() {
		Intent intent = getIntent();
		String str1 = intent.getStringExtra("name");
		String str2 = intent.getStringExtra("personNumber");

	}

	/**
	 * 初始化按钮
	 */
	private void initview() {
		// 设置标题栏名称
		title = (TextView) findViewById(R.id.common_tv_title);
		title.setText(R.string.applicationinformation);
		// 设置返回按键
		imageback = (ImageView) findViewById(R.id.common_title_back);
		imageback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
