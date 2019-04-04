package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.utils.Util;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.pda.scan.ScanThread;
import cn.pda.serialport.SerialPort;

public class LockRegisteredActivity extends AppCompatActivity implements OnClickListener, SensorEventListener {

    //二维码扫描
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private TextView title;
    private TextView lockregist_lockposition;
    private TextView lockregist_datetime;
    private EditText lockregister_lockId, lockregist_name, lockregist_registaddress, lockregist_number,
            lockregist_areaname;
    private ImageView imageback, lockregist_positionimage2, lockregist_numberID, imageview_qrcode_button;
    private Spinner lockregist_type;
    private Button lockregist_complete;
    private double latitude;
    private double longitude;
    private String strAddress;
    private String lockID, lockName, registAddress, lockNumber, lockType, lockAreaName, lockposition, lockDateTime, equipmentNumber, ID;
    private ArrayList<String> typeList = new ArrayList<String>();
    private ArrayAdapter<String> arr_adapter;
    private AcacheUserBean LoginInfo;
    private ACache aCache;
    private String personNumber;
    private String LockType;
    private String strState;
    //NFC
    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private String areaAddress, areaName;
    private String lockRegistID;
    private String typeNumber;
    //请求网络的等待界面
    private LoadingDialog dialog;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private GeoCoder mSearch;

    private DemoApplication demoApplication;
    private ImageView bluebg;
    private boolean btimgbg = true;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;

    private LockUtil mLockUtil;

