package com.xyw.smartlock.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.BaiDuMapData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LockContrailActivity extends AppCompatActivity {

    public static double pi = 3.1415926535897932384626;

    private TextView lockContrailTitle;
    private ImageView lockContrailTitleBack, lockContrailSearch;
    private GeoCoder mSearch = null;
    private List<HashMap<String, String>> baiDuMapDatas = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> baiDuMap = new HashMap<String, String>();
    private List<BitmapDescriptor> customList = new ArrayList<BitmapDescriptor>();
    private List<LatLng> points = new ArrayList<LatLng>();
    private List<Integer> index = new ArrayList<Integer>();
    private List<BaiDuMapData> baiDuMapDataTime = new ArrayList<BaiDuMapData>();

//    private BaiDuMapData baiDuMapData;
    //给每一个点添加覆盖物
    private String startDateTime, endDateTime, number, personNumber, account, myLid;
    //地图上画出轨迹
    private MapView lockcontrail_baidumapview;
    private BaiduMap mBaiduMap;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    //请求网络的等待弹框
    private LoadingDialog dialog;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockcontrail);
        getSupportActionBar().hide();
        lockcontrail_baidumapview = (MapView) findViewById(R.id.lockcontrail_baidumapview);
        mBaiduMap = lockcontrail_baidumapview.getMap();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String info = (String) marker.getExtraInfo().get("info");
                InfoWindow infoWindow;
                //动态生成一个Button对象，用户在地图中显示InfoWindow
                final Button textInfo = new Button(getApplicationContext());
                textInfo.setBackgroundResource(R.mipmap.popup);
                textInfo.setPadding(10, 10, 10, 10);
                textInfo.setTextColor(Color.BLACK);
                textInfo.setTextSize(12);
                textInfo.setText(info);
                //得到点击的覆盖物的经纬度
                LatLng ll = marker.getPosition();
                //将marker所在的经纬度的信息转化成屏幕上的坐标
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 90;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                infoWindow = new InfoWindow(textInfo, llInfo, 0);
                mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                //让地图以备点击的覆盖物为中心
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.setMapStatus(status);
                //弹框等待3秒，自动消失
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mBaiduMap.hideInfoWindow();
                    }
                }, 3000);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mBaiduMap.hideInfoWindow();
                    }
                }, 3000);
                return false;
            }
        });
        initview();
    }

    private void initview() {
        lockContrailTitle = (TextView) findViewById(R.id.lockcontrail_tv_title);
        lockContrailTitle.setText(R.string.unlockcontrail);
        lockContrailTitleBack = (ImageView) findViewById(R.id.lockcontrail_title_back);
        lockContrailTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lockContrailSearch = (ImageView) findViewById(R.id.lockcontrail_title_search);
        lockContrailSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LockContrailActivity.this, OperatorActivity.class), 1);
            }
        });
        // 获取缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        personNumber = acacheUserBean.getUSER_CONTEXT().toString().trim();
        account = acacheUserBean.getOP_NO().toString().trim();//当前地点标识物
    }

    /**
     * 给每一个点添加覆盖物
     */
    private void addOverlay() {
        Marker marker = null;
        LatLng point = null;
        MarkerOptions option = null;
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress);
        for (BaiDuMapData data : baiDuMapDataTime) {
            if (data.getType().equals("1")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress);
            } else if (data.getType().equals("2")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress_green);
            } else if (data.getType().equals("3")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress_red);
            } else if (data.getType().equals("4")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress_black);
            }
            point = new LatLng(Double.valueOf(data.getLatitude()), Double.valueOf(data.getLongitude()));
            option = new MarkerOptions().position(point).icon(bitmap);
            marker = (Marker) mBaiduMap.addOverlay(option);
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", "开锁人" + data.getName() + "\n" + "开锁时间：" + data.getDateTime());
            marker.setExtraInfo(bundle);
        }
