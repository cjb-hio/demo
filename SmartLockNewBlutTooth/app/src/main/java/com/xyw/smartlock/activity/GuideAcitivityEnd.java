package com.xyw.smartlock.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.PostTaskData;

import org.json.JSONException;

import java.io.IOException;

public class GuideAcitivityEnd extends AppCompatActivity {
    private TextView title;
    private ImageView backImg;
    private String path;
    //中间参数
    private String num;
    private String lid;
    private String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_fragment4_layout);
        getSupportActionBar().hide();
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("确认结果");
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
        path = HttpServerAddress.SETLOCKPASSWDNUM+"&lid="+lid+"&passnum="+num+"&user_context="+userid;
        Log.e("TAG",path);
    }

    final Thread thread = new Thread(){
        @Override
        public void run() {
            super.run();
            PostTaskData postTaskData = new PostTaskData();
            try {
                postTaskData.run(path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    public void GuideTrue(View view) {
        Toast.makeText(this,"成功开锁",Toast.LENGTH_SHORT).show();
        thread.start();
        finish();
    }

    public void GuideFalse(View view) {
        final Dialog dialog = new Dialog(GuideAcitivityEnd.this, R.style.dailogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 0.9f;//透明度
        View view1 = getLayoutInflater().inflate(R.layout.dailog_guide_sure,null);
        dialog.setContentView(view1);
        Button sure = (Button) view1.findViewById(R.id.bt_sure);
        Button sure_no = (Button) view1.findViewById(R.id.bt_sure_no);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                thread.start();
                back();
            }
        });
        sure_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                back();
            }
        });
        dialog.show();
    }

    private void back() {
        Intent intent = new Intent();
        intent.setClass(GuideAcitivityEnd.this,GuideActivity.class);
        startActivity(intent);
        finish();
    }
}
