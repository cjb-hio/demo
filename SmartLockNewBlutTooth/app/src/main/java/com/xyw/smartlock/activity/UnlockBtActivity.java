package com.xyw.smartlock.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.MyView;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.Fiale_dailog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnlockBtActivity extends AppCompatActivity implements SensorEventListener {
    //获取缓存数据
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String personNumber, keyValue;//身份令牌
    private ImageView titleBack;
    private TextView title;
    private ImageView iv_loading;
    private String result, lid;
    private String loctionInfo;
    private String lockID;
    private String opNumber;
    private String operationType;
    private String lockTime;
    private String operatingState;
    private TextView unlock_currentArea;
    private TextView tv_unlock_address;
    private MyView myView;
    private TextView tv_Unlock;
    private Fiale_dailog fiale_dailog;
    private float mcount0 = 0;
    private ImageView iv_anim;
    private ImageView iv_jingao;
    private boolean fg = false;
    private boolean fg2 = false;
    private LocationClient mLocationClient = null;
    private DemoApplication demoApplication;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;

    //震动
    private Vibrator mVibrator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        getSupportActionBar().hide();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        demoApplication = (DemoApplication) getApplicationContext();
        initview();
    }

    private void initview() {
        //获取系统时间
        Date currentTime = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lockDateTime = fmt.format(currentTime);
        String date = lockDateTime.substring(0, 10);
        String time = lockDateTime.substring(11, 19);
        lockTime = date + "%20" + time;

        //初始化标题栏
        titleBack = (ImageView) findViewById(R.id.common_title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.unlockactviity);
        // 声明LocationClient类
        //mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        //mLocationClient.registerLocationListener(myListener);
        // 设置先关参数
        //initLocation();

        //落锁
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().equals("解锁")) {
                    title.setText("落锁");
                } else {
                    title.setText("解锁");
                }
            }
        });
        tv_unlock_address = (TextView) findViewById(R.id.tv_unlock_address);
        tv_unlock_address.setText("地址："+demoApplication.getBtName());
        // 开始网络定位
//        mLocationClient.start();
        //自定义控件
        myView = (MyView) findViewById(R.id.my_view);
        tv_Unlock = (TextView) findViewById(R.id.tv_Unlock);
        tv_Unlock.setText("摇一摇开锁");
        tv_Unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (demoApplication.getConnect()==1){
                Intent intentop=new Intent();
                intentop.setAction("OPEN");
                sendBroadcast(intentop);
                }else {
                    Toast.makeText(UnlockBtActivity.this, "蓝牙未连接设备 ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_anim = (ImageView) findViewById(R.id.iv_anim);
        iv_jingao = (ImageView) findViewById(R.id.iv_jingao);
        //获取数据库
        //dateBaseUtil = new DateBaseUtil(UnlockBtActivity.this);
        // 获取缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        keyValue = LoginInfo.getKEYVALUE().toString().trim();
        unlock_currentArea = (TextView) findViewById(R.id.unlock_currentArea);
        unlock_currentArea.setText(LoginInfo.getAREA_NAME().toString().trim());
        opNumber = LoginInfo.getOP_NO().toString().trim();
        operationType = "1";
        personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER){
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14){
                mVibrator.vibrate(100);
                TextView tv1 = (TextView)findViewById(R.id.textView1);
                SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                if (demoApplication.getConnect()==1){
                    Intent intentop=new Intent();
                    intentop.setAction("OPEN");
                    sendBroadcast(intentop);
                }else {
                    Toast.makeText(UnlockBtActivity.this, "蓝牙未连接设备 ", Toast.LENGTH_SHORT).show();
                }
                mVibrator.vibrate(new long[]{100,10,100,1000}, -1);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
}
