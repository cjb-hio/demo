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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;


public class SearchDialogActivity extends Activity implements SensorEventListener {

    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;

    private DemoApplication demoApplication;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;

    private LockUtil mLockUtil;
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
                            searchDialog_Id.setText(mLockUtil.bytes2HexString(lockId));
                        }
                    } else {
                        String lid = (String) msg.obj;
                        searchDialog_Id.setText(lid);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private TextView searchDialog_title;
    private EditText searchDialog_Id, searchDialog_Name, searchDialog_Address, searchDialog_meter;
    private Button searchDialog_button1, searchDialog_button2;
    private String searchID, searchName, searchAddress, searchMeterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog);
        demoApplication = (DemoApplication) getApplication();
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mLockUtil = LockUtil.getInstance();

        initView();

        // 判断是否有Nfc功能
        if (demoApplication.getMS() == 0) {
            checkNFC();
            setNfcForeground();
        } else {
            initBluetooth();
        }
    }

    private void initBluetooth() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initView() {
        searchDialog_title = (TextView) findViewById(R.id.searchDialog_title);
        searchDialog_title.setText(R.string.enter_conditSearch);
        searchDialog_Id = (EditText) findViewById(R.id.searchDialog_Id);
        searchDialog_Name = (EditText) findViewById(R.id.searchDialog_Name);
        searchDialog_Address = (EditText) findViewById(R.id.searchDialog_Address);
        searchDialog_meter = (EditText) findViewById(R.id.searchDialog_meter);
        searchDialog_button1 = (Button) findViewById(R.id.searchDialog_button1);
        searchDialog_button2 = (Button) findViewById(R.id.searchDialog_button2);
        searchDialog_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到文本框里面的值
                searchID = searchDialog_Id.getText().toString().trim();
                searchName = searchDialog_Name.getText().toString().trim();
                searchAddress = searchDialog_Address.getText().toString().trim();
                searchMeterId = searchDialog_meter.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("searchID", searchID);
                intent.putExtra("searchName", searchName);
                intent.putExtra("searchAddress", searchAddress);
                intent.putExtra("searchMeterId", searchMeterId);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        searchDialog_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private final static int REQ_CODE_SET_MS = 4;

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
                        SearchDialogActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }
        } else {
            new AlertDialog.Builder(this).setTitle("没有可用的NFC。去切换当前模式。")
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SearchDialogActivity.this.finish();
                        }
                    }).setPositiveButton("前往", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(SearchDialogActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
                }
            }).setCancelable(false).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SET_MS) {
            if (resultCode == SetMsActivity.REQ_RES_SET_MS) {
                String result = data.getStringExtra(SetMsActivity.SET_MS_RESULT);
                if (result.equals("true")) {
                    initBluetooth();
                } else if (result.equals("false")) {
                    SearchDialogActivity.this.finish();
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
        demo.ReadIdBegin(handler);
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
                readIdBegin();
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    private void readIdBegin() {
        if (demoApplication.getConnect() == 1) {
            demoApplication.ReadIdBegin(handler);
        } else {
            Toast.makeText(SearchDialogActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
