package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.view.LayoutItem1;

public class TaskAndSendActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private ImageView titleBack;
    public static TaskAndSendActivity instance = null;
    private RelativeLayout taskAndSend_RelativeLayout01, taskAndSend_RelativeLayout02;
    private LayoutItem1 taskAndSend_RelativeLayout01_textView1, taskAndSend_RelativeLayout02_textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskandsend);

        instance = this;
        getSupportActionBar().hide();

        initView();
    }

    private void initView() {
        title= (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.applyTask);
        titleBack= (ImageView) findViewById(R.id.common_tv_back);
        titleBack.setOnClickListener(this);
        titleBack.setVisibility(View.VISIBLE);
//        taskAndSend_RelativeLayout01 = (RelativeLayout) findViewById(R.id.taskAndSend_RelativeLayout01);
//        taskAndSend_RelativeLayout02 = (RelativeLayout) findViewById(R.id.taskAndSend_RelativeLayout02);
//        taskAndSend_RelativeLayout01.setOnClickListener(this);
//        taskAndSend_RelativeLayout02.setOnClickListener(this);
        taskAndSend_RelativeLayout01_textView1 = (LayoutItem1) findViewById(R.id.taskAndSend_RelativeLayout01_textView1);
        taskAndSend_RelativeLayout02_textView1 = (LayoutItem1) findViewById(R.id.taskAndSend_RelativeLayout02_textView1);
        taskAndSend_RelativeLayout01_textView1.setOnClickListener(this);
        taskAndSend_RelativeLayout02_textView1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_tv_back:
                finish();
                break;
            case R.id.taskAndSend_RelativeLayout01_textView1:
                Intent intent1 = new Intent(TaskAndSendActivity.this, TaskApplyActivitys.class);
                startActivity(intent1);
                break;
            case R.id.taskAndSend_RelativeLayout02_textView1:
                Intent intent2 = new Intent(TaskAndSendActivity.this,SendOrderActivity.class);
                startActivity(intent2);
                break;
//            case R.id.taskAndSend_RelativeLayout01:
//                Intent intent1 = new Intent(TaskAndSendActivity.this, TaskApplyActivitys.class);
//                startActivity(intent1);
//                break;
//            case R.id.taskAndSend_RelativeLayout02:
//                Intent intent2 = new Intent(TaskAndSendActivity.this,SendOrderActivity.class);
//                startActivity(intent2);
//                break;
            default:
                break;
        }
    }
}
