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

public class TaskAndSendListActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private ImageView titleBack;
    private RelativeLayout taskAndSendList_RelativeLayout01, taskAndSendList_RelativeLayout02;
    private LayoutItem1 taskAndSendList_RelativeLayout01_textView1, taskAndSendList_RelativeLayout02_textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskandsendlist);
        getSupportActionBar().hide();

        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.TaskInfo);
        titleBack = (ImageView) findViewById(R.id.common_tv_back);
        titleBack.setOnClickListener(this);
        titleBack.setVisibility(View.VISIBLE);
//        taskAndSendList_RelativeLayout01 = (RelativeLayout) findViewById(R.id.taskAndSendList_RelativeLayout01);
//        taskAndSendList_RelativeLayout02 = (RelativeLayout) findViewById(R.id.taskAndSendList_RelativeLayout02);
//        taskAndSendList_RelativeLayout01.setOnClickListener(this);
//        taskAndSendList_RelativeLayout02.setOnClickListener(this);
        taskAndSendList_RelativeLayout01_textView1 = (LayoutItem1) findViewById(R.id.taskAndSendList_RelativeLayout01_textView1);
        taskAndSendList_RelativeLayout02_textView1 = (LayoutItem1) findViewById(R.id.taskAndSendList_RelativeLayout02_textView1);
        taskAndSendList_RelativeLayout01_textView1.setOnClickListener(this);
        taskAndSendList_RelativeLayout02_textView1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_tv_back:
                finish();
                break;
            case R.id.taskAndSendList_RelativeLayout01_textView1:
                Intent intent1 = new Intent(TaskAndSendListActivity.this, TaskListActivityNew.class);
                startActivity(intent1);
                break;
            case R.id.taskAndSendList_RelativeLayout02_textView1:
                Intent intent2 = new Intent(TaskAndSendListActivity.this, SendWorkListActivity.class);
                startActivity(intent2);
                break;
//            case R.id.taskAndSendList_RelativeLayout01:
//                Intent intent1 = new Intent(TaskAndSendListActivity.this, TaskListActivityNew.class);
//                startActivity(intent1);
//                break;
//            case R.id.taskAndSendList_RelativeLayout02:
//                Intent intent2 = new Intent(TaskAndSendListActivity.this, SendWorkListActivity.class);
//                startActivity(intent2);
//                break;
            default:
                break;
        }
    }
}