//        for (HashMap<String, String> baiDuMap : baiDuMapDatas) {
//            for (Map.Entry<String, String> entry : baiDuMap.entrySet()) {
//                point = new LatLng(Double.valueOf(entry.getKey()), Double.valueOf(entry.getValue()));
//                option = new MarkerOptions().position(point).icon(bitmap);
//                marker = (Marker) mBaiduMap.addOverlay(option);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("info", "纬度：" + entry.getKey() + "经度：" + entry.getValue());
//                marker.setExtraInfo(bundle);
//            }
//        }
        //将地图移动到最后一个标志点
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.setMapStatus(status);

        //地图状态显示到坐标点
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(14).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) return;
        startDateTime = data.getStringExtra("start_date");
        endDateTime = data.getStringExtra("end_date");
        if (startDateTime != null && endDateTime != null) {
            number = data.getStringExtra("number");
            choose_volley_post();
//          mapTrajectory();
        }
    }

    // 通过查询向服务端发起请求
    private void choose_volley_post() {

        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockLogListD&OP_NO=" + account
                    + "&Begin_time=" + startDateTime + "&end_time=" + endDateTime + "&USER_CONTEXT="
                    + personNumber + "&lid=" + "" + "&op_number=" + number;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;
                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析
                        System.out.println("strState=" + strState);
                        JSONArray array = strState.getJSONArray("LOCK_LOG");
                        System.out.println("array=" + array);

                        for (int i = 0; i < array.length(); i++) {
                            System.out.println("array=" + array);
                            JSONObject object = array.getJSONObject(i);
                            // 解析出单个json集中所有数据
                            String str = "OP_NAME:" + object.getString("OP_NAME").trim()
                                    + "L_RET:" + object.getString("L_RET").trim()
                                    + "L_OPTYPE:" + object.getString("L_OPTYPE").trim()
                                    + "L_GPS_X:" + object.getString("L_GPS_X").trim()
                                    + "L_GPS_Y:" + object.getString("L_GPS_Y").trim()
                                    + "L_CREATE_DT:" + object.getString("L_CREATE_DT").trim()
                                    + "LID:" + object.getString("LID").trim()
                                    + "L_CREATE_OP:" + object.getString("L_CREATE_OP").trim();
                            if ((object.getString("L_GPS_Y").trim().equals("0.0")) || (object.getString("L_GPS_X").trim().equals("0.0")) ||
                                    (object.getString("L_GPS_Y").trim().equals("000000")) || (object.getString("L_GPS_X").trim().equals("000000"))) {

                            } else {
                                Log.e("TAG", object.getString("L_GPS_Y").trim());
                                Log.e("TAG", object.getString("L_GPS_X").trim());
                                //将国测局坐标转换为百度地图坐标
                                double x = Double.valueOf(object.getString("L_GPS_X").trim());
                                double y = Double.valueOf(object.getString("L_GPS_Y").trim());
                                double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
                                double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
                                double bd_lon = z * Math.cos(theta) + 0.0065;
                                double bd_lat = z * Math.sin(theta) + 0.006;
                                latitude = String.valueOf(bd_lat);
                                longitude = String.valueOf(bd_lon);

//                            HashMap<String, String> baiDuMap = null;
//                            baiDuMap = new HashMap<String, String>();
//                            baiDuMap.put(object.getString("L_GPS_Y").trim(), object.getString("L_GPS_X").trim());
//                            baiDuMapDatas.add(baiDuMap);

                                BaiDuMapData baiDuMapData = new BaiDuMapData();
                                baiDuMapData.setLatitude(latitude);
                                baiDuMapData.setLongitude(longitude);
                                baiDuMapData.setDateTime(object.getString("L_CREATE_DT").trim());
                                baiDuMapData.setName(object.getString("OP_NAME").trim());
                                baiDuMapData.setType(object.getString("L_OPTYPE"));
                                baiDuMapDataTime.add(baiDuMapData);

                                //画出地图轨迹
                                // 定义点
                                LatLng pt = new LatLng(bd_lat, bd_lon);
                                //构造纹理资源
                                BitmapDescriptor custom = BitmapDescriptorFactory.fromResource(R.mipmap.icon_road_red_arrow);
                                customList.add(custom);
                                points.add(pt);//点元素
                                index.add(0);//设置该点的纹理索引
                            }
                        }
                        addOverlay();
                        attachment();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
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
            //设置volley请求网络延迟
            jsonObjectRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            10 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attachment() {
        //构造对象
        OverlayOptions ooPolyline = new PolylineOptions().width(5).color(0xAAFF0000).points(points)
                .customTextureList(customList).textureIndex(index);
        //添加到地图
        mBaiduMap.addOverlay(ooPolyline);
    }

}