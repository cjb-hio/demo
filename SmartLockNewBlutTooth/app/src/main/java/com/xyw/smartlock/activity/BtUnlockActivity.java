package com.xyw.smartlock.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.AcacheBtBean;
import com.xyw.smartlock.bean.AcacheSetBean;
import com.xyw.smartlock.bean.UartService;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.DemoApplication;

import java.text.DateFormat;
import java.util.Date;

public class BtUnlockActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
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
    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect,btnSend;
    private EditText edtMessage;
    private String myAddress;
    private AcacheBtBean acacheBtBean;
    private AcacheSetBean acacheSetBean;
    private ACache aCache;
    private boolean bg;
    private LinearLayout bt_btn;
    DemoApplication demoApplication;
    //其他
    private TextView title;
    private ImageView backImg;
    private TextView max;
    private TextView lishit;
    private TextView lishi;
    private LinearLayout lishi_line;
    private TextView tv_bt_unlock;
    private boolean btqx = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_ublock);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        // 缓存数据
        aCache = ACache.get(this);
        acacheBtBean = new AcacheBtBean();
        // 读取缓存数据
        acacheBtBean = (AcacheBtBean) aCache.getAsObject("BTADRESS");
        acacheSetBean = (AcacheSetBean) aCache.getAsObject("SET");
        //mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        /*if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/
        initView();
        service_init();
    }
    private void connect(){
        if (acacheBtBean!=null&& acacheBtBean.getAddress().length()>1){
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(acacheBtBean.getAddress());
            mService.connect(acacheBtBean.getAddress());
        }
    }
    private void initView(){
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(getString(R.string.bt_unlock));
        // 监听返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        max = (TextView) findViewById(R.id.max);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_bt_unlock = (TextView) findViewById(R.id.tv_bt_unlock);
        lishit = (TextView) findViewById(R.id.lishit);
        lishi = (TextView) findViewById(R.id.tv_lishi);
        lishi_line = (LinearLayout) findViewById(R.id.lishi_line);
        lishi_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        if (!(acacheSetBean!=null&&acacheSetBean.getMs().equals("1")||demoApplication.getMS()==1)){
            lishit.setVisibility(View.INVISIBLE);
            lishi.setVisibility(View.INVISIBLE);
        }
        if (acacheBtBean!=null&& acacheBtBean.getAddress().length()>1){
            lishi.setText(acacheBtBean.getAddress());
        }else {
            lishi.setText("无");
        }
        bt_btn = (LinearLayout) findViewById(R.id.bt_btn);
        bt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice!=null)
                {
                    mService.disconnect();
                }
                Intent newIntent = new Intent(BtUnlockActivity.this, BTselectActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
            }
        });
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // Handler Send button
        tv_bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send data to service
                byte[] value;
                value = hexStr2Bytes("55082a000000000000000087AA");
                mService.writeRXCharacteristic(value);
            }
        });
    }
    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mService.initialize()) {
                Log.e(TAG, "无法初始化蓝牙");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        //Handler events that received from UART service
        public void handleMessage(Message msg) {

        }
    };

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        tv_bt_unlock.setEnabled(true);
                        System.out.println("可以点击啦啦啦啦啦阿");
                        ((TextView) findViewById(R.id.deviceName)).setText("设备： "+mDevice.getName());
                        max.setText("地址： "+mDevice.getAddress());
                  //      listAdapter.add("["+currentDateTimeString+"] Connected to: "+ mDevice.getName());
                   //     messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
//                        btnConnectDisconnect.setText("Connect");
                        //edtMessage.setEnabled(false);
                        //tv_bt_unlock.setEnabled(false);
                        tv_bt_unlock.setEnabled(false);
                        ((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                //        listAdapter.add("["+currentDateTimeString+"] Disconnected to: "+ mDevice.getName());
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            //String text = new String(txValue, "UTF-8");
                            String text = bytes2HexString(txValue);
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            System.out.println("蓝牙设备返回的参数是："+text);
                 //           listAdapter.add("["+currentDateTimeString+"] RX: "+text);
                 //           messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                showMessage("设备不支持UART.断开");
                mService.disconnect();
            }


        }
    };
    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - 蓝牙 未启用");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

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
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    mService.connect(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "蓝牙已打开 ", Toast.LENGTH_SHORT).show();
                    btqx=true;
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "蓝牙打开 ", Toast.LENGTH_SHORT).show();
                    if (!btqx){
                        finish();
                    }
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


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    //发送数据字符串转换byte
    public static byte[] hexStr2Bytes(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    //byte转换字符串
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[ i ] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
}