    private BroadcastReceiver upbtimg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
                    bluebg.setImageResource(R.mipmap.bluet);
                    break;
                case "UPBTIMG_DIS":
                    bluebg.setImageResource(R.mipmap.bluef);
                    break;
            }
        }
    };
    //使用Handler更新UI
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if (demoApplication.getMS() == 2) {
                        if (msg.obj != null) {
                            byte[] lock_data = (byte[]) msg.obj;
                            byte[] lockId = new byte[8];
                            for (int i = 0; i < 8; i++) {
                                lockId[i] = lock_data[i];
                            }
                            bluebg.setImageResource(R.mipmap.bluet);
                            lockregister_lockId.setText(mLockUtil.bytes2HexString(lockId));
                        }
                    } else {
                        String lid = (String) msg.obj;
                        System.out.println("lid=" + lid);
                        bluebg.setImageResource(R.mipmap.bluet);
                        lockregister_lockId.setText(lid);
                    }
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //禁用主题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lockregistered);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        registerReceiver();
        // 判断是否有Nfc功能
        if (demoApplication.getMS() == 0) {
            setNfcForeground();
            mAdapter = NfcAdapter.getDefaultAdapter(this);
        } else {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        mLockUtil = LockUtil.getInstance();
        Util.initSoundPool(this);

        // 获取缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        // 初始化控件
        initview();
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        // 设置先关参数
        initLocation();
        mLocationClient.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.rfid.FUN_KEY");
        registerReceiver(receiver,intentFilter);
    }

    private static ScanThread scanThread;
    private boolean keyDown = false;
    private boolean keyActive = true;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0) ;
            keyDown = intent.getBooleanExtra("keydown", false) ;
			Log.e("down", ""+keyDown);
            if(keyDown && keyActive) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_F1:
                        scanThread.scan();
                        break;
                    case KeyEvent.KEYCODE_F2:
                        scanThread.scan();
                        break;
                    case KeyEvent.KEYCODE_F3:
                        scanThread.scan();
                        break;
                    case KeyEvent.KEYCODE_F5:
                        scanThread.scan();
                        break;
                    case KeyEvent.KEYCODE_F4:
                        scanThread.scan();
                        break;
                }
            } else if (!keyDown) {
                keyActive = true;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        ScanThread.BaudRate = SerialPort.baudrate9600;
        ScanThread.Port = SerialPort.com0;
        ScanThread.Power = SerialPort.Power_Scaner;
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            // �����쳣
            Toast.makeText(getApplicationContext(), "serialport init fail", Toast.LENGTH_SHORT)
                    .show();
            return;
            // e.printStackTrace();
        }
        scanThread.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ScanThread.SCAN) {
                String data = msg.getData().getString("data");
                if(data != null){
                    if (keyDown)
                        keyActive = false;
                    Util.play(1, 0);
                    lockregist_number.setText(data);
                    scanThread.setTrig(false);
                }
            }
        }
    };

    /**
     * 注册蓝牙连接状态广播
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPBTIMG");
        filter.addAction("UPBTIMG_DIS");
        registerReceiver(upbtimg, filter);
    }


    /**
     * 初始化控件
     */
    private void initview() {
        lockregister_lockId = (EditText) findViewById(R.id.lockregister_lockId);
        // 设置标题栏
        title = (TextView) findViewById(R.id.lockregistered_tv_title);
        title.setText(R.string.lockregistered);
        // 设置标题返回按钮
        imageback = (ImageView) findViewById(R.id.lockregistered_title_back);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //扫一扫界面
        imageview_qrcode_button = (ImageView) findViewById(R.id.imageview_qrcode_button);
        imageview_qrcode_button.setOnClickListener(this);
        //蓝牙图标
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
                bluebg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bluet));
            } else {
                bluebg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bluef));
            }
        } else {
            bluebg.setVisibility(View.GONE);
        }
        // 绑定控件
        Intent intent = getIntent();
        if (null != intent) {
            areaAddress = intent.getStringExtra("areaAddress");
            lockRegistID = intent.getStringExtra("ID");
            typeNumber = intent.getStringExtra("TypeNumber");
            areaName = intent.getStringExtra("areaName");
        }
        lockregister_lockId.setText(lockRegistID);
        lockregist_name = (EditText) findViewById(R.id.lockregist_name);
        lockregist_name.setText(areaName);
        lockregist_registaddress = (EditText) findViewById(R.id.lockregist_registaddress);
        lockregist_registaddress.setText(areaAddress);
        if (typeNumber == null || typeNumber.equals("")) {
            typeNumber = "000000000000";
        }
        lockregist_number = (EditText) findViewById(R.id.lockregist_number);
        lockregist_number.setText(typeNumber);
        lockregist_datetime = (TextView) findViewById(R.id.lockregist_datetime);
        //获取系统时间
        lockregist_datetime.setText(ActivityUtils.getInstance().getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        lockregist_positionimage2 = (ImageView) findViewById(R.id.lockregist_positionimage2);
        lockregist_numberID = (ImageView) findViewById(R.id.lockregist_numberID);
        lockregist_numberID.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String lid = "";
                lid = lockregister_lockId.getText().toString().trim();
                StringBuffer sb = new StringBuffer();
                sb.append(lid);
                if (sb.length() > 16) {
                    Toast.makeText(LockRegisteredActivity.this, "锁ID不能超过16位", Toast.LENGTH_SHORT).show();
                    return;
                }
                while (sb.length() < 16) {
                    sb.delete(0, sb.length());
                    sb.append("0").append(lid);
                    lid = sb.toString();
                }
                if (!(lockregister_lockId.getText().toString().trim()).equals("")) {
                    Intent intent = new Intent(LockRegisteredActivity.this, PDALockRegistersdChildMeterActivity.class);
                    lockType = lockregist_type.getSelectedItem().toString().trim();
                    equipmentNumber = lockregist_number.getText().toString().trim();
                    intent.putExtra("ID", lid);
                    intent.putExtra("LockType", lockType);
                    startActivity(intent);
                } else {
                    ToastUtil.MyToast(LockRegisteredActivity.this, "锁ID不能为空");
                }
            }
        });
        lockregist_lockposition = (TextView) findViewById(R.id.lockregist_lockposition);
        lockregist_lockposition.setText(areaAddress);
        lockregist_lockposition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(lockregist_lockposition).equals("")) {
                    startBaiDuMap();
                }
            }
        });
        lockregist_complete = (Button) findViewById(R.id.lockregist_complete);
        lockregist_complete.setOnClickListener(this);
        lockregist_positionimage2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(lockregist_lockposition).equals("")) {
                    startBaiDuMap();
                }
            }
        });

        // 选择设备类型下拉框
        lockregist_type = (Spinner) findViewById(R.id.lockregist_type);

        // 给下拉框添加数据
        typeList.add("表箱");
        typeList.add("变压器室");
        typeList.add("台变配电室");
        typeList.add("台变计量箱");
        // 适配器
//        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        arr_adapter = new ArrayAdapter<String>(this, R.layout.permiss_spinner_item, typeList);
        // 设置样式
