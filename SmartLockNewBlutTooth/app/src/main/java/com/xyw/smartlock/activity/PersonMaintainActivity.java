package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.view.LayoutItem1;


public class PersonMaintainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private ImageView titleBack;

    private LayoutItem1 person_maintain_RelativeLayout01, person_maintain_RelativeLayout02, person_maintain_RelativeLayout03,
            person_maintain_RelativeLayout04, person_maintain_RelativeLayout05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_maintain);
        getSupportActionBar().hide();

        initView();

    }

    private void initView() {
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.PersonnelMaintain);
        titleBack = (ImageView) findViewById(R.id.common_tv_back);
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);

        person_maintain_RelativeLayout01 = (LayoutItem1) findViewById(R.id.person_maintain_RelativeLayout01);
        person_maintain_RelativeLayout02 = (LayoutItem1) findViewById(R.id.person_maintain_RelativeLayout02);
        person_maintain_RelativeLayout03 = (LayoutItem1) findViewById(R.id.person_maintain_RelativeLayout03);
        person_maintain_RelativeLayout04 = (LayoutItem1) findViewById(R.id.person_maintain_RelativeLayout04);
        person_maintain_RelativeLayout05 = (LayoutItem1) findViewById(R.id.person_maintain_RelativeLayout05);
        person_maintain_RelativeLayout01.setOnClickListener(this);
        person_maintain_RelativeLayout02.setOnClickListener(this);
        person_maintain_RelativeLayout03.setOnClickListener(this);
        person_maintain_RelativeLayout04.setOnClickListener(this);
        person_maintain_RelativeLayout05.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_tv_back:
                finish();
                break;
            case R.id.person_maintain_RelativeLayout01:
                Intent intent1 = new Intent(PersonMaintainActivity.this, UnitActivity.class);
                startActivity(intent1);
                break;
            case R.id.person_maintain_RelativeLayout02:
                Intent intent2 = new Intent(PersonMaintainActivity.this, TeamActivity.class);
                startActivity(intent2);
                break;
            case R.id.person_maintain_RelativeLayout03:
                Intent intent3 = new Intent(PersonMaintainActivity.this, TrafficActivity.class);
                startActivity(intent3);
                break;
            case R.id.person_maintain_RelativeLayout04:
                Intent intent4 = new Intent(PersonMaintainActivity.this, ProductionActivity.class);
                startActivity(intent4);
                break;
            case R.id.person_maintain_RelativeLayout05:
                Intent intent5 = new Intent(PersonMaintainActivity.this, SafetyActivity.class);
                startActivity(intent5);
                break;
            default:
                break;
        }
    }


}
