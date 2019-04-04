package com.xyw.smartlock.nfctest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xyw.smartlock.MainActivity;
import com.xyw.smartlock.R;
import com.xyw.smartlock.activity.BTselectActivity;
import com.xyw.smartlock.bean.MyView;
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.UnLock;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.Fiale_dailog;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class UnlockActivity extends Activity implements SensorEventListener {

    private static final String TAG = "UnlockActivity";
    private static final int REQUEST_SELECT_DEVICE = 3;

    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    //定位当前地址
    private double latitude;
    private double longitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
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
    private MyView myView;
    private TextView tv_Unlock;
    private Fiale_dailog fiale_dailog;
    private float mcount0 = 0;
    private ImageView iv_anim;
    private ImageView iv_jingao;
    private boolean fg = false;
    private boolean fg2 = false;
    //操作数据库
    private DateBaseUtil dateBaseUtil;
    private List<UnLock> list = new ArrayList<UnLock>();
    private String OP_DT, OP_PHONE, Result, BeginTime, EndTime, OP_NAME, AREA_NAME, KEYVALUE, Area_id, MAXVER, ROLE_ID, USER_CONTEXT;

    //蓝牙相关
    private DemoApplication demoApplication;
    private TextView tv_unlock_address;
    private ImageView bluebg;
    private boolean btimgbg = true;
    private boolean yaobg = true;
    //提示音
    private SoundPool pool;
    private int sourceid;
    private int sourceidfalse;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;
    private BroadcastReceiver upbtimg = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
//            tv_unlock_address.setText("地址：" + intent.getStringExtra("dizhi"));
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
//                    bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
                    bluebg.setImageResource(R.mipmap.bluet);
                    tv_unlock_address.setText("地址：" + intent.getStringExtra("dizhi"));
                    openLock();
                    break;
                case "UPBTIMG_DIS":
//                    bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluef));
                    bluebg.setImageResource(R.mipmap.bluef);
                    tv_unlock_address.setText("地址：" + intent.getStringExtra("dizhi"));
                    break;
            }
        }
    };


    private LockUtil mLockUtil;

    private byte[] lockSafe = new byte[4];//存放锁设备返回安全码
    private byte[] lockId = new byte[8];//存放锁设备返回锁ID
    //用Handler更新UI
    private Handler handler = new Handler() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if (demoApplication.getMS() == 2) {
                        if (msg.obj != null) {
                            byte[] lock_data = (byte[]) msg.obj;
                            byte[] b_lock_state = {lock_data[12]};
                            byte[] b_lock_power = {lock_data[13]};
                            for (int i = 0; i < 8; i++) {
                                lockId[i] = lock_data[i];
                            }
                            for (int j = 0; j < 4; j++) {
                                lockSafe[j] = lock_data[j + 8];
                            }
                            lid = mLockUtil.bytes2HexString(lockId);
                            String lockState = mLockUtil.bytes2HexString(b_lock_state);
                            String lockPower = mLockUtil.bytes2HexString(b_lock_power);
                            Log.e(TAG, "lockId = " + mLockUtil.bytes2HexString(lockId));
                            Log.e(TAG, "lockState = " + lockState);
                            Log.e(TAG, "lockSafe = " + mLockUtil.bytes2HexString(lockSafe));
                            //读完ID 马上开锁
                            if (title.getText().equals("解锁")) {
                                demoApplication.bleOpenLock(lockId, lockSafe, keyValue, handler);
                            } else {
                                demoApplication.bleControlLock(DemoApplication.LOCK_BEGIN,lockId, lockSafe, keyValue, handler);
                            }
                        }
                    } else {
                        if (msg.obj != null) {
                            lid = (String) msg.obj;
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~lid = " + lid + "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            lockID = lid.toString();
                        }
                    }
                    break;
                case 12:
                    result = (String) msg.obj;
                    Log.e(TAG, "handleMessage: result = " + result);
                    System.out.println("result=" + result);

                    if ("true".equals(result)) {
                        if (!fg) {
                            fg = true;
                            unlockSuccessCallback();
                        }
                    } else {
                        if (!fg2) {
                            fg2 = true;
                            unlockFaileCallback();
                        }
                    }
                    break;
                case 13:
                    result = (String) msg.obj;
                    System.out.println("result=" + result);
                    if ("true".equals(result)) {
                        if (!fg) {
                            fg = true;
                            operatingState = "落锁成功";
                            //解锁成功上传解锁信息
                            if (!lid.equals("")) {
                                isServiceRunning();
                            }
                            myView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.suo_true));
                            tv_Unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.zhuangtai_false_bg));
                            tv_Unlock.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.zhuangtai_true));
                            tv_Unlock.setText("落锁成功");
                            myView.setVisibility(View.GONE);
                            //动画效果
                            iv_anim.setVisibility(View.VISIBLE);
                            AnimationDrawable ad = (AnimationDrawable) iv_anim.getBackground();
                            ad.start();
                            //延迟两秒跳转
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    Intent intent = new Intent(UnlockActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);
                        }
                    } else {
                        if (!fg2) {
                            fg2 = true;
                            operatingState = "落锁失败";
                            //解锁失败上传解锁信息
                            if (!lid.equals("")) {
                                isServiceRunning();
                            }
                            tv_Unlock.setText("落锁失败");
                            tv_Unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.zhuangtai_false_bg));
                            tv_Unlock.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                            myView.setVisibility(View.GONE);
                            iv_jingao.setVisibility(View.VISIBLE);
                            AnimationDrawable adc = (AnimationDrawable) iv_jingao.getBackground();
                            adc.start();
                            //延迟两秒跳转
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    iv_jingao.setVisibility(View.GONE);
                                    myView.setVisibility(View.VISIBLE);
                                    tv_Unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.zhuangtai_wait_bg));
                                    tv_Unlock.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.zhuangtai_wait));
                                    tv_Unlock.setText("请靠近感应落锁");
                                    fg2 = false;
                                }
                            }, 5000);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private void unlockFaileCallback() {
        operatingState = "解锁失败";
        //解锁失败上传解锁信息
        if (!lid.equals("")) {
            try {
                netWork();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        pool.play(sourceidfalse, 1, 1, 0, 0, 1);
        bluebg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bluet));
        tv_Unlock.setText("开锁失败");
        tv_Unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.zhuangtai_false_bg));
        tv_Unlock.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
        myView.setVisibility(View.GONE);
        iv_jingao.setVisibility(View.VISIBLE);
        AnimationDrawable adc = (AnimationDrawable) iv_jingao.getBackground();
        adc.start();
        //延迟两秒跳转
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                iv_jingao.setVisibility(View.GONE);
                myView.setVisibility(View.VISIBLE);
                tv_Unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.zhuangtai_wait_bg));
                tv_Unlock.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.zhuangtai_wait));
                if (demoApplication.getMS() == 0) {
                    tv_Unlock.setText("请靠近感应解锁");
                } else if (demoApplication.getMS() == 1) {
                    tv_Unlock.setText("点击或摇一摇解锁");
                } else if (demoApplication.getMS() == 2) {
                    tv_Unlock.setText("点击解锁");
                }
                fg2 = false;
            }
        }, 3000);
    }

    private void unlockSuccessCallback() {
        operatingState = "解锁成功";
        pool.play(sourceid, 1, 1, 1, 0, 1);
//        bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
        bluebg.setImageResource(R.mipmap.bluet);
        //解锁成功上传解锁信息
        if (lid != null && !lid.equals("")) {
            try {
                netWork();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        myView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.suo_true));
        tv_Unlock.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.zhuangtai_true_bg));
        tv_Unlock.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.zhuangtai_true));
        tv_Unlock.setText("成功解锁");
        myView.setVisibility(View.GONE);
        //动画效果
        iv_anim.setVisibility(View.VISIBLE);
        AnimationDrawable ad = (AnimationDrawable) iv_anim.getBackground();
        ad.start();
        //延迟两秒跳转
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //Intent intent = new Intent(UnlockActivity.this, MainActivity.class);
                //startActivity(intent);
                if (isConnectOpen) {
                    isConnectOpen = false;
                    demoApplication.setISManual(true);
                    demoApplication.disconnect();
                }
                finish();
            }
        }, 3000);
    }

    //判断当前网络是否可用，可用的话启动服区进行定位，不可用的话将开锁信息存在数据库中
    private void netWork() throws UnsupportedEncodingException {
        //判断当前网络是否可用
        if (isNetworkAvailable(UnlockActivity.this)) {
            //有网的话启动Service进行定位并且向后台传值
            isServiceRunning();
        } else {
            //没网的话启动数据库，把信息暂时存储在数据库中
            dateBase();
        }
    }
    //判断服务是否已经运行

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.xyw.smartlock.nfctest.UnlockService".equals(service.service.getClassName())) {
                ToastUtil.MyToast(UnlockActivity.this, "当前网络状态不好，可能无法上传开锁信息");
                try {
                    Thread.sleep(5000);
                    final Intent intent = new Intent();
                    intent.setAction("ITOP.MOBILE.SIMPLE.SERVICE.SENSORSERVICE");
                    stopService(intent);
                    Log.e("TAG", "服务关闭了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                Intent intent = new Intent(UnlockActivity.this, UnlockService.class);
                intent.putExtra("Lid", lid);
                intent.putExtra("operatingState", operatingState);
                startService(intent);
            }
        }
        return false;
    }

    //创建接收器对象，并在onReceive方法中接收键值为id的数据
    private BroadcastReceiver getmsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String btmsg = intent.getStringExtra("msg");
            Message message = new Message();
            message.what = 10;
            message.obj = btmsg;
            handler.sendMessage(message);
            Message message1 = new Message();
            message1.what = 12;
            message1.obj = "true";
            handler.sendMessage(message1);
        }
    };

    private boolean isConnectOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        demoApplication = (DemoApplication) getApplicationContext();
        registerReceiver(upbtimg, new IntentFilter("UPBTIMG"));
        registerReceiver(getmsg, new IntentFilter("GETMSG"));
        //指定声音池的最大音频流数目为10，声音品质为5
        pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //载入音频流，返回在池中的id
        sourceid = pool.load(this, R.raw.locktrue, 1);
        sourceidfalse = pool.load(this, R.raw.lockfalse, 0);
        if (demoApplication.getMS() == 1) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //初始化设置
            initview();
            //判断当前时间是否在分配的时间之内
            dateTime();
        } else if (demoApplication.getMS() == 0){
            if (isNetworkAvailable(UnlockActivity.this)) {
                //初始化设置
                initview();
                //NFC功能
                setNfcForeground();
                //判断当前时间是否在分配的时间之内,若果在,启动NFC功能,若果不在,不启动NFC功能
                dateTime();
            } else {
                ToastUtil.MyToast(UnlockActivity.this, "当前没有可用网络,请打开数据或者WIFI网络");
                dateBaseUtil = new DateBaseUtil(UnlockActivity.this);
                list = dateBaseUtil.queryUnLock();
                if (list.size() > 14) {
                    ToastUtil.MyToast(UnlockActivity.this, "离线开锁只能开15次，15次以后请联网开锁！");
                    //初始化设置
                    initview();
                } else {
                    //初始化设置
                    initview();
                    //NFC功能
                    setNfcForeground();
                    //判断当前时间是否在分配的时间之内,若果在,启动NFC功能,若果不在,不启动NFC功能
                    dateTime();
                }
            }
        } else if (demoApplication.getMS() == 2) {
            //初始化设置
            initview();
            //判断当前时间是否在分配的时间之内
            dateTime();
            if (demoApplication.getConnect() == 0) {
                isConnectOpen = true;
                Intent newIntent = new Intent(UnlockActivity.this, BTselectActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                if (deviceAddress != null) {
                    demoApplication.adconnect(deviceAddress);
                } else {
                    isConnectOpen = false;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Nfc功能
    public void setNfcForeground() {
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};
        mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        if (demoApplication.getMS() == 1) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } else if (demoApplication.getMS() == 0){
            if (mAdapter != null) {
                mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
            }

            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) && newIntent == false) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onNewIntent(getIntent());
                    }
                }, 100);
            }
            newIntent = false;
        }
    }

    @Override
    public void onPause() {
        if (demoApplication.getMS() == 1) {
            mSensorManager.unregisterListener(this);
        } else {
            if (mAdapter != null && newIntent == false) {
                mAdapter.disableForegroundDispatch(this);
            }
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent nfc_intent) {
        newIntent = true;
        super.onNewIntent(nfc_intent);
        // Set the pattern for vibration
        long pattern[] = {0, 100};
        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
        doProcess(nfc_intent);
    }

    public void doProcess(Intent nfc_intent) {
        final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        demo = new Ntag_I2C_Demo(tag, this);
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        keyValue = LoginInfo.getKEYVALUE().toString().trim();
        if (title.getText().equals("解锁")) {
            demo.UnLockBegin(keyValue, handler);
        } else {
            demo.LockBegin(keyValue, handler);
        }
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
        bluebg = (ImageView) findViewById(R.id.bluebg);
        if (demoApplication.getMS() == 1 || demoApplication.getMS() == 2) {
            bluebg.setVisibility(View.VISIBLE);
            bluebg.setOnClickListener(new View.OnClickListener() {
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
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        // 设置先关参数
        initLocation();

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
        // 开始网络定位
        mLocationClient.start();
        //自定义控件
        myView = (MyView) findViewById(R.id.my_view);
        tv_Unlock = (TextView) findViewById(R.id.tv_Unlock);
        iv_anim = (ImageView) findViewById(R.id.iv_anim);
        iv_jingao = (ImageView) findViewById(R.id.iv_jingao);
        //获取数据库
        dateBaseUtil = new DateBaseUtil(UnlockActivity.this);
        // 获取缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        keyValue = LoginInfo.getKEYVALUE().trim();
        unlock_currentArea = (TextView) findViewById(R.id.unlock_currentArea);
        unlock_currentArea.setText(LoginInfo.getAREA_NAME().trim());
        opNumber = LoginInfo.getOP_NO().trim();
        operationType = "1";
        personNumber = LoginInfo.getUSER_CONTEXT().trim();

        tv_unlock_address = (TextView) findViewById(R.id.tv_unlock_address);
        //蓝牙相关
        if (demoApplication.getMS() == 1) {
            tv_Unlock.setText("点击或摇一摇解锁");
            tv_Unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (yaobg) {
                        yaobg = false;
                        if (demoApplication.getConnect() == 1) {
                            lockControl();
                        } else {
                            Toast.makeText(UnlockActivity.this, "未连接蓝牙设备 ", Toast.LENGTH_SHORT).show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                yaobg = true;
                            }
                        }, 5000);
                    }
                }
            });
            tv_unlock_address.setText("地址：" + demoApplication.getBtName());
        } else if (demoApplication.getMS() == 2) {
            if (demoApplication.getConnect() == 1){
                //如果蓝牙已建立连接则直接开锁
                openLock();
            } else {
                Toast.makeText(UnlockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
            }
            mLockUtil = LockUtil.getInstance();
            tv_Unlock.setText("点击解锁");
            tv_unlock_address.setText("地址：" + demoApplication.getBtName());
            tv_Unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (demoApplication.getConnect() == 1) {
                        if (title.getText().equals("解锁")) {
                            demoApplication.ReadIdBegin(handler);
                        } else {
                            demoApplication.ReadIdBegin(handler);
                        }
                    } else {
                        Toast.makeText(UnlockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void openLock() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                demoApplication.ReadIdBegin(handler);
//                demoApplication.bleOpenLock(demoApplication.getLock_id(), demoApplication.getLock_safe(), keyValue, handler);
            }
        }, 2000);
    }

    /**
     * 检查当前网络是否可用
     */
    private boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //定位当前的地址
    /**
     * 设置相关参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("\n时间 : ");
            sb.append(location.getTime());
            sb.append("\n错误代码 : ");
            sb.append(location.getLocType());
            sb.append("\n纬度 : ");
            sb.append(location.getLatitude());
            sb.append("\n经度 : ");
            sb.append(location.getLongitude());
            sb.append("\n半径 : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\n速度 : ");
                sb.append(location.getSpeed());
                sb.append("\n卫星 : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\n方向 : ");
                sb.append(location.getDirection());
                sb.append("\n地址 : ");
                sb.append(location.getAddrStr());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\n地址 : ");
                sb.append(location.getAddrStr());
                sb.append(location.getLatitude());
                sb.append(location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            }
            // 定位纬度,经度
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("TAG", String.valueOf(latitude));
            Log.e("TAG", String.valueOf(longitude));
            System.out.println("latitude=" + latitude);
            System.out.println("longitude=" + longitude);
            loctionInfo = location.toString().trim();
            if (location != null)
                mLocationClient.stop();
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                ToastUtil.MyToast(UnlockActivity.this, "定位成功");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                ToastUtil.MyToast(UnlockActivity.this, "无法获取有效定位地址，请检查手机的网络状态,或者重新启动手机");
            }
        }

    }

    //判断当前时间是否在分配的时间之内
    public void dateTime() {
        //获取当前系统时间
        Date currentTime = new Date();
        //获取缓存数据
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        String BeginTime = LoginInfo.getBeginTime();
        String EndTime = LoginInfo.getEndTime();
        String strBeginTime = BeginTime.replaceAll("/", "-");
        String strEndTime = EndTime.replaceAll("/", "-");
        System.out.println("strBeginTime=" + strBeginTime);
        System.out.println("strEndTime=" + strEndTime);
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strbeginDate = null;
        Date strendDate = null;
        try {
            strbeginDate = fmt.parse(strBeginTime.toString());
            strendDate = fmt.parse(strEndTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((currentTime.getTime() - strbeginDate.getTime()) > 0 && (strendDate.getTime() - currentTime.getTime()) > 0) {
            if (demoApplication.getMS() == 0) {
                // 获取NFC实例
                mAdapter = NfcAdapter.getDefaultAdapter(this);
            }
        } else {
            ToastUtil.MyToast(UnlockActivity.this, "您的操作时间已到期,请重新申请操作时间");
        }

    }


    //当没网时解锁成功或解锁失败时进行数据库操作
    public void dateBase() throws UnsupportedEncodingException {

        UnLock unLock = new UnLock();
        //向数据库里面添加数据
        unLock.setLid(lockID);
        unLock.setGPS_X(String.valueOf(longitude));
        unLock.setGPS_Y(String.valueOf(latitude));
        unLock.setOP_NO(opNumber);
        unLock.setOP_TYPE(operationType);
        unLock.setOP_RET(URLEncoder.encode(operatingState, "utf-8"));
        unLock.setOP_DATETIME(lockTime);
        unLock.setUSER_CONTEXT(personNumber);
        dateBaseUtil.Insert3(unLock);
    }

    //蓝牙相关
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14) && yaobg) {
                yaobg = false;
                mVibrator.vibrate(100);
                TextView tv1 = (TextView) findViewById(R.id.textView1);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                if (demoApplication.getConnect() == 1) {
                    lockControl();
                    bluebg.setImageResource(R.mipmap.blueconnect);
                } else {
                    Toast.makeText(UnlockActivity.this, "蓝牙未连接设备 ", Toast.LENGTH_SHORT).show();
                }
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        yaobg = true;
                    }
                }, 5000);
            }
        }
    }

    private void lockControl() {
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        keyValue = LoginInfo.getKEYVALUE().trim();
        if (title.getText().equals("解锁")) {
            demoApplication.UnLockBegin(keyValue, handler);
        } else {
            demoApplication.LockBegin(keyValue, handler);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(upbtimg);
        unregisterReceiver(getmsg);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (demoApplication.getMS() == 1) {
            mSensorManager.unregisterListener(this);
        }
        super.onStop();
    }
}
