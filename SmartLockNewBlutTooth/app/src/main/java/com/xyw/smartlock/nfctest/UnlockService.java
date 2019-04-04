package com.xyw.smartlock.nfctest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

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
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.UnLock;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 写一个服务后台运行，进行定位和发送消息。
 */
public class UnlockService extends Service {
    //定位当前地址
    private double latitude;
    private double longitude;
    private String lockTime;
    private LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    //获取缓存数据
    private AcacheUserBean acacheUserBean;
    private ACache mCache;
    private String strState;
    private String personNumber, opNumber, operationType, lockID, operatingState;
    private DateBaseUtil dateBaseUtil;
    private CountDownTimer timer = new CountDownTimer(10000, 10000) {

        @Override
        public void onTick(long millisUntilFinished) {
            onDestroy();
            stopSelf();
        }

        @Override
        public void onFinish() {
            onDestroy();
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // 判断当前网络是否可用
        if (isNetworkAvailable(getApplicationContext())) {
            initview();
        } else {
            ToastUtil.MyToast(getApplicationContext(), "当前网络不可用，请检查网络连接！");
            //向数据库添加数据，进行数据库操作
            //停止这个服务
            Log.e("TAG", "服务已经停止！");
            onDestroy();
            stopSelf();
        }
    }

    /**
     * 判断当前网络是否可用
     */

    private boolean isNetworkAvailable(Context context) {
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

    //初始化数
    private void initview() {
        dateBaseUtil = new DateBaseUtil(UnlockService.this);
        // 获取缓存数据
        mCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        //设置相关参数
        initLocation();
        //开始定位
        mLocationClient.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    //开始定位
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("\n时间 : ");
            sb.append(location.getTime());
            sb.append("\n错误代码 : ");
            sb.append(location.getLocType());
            sb.append("\n纬度 : ");
            sb.append(location.getLatitude());
            sb.append("\n经度 : ");
            sb.append(location.getLongitude());
            sb.append("\n半径 : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\n速度 : ");
                sb.append(location.getSpeed());
                sb.append("\n卫星 : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\n方向 : ");
                sb.append(location.getDirection());
                sb.append("\n地址 : ");
                sb.append(location.getAddrStr());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\n地址 : ");
                sb.append(location.getAddrStr());
                sb.append(location.getLatitude());
                sb.append(location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            }

            // 定位纬度,经度
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("TAG", String.valueOf(latitude));
            Log.e("TAG", String.valueOf(longitude));

            //获取系统时间
            Date currentTime = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lockDateTime = fmt.format(currentTime);
            String date = lockDateTime.substring(0, 10);
            String time = lockDateTime.substring(11, 19);
            lockTime = date + "%20" + time;
            System.out.println("lockTime=" + lockTime);
            Log.e("TAG", lockTime);
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                //网络定位成功，向服务端发起网络请求，上传开锁记录
                volley_post();
                mLocationClient.stop();
            }
        }
    }

    private void volley_post() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
            // 读取缓存数据
            AcacheUserBean LoginInfo = (AcacheUserBean) mCache.getAsObject("LoginInfo");
            personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();
            opNumber = LoginInfo.getOP_NO().toString().trim();
            // 2.服务器网址
            operationType = "1";

            final String URL = HttpServerAddress.BASE_URL + "?m=insertlocklog&lid=" + lockID + "&GPS_X=" + longitude
                    + "&GPS_Y=" + latitude + "&OP_NO=" + opNumber + "&OP_TYPE=" + operationType
                    + "&OP_RET=" + URLEncoder.encode(operatingState) + "&OP_DATETIME=" + lockTime + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        strState = arg0.getString("result");
                        System.out.println("strState=" + strState);
                        if (strState.equals("true")) {
                            //停止这个服务
                            Log.e("TAG", "strState=" + strState);
                            onDestroy();
                            stopSelf();

                        }
                        System.out.println("------------" + strState + "--------------");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), strState);
                    Log.e("TAG", "服务已经停止！");
                    onDestroy();
                    stopSelf();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse arg0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(arg0.data, "UTF-8"));
                        return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(arg0));
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
            e.printStackTrace();
        }
    }

    //回掉方法得到activity传过来的值
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lockID = intent.getStringExtra("Lid");
        operatingState = intent.getStringExtra("operatingState");
        return super.onStartCommand(intent, flags, startId);
    }

    //当没网时解锁成功或解锁失败时进行数据库操作
    public void dateBase() {

        UnLock unLock = new UnLock();
        //向数据库里面添加数据
        unLock.setLid(lockID);
        unLock.setGPS_X(String.valueOf(longitude));
        unLock.setGPS_Y(String.valueOf(latitude));
        unLock.setOP_NO(opNumber);
        unLock.setOP_TYPE(operationType);
        unLock.setOP_RET(URLEncoder.encode(operatingState));
        unLock.setOP_DATETIME(lockTime);
        unLock.setUSER_CONTEXT(personNumber);
        dateBaseUtil.Insert3(unLock);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
