package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.xyw.smartlock.R;
import com.xyw.smartlock.adapter.MoreLockMenuAdapter;
import com.xyw.smartlock.bean.BluetoothDeviceBean;
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.UnLock;
import com.xyw.smartlock.listener.OnItemClickListener;
import com.xyw.smartlock.nfctest.UnlockService;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.view.ListViewDecoration;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MoreUnlockActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MoreUnlockActivity";

    private static final int REQUEST_ENABLE_BT = 2;

    private TextView common_tv_title;
    private ImageView common_title_back;
    private SwipeRefreshLayout swipe_layout_more_lock;
    private SwipeMenuRecyclerView recycler_view_more_lock;
    private MoreLockMenuAdapter mMoreLockMenuAdapter;
    private List<BluetoothDeviceBean> deviceBeans;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;

    private DemoApplication demoApplication;
    private LockUtil mLockUtil;
    private ACache mACache;
    private AcacheUserBean mAcacheUserBean;
    private String result;
    private String operatingState;

    //定位当前地址
    private double latitude;
    private double longitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MoreUnlockActivity.MyLocationListener();
    //操作数据库
    private DateBaseUtil dateBaseUtil;
    private String opNumber;
    private String operationType;
    private String lockTime;
    private String personNumber;
    private String loctionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_unlock);
        getSupportActionBar().hide();
        initData();

        initView();
        initBle();
        registerBroadcastReceiver();
    }

    private void initData() {
        demoApplication = (DemoApplication) getApplication();
        mLockUtil = LockUtil.getInstance();
        mACache = ACache.get(MoreUnlockActivity.this);
        mAcacheUserBean = (AcacheUserBean) mACache.getAsObject("LoginInfo");
        keyValue = mAcacheUserBean.getKEYVALUE().toString().trim();
        opNumber = mAcacheUserBean.getOP_NO().trim();
        operationType = "1";
        personNumber = mAcacheUserBean.getUSER_CONTEXT().trim();
        //获取系统时间
        Date currentTime = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lockDateTime = fmt.format(currentTime);
        String date = lockDateTime.substring(0, 10);
        String time = lockDateTime.substring(11, 19);
        lockTime = date + "%20" + time;
    }

    private void initView() {
        common_tv_title = (TextView) findViewById(R.id.common_tv_title);
        common_tv_title.setText(R.string.direct_more_unlock);
        common_title_back = (ImageView) findViewById(R.id.common_title_back);
        common_title_back.setOnClickListener(this);

        swipe_layout_more_lock = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_more_lock);
        swipe_layout_more_lock.setOnRefreshListener(mOnRefreshListener);

        recycler_view_more_lock = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view_more_lock);
        recycler_view_more_lock.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recycler_view_more_lock.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recycler_view_more_lock.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recycler_view_more_lock.addItemDecoration(new ListViewDecoration(MoreUnlockActivity.this));// 添加分割线。

        deviceBeans = new ArrayList<>();
        mMoreLockMenuAdapter = new MoreLockMenuAdapter(deviceBeans);
        mMoreLockMenuAdapter.setOnItemClickListener(onItemClickListener);
        recycler_view_more_lock.setAdapter(mMoreLockMenuAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_title_back:
                MoreUnlockActivity.this.finish();
                break;
        }
    }

    /**
     * 刷新事件
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (isUnlocking) {
                swipe_layout_more_lock.setRefreshing(false);
                Toast.makeText(MoreUnlockActivity.this, "开锁中，请稍后刷新", Toast.LENGTH_SHORT).show();
            } else {
                deviceBeans.clear();
                mMoreLockMenuAdapter.notifyDataSetChanged();
                scanLeDevice(false);
                scanLeDevice(true);
            }
        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPBTIMG");
        filter.addAction("UPBTIMG_DIS");
        registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
                    notifyState(true, "开锁中...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            demoApplication.ReadIdBegin(mHandler);
                        }
                    }, 2000);
                    break;
                case "UPBTIMG_DIS":
                    notifyState(false, "点击解锁");
                    break;
            }
        }
    };

    private void notifyState(boolean state, String text) {
        isUnlocking = state;
        deviceBeans.get(itemPositon).setState(text);
        mMoreLockMenuAdapter.notifyItemChanged(itemPositon);
    }

    private boolean isUnlocking = false;
    private int itemPositon;
    /**
     * item点击事件
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (!isUnlocking) {
                itemPositon = position;
                demoApplication.setCurrentAddress(deviceBeans.get(position).getMac());
                demoApplication.adconnect(deviceBeans.get(position).getMac());
                notifyState(true, "开锁中...");
            } else {
                Toast.makeText(MoreUnlockActivity.this, "开锁中，请等待,,,", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private byte[] lockSafe = new byte[4];//存放锁设备返回安全码
    private byte[] lockId = new byte[8];//存放锁设备返回锁ID
    private String keyValue;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
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
                        //读完ID 马上开锁
                        demoApplication.bleOpenLock(lockId, lockSafe, keyValue, mHandler);
                    }
                    break;
                case 12:
                    result = (String) msg.obj;
                    if ("true".equals(result)) {
                        operatingState = "解锁成功";
                        deviceBeans.get(itemPositon).setState("解锁成功");
                        mMoreLockMenuAdapter.notifyItemChanged(itemPositon);
                        try {
                            netWork();
                            demoApplication.setISManual(true);
                            demoApplication.disconnect();
                            isUnlocking = false;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        operatingState = "解锁失败";
                        deviceBeans.get(itemPositon).setState("解锁失败");
                        mMoreLockMenuAdapter.notifyItemChanged(itemPositon);
                        try {
                            netWork();
                            demoApplication.setISManual(true);
                            demoApplication.disconnect();
                            isUnlocking = false;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    //判断当前网络是否可用，可用的话启动服区进行定位，不可用的话将开锁信息存在数据库中
    private void netWork() throws UnsupportedEncodingException {
        //判断当前网络是否可用
        if (isNetworkAvailable(MoreUnlockActivity.this)) {
            //有网的话启动Service进行定位并且向后台传值
            isServiceRunning();
        } else {
            //没网的话启动数据库，把信息暂时存储在数据库中
            dateBase();
        }
    }

    //当没网时解锁成功或解锁失败时进行数据库操作
    public void dateBase() throws UnsupportedEncodingException {

        UnLock unLock = new UnLock();
        //向数据库里面添加数据
        unLock.setLid(mLockUtil.bytes2HexString(lockId));
        unLock.setGPS_X(String.valueOf(longitude));
        unLock.setGPS_Y(String.valueOf(latitude));
        unLock.setOP_NO(opNumber);
        unLock.setOP_TYPE(operationType);
        unLock.setOP_RET(URLEncoder.encode(operatingState, "utf-8"));
        unLock.setOP_DATETIME(lockTime);
        unLock.setUSER_CONTEXT(personNumber);
        dateBaseUtil.Insert3(unLock);
    }

    /**
     * 检查当前网络是否可用
     */
    private boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context

                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //判断服务是否已经运行
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.xyw.smartlock.nfctest.UnlockService".equals(service.service.getClassName())) {
                ToastUtil.MyToast(MoreUnlockActivity.this, "当前网络状态不好，可能无法上传开锁信息");
                try {
                    Thread.sleep(5000);
                    final Intent intent = new Intent();
                    intent.setAction("ITOP.MOBILE.SIMPLE.SERVICE.SENSORSERVICE");
                    stopService(intent);
                    Log.e("TAG", "服务关闭了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                Intent intent = new Intent(MoreUnlockActivity.this, UnlockService.class);
                intent.putExtra("Lid", mLockUtil.bytes2HexString(lockId));
                intent.putExtra("operatingState", operatingState);
                startService(intent);
            }
        }
        return false;
    }

    /**
     * 初始化蓝牙 BLE
     */
    private void initBle() {
        //检查手机是否支持BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        //获取蓝牙适配器
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        //检查手机是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
           finish();
        }
        if (ActivityUtils.getInstance().checkPermission(MoreUnlockActivity.this)) {
            openBle();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ActivityUtils.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openBle();
            } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                MoreUnlockActivity.this.finish();
            }
        }
    }

    //打开蓝牙
    private void openBle() {
        if (!mBluetoothAdapter.isEnabled()) {
            //询问打开蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case Activity.RESULT_OK:// 点击确认按钮，打开蓝牙
                    scanLeDevice(true);
                    break;
                case Activity.RESULT_CANCELED:// 点击取消按钮或点击返回键
                    MoreUnlockActivity.this.finish();
                    break;
            }
        }
    }

    private Runnable stopScanRunable = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
            mMoreLockMenuAdapter.notifyDataSetChanged();
            swipe_layout_more_lock.setRefreshing(false);
        }
    };

    public void scanLeDevice(boolean enable) {
        if (enable) {
            if (!mScanning) {
                mHandler.postDelayed(stopScanRunable, 5 * 1000); //30秒后停止搜索
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);//开始搜索
            }
        } else {
            if (mScanning) {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
                mHandler.removeCallbacks(stopScanRunable);
            }
        }
    }

    /**
     * 蓝牙扫描回调接口
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (BluetoothDeviceBean deviceBean : deviceBeans) {
                        if (deviceBean.getMac().equals(device.getAddress())) {
                            return;
                        }
                    }
                    BluetoothDeviceBean bean = new BluetoothDeviceBean();
                    bean.setMac(device.getAddress());
                    bean.setName(device.getName());
                    bean.setRssi(rssi);
                    bean.setState(getString(R.string.click_unlock));
                    deviceBeans.add(bean);
                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
//                    if (isScanning == true) {
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                        mBluetoothAdapter.startLeScan(mLeScanCallback);
//                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        demoApplication.setISManual(true);
        demoApplication.disconnect();
        unregisterReceiver(mBroadcastReceiver);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            // 定位纬度,经度
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            loctionInfo = location.toString().trim();
            if (location != null)
                mLocationClient.stop();
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                ToastUtil.MyToast(MoreUnlockActivity.this, "定位成功");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                ToastUtil.MyToast(MoreUnlockActivity.this, "无法获取有效定位地址，请检查手机的网络状态,或者重新启动手机");
            }
        }

    }
}
