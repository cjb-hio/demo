package com.xyw.smartlock.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.GuideBean;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.Fiale_dailog;
import com.xyw.smartlock.utils.GetPressword;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {
    private TextView title;
    private ImageView backImg;
    private EditText task_no_text;
    private String path;
    private String num = "1";
    private String lid;
    private String keyV;
    private int[] passwd;
    private List<String> pasList = new ArrayList<String>();
    private GuideBean guideBean = new GuideBean();
    private String passnum;
    private String user_context_number;
    private AcacheUserBean acacheUserBean;
    private ACache mCache;
    private boolean flage = false;
    private boolean flage2;
    private boolean flage3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        getSupportActionBar().hide();
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("手开申请");
        // 监听返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        task_no_text = (EditText) findViewById(R.id.guide_task_no);
        // 缓存数据
        mCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) mCache.getAsObject("LoginInfo");
        user_context_number = acacheUserBean.getUSER_CONTEXT().toString();
        keyV = acacheUserBean.getKEYVALUE().toString();
        guideBean.setUser_cont(user_context_number);

    }

    public void toGupasswd(View view) {
        if (task_no_text.getText().toString().length() <= 0) {
            new AlertDialog.Builder(GuideActivity.this).setMessage("请输入设备ID").setPositiveButton(R.string.ok, null).create().show();
            /*final Dialog dialogtip = new Dialog(GuideActivity.this, R.style.dailogStyle);
            dialogtip.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = dialogtip.getWindow().getAttributes();
            lp.alpha = 0.9f;//透明度
            View view1 = getLayoutInflater().inflate(R.layout.dailog_tip2_layout, null);
            dialogtip.setContentView(view1);
            Button bt_tip = (Button) view1.findViewById(R.id.bt_tip);
            bt_tip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogtip.dismiss();
                }
            });
            dialogtip.show();*/
        } else {
            getPass();
            while (!flage) {
                try {
                    thread2.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            new Thread(DownloadRunnable).start();
        }
    }

    public void jumpToPass() {
        if (flage2) {
            if (flage3) {
                handler4.sendEmptyMessage(0);
                flage = false;
            } else {
                handler2.sendEmptyMessage(0);
            }
        } else {
            handler3.sendEmptyMessage(0);
            flage = false;
        }

    }

    public void getPass() {
        String str = "0000000000000000";
        lid = task_no_text.getText().toString();
        lid = str.substring(0, 16 - lid.length()) + lid;
        path = HttpServerAddress.GETLOCKPASSWD + "&lid=" + lid + "&user_context=" + user_context_number;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                GetPressword getPressword = new GetPressword();
                try {
                    GuideBean bean = getPressword.run(path);
                    guideBean.setPressword(bean.getPressword());
                    guideBean.setGuresult(bean.getGuresult());
                    guideBean.setMyLid(bean.getMyLid());
                    int myNum = Integer.parseInt(guideBean.getPressword());
                    if (bean.getGuresult().equals("true")) {
                        passwd = new Ntag_I2C_Demo(null, GuideActivity.this).GetLockPasswd(guideBean.getMyLid(), keyV, myNum);
                        for (int i = 0; i < passwd.length; i++) {
                            String t = String.valueOf(passwd[i]);
                            pasList.add(t);
                        }
                    }
                    if (guideBean.getGuresult().equals("true")) {
                        flage2 = true;
                        flage3 = false;
                    } else if (guideBean.getGuresult().equals("false")) {
                        flage3 = true;
                        flage2 = true;
                    } else {
                        flage3 = false;
                        flage2 = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                flage = true;
            }

        };
        thread.start();

    }

    Runnable DownloadRunnable = new Runnable() {
        @Override
        public void run() {
            jumpToPass();
        }
    };

    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            for (int i = 0; i < pasList.size(); i++) {
                intent.putExtra("passwd" + i, pasList.get(i));
            }
            intent.putExtra("lid", guideBean.getMyLid());
            intent.putExtra("num", guideBean.getPressword());
            intent.putExtra("userid", user_context_number);
            intent.setClass(GuideActivity.this, GuideAcitivityPassWd.class);
            startActivity(intent);
            finish();
        }

    };
    private Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Fiale_dailog dailog = new Fiale_dailog(GuideActivity.this, R.style.dailogStyle);
            dailog.show();
        }

    };
    private Handler handler4 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Dialog dialogtip = new Dialog(GuideActivity.this, R.style.dailogStyle);
            dialogtip.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = dialogtip.getWindow().getAttributes();
            lp.alpha = 0.9f;//透明度
            View view1 = getLayoutInflater().inflate(R.layout.dialog_tip_id_layout, null);
            dialogtip.setContentView(view1);
            Button bt_tip = (Button) view1.findViewById(R.id.bt_tip);
            bt_tip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogtip.dismiss();
                }
            });
            dialogtip.show();
        }

    };
    Thread thread2 = new Thread() {
        @Override
        public void run() {
            super.run();
        }
    };
}
