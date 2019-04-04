package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;

import java.util.ArrayList;
import java.util.List;

public class GuideAcitivityPassWd extends AppCompatActivity {
    private TextView title;
    private TextView tv_lid;
    private ImageView backImg;
    private Button bt_ag;
    private Button toGuEnd;
    private TextView tv_num_1;
    private TextView tv_num_2;
    private TextView tv_num_3;
    private TextView tv_num_4;
    private TextView tv_num_5;
    private TextView tv_num_6;
    private TextView guide_task_content;
    private LinearLayout ll_frist;
    private LinearLayout ll_passwd;
    private String[] mPass = new String[6];
    private String pasNum = "";
    //中间参数
    private String num;
    private String lid;
    private String userid;
    private List<TextView> tvList = new ArrayList<TextView>();
    private int flage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_fragment2_layout);
        getSupportActionBar().hide();
        guide_task_content = (TextView) findViewById(R.id.guide_task_content);
        bt_ag = (Button) findViewById(R.id.bt_ag);
        tv_lid = (TextView) findViewById(R.id.tv_lid);
        toGuEnd = (Button) findViewById(R.id.bt_next_guide2);
        //密码
        tv_num_1 = (TextView) findViewById(R.id.tv_num_1);
        tv_num_2 = (TextView) findViewById(R.id.tv_num_2);
        tv_num_3 = (TextView) findViewById(R.id.tv_num_3);
        tv_num_4 = (TextView) findViewById(R.id.tv_num_4);
        tv_num_5 = (TextView) findViewById(R.id.tv_num_5);
        tv_num_6 = (TextView) findViewById(R.id.tv_num_6);
        tvList.add(tv_num_1);
        tvList.add(tv_num_2);
        tvList.add(tv_num_3);
        tvList.add(tv_num_4);
        tvList.add(tv_num_5);
        tvList.add(tv_num_6);
        //动态页面
        ll_passwd = (LinearLayout) findViewById(R.id.ll_passwd);
        ll_frist = (LinearLayout) findViewById(R.id.ll_frist);
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("验证密码");
        // 监听返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        num = intent.getStringExtra("num");
        lid = intent.getStringExtra("lid");
        userid = intent.getStringExtra("userid");
        tv_lid.setText(lid);
        for (int i = 0; i < 6; i++) {
            mPass[i] = intent.getStringExtra("passwd" + i);
            tvList.get(i).setText(mPass[i]);
            pasNum = pasNum + mPass[i];
            System.out.println();
        }
    }

    public void toGuEnd(View view) {
        Intent intent = new Intent();
        intent.putExtra("lid", lid);
        intent.putExtra("num", num);
        intent.putExtra("userid", userid);
        intent.setClass(GuideAcitivityPassWd.this, GuideAcitivityEnd.class);
        startActivity(intent);
        finish();
    }

    public void ag(View view) {
        if (flage == 0) {
            ll_frist.setVisibility(View.GONE);
            ll_passwd.setVisibility(View.VISIBLE);
            guide_task_content.setText(mPass[flage]);
            tvList.get(flage).setTextColor(getResources().getColor(R.color.red));
            tvList.get(flage).setTextSize(25);
            flage++;
        } else {
            guide_task_content.setText(mPass[flage]);
            tvList.get(flage).setTextColor(getResources().getColor(R.color.red));
            tvList.get(flage).setTextSize(25);
            if (flage == 5) {
                toGuEnd.setVisibility(View.VISIBLE);
                bt_ag.setVisibility(View.GONE);
            }
            flage++;
        }
    }
}
