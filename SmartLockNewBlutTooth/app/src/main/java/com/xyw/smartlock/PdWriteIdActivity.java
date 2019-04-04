package com.xyw.smartlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.xyw.smartlock.activity.SetMsActivity;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import static com.baidu.mapapi.common.Logger.logE;

public class PdWriteIdActivity extends Activity implements SensorEventListener{

    private static final String TAG = "PdWriteIdActivity";

    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private TextView title;
    private ImageView backImage;
    private ImageView button;
    private EditText writeid_et;
    private Button write_tv_blue;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;

    private String maxValue;
    private long maxNumber;
    //定位当前地址
    private LocationClient mLocationClient = null;
    private double latitude;
    private double longitude;
    private BDLocationListener myListener = new MyLocationListener();
    private DemoApplication demoApplication;
    private boolean yaobg= true;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;

    //震动
    private Vibrator mVibrator = null;
    private ActivityUtils mActivityUtils;

    private byte[] lockSafe = new byte[4];//存放锁设备返回安全码
    private byte[] lockId = new byte[8];//存放锁设备返回锁ID
    private LockUtil mLockUtil;
    private Handler handler = new Handler() {

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
                            aCache = ACache.get(PdWriteIdActivity.this);
                            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                            String keyValue = acacheUserBean.getKEYVALUE();
                            String lockState = mLockUtil.bytes2HexString(b_lock_state);
                            logE(TAG, "lockId = " + mLockUtil.bytes2HexString(lockId));
                            logE(TAG, "lockState = " + lockState);
                            logE(TAG, "lockSafe = " + mLockUtil.bytes2HexString(lockSafe));
                            //读完ID 马上写ID
//                            writeId2();
                            maxValue = writeid_et.getText().toString().trim();
                            StringBuffer sb = new StringBuffer();
                            String wid = maxValue;
                            if (TextUtils.isEmpty(wid)) {
                                ToastUtil.MyToast(getApplicationContext(), "输入的值不能为空");
                                return;
                            }
                            while (wid.length() < 16) {
                                StringBuffer id = new StringBuffer();
                                id.append("0").append(wid);//左补0
                                wid = id.toString();
                            }
                            String timeScale = mActivityUtils.getTimeScale();
                            sb.append(timeScale).append(wid);
                            Log.e(TAG, "handleMessage: sb = " + sb.toString());
                            demoApplication.bleWriteLock(lockId, lockSafe, keyValue, handler);
//                            demoApplication.WriteIdBegin(sb.toString(), handler);
                        }
                    }
                    break;
                case 11:
                    String wResult = (String) msg.obj;
                    if ("true".equals(wResult)) {
                        maxValue = writeid_et.getText().toString();
                        maxNumber = Long.valueOf(maxValue);
                        maxNumber++;
                        maxValue = String.valueOf(maxNumber);

                        while (maxValue.length() < 16) {
                            StringBuffer id = new StringBuffer();
                            id.append("0").append(maxValue);//左补0
                            maxValue = id.toString();
                        }
                        WriteID();
                    } else {
                        ToastUtil.MyToast(getApplicationContext(), "写入失败");
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
        setContentView(R.layout.activity_pd_write_id);
        demoApplication = (DemoApplication) getApplicationContext();
        mLockUtil = LockUtil.getInstance();
        // TODO Auto-generated method stub
        // 设置标题栏名称
        initView();
        acacheUserBean = new AcacheUserBean();
        mActivityUtils = ActivityUtils.getInstance();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        // 设置先关参数

        initLocation();
        // 开始网络定位
        mLocationClient.start();
        //获取最大ID
        volley_writeID();
        //
        setNfcForeground();
        // 判断是否有Nfc功能
        if (demoApplication.getMS() == 0){
            checkNFC();
            initWriteBtnUnEnable(false, R.drawable.bottom_radius, getString(R.string.near_write));
        }else {
            initBluetooth();
            initWriteBtn();
        }
    }

    private void writeIdMs2() {
        if (demoApplication.getConnect() == 1) {
//            demoApplication.ReadIdBegin(handler);
            maxValue = writeid_et.getText().toString().trim();
            StringBuffer sb = new StringBuffer();
            String wid = maxValue;
            if (TextUtils.isEmpty(wid)) {
                ToastUtil.MyToast(getApplicationContext(), "输入的值不能为空");
                return;
            }
            while (wid.length() < 16) {
                StringBuffer id = new StringBuffer();
                id.append("0").append(wid);//左补0
                wid = id.toString();
            }
            String mac = mActivityUtils.getTimeScale();
            sb.append(mac).append(wid);
            Log.e(TAG, "writeIdMs2: s  = " + sb.toString());
            demoApplication.bleReadId(sb.toString(), handler);
        } else {
            Toast.makeText(PdWriteIdActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
        }
    }

    private void writeId2() {
        maxValue = writeid_et.getText().toString().trim();
        StringBuffer sb = new StringBuffer();
        String wid = maxValue;
        if (TextUtils.isEmpty(wid)) {
            ToastUtil.MyToast(getApplicationContext(), "输入的值不能为空");
            return;
        }
        while (wid.length() < 16) {
            StringBuffer id = new StringBuffer();
            id.append("0").append(wid);//左补0
            wid = id.toString();
        }
        String mac = mActivityUtils.getEnterMac(demoApplication.getBtName());
        sb.append(mac).append(wid);
        demoApplication.WriteIdBegin(sb.toString(),handler);
    }

    private void initWriteBtnUnEnable(boolean clickable, int bottom_radius, String text) {
        write_tv_blue.setClickable(clickable);
        write_tv_blue.setBackgroundResource(bottom_radius);
        write_tv_blue.setText(text);
    }

    private void initWriteBtn() {
        if (demoApplication.getMS() == 1) {
            initWriteBtnUnEnable(true, R.drawable.btn_bg, getString(R.string.click_sensor_write));
        } else if (demoApplication.getMS() == 2) {
            initWriteBtnUnEnable(true, R.drawable.btn_bg, getString(R.string.click_write));
        }
        write_tv_blue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (demoApplication.getMS() == 1) {
                    writeIdBegin();
                } else if (demoApplication.getMS() == 2) {
                    writeIdMs2();
                }
            }
        });
    }

    private void initBluetooth() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }

    private void WriteID() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 请求地址
            final String URL = HttpServerAddress.PDWRITEID + "&LID=" + (maxNumber-1l) + "&L_GPS_X=" + longitude + "&L_GPS_Y=" + latitude + "&USER_CONTEXT=" + acacheUserBean.getUSER_CONTEXT();
            Log.e("URL", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        String strState = arg0.getString("result");
                        if (strState != null
                                && strState.equalsIgnoreCase("true")) {
//                            writeid_et.setText(arg0.getString("LID"));
                            ToastUtil.MyToast(getApplicationContext(), "写入成功,并成功提交");
                            writeid_et.setText(maxValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), arg0.toString());
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(
                                arg0.data, "UTF-8"));
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(arg0));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initView() {
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("写ID");
        // 设置返回按键
        backImage = (ImageView) findViewById(R.id.common_title_back);
        backImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        // 初始化控件
        writeid_et = (EditText) findViewById(R.id.write_et);
        button = (ImageView) findViewById(R.id.write_btn);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        write_tv_blue = (Button) findViewById(R.id.write_tv_blue);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
    }


    private final static int REQ_CODE_SET_MS = 7;
    private final static int REQ_RES_SET_MS = 2;
    private void checkNFC() {
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                new AlertDialog.Builder(this).setTitle("NFC关闭了").setMessage("去设置")
                        .setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (android.os.Build.VERSION.SDK_INT >= 16) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                                } else {
                                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PdWriteIdActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }
        } else {
            new AlertDialog.Builder(this).setTitle("没有可用的NFC。去切换当前模式。")
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PdWriteIdActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(PdWriteIdActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
                }
            }).setCancelable(false).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SET_MS) {
            if (resultCode == REQ_RES_SET_MS) {
                String result = data.getStringExtra(SetMsActivity.SET_MS_RESULT);
                if (result.equals("true")) {
                    initBluetooth();
                    initWriteBtn();
                    write_tv_blue.setBackgroundResource(R.drawable.btn_bg);
                } else if (result.equals("false")) {
                    PdWriteIdActivity.this.finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
        if (demoApplication.getMS()==1){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }else {
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
        if (demoApplication.getMS()==1){
            mSensorManager.unregisterListener(this);
        }else {
            if (mAdapter != null && newIntent == false) {
                mAdapter.disableForegroundDispatch(this);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent nfc_intent) {
        newIntent = true;
        super.onNewIntent(nfc_intent);
        long pattern[] = {0, 100};

        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
        doProcess(nfc_intent);
    }

    private void volley_writeID() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 请求地址
            final String URL = HttpServerAddress.PDLOCKMAXID + "&add=1" + "&USER_CONTEXT=" + acacheUserBean.getUSER_CONTEXT().toString();
            Log.e("URL", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("onResponse", arg0.toString());
                    try {
                        String strState = arg0.getString("result");
                        if (strState != null
                                && strState.equalsIgnoreCase("true")) {
                            writeid_et.setText(arg0.getString("LID"));
                        } else {
                            ToastUtil.MyToast(getApplicationContext(), arg0.toString() + "2");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(),
                            arg0.toString());
                    Log.e("ErrorListener", arg0.toString());
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(
                                arg0.data, "UTF-8"));
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(arg0));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doProcess(Intent nfc_intent) {
        final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        demo = new Ntag_I2C_Demo(tag, this);
        maxValue = writeid_et.getText().toString();
        String wid = maxValue;
        if (TextUtils.isEmpty(wid)) {
            ToastUtil.MyToast(getApplicationContext(), "输入的值不能为空");
            return;
        }
        while (wid.length() < 16) {
            StringBuffer id = new StringBuffer();
            id.append("0").append(wid);//左补0
            wid = id.toString();
        }
       demo.WriteIdBegin(wid, handler);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
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
            // 定位纬度,经度
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            System.out.println("latitude=" + latitude);
            System.out.println("longitude=" + longitude);
            if (location != null)
                mLocationClient.stop();
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                ToastUtil.MyToast(getApplicationContext(), "定位成功");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                ToastUtil.MyToast(getApplicationContext(), "无法获取有效定位地址，请检查手机的网络状态,或者重新启动手机");
            }
        }
    }
    /**
     * 设置相关参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.disableCache(true);// 禁止启用缓存定位
        mLocationClient.setLocOption(option);
    }
    //蓝牙相关
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER){
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14&&yaobg){
                yaobg = false;
                mVibrator.vibrate(100);
                TextView tv1 = (TextView)findViewById(R.id.textView1);
                SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                writeIdBegin();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        yaobg = true;
                    }
                }, 2000);
                mVibrator.vibrate(new long[]{100,10,100,1000}, -1);
            }
        }
    }

    private void writeIdBegin() {
        if (demoApplication.getConnect()==1){
            writeId();
        }else {
            Toast.makeText(this, "蓝牙未连接设备 ", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeId() {
        maxValue = writeid_et.getText().toString().trim();
        String wid = maxValue;
        if (TextUtils.isEmpty(wid)) {
            ToastUtil.MyToast(getApplicationContext(), "输入的值不能为空");
            return;
        }
        while (wid.length() < 16) {
            StringBuffer id = new StringBuffer();
            id.append("0").append(wid);//左补0
            wid = id.toString();
        }
        demoApplication.WriteIdBegin(wid,handler);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (demoApplication.getMS()==1){
            mSensorManager.unregisterListener(this);
        }
        super.onStop();
    }
}