//        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 加载适配器
        lockregist_type.setAdapter(arr_adapter);

        if (demoApplication.getMS() == 2) {
            //模式为 蓝牙直连，且设备已连接，则读取设备ID
            if (demoApplication.getConnect() == 1) {
                demoApplication.ReadIdBegin(handler);
            } else if (demoApplication.getConnect() == 0) {
                Toast.makeText(LockRegisteredActivity.this, "未连接蓝牙设备", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 监听按钮
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lockregist_complete:
                // 获取文本框里
                lockID = lockregister_lockId.getText().toString().trim();
                lockName = lockregist_name.getText().toString().trim();
                registAddress = lockregist_registaddress.getText().toString().trim();
                lockNumber = lockregist_number.getText().toString().trim();
                lockType = lockregist_type.getSelectedItem().toString().trim();
                if (lockType.equals("表箱")) {
                    LockType = "1";
                } else if (lockType.equals("变压器室")) {
                    LockType = "2";
                } else if (lockType.equals("台变配电室")) {
                    LockType = "3";
                } else if (lockType.equals("台变计量箱")) {
                    LockType = "4";
                }
                lockDateTime = lockregist_datetime.getText().toString().trim();
                if (lockID.length() != 16) {
                    ToastUtil.MyToast(LockRegisteredActivity.this, "锁ID号是16位,请重新输入");
                    return;
                }
                if (lockName.equals("")) {
                    ToastUtil.MyToast(LockRegisteredActivity.this, "锁名称不能为空,请重新输入锁名称");
                    return;
                }
                if (registAddress.equals("")) {
                    ToastUtil.MyToast(LockRegisteredActivity.this, "注册地不能为空,请输入注册地");
                    return;
                }
                if (lockNumber.equals("")) {
                    ToastUtil.MyToast(LockRegisteredActivity.this, "设备编号不能为空,请输入设备编号");
                    return;
                }
                if (lockDateTime.equals("")) {
                    ToastUtil.MyToast(LockRegisteredActivity.this, "没有定位信息,请重新定位或者检查网络");
                    return;
                }
                volley_post();
                break;
            case R.id.imageview_qrcode_button:
                //二维码扫描功能
//                Intent intent1 = new Intent();
//                intent1.setClass(LockRegisteredActivity.this, QrcodeActivityCapture.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityForResult(intent1, SCANNIN_GREQUEST_CODE);
                //PDA条码枪
                scanThread.scan();
                break;
            default:
                break;
        }
    }

    //获取二维码扫描界面的值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle1 = data.getExtras();
                    //显示扫描到的内容
                    lockregist_number.setText(bundle1.getString("result"));
                }
                break;
            case REQ_BAIDUMAP:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra("areaAddress")) {
                        areaAddress = data.getStringExtra("areaAddress");
                        lockregist_lockposition.setText(areaAddress);
                    }
                    if (data.hasExtra("ID")) {
                        lockRegistID = data.getStringExtra("ID");
                        lockregister_lockId.setText(lockRegistID);
                    }
                    if (data.hasExtra("TypeNumber"))
                        typeNumber = data.getStringExtra("TypeNumber");
                    if (data.hasExtra("areaName")) {
                        areaName = data.getStringExtra("areaName");
                        lockregist_name.setText(areaName);
                    }
                }
                break;
            default:
                break;
        }
    }

    // 向服务端发起锁注册请求
    private void volley_post() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(LockRegisteredActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();

            // 提交的参数数据

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=insertlockinfo&LID=" + lockID + "&L_NAME=" + URLEncoder.encode(lockName)
                    + "&L_ADDR=" + URLEncoder.encode(registAddress, "UTF-8") + "&L_GPS_X=" + longitude + "&L_GPS_Y=" + latitude + "&L_BOX_NO=" + lockNumber + "&L_BOX_TYPE=" + LockType + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        Log.e("TAG", String.valueOf(arg0));
                        strState = arg0.getString("result");
                        String strTaskNo = arg0.getString("TASK_NO");

                        if (strState.equals("true")) {
                            dialog.dismiss();
                            ToastUtil.MyToast(LockRegisteredActivity.this, "注册成功");
                            Intent intent = new Intent(LockRegisteredActivity.this, LockFileActivity.class);
                            intent.putExtra("LockID", strTaskNo);
                            intent.putExtra("Used", "0");
                            System.out.println("strTaskNo=" + strTaskNo);
                            startActivity(intent);
                            finish();
                        } else {
                            dialog.dismiss();
                        }
                        System.out.println("------------" + strState + "--------------");

                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        ToastUtil.MyToast(LockRegisteredActivity.this, strState + "请重新输入ID");
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), strState);
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse arg0) {
                    try {
                        dialog.dismiss();
                        JSONObject jsonObject = new JSONObject(new String(arg0.data, "UTF-8"));
                        return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(arg0));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };

            //设置volley请求网络延迟
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //蓝牙相关
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14) {
                mVibrator.vibrate(100);
                if (demoApplication.getConnect() == 1) {
                    demoApplication.ReadIdBegin(handler);
                    bluebg.setImageResource(R.mipmap.blueconnect);
                } else {
                    Toast.makeText(LockRegisteredActivity.this, "蓝牙未连接设备 ", Toast.LENGTH_SHORT).show();
                }
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (demoApplication.getMS() == 1) {
            mSensorManager.unregisterListener(this);
        }
        super.onStop();
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
        if (demoApplication.getMS() == 1) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            if (mAdapter != null) {
                mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
            }

            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) && newIntent == false) {
                // give the UI some time to load, then execute the Demo
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
        super.onPause();
        if (demoApplication.getMS() == 1) {
            mSensorManager.unregisterListener(this);
        } else if (demoApplication.getMS() == 0) {
            if (mAdapter != null && newIntent == false) {
                mAdapter.disableForegroundDispatch(this);
            }
        }
        scanThread.close();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            scanThread.scan();
        }
        if(keyCode == 131 || keyCode == 132 || keyCode == 133 || keyCode == 134|| keyCode == 135){
            scanThread.scan();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent nfc_intent) {
        newIntent = true;
        super.onNewIntent(nfc_intent);
        // Set the pattern for vibration
        long pattern[] = {0, 100};

        // Vibrate on new Intent
        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
        doProcess(nfc_intent);
    }

    public void doProcess(Intent nfc_intent) {
        final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        demo = new Ntag_I2C_Demo(tag, this);
        demo.ReadIdBegin(handler);
    }

    private static final int REQ_BAIDUMAP = 23;

    private void startBaiDuMap() {
        new AlertDialog.Builder(this)
                .setMessage("你确定要调起百度地图吗？请打开WIFI开关和GPS，这会更容易找到定位信息")// 设置显示的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 添加确定按钮

                    @Override

                    public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件

                        Intent intent = new Intent(LockRegisteredActivity.this, BaiDuMapActivity.class);
                        String ID = lockregister_lockId.getText().toString().trim();
                        String TypeNumber = lockregist_number.getText().toString().trim();
                        intent.putExtra("TypeNumber", TypeNumber);
                        intent.putExtra("ID", ID);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        startActivityForResult(intent, REQ_BAIDUMAP);
//                        finish();
                    }

                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 添加返回按钮

            @Override

            public void onClick(DialogInterface dialog, int which) {// 响应事件

            }

        }).show();// 在按键响应事件中显示此对话框

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
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getSatelliteNumber() = " + location.getSatelliteNumber());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getOperators() = " + location.getOperators());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getStreetNumber() = " + location.getStreetNumber());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getAddrStr() = " + location.getAddrStr());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getBuildingID() = " + location.getBuildingID());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getCountryCode() = " + location.getCountryCode());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getFloor() = " + location.getFloor());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getNetworkLocationType() = " + location.getNetworkLocationType());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getBuildingID() = " + location.getBuildingID());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getProvince() = " + location.getProvince());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getSemaAptag() = " + location.getSemaAptag());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getCity() = " + location.getCity());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getDistrict() = " + location.getDistrict());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getBuildingName() = " + location.getBuildingName());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getTime() = " + location.getTime());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getStreet() = " + location.getStreet());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getLongitude() = " + location.getLongitude());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getLocType() = " + location.getLocType());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getCoorType() = " + location.getCoorType());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getLatitude() = " + location.getLatitude());
            Log.e("LockRegisteredActivity", "onReceiveLocation: location.getSpeed() = " + location.getSpeed());

            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("\n纬度 : ");
            sb.append(location.getLatitude());
            sb.append("\n经度 : ");
            sb.append(location.getLongitude());
            sb.append("\n地址 : ");
            sb.append(location.getAddrStr());
            sb.append("\n时间 : ");
            sb.append(location.getTime());

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("TAG", String.valueOf(latitude));
            Log.e("TAG", String.valueOf(longitude));

            //判断注册地是否填写内容,如果填写不操作,没有填写的话定位当前地址到注册地
            if ((lockregist_lockposition.getText().toString().trim()).equals("")) {
                lockregist_lockposition.setText(location.getAddrStr());
            }
            if ((lockregist_registaddress.getText().toString().trim()).equals("")) {
                lockregist_registaddress.setText(location.getAddrStr());
            }
            if ((lockregist_name.getText().toString().trim()).equals("")) {
                lockregist_name.setText(location.getAddrStr());
            }

            if (location != null)
                mLocationClient.stop();
            if (!(lockregist_lockposition.getText().toString().trim()).equals("")) {
                mLocationClient.stop();
            }

            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                ToastUtil.MyToast(LockRegisteredActivity.this, "定位成功");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                ToastUtil.MyToast(LockRegisteredActivity.this, "无法获取有效定位地址，请检查手机的网络状态,或者重新启动手机");
            }
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
        unregisterReceiver(upbtimg);
        unregisterReceiver(receiver);
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
        }
    }
}
