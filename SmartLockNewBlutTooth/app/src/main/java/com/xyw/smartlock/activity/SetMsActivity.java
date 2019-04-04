package com.xyw.smartlock.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.AcacheBtBean;
import com.xyw.smartlock.bean.AcacheSetBean;
import com.xyw.smartlock.bean.UartService;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.NfcUtils;

public class SetMsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private DemoApplication demoApplication;
    private TextView title;
    private ImageView backImg;
    private TextView deviceName;
    private ImageView BTgou;
    private ImageView NFCgou;
    private RelativeLayout BTline;
    private LinearLayout NFCline;
    private ImageView ble_direct_gou;
    private RelativeLayout ble_direct_line;
    private LinearLayout linearLayout_ble_direct;
    private TextView tv_device_state;
    private TextView tv_device_mac;
    private AcacheSetBean acacheSetBean;
    private ACache aCache;
    private TextView max;
    private LinearLayout bt_btn;
    private LinearLayout lishi_line;
    private TextView lishi;
    //蓝牙
    private BluetoothAdapter mBtAdapter = null;
    private BluetoothDevice mDevice = null;
    private UartService mService = null;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;
    private int mState = UART_PROFILE_DISCONNECTED;
    private AcacheBtBean acacheBtBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_acitivity);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        // 缓存数据
        aCache = ACache.get(this);
        acacheSetBean = new AcacheSetBean();

        initView();
    }

    private void initView() {
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(getString(R.string.set));
        // 监听返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        max = (TextView) findViewById(R.id.max);
        bt_btn = (LinearLayout) findViewById(R.id.bt_btn);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BTgou = (ImageView) findViewById(R.id.bt_gou);
        NFCgou = (ImageView) findViewById(R.id.nfc_gou);

        ble_direct_line = (RelativeLayout) findViewById(R.id.ble_direct_line);
        ble_direct_gou = (ImageView) findViewById(R.id.ble_direct_gou);
        linearLayout_ble_direct = (LinearLayout) findViewById(R.id.linearLayout_ble_direct);
        ble_direct_line.setOnClickListener(this);
        tv_device_state = (TextView) findViewById(R.id.tv_device_state);
        tv_device_mac = (TextView) findViewById(R.id.tv_device_mac);
//        tv_device_state.setVisibility(View.INVISIBLE);
        tv_device_mac.setVisibility(View.INVISIBLE);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        deviceName = (TextView) findViewById(R.id.deviceName);

        if (demoApplication.getMS() == 1) {
            BTgou.setVisibility(View.VISIBLE);
            NFCgou.setVisibility(View.INVISIBLE);
            ble_direct_gou.setVisibility(View.INVISIBLE);
            bt_btn.setVisibility(View.VISIBLE);
            linearLayout_ble_direct.setVisibility(View.INVISIBLE);
        } else if (demoApplication.getMS() == 0){
            BTgou.setVisibility(View.INVISIBLE);
            NFCgou.setVisibility(View.VISIBLE);
            ble_direct_gou.setVisibility(View.INVISIBLE);
            bt_btn.setVisibility(View.INVISIBLE);
            linearLayout_ble_direct.setVisibility(View.INVISIBLE);
        } else if (demoApplication.getMS() == 2) {
            BTgou.setVisibility(View.INVISIBLE);
            NFCgou.setVisibility(View.INVISIBLE);
            ble_direct_gou.setVisibility(View.VISIBLE);
            bt_btn.setVisibility(View.INVISIBLE);
            linearLayout_ble_direct.setVisibility(View.VISIBLE);
        }
        if (demoApplication.getBtName() != null && !demoApplication.getBtName().equals("未连接")) {
            deviceName.setVisibility(View.GONE);
            if (demoApplication.getConnect() == 1) {
                if (demoApplication.getMS() == 1) {
                    max.setText("已连接地址： " + demoApplication.getBtName());
                } else if (demoApplication.getMS() == 2) {
                    showBleDirect(true, "已连接地址：", demoApplication.getBtName());
                }
            } else if (demoApplication.getConnect() == 0) {
                if (demoApplication.getMS() == 1) {
                    max.setText("失去连接地址： " + demoApplication.getBtName());
                } else if (demoApplication.getMS() == 2) {
                    showBleDirect(true, "失去连接地址：", demoApplication.getBtName());
                }
            }
        }
        BTline = (RelativeLayout) findViewById(R.id.BT_line);
        NFCline = (LinearLayout) findViewById(R.id.NFC_line);
        BTline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoApplication.setMS(1);
                acacheSetBean.setMs("1");
                BTgou.setVisibility(View.VISIBLE);
                NFCgou.setVisibility(View.INVISIBLE);
                ble_direct_gou.setVisibility(View.INVISIBLE);
                bt_btn.setVisibility(View.VISIBLE);
                deviceName.setVisibility(View.GONE);
                max.setText("未连接设备： " + "");
                linearLayout_ble_direct.setVisibility(View.INVISIBLE);
//                if (!mBtAdapter.isEnabled()) {
//                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//                }
                if (demoApplication.getConnect() == 1) {
                    demoApplication.disconnect();//断开连接
                }
                Intent newIntent = new Intent(SetMsActivity.this, BTselectActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                aCache.put("SET", acacheSetBean);
                setFinishResult("true");
            }
        });
        NFCline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NfcUtils.getInstance().isHasNFC()) {
                    BTgou.setVisibility(View.INVISIBLE);
                    NFCgou.setVisibility(View.VISIBLE);
                    ble_direct_gou.setVisibility(View.INVISIBLE);
                    bt_btn.setVisibility(View.INVISIBLE);
                    linearLayout_ble_direct.setVisibility(View.INVISIBLE);
                    demoApplication.setMS(0);
                    acacheSetBean.setMs("0");
                    aCache.put("SET", acacheSetBean);
                    max.setText("已连接地址： " + "");
                    if (demoApplication.getConnect() == 1) {
                        demoApplication.disconnect();
                    }
                    if (!NfcUtils.getInstance().isOpenNFC()) {
                        NfcUtils.getInstance().requestOpenNFC(SetMsActivity.this);
                    }
                } else {
                    Toast.makeText(SetMsActivity.this, R.string.unsupport_nfc, Toast.LENGTH_SHORT).show();
                }
                setFinishResult("false");
            }
        });
        setFinishResult("false");
    }

    private Intent resultIntent;
    public static final String SET_MS_RESULT = "SET_MS_RESULT";
    private void setFinishResult(String result) {
        resultIntent = new Intent();
        resultIntent.putExtra(SET_MS_RESULT, result);
        SetMsActivity.this.setResult(REQ_RES_SET_MS, resultIntent);
    }

    private void showBleDirect(boolean isShow, String state, String mac) {
        if (isShow) {
            tv_device_state.setVisibility(View.VISIBLE);
            tv_device_mac.setVisibility(View.VISIBLE);
            tv_device_state.setText(state);
            tv_device_mac.setText(mac);
        } else {
            tv_device_state.setVisibility(View.INVISIBLE);
            tv_device_mac.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ble_direct_line:
                demoApplication.setMS(2);
                acacheSetBean.setMs("2");
                BTgou.setVisibility(View.INVISIBLE);
                NFCgou.setVisibility(View.INVISIBLE);
                ble_direct_gou.setVisibility(View.VISIBLE);
                bt_btn.setVisibility(View.INVISIBLE);
                linearLayout_ble_direct.setVisibility(View.VISIBLE);
                tv_device_mac.setText("");
                tv_device_state.setText("未连接设备：");
//                if (!mBtAdapter.isEnabled()) {
//                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//                }
                if (demoApplication.getConnect() == 1) {
                    demoApplication.setISManual(true);
                    demoApplication.disconnect();//断开连接
                }
                Intent newIntent = new Intent(SetMsActivity.this, BTselectActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                aCache.put("SET", acacheSetBean);
                setFinishResult("true");
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public final static int REQ_RES_SET_MS = 2;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (demoApplication.getMS() == 1) {
                        String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (deviceAddress != null) {
                            demoApplication.adconnect(deviceAddress);
                            deviceName.setVisibility(View.GONE);
                            max.setText("已连接地址： " + deviceAddress);
                        }
                    } else if (demoApplication.getMS() == 2) {
                        String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (deviceAddress != null) {
                            demoApplication.adconnect(deviceAddress);
                            showBleDirect(true, "已连接地址：", deviceAddress);
                        }
                    } else {
                        max.setText("已连接地址： " + "未连接");
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "蓝牙已打开 ", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "BT not enabled");
                }
                break;
            default:
                Log.e(TAG, "请求错误");
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
