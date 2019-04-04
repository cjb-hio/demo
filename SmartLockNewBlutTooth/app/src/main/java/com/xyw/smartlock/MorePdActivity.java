package com.xyw.smartlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.activity.SetMsActivity;
import com.xyw.smartlock.adapter.MyGridAdapter;
import com.xyw.smartlock.common.MyGridView;
import com.xyw.smartlock.listener.OnCustomViewOnclickListener;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.view.CustomView;

public class MorePdActivity extends AppCompatActivity {
    private MyGridView gridView;
    private TextView title;
    private ImageView backImage;
    private DemoApplication demoApplication;
    private ImageView bluebg;
    private TextView imgBtn_Add_more;
    private boolean btimgbg = true;
    protected Intent intent;

    private CustomView cv_pd;

    private BroadcastReceiver upbtimg = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
                    bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
                    break;
                case "UPBTIMG_DIS":
                    bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluef));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_pd);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPBTIMG");
        intentFilter.addAction("UPBTIMG_DIS");
        registerReceiver(upbtimg, intentFilter);
        initView();
    }

    private int[] imgs = {
//            R.mipmap.pdwrite,
            R.mipmap.read_id,
            R.mipmap.pduplock,
            R.mipmap.suo_faile,
//            R.mipmap.pdwrite,
            R.mipmap.write_id,
            R.mipmap.pdxz,
//            R.mipmap.pdwrite,
            R.mipmap.write_id,
            R.mipmap.temper,
            R.mipmap.read_write};
    private String[] img_text;

    private void initView() {
        // TODO Auto-generated method stub
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("生产管理");
        // 设置返回按键
        backImage = (ImageView) findViewById(R.id.common_title_back);
        backImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        imgBtn_Add_more = (TextView) findViewById(R.id.imgBtn_Add_more);
        imgBtn_Add_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MorePdActivity.this, SetMsActivity.class);
                startActivity(intent);
            }
        });
        bluebg = (ImageView) findViewById(R.id.bluebg);
        if (demoApplication.getMS() == 1 || demoApplication.getMS() == 2) {
            bluebg.setVisibility(View.VISIBLE);
            bluebg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //demoApplication.connect();
                    if (demoApplication.getConnect() == 1) {

                    } else {
                        if (btimgbg) {
                            demoApplication.connect();
                            btimgbg = false;
                        }
                    }
                }
            });
            if (demoApplication.getConnect() == 1) {
                bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
            } else {
                bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluef));
            }
        }
        gridView = (MyGridView) findViewById(R.id.gv);
        img_text = getResources().getStringArray(R.array.my_gird_img_text);
        setAdapter();
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        intent = new Intent(getApplicationContext(), PdReadIdActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), PdUnLockActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), PdLockActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getApplicationContext(), PdWriteIdActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getApplicationContext(), PdXiazhuangActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(getApplicationContext(), PdReWriteIdActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(getApplicationContext(), PdTemperActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        if (demoApplication.getConnect() == 1) {
                            intent = new Intent(getApplicationContext(), PdReadWriteDataActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MorePdActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        cv_pd = (CustomView) findViewById(R.id.cv_pd);
        cv_pd.setOnCustomViewOnclickListener(new OnCustomViewOnclickListener() {
            @Override
            public void onclick(View view, int child) {
                switch (child) {
                    case 1:
                        intent = new Intent(getApplicationContext(), PdReadIdActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), PdUnLockActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getApplicationContext(), PdLockActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getApplicationContext(), PdWriteIdActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(getApplicationContext(), PdXiazhuangActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(getApplicationContext(), PdReWriteIdActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent = new Intent(getApplicationContext(), PdTemperActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        if (demoApplication.getConnect() == 1) {
                            intent = new Intent(getApplicationContext(), PdReadWriteDataActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MorePdActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setAdapter() {
//        if (demoApplication.getMS() == 2) {
//            gridView.setAdapter(new MyGridAdapter(getApplicationContext(), img_text, imgs));
//        } else {
            String[] text = new String[img_text.length - 1];
            for (int i = 0; i < img_text.length - 1; i++) {
                text[i] = img_text[i];
            }
            int[] img = new int[imgs.length];
            for (int i = 0; i < imgs.length - 1; i++) {
                img[i] = imgs[i];
            }
            gridView.setAdapter(new MyGridAdapter(getApplicationContext(), text, img));
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(upbtimg);
    }

    @Override
    protected void onStart() {
        btimgbg = true;
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (demoApplication.getMS() == 1 || demoApplication.getMS() == 2) {
            bluebg.setVisibility(View.VISIBLE);
        } else {
            bluebg.setVisibility(View.INVISIBLE);
        }
        setAdapter();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        demoApplication = (DemoApplication) getApplicationContext();
        if (demoApplication.getMS() == 1 || demoApplication.getMS() == 2) {
            bluebg.setVisibility(View.VISIBLE);
        } else {
            bluebg.setVisibility(View.INVISIBLE);
        }
        if (demoApplication.getConnect() == 1) {
            bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
        } else {
            bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluef));
        }
    }
}
