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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.activity.SetMsActivity;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.DemoApplication;

import java.text.SimpleDateFormat;

public class PdTemperActivity extends Activity implements SensorEventListener {

    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private TextView temper_textView02;
    private ImageView temper_button;
    private Button read_temper_tv_blue;
    private DemoApplication demoApplication;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    String temper = (String) msg.obj;
                    String strTemper = temper.substring(temper.length() - 4, temper.length());
                    temper_textView02.setText(strTemper);
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
        setContentView(R.layout.activity_temper);
        demoApplication = (DemoApplication) getApplicationContext();
        // TODO Auto-generated method stub
        // 设置标题栏名称
        initView();

        // 判断是否有Nfc功能
        if (demoApplication.getMS() == 0) {
            setNfcForeground();
            checkNFC();
            initBtnUnEnable(R.string.near_read_temper, false, R.drawable.bottom_radius);
        } else {
            initBluetoothData();
            initBtn();
        }
        if (demoApplication.getMS() == 2) {
            readTemperMs2();
        }
    }

    private void initBtnUnEnable(int near_read_temper, boolean clickable, int bottom_radius) {
        read_temper_tv_blue.setText(near_read_temper);
        read_temper_tv_blue.setClickable(clickable);
        read_temper_tv_blue.setBackgroundResource(bottom_radius);
    }

    private void initBtn() {
        if (demoApplication.getMS() == 1) {
            initBtnUnEnable(R.string.click_read_temper, true, R.drawable.btn_bg);
        } else if (demoApplication.getMS() == 2) {
            initBtnUnEnable(R.string.click_read, true, R.drawable.btn_bg);
        }
        read_temper_tv_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (demoApplication.getMS() == 1) {
                    readIdBegin();
                } else if (demoApplication.getMS() == 2) {
                    readTemperMs2();
                }
            }
        });
    }

    private void initBluetoothData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void readTemperMs2() {
        if (demoApplication.getConnect() == 1) {

        } else {
            Toast.makeText(PdTemperActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        read_temper_tv_blue = (Button) findViewById(R.id.read_temper_tv_blue);
        temper_textView02 = (TextView) findViewById(R.id.temper_textView02);
        temper_button = (ImageView) findViewById(R.id.temper_button);
        temper_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//demo = new Ntag_I2C_Demo(null, this);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private final static int REQ_CODE_SET_MS = 8;
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
                        PdTemperActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }
        } else {
//            ToastUtil.MyToast(getApplicationContext(), "没有可用的NFC");
            new AlertDialog.Builder(this).setTitle("没有可用的NFC。去切换当前模式。")
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PdTemperActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(PdTemperActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
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
                    initBluetoothData();
                    initBtn();
                } else if (result.equals("false")) {
                    PdTemperActivity.this.finish();
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
        if (demoApplication.getMS() == 1) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } else if (demoApplication.getMS() == 0) {
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
        long pattern[] = {0, 100};
        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
        doProcess(nfc_intent);
    }

    public void doProcess(Intent nfc_intent) {
        final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        demo = new Ntag_I2C_Demo(tag, this);
        demo.ReadIdEXBegin(handler);
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
                TextView tv1 = (TextView) findViewById(R.id.textView1);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                readIdBegin();
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    private void readIdBegin() {
        if (demoApplication.getConnect() == 1) {
            demoApplication.ReadIdBegin(handler);
        } else {
            Toast.makeText(PdTemperActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
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
