package com.xyw.smartlock.activity;

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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;

import java.text.SimpleDateFormat;

import static com.xyw.smartlock.bean.MyService.TAG;

/**
 * Created by 19428 on 2016/5/11.
 */
public class KeyStateActivity extends Activity implements SensorEventListener{
    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String lid;
    private TextView key_tv_ts;
    private DemoApplication demoApplication;
    private boolean yaobg= true;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;

    private LockUtil mLockUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keystate);
        // TODO Auto-generated method stub
        demoApplication = (DemoApplication) getApplicationContext();
        mLockUtil = LockUtil.getInstance();
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        keyValue = acacheUserBean.getKEYVALUE();
        //设置为true点击区域外消失
//        setFinishOnTouchOutside(true);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (demoApplication.getMS() == 0){
            //判断是否有NFC功能
            checkNFC();
            //启动NFC功能
            setNfcForeground();
        }else {
            initBluetooth();
        }

    }

    private void initBluetooth() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        key_tv_ts = (TextView) findViewById(R.id.key_tv_ts);
        if (demoApplication.getMS()==1){
            key_tv_ts.setVisibility(View.VISIBLE);
            key_tv_ts.setText("点击或摇一摇下装");
            key_tv_ts.setBackgroundResource(R.drawable.text_color_bg);
            key_tv_ts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (demoApplication.getConnect()==1){
                        String zoneId = acacheUserBean.getArea_id();
                        String key = acacheUserBean.getKEYVALUE();
                        demoApplication.UpdateKeyBegin(zoneId,key,handler);
                    }else {
                        Toast.makeText(KeyStateActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (demoApplication.getMS() == 2) {
            key_tv_ts.setVisibility(View.VISIBLE);
            key_tv_ts.setText("点击下装");
            key_tv_ts.setBackgroundResource(R.drawable.text_color_bg);
            key_tv_ts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(KeyStateActivity.this, "点击下装", Toast.LENGTH_SHORT).show();

                    if (demoApplication.getConnect() == 1) {
                        StringBuffer sb = new StringBuffer();
                        String lock_data = "1111111111111111";
                        sb.append(ActivityUtils.getInstance().getTimeScale()).append(lock_data);
                        demoApplication.bleReadId(sb.toString(), handler);
                    } else {
                        Toast.makeText(KeyStateActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                    }

                    /*if (demoApplication.getConnect()==1){
                        String zoneId = acacheUserBean.getArea_id();
                        String key = acacheUserBean.getKEYVALUE();
                        demoApplication.UpdateKeyBegin(zoneId,key,handler);
                    }else {
                        Toast.makeText(KeyStateActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                    }*/
                }
            });
        }
    }

    private static final int REQ_CODE_SET_MS = 10;
    private final static int REQ_RES_SET_MS = 2;
    //判断是否有nfc功能
    private void checkNFC() {
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                new AlertDialog.Builder(this).setTitle(R.string.nfc_close).setMessage(R.string.go_setting)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
                        KeyStateActivity.this.finish();
                    }
                }).show();
            }
        } else {
            /*new AlertDialog.Builder(this).setTitle("没有可用的NFC。应用程序将被关闭。")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).show();*/
                 new AlertDialog.Builder(this).setTitle(R.string.no_nfc_go_setting)
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            KeyStateActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         startActivityForResult(new Intent(KeyStateActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
                     }
                 }).setCancelable(false).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SET_MS) {
            if (resultCode == REQ_RES_SET_MS) {
                Log.e("KeyStateActivity", "onActivityResult: REQ_RES_SET_MS = " + REQ_RES_SET_MS);
                String result = data.getStringExtra(SetMsActivity.SET_MS_RESULT);
                if (result.equals("true")) {
                    initBluetooth();
                } else if (result.equals("false")) {
                    KeyStateActivity.this.finish();
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

    public void doProcess(Intent nfc_intent) {
        final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        demo = new Ntag_I2C_Demo(tag, this);
//        demo.ReadIdBegin(handler);
        String zoneId = acacheUserBean.getArea_id();
        String key = acacheUserBean.getKEYVALUE();
        demo.UpdateKeyBegin(zoneId, key, handler);
        //没有区域ID的缓存

    }

    private byte[] lockSafe = new byte[4];//存放锁设备返回安全码
    private byte[] lockId = new byte[8];//存放锁设备返回锁ID
    private String keyValue;
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
                            String lockState = mLockUtil.bytes2HexString(b_lock_state);
                            String lockPower = mLockUtil.bytes2HexString(b_lock_power);
                            Log.e(TAG, "lockId = " + mLockUtil.bytes2HexString(lockId));
                            Log.e(TAG, "lockState = " + lockState);
                            Log.e(TAG, "lockSafe = " + mLockUtil.bytes2HexString(lockSafe));

                            demoApplication.bleXiaZhuang(lockId, lockSafe, keyValue, handler);
                        }
                    } else {
                        lid = (String) msg.obj;
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~lid=" + lid + "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                    break;
                case 14:
                    String UpdateKeyResult = (String) msg.obj;
                    System.out.println("UpdateKeyResult=" + UpdateKeyResult);
                    if ("true".equals(UpdateKeyResult)) {
                        Intent intent = new Intent();
                        intent.putExtra("keyState", "下装成功");
                        intent.putExtra("Lid", lid);
                        KeyStateActivity.this.setResult(RESULT_OK, intent);
                        // 关闭Activity
                        KeyStateActivity.this.finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("keyState", "下装失败");
                        KeyStateActivity.this.setResult(RESULT_OK, intent);
                        // 关闭Activity
                        KeyStateActivity.this.finish();
                    }
                    break;
                default:
                    break;
            }
        }

    };

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
                if (demoApplication.getConnect()==1){
                    String zoneId = acacheUserBean.getArea_id();
                    String key = acacheUserBean.getKEYVALUE();
                    demoApplication.UpdateKeyBegin(zoneId,key,handler);
                }else {
                    Toast.makeText(this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
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

    //用Handler取到锁返回来的信息
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 10:
//                    lid = (String) msg.obj;
//                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~lid=" + lid + "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                    if (lid != null) {
//                        handler2.sendEmptyMessage(0);
//
//                    }else{
//                        ToastUtil.MyToast(getApplication(),"请靠近锁!");
//                    }
//
//            }
//        }
//
//    };
//    private Handler handler2 = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            System.out.println("888888888888888888888888888888888888888888888888888888888888888888888");
//            switch (msg.what) {
//                case 14:
//                    String UpdateKeyResult = (String) msg.obj;
//                    System.out.println("UpdateKeyResult=" + UpdateKeyResult);
//                    if ("true".equals(UpdateKeyResult)) {
//                        Intent intent = new Intent();
//                        intent.putExtra("Lid", lid);
//                        intent.putExtra("keyState", "下装成功");
//                        KeyStateActivity.this.setResult(RESULT_OK, intent);
//                        // 关闭Activity
//                        KeyStateActivity.this.finish();
//                    } else {
//                        Intent intent = new Intent();
//                        intent.putExtra("keyState", "下装失败");
//                        KeyStateActivity.this.setResult(RESULT_OK, intent);
//                        // 关闭Activity
//                        KeyStateActivity.this.finish();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
}