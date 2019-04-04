package com.xyw.smartlock.bean;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xyw.smartlock.MainActivity;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.UnLock;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.GetService;
import com.xyw.smartlock.utils.GetSpace;
import com.xyw.smartlock.utils.LockUtil;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyService extends Service implements RadioGroup.OnCheckedChangeListener {
    private boolean running = false;
    private String data = "默认信息";
    private String op_no;
    private String user_context_number;
    private ACache mCache;
    private String role_id;
    private String datapath;
    private AcacheUserBean acacheUserBean;
    // private List<TaskBean> mlist;
    private String process = "kong";
    private Intent intent;
    private DateBaseUtil dateBaseUtil;
    private List<UnLock> list = new ArrayList<UnLock>();
    private String result;
    private int jsq = 0;
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
    DemoApplication demoApplication;

    private LockUtil mLockUtil;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public class Binder extends android.os.Binder {

        public void setData(String data) {
            MyService.this.data = data;
        }

        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //System.out.println("重启服务");
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent contextIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setTicker("下线通知：您的账号已在另一台android手机登录")
                .setContentTitle("您的账号已下线")
                .setContentText("请重新登录")
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
                .setContentIntent(contextIntent)
                .build();
        //notification.flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻 Flag
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 缓存数据
        //接收器的动态注册，Action必须与Service中的Action一致
        demoApplication = (DemoApplication) getApplicationContext();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(UARTStatusChangeReceiver, myServiceIntentFilter());
        mLockUtil = LockUtil.getInstance();
        mCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        acacheBtBean = new AcacheBtBean();
        // 读取缓存数据
        acacheBtBean = (AcacheBtBean) mCache.getAsObject("BTADRESS");
        service_init();
        // 读取缓存数据
        AcacheUserBean LoginInfo = (AcacheUserBean) mCache.getAsObject("LoginInfo");
        op_no = LoginInfo.getOP_NO().toString();
        user_context_number = LoginInfo.getUSER_CONTEXT().toString();
        role_id = LoginInfo.getROLE_ID().toString();
        datapath = HttpServerAddress.BASE_URL + "?m=heard" + "&user_context=" + user_context_number;
        running = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                int num = 0;
                int count = 0;
                while (running) {
                    try {
                        num++;
                        System.out.println("发送请求");
                        process = new GetService().run(datapath);
                        System.out.println("心跳返回的结果是" + process);
                        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBtAdapter.isEnabled() && mDevice == null) {
                            //connect();
                        }
                        test();
                        jsq++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (process.equals("logout")) {
                        if (count == 1) {
                            showNotification();
                            intent = new Intent();
                            intent.setAction("ACTION_MY");
                            sendBroadcast(intent);
                        } else {
                            try {
                                count++;
                                sleep(5000);
                                continue;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (callback != null) {
                        callback.onDataChange(process);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        mLockUtil = null;
        running = false;
        System.out.println("服务关闭了");
        demoApplication.setConnect(0);
        demoApplication.setBtName("无");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;
        super.onDestroy();
    }

    private Callback callback = null;

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static interface Callback {
        void onDataChange(String data);
    }

    /**
     * 蓝牙相关操作
     */
    private void connect() {
        if (acacheBtBean != null && acacheBtBean.getAddress().length() > 1) {
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(acacheBtBean.getAddress());
            mService.connect(acacheBtBean.getAddress());
            demoApplication.setBtName(acacheBtBean.getAddress());
        }
    }

    private void adconnect(String address) {
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        mService.connect(address);
        demoApplication.setBtName(address);
    }

    private void disconnect() {
        if (mDevice != null) {
            mService.disconnect();
            System.out.println("断开蓝牙连接");
            demoApplication.setConnect(0);
            demoApplication.setBtName("无");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter myServiceIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ReadIdBegin");
        intentFilter.addAction("WriteIdBegin");
        intentFilter.addAction("UnLockBegin");
        intentFilter.addAction("LockBegin");
        intentFilter.addAction("UpdateKeyBegin");
        intentFilter.addAction("CONNECT");
        intentFilter.addAction("ADCONNECT");
        intentFilter.addAction("DISCONNECT");
        intentFilter.addAction("READ_DATA");
        intentFilter.addAction("WRITE_DATA");
        intentFilter.addAction("OPEN_WRITE_READ");
        intentFilter.addAction("CLOSE_WRITE_READ");
        return intentFilter;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.START_WRITE_SERVER);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mService.initialize()) {
                Log.e(TAG, "无法初始化蓝牙");
                //finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                //tv_bt_unlock.setEnabled(true);
//                demoApplication.setConnect(1);
//                intent = new Intent();
//                intent.setAction("UPBTIMG");
//                intent.putExtra("dizhi", mDevice.getAddress());
//                sendBroadcast(intent);
//                mState = UART_PROFILE_CONNECTED;
                //((TextView) findViewById(R.id.deviceName)).setText("设备： "+mDevice.getName());
                //max.setText("地址： "+mDevice.getAddress());
                //      listAdapter.add("["+currentDateTimeString+"] Connected to: "+ mDevice.getName());
                //     messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "UART_DISCONNECT_MSG");
//                        btnConnectDisconnect.setText("Connect");
                //edtMessage.setEnabled(false);
                //tv_bt_unlock.setEnabled(false);
                //tv_bt_unlock.setEnabled(false);
                //((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                //        listAdapter.add("["+currentDateTimeString+"] Disconnected to: "+ mDevice.getName());
                mState = UART_PROFILE_DISCONNECTED;
                mService.close();
                //setUiState();

                demoApplication.setConnect(0);
                if (demoApplication.getMS() == 2) {
                    Log.e(TAG, "onReceive: demoApplication.getMS() == 2 " + demoApplication.getCurrentAddress());
                    if (!demoApplication.getIsManual()) {
                        mService.connect(demoApplication.getCurrentAddress());
                    } else {
                        demoApplication.setISManual(false);
                    }
                }

                intent = new Intent();
                intent.setAction("UPBTIMG_DIS");
                intent.putExtra("dizhi", mDevice.getAddress());
                sendBroadcast(intent);
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            if (action.equals(UartService.START_WRITE_SERVER)) {
                demoApplication.setConnect(1);
                intent = new Intent();
                intent.setAction("UPBTIMG");
                intent.putExtra("dizhi", mDevice.getAddress());
                sendBroadcast(intent);
                mState = UART_PROFILE_CONNECTED;
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                Message msg1 = new Message();
                Message msg2 = new Message();
                byte[] lid = new byte[8];
                String lidstr;
                int i;
                if (checkPacket(txValue) == 1) {
                    if (demoApplication.getMS() == 2) {
                        byte[] tv = new byte[1];
                        tv[0] = (byte) (txValue[2] & 0x3f);
                        Log.e(TAG, "onReceive: 收到反馈" + bytes2HexString(tv));
                        Log.e(TAG, "onReceive: txValue = " + bytes2HexString(txValue));
                        switch ((byte) (txValue[2] & 0x3f)) {
                            case 0x01://readId
                                byte[] lock_data = new byte[14];
                                if (txValue[2] < ((byte) 0xC0)) {
                                    for (int j = 0; j < 14; j++) {
                                        lock_data[j] = txValue[3 + j];
                                    }
                                    msg1.what = 10;
                                    msg1.obj = lock_data;
                                    demoApplication.mHandler.sendMessage(msg1);
                                } else {
                                    demoApplication.mHandler.sendEmptyMessage(10);
                                }
                                break;
                            case 0x31://开锁
                                if (demoApplication.getMS() == 2) {
                                    Log.e(TAG, "onReceive: 收到开锁反馈");
                                    if (txValue[2] < 0xc0) {
                                        if (txValue[2] < ((byte) 0xC0)) {
                                            msg2.what = 12;
                                            msg2.obj = "true";
                                        } else {
                                            msg2.what = 12;
                                            msg2.obj = "false";
                                        }
                                        demoApplication.mHandler.sendMessage(msg2);
                                    }
                                }
                                break;
                            case 0x32://落锁
                                if (txValue[2] < ((byte) 0xC0)) {
                                    msg2.what = 13;
                                    msg2.obj = "true";
                                } else {
                                    msg2.what = 13;
                                    msg2.obj = "false";
                                }
                                demoApplication.mHandler.sendMessage(msg2);
                                break;
                            case 0x34://写ID
                                msg1.what = 11;
                                if (txValue[2] < ((byte) 0xC0)) {
                                    msg1.obj = "true";
                                    Log.e(TAG, "onReceive: 写ID成功");
                                } else {
                                    msg1.obj = "false";
                                    Log.e(TAG, "onReceive: 写ID失败");
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                                Log.e(TAG, "onReceive: 收到写ID反馈");
                                break;
                            case 0x35://下装
                                msg1.what = 14;
                                if (txValue[2] < ((byte) 0xC0)) {
                                    msg1.obj = "true";
                                    Log.e(TAG, "onReceive: 下装成功");
                                } else {
                                    msg1.obj = "false";
                                    Log.e(TAG, "onReceive: 下装失败");
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                                Log.e(TAG, "onReceive: 收到下装反馈");
                                break;
                            case 0x11:
                                msg1.what = 21;
                                if (txValue[2] < ((byte) 0xC0)) {
                                    byte[] lock_dt = new byte[12];
                                    for (int j = 0; j < 12; j++) {
                                        lock_dt[j] = txValue[3 + j];
                                    }
                                    msg1.obj = lock_dt;
                                    Log.e(TAG, "onReceive: 读数据成功");
                                } else {
                                    Log.e(TAG, "onReceive: 读数据失败");
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                                Log.e(TAG, "onReceive: 收到读数据反馈");
                                break;
                            case 0x12:
                                msg1.what = 22;
                                if (txValue[2] < ((byte) 0xC0)) {
                                    msg1.obj = true;
                                    Log.e(TAG, "onReceive: 写数据成功");
                                } else {
                                    msg1.obj = false;
                                    Log.e(TAG, "onReceive: 写数据失败");
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                                Log.e(TAG, "onReceive: 收到写数据反馈");
                                break;
                            case 0x38:
                                msg1.what = 23;
                                if (txValue[2] < ((byte) 0xC0)) {
                                    msg1.obj = true;
                                    Log.e(TAG, "onReceive: 关闭内部读写成功");
                                } else {
                                    msg1.obj = false;
                                    Log.e(TAG, "onReceive: 关闭内部读写失败");
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                                Log.e(TAG, "onReceive: 收到关闭内部读写反馈");
                                break;
                            case 0x37:
                                msg1.what = 24;
                                if (txValue[2] < ((byte) 0xC0)) {
                                    msg1.obj = true;
                                    Log.e(TAG, "onReceive: 打开内部读写成功");
                                } else {
                                    msg1.obj = false;
                                    Log.e(TAG, "onReceive: 打开内部读写失败");
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                                Log.e(TAG, "onReceive: 收到打开内部读写反馈");
                                break;
                        }
                    }
                    switch ((byte) (txValue[2] & 0x0F)) {
                        case 0x0e: //readid
                            if (demoApplication.getMS() == 2) {
//                                byte[] lock_data = new byte[14];
//                                if (txValue[2] < ((byte) 0xC0)) {
//                                    for (int j = 0; j < 14; j++) {
//                                        lock_data[j] = txValue[3 + j];
//                                    }
//                                    msg1.what = 10;
//                                    msg1.obj = lock_data;
//                                    demoApplication.mHandler.sendMessage(msg1);
//                                } else {
//                                    demoApplication.mHandler.sendEmptyMessage(10);
//                                }
                            } else {
                                if (txValue[2] < ((byte) 0xC0)) {
                                    for (i = 0; i < 8; i++) {
                                        lid[i] = txValue[3 + i];
                                    }
                                    lidstr = bytes2HexString(lid);
                                    msg1.what = 10;
                                    msg1.obj = lidstr;
                                } else {
                                    msg1.what = 10;
                                    msg1.obj = "";
                                }
                                demoApplication.mHandler.sendMessage(msg1);
                            }
                            break;
                        case 0x0c: //writeid
                            msg1.what = 11;
                            if (txValue[2] < ((byte) 0xC0)) {
                                msg1.obj = "true";
                            } else {
                                msg1.obj = "false";
                            }
                            demoApplication.mHandler.sendMessage(msg1);
                            break;
                        case 0x0a: //unlock
                            if (txValue[1] > 0) {
                                for (i = 0; i < 8; i++) {
                                    lid[i] = txValue[3 + i];
                                }
                                lidstr = bytes2HexString(lid);
                                msg1.what = 10;
                                msg1.obj = lidstr;
                                demoApplication.mHandler.sendMessage(msg1);
                            } else {
                                msg1.what = 10;
                                msg1.obj = "";
                                demoApplication.mHandler.sendMessage(msg1);
                            }
                            //
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (txValue[2] < ((byte) 0xC0)) {
                                msg2.what = 12;
                                msg2.obj = "true";
                            } else {
                                msg2.what = 12;
                                msg2.obj = "false";
                            }
                            demoApplication.mHandler.sendMessage(msg2);
                            break;
                        case 0x0b: //unlock
                            if (txValue[1] > 0) {
                                for (i = 0; i < 8; i++) {
                                    lid[i] = txValue[3 + i];
                                }
                                lidstr = bytes2HexString(lid);
                                msg1.what = 10;
                                msg1.obj = lidstr;
                                demoApplication.mHandler.sendMessage(msg1);
                            } else {
                                msg1.what = 10;
                                msg1.obj = "";
                                demoApplication.mHandler.sendMessage(msg1);
                            }
                            //
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (txValue[2] < ((byte) 0xC0)) {
                                msg2.what = 13;
                                msg2.obj = "true";
                            } else {
                                msg2.what = 13;
                                msg2.obj = "false";
                            }
                            demoApplication.mHandler.sendMessage(msg2);
                            break;
                        case 0x0d: //unlock
                            if (txValue[1] > 0) {
                                for (i = 0; i < 8; i++) {
                                    lid[i] = txValue[3 + i];
                                }
                                lidstr = bytes2HexString(lid);
                                msg1.what = 10;
                                msg1.obj = lidstr;
                                demoApplication.mHandler.sendMessage(msg1);
                            } else {
                                msg1.what = 10;
                                msg1.obj = "";
                                demoApplication.mHandler.sendMessage(msg1);
                            }
                            //
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (txValue[2] < ((byte) 0xC0)) {
                                msg2.what = 14;
                                msg2.obj = "true";
                            } else {
                                msg2.what = 14;
                                msg2.obj = "false";
                            }
                            demoApplication.mHandler.sendMessage(msg2);
                            break;
                    }
                }
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                showMessage("设备不支持UART.断开");
                mService.disconnect();
            }
            //
            byte[] value;
            byte[] pdata;
            byte[] pdata1;
            String templid;
            switch (action) {
                case "ReadIdBegin":
                    if (demoApplication.getMS() == 2) {
                        templid = intent.getStringExtra("LockId");
                        if (templid != null) {
                            pdata = hexStr2Bytes(templid);
                        } else {
                            pdata = new byte[0];
                        }
                        value = makePacket((byte) 0x01, pdata);
                    } else {
                        pdata = new byte[0];
                        value = makePacket((byte) 0x2e, pdata);
                    }
                    mService.writeRXCharacteristic(value);
                    break;
                case "WriteIdBegin":
                    if (demoApplication.getMS() == 2) {
//                        value = makePacket((byte) 0x34, pdata);
                        String zoneKey = intent.getStringExtra("ZoneKey");
                        byte[] lockSafe = intent.getByteArrayExtra("LockSafe");
                        byte[] lockId = intent.getByteArrayExtra("LockId");
                        openLock((byte) 0x34, lockId, lockSafe, zoneKey);
                        Log.e(TAG, "onReceive: 发送写ID指令");
                    } else {
                        templid = intent.getStringExtra("Lid");
                        pdata = hexStr2Bytes(templid);
                        value = makePacket((byte) 0x2c, pdata);
                        Log.e(TAG, "onReceive: pdata.length = " + pdata.length);
                        mService.writeRXCharacteristic(value);
                    }
                    break;
                case "UnLockBegin":
                    if (demoApplication.getMS() == 2) {
                        String zoneKey = intent.getStringExtra("ZoneKey");
                        byte[] lockId = intent.getByteArrayExtra("LockId");
                        byte[] lockSafe = intent.getByteArrayExtra("LockSafe");
                        openLock((byte) 0x31, lockId, lockSafe, zoneKey);
                        Log.e(TAG, "onReceive: 发送开锁指令");
                    } else {
                        templid = intent.getStringExtra("ZoneKey");
                        pdata = hexStr2Bytes(templid);
                        value = makePacket((byte) 0x2a, pdata);
                        mService.writeRXCharacteristic(value);
                    }
                    break;
                case "LockBegin":
                    if (demoApplication.getMS() == 2) {
                        String zoneKey = intent.getStringExtra("ZoneKey");
                        byte[] lockId = intent.getByteArrayExtra("LockId");
                        byte[] lockSafe = intent.getByteArrayExtra("LockSafe");
                        openLock((byte) 0x32, lockId, lockSafe, zoneKey);
                        Log.e(TAG, "onReceive: 发送落锁指令");
                    } else {
                        templid = intent.getStringExtra("ZoneKey");
                        pdata = hexStr2Bytes(templid);
                        value = makePacket((byte) 0x2b, pdata);
                        mService.writeRXCharacteristic(value);
                    }
                    break;
                case "UpdateKeyBegin":
                    if (demoApplication.getMS() == 2) {
                        String zoneKey = intent.getStringExtra("ZoneKey");
                        byte[] lockId = intent.getByteArrayExtra("LockId");
                        byte[] lockSafe = intent.getByteArrayExtra("LockSafe");
                        openLock((byte) 0x35, lockId, lockSafe, zoneKey);
                        Log.e(TAG, "onReceive: 发送下装指令");
                    } else {
                        templid = intent.getStringExtra("ZoneKey");
                        templid = templid + intent.getStringExtra("ZoneId");
                        pdata = hexStr2Bytes(templid);
                        value = makePacket((byte) 0x2d, pdata);
                        mService.writeRXCharacteristic(value);
                    }
                    break;
                case "CONNECT":
                    connect();
                    break;
                case "ADCONNECT":
                    adconnect(mIntent.getStringExtra("ADDRESS"));
                    break;
                case "DISCONNECT":
                    disconnect();
                    break;
                case "READ_DATA":
                    if (demoApplication.getMS() == 2) {
                    int qu = intent.getIntExtra("QU", 0);
                    String str = null;
                    if (Integer.toHexString(qu).length() == 1) {
                        str = "0" + Integer.toHexString(qu);
                    } else {
                        str = Integer.toHexString(qu);
                    }
                    pdata = mLockUtil.hexStr2Bytes(str);
                    value = mLockUtil.makePacket((byte) 0x11, pdata);
                    mService.writeRXCharacteristic(value);
                    } else {

                    }
                    break;
                case "WRITE_DATA":
                    if (demoApplication.getMS() == 2) {
                    int qu1 = intent.getIntExtra("QU", 0);
                    String data = intent.getStringExtra("DATA");
                    String strW = null;
                    if (Integer.toHexString(qu1).length() == 1) {
                        strW = "0" + Integer.toHexString(qu1);
                    } else {
                        strW = Integer.toHexString(qu1);
                    }
                    byte[] strQu = mLockUtil.hexStr2Bytes(strW);
                    pdata = mLockUtil.hexStr2Bytes(data);

                    //拼接数据
                    byte[] data3 = new byte[pdata.length + strQu.length];
                    System.arraycopy(strQu, 0, data3, 0, strQu.length);
                    System.arraycopy(pdata, 0, data3, strQu.length, pdata.length);

                    value = mLockUtil.makePacket((byte) 0x12, data3);
                    mService.writeRXCharacteristic(value);
                    } else {

                    }
                    break;
                case "OPEN_WRITE_READ":
                    if (demoApplication.getMS() == 2) {
                        String zoneKey = intent.getStringExtra("ZoneKey");
                        byte[] lockId = intent.getByteArrayExtra("LockId");
                        byte[] lockSafe = intent.getByteArrayExtra("LockSafe");
                        openLock((byte) 0x37, lockId, lockSafe, zoneKey);
                        Log.e(TAG, "onReceive: 发送打开读写指令");
                    } else {

                    }
                    break;
                case "CLOSE_WRITE_READ":
                    if (demoApplication.getMS() == 2) {
                        String zoneKey = intent.getStringExtra("ZoneKey");
                        byte[] lockId = intent.getByteArrayExtra("LockId");
                        byte[] lockSafe = intent.getByteArrayExtra("LockSafe");
                        openLock((byte) 0x38, lockId, lockSafe, zoneKey);
                        Log.e(TAG, "onReceive: 发送关闭读写指令");
                    } else {

                    }
                    break;
            }
        }
    };

    /**
     * 获取锁 ID
     */
    private void getLockId() {
        byte[] pdata = new byte[0];
        byte[] value = mLockUtil.makePacket((byte) 0x2e, pdata);
        mService.writeRXCharacteristic(value);
    }

    /**
     * 开锁
     */
    private void openLock(byte type, byte[] lockId, byte[] lockSafe, String currentZonKey) {
        //开锁加密
        byte[] pdata = mLockUtil.makeorder(lockId, lockSafe, currentZonKey);
        byte[] value = mLockUtil.makePacket(type, pdata);
        mService.writeRXCharacteristic(value);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    /**
     * 数据库读取操作
     */
    public void test() {
        dateBaseUtil = new DateBaseUtil(MyService.this);
        list = dateBaseUtil.queryUnLock();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String path;
                path = HttpServerAddress.BASE_URL + "?m=insertlocklog&lid=" + list.get(i).getLid() + "&GPS_X=" + "000000"
                        + "&GPS_Y=" + "000000" + "&OP_NO=" + list.get(i).getOP_NO() + "&OP_TYPE=" + list.get(i).getOP_TYPE()
                        + "&OP_RET=" + list.get(i).getOP_RET() + "&OP_DATETIME=" + list.get(i).getOP_DATETIME() + "&USER_CONTEXT=" + user_context_number;
                try {
                    result = new GetSpace().run(path);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            dateBaseUtil.deleteUnLock();
        }

    }

    public byte[] hexStr2Bytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }


    public String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public byte CheckSum(byte[] data) {
        byte sum = 0;
        if (data == null) return sum;
        for (int i = 0; i < data.length; i++)
            sum += data[i];
        return sum;
    }

    public byte[] makePacket(byte command, byte[] data) {
        byte[] buffer = new byte[data.length + 5];
        buffer[0] = (byte) 0x55;
        buffer[1] = (byte) data.length;
        buffer[2] = command;
        if (data != null && data.length > 0)
            for (int i = 0; i < data.length; i++) buffer[3 + i] = data[i];

        buffer[3 + data.length] = (byte) CheckSum(data);
        buffer[3 + data.length] += buffer[0];
        buffer[3 + data.length] += buffer[1];
        buffer[3 + data.length] += buffer[2];
        buffer[3 + data.length + 1] = (byte) 0xaa;
        Log.e(TAG, "makePacket: buffer = " + bytes2HexString(buffer));
        return buffer;
    }

    byte checkPacket(byte[] data) {

        byte len;
        if (data[0] == 0x55) {
            len = data[1];
            byte[] data1 = new byte[len + 3];
            for (int i = 0; i < len + 3; i++) data1[i] = data[i];
            if (len > 32 - 5) return 0;
            if (CheckSum(data1) == data[len + 3]) {
                if (data[len + 4] == (byte) 0xaa)
                    return 1;
            }
        }
        return 0;
    }


}
