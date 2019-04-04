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
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.activity.SetMsActivity;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.ToastUtil;

import java.text.SimpleDateFormat;

import static com.baidu.mapapi.common.Logger.logE;


public class PdUnLockActivity extends Activity implements SensorEventListener {

    private static final String TAG = "PdUnLockActivity";

    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private TextView title;
    private ImageView backImage;
    private Button button;
    private EditText unlock_EditText;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    //蓝牙相关
    private DemoApplication demoApplication;
    private TextView tv_unlock_address;
    private boolean yaobg = true;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;

    private LockUtil mLockUtil;
    private ImageView iv_lock_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_un_lock);
        // TODO Auto-generated method stub
        // 设置标题栏名称
        demoApplication = (DemoApplication) getApplicationContext();
        mLockUtil = LockUtil.getInstance();
        initView();
        //
        if (demoApplication.getMS() == 0) {
            setNfcForeground();
            // 判断是否有Nfc功能
            checkNFC();
            initBtnUnEnable(R.string.near_unlock, false, R.drawable.bottom_radius);
        } else {
            initBluetooth();
            initBtnEnable();
        }
        if (demoApplication.getMS() == 2) {
            //如果蓝牙已经建立连接，则直接开锁
            unlockMs2();
        }
    }

    private void unlockMs2() {
        if (demoApplication.getConnect() == 1) {
            demoApplication.ReadIdBegin(handler);
        } else {
            Toast.makeText(PdUnLockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
        }
    }

    private void initBtnUnEnable(int near_unlock, boolean clickable, int bottom_radius) {
        button.setText(near_unlock);
        button.setClickable(clickable);
        button.setBackgroundResource(bottom_radius);
    }

    private void initBtnEnable() {
        if (demoApplication.getMS() == 1) {
            initBtnUnEnable(R.string.click_senser_unlock, true, R.drawable.btn_bg);
        } else if (demoApplication.getMS() == 2) {
            initBtnUnEnable(R.string.click_unlock, true, R.drawable.btn_bg);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (demoApplication.getMS() == 1) {
                    unlockBegin();
                } else if (demoApplication.getMS() == 2) {
                    unlockMs2();
                }
            }
        });
    }

    private void initBluetooth() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initView() {
        button = (Button) findViewById(R.id.unlock_btn);
        iv_lock_btn = (ImageView) findViewById(R.id.iv_lock_btn);
        iv_lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdUnLockActivity.this.finish();
            }
        });
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("解锁");
        backImage = (ImageView) findViewById(R.id.common_title_back);
        backImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        unlock_EditText = (EditText) findViewById(R.id.unlock_EditText);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private final static int REQ_CODE_SET_MS = 5;
    private final static int REQ_RES_SET_MS = 2;

    private void checkNFC() {
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                new AlertDialog.Builder(this).setTitle("NFC关闭了").setMessage("去设置")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
                        PdUnLockActivity.this.finish();
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
                            PdUnLockActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(PdUnLockActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
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
                    initBtnEnable();
                } else if (result.equals("false")) {
                    PdUnLockActivity.this.finish();
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

        if (demoApplication.getMS() == 2) {
            button.setBackgroundResource(R.drawable.btn_bg);
        } else {
            button.setBackgroundResource(R.drawable.bottom_radius);
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
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        if (unlock_EditText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "输入的key值不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String key = acacheUserBean.getKEYVALUE() + unlock_EditText.getText().toString().trim();
        demo.UnLockBegin(key, handler);

    }

    //蓝牙相关
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14 && yaobg) {
                yaobg = false;
                mVibrator.vibrate(100);
                TextView tv1 = (TextView) findViewById(R.id.textView1);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                unlockBegin();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        yaobg = true;
                    }
                }, 2000);
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    private void unlockBegin() {
        if (demoApplication.getConnect() == 1) {
            acacheUserBean = new AcacheUserBean();
            aCache = ACache.get(this);
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            String key = acacheUserBean.getKEYVALUE();
            System.out.println("KEYYYYYYYYYYYYY====" + key);
            demoApplication.UnLockBegin(key, handler);
        } else {
            Toast.makeText(PdUnLockActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
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
                            aCache = ACache.get(PdUnLockActivity.this);
                            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                            String keyValue = acacheUserBean.getKEYVALUE();
                            String lockState = mLockUtil.bytes2HexString(b_lock_state);
                            logE(TAG, "lockId = " + mLockUtil.bytes2HexString(lockId));
                            logE(TAG, "lockState = " + lockState);
                            logE(TAG, "lockSafe = " + mLockUtil.bytes2HexString(lockSafe));
                            //读完ID 马上开锁
                            demoApplication.bleOpenLock(lockId, lockSafe, keyValue, handler);
                        }
                    }
                    break;
                case 12:
                    String unLockResult = (String) msg.obj;
                    if ("true".equals(unLockResult)) {
                        ToastUtil.MyToast(getApplicationContext(), "解锁成功");
                        button.setText("解锁成功");
                        button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                    } else {
                        ToastUtil.MyToast(getApplicationContext(), "解锁失败");
                        button.setText("解锁失败");
                        button.setBackgroundColor(Color.RED);
                    }
                    break;
                default:
                    break;
            }
        }

    };
}
