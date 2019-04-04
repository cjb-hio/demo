package com.xyw.smartlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.activity.SetMsActivity;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;

import java.text.SimpleDateFormat;

import static com.baidu.mapapi.common.Logger.logE;
import static com.xyw.smartlock.bean.MyService.TAG;

public class PdLockActivity extends Activity implements SensorEventListener {

    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private TextView title;
    private ImageView backImage;
    private ImageView cancel_lock_btn;
    private Button lock_btn;
    private Button lock_tv_blue;

    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    //蓝牙相关
    private DemoApplication demoApplication;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;
    private LockUtil mLockUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_lock);
        demoApplication = (DemoApplication) getApplicationContext();
        mLockUtil = LockUtil.getInstance();
        // TODO Auto-generated method stub
        // 设置标题栏名称
        initView();
        //
        if (demoApplication.getMS() == 0) {
            setNfcForeground();
            // 判断是否有Nfc功能
            checkNFC();
            initBtnUnEnable(getString(R.string.near_lock), false, R.drawable.bottom_radius);
        } else {
            initBluetooth();
            initBtnEnable();
        }
        if (demoApplication.getMS() == 2) {
//            lockMs2();
        }
    }

    private void lockMs2() {
        if (demoApplication.getConnect() == 1) {
            demoApplication.ReadIdBegin(handler);
        } else {
            Toast.makeText(PdLockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
        }
    }

    private void initBtnUnEnable(String text, boolean clickable, int bottom_radius) {
        lock_tv_blue.setText(text);
        lock_tv_blue.setClickable(clickable);
        lock_tv_blue.setBackgroundResource(bottom_radius);
    }

    private void initBtnEnable() {
        if (demoApplication.getMS() == 1) {
            initBtnUnEnable(getString(R.string.click_sensor_lock), true, R.drawable.btn_bg);
        } else if (demoApplication.getMS() == 2) {
            initBtnUnEnable(getString(R.string.click_lock), true, R.drawable.btn_bg);
            if (demoApplication.getConnect() == 1) {
                lockMs2();
            } else {
                Toast.makeText(PdLockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
            }
        }
        lock_tv_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (demoApplication.getConnect() == 1) {
                    if (demoApplication.getMS() == 1) {
                        lockBegin();
                    } else if (demoApplication.getMS() == 2) {
                        lockMs2();
                    }
                } else {
                    Toast.makeText(PdLockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initBluetooth() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initView() {
        cancel_lock_btn = (ImageView) findViewById(R.id.cancel_lock_btn);
        cancel_lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdLockActivity.this.finish();
            }
        });
        lock_btn = (Button) findViewById(R.id.lock_btn);
        lock_tv_blue = (Button) findViewById(R.id.lock_tv_blue);
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("落锁");
        // 设置返回按键
        backImage = (ImageView) findViewById(R.id.common_title_back);
        backImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        // 初始化控件
        //demo = new Ntag_I2C_Demo(null, this);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private final static int REQ_CODE_SET_MS = 6;
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
                                // startActivity(new
                                // Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PdLockActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }
        } else {
            /*new AlertDialog.Builder(this).setTitle("没有可用的NFC。应用程序将被关闭。")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).show();*/
            new AlertDialog.Builder(this).setTitle("没有可用的NFC。去切换当前模式。")
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PdLockActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(PdLockActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
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
                    initBtnEnable();
                } else if (result.equals("false")) {
                    PdLockActivity.this.finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setNfcForeground() {
        // Create a generic PendingIntent that will be delivered to this
        // activity. The NFC stack will fill
        // in the intent with the details of the discovered tag before
        // delivering it to this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all NDEF based dispatches
        mFilters = new IntentFilter[]{
                // new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};

        // Setup a tech list for all NFC tags
        mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
        if (demoApplication.getMS() == 1) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } else if (demoApplication.getMS() == 0) {
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
        } else {
            if (mAdapter != null && newIntent == false) {
                mAdapter.disableForegroundDispatch(this);
            }
        }
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
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        String key = acacheUserBean.getKEYVALUE();
        demo.LockBegin(key, handler);

    }

    private byte[] lockSafe = new byte[4];//存放锁设备返回安全码
    private byte[] lockId = new byte[8];//存放锁设备返回锁ID
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
                            aCache = ACache.get(PdLockActivity.this);
                            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                            String keyValue = acacheUserBean.getKEYVALUE();
                            String lockState = mLockUtil.bytes2HexString(b_lock_state);
                            logE(TAG, "lockId = " + mLockUtil.bytes2HexString(lockId));
                            logE(TAG, "lockState = " + lockState);
                            logE(TAG, "lockSafe = " + mLockUtil.bytes2HexString(lockSafe));
                            //读完ID 马上落锁
                            demoApplication.bleControlLock(DemoApplication.LOCK_BEGIN,lockId, lockSafe, keyValue, handler);
                        }
                    }
                    break;
                case 13:
                    String LockResult = (String) msg.obj;
                    if ("true".equals(LockResult)) {
//                        ToastUtil.MyToast(getApplicationContext(), "落锁成功");
                        lock_btn.setBackgroundColor(Color.GREEN);
                        lock_btn.setText("落锁成功");
                    } else {
//                        ToastUtil.MyToast(getApplicationContext(), "落锁失败");
                        lock_btn.setBackgroundColor(Color.RED);
                        lock_btn.setText("落锁失败");
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
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14) {
                mVibrator.vibrate(100);
                TextView tv1 = (TextView) findViewById(R.id.textView1);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                lockBegin();
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    private void lockBegin() {
        if (demoApplication.getConnect() == 1) {
            aCache = ACache.get(this);
            acacheUserBean = new AcacheUserBean();
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            String key = acacheUserBean.getKEYVALUE();
            demoApplication.LockBegin(key, handler);
        } else {
            Toast.makeText(this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
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
}
