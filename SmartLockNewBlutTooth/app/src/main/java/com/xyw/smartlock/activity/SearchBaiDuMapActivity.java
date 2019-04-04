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
import com.baidu.mapapi.SDKInitializer;
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
import com.baidu.mapapi.model.LatLng;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.SearchBaiDuMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * 锁分布图
 */
public class SearchBaiDuMapActivity extends AppCompatActivity {

    public static double pi = 3.1415926535897932384626;
    private static final String TAG = "SearchBaiDuMapActivity";

    private TextView title;
    private ImageView titleBack;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String searchID, searchName, searchAddress, searchMeterId, personNumber;
    private LoadingDialog dialog;
    //    private ArrayList<HashMap<String, String>> searchBaiDu = new ArrayList<HashMap<String, String>>();
//    private HashMap<String, String> searchBaiDuMap = new HashMap<String, String>();
    private List<SearchBaiDuMap> searchBaiDu = new ArrayList<SearchBaiDuMap>();

    private String latitude, longitude;

    private MapView searchBaiDuMap_baidumapview;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_searchbaidumap);
        getSupportActionBar().hide();
        searchBaiDuMap_baidumapview = (MapView) findViewById(R.id.searchBaiDuMap_baidumapview);
        mBaiduMap = searchBaiDuMap_baidumapview.getMap();
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

        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        personNumber = acacheUserBean.getUSER_CONTEXT().toString().trim();
        initview();
    }

    private void initview() {
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.SearchBaiDuMap);
        titleBack = (ImageView) findViewById(R.id.common_title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //获取从所档案传过来的值，用于请求网络，获取锁信息
        Intent intent = getIntent();
        if (null != intent) {
            String SearchID = intent.getStringExtra("searchID");
            if (SearchID == null) {
                searchID = "";
            } else {
                searchID = SearchID;
            }
            String SearchName = intent.getStringExtra("searchName");
            if (SearchName == null) {
                searchName = "";
            } else {
                searchName = SearchName;
            }
            String SearchAddress = intent.getStringExtra("searchAddress");
            if (SearchAddress == null) {
                searchAddress = "";
            } else {
                searchAddress = SearchAddress;
            }
            String SearchMeterId = intent.getStringExtra("searchMeterId");
            if (searchMeterId == null) {
                searchMeterId = "";
            } else {
                searchMeterId = SearchMeterId;
            }
        } else {
            searchID = "";
            searchName = "";
            searchAddress = "";
            searchMeterId = "";
        }

        //从活态获取锁的ID，和经纬度，显示在地图上
        vooleySearchBaiDuMap();
    }

    private void vooleySearchBaiDuMap() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(SearchBaiDuMapActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockInfoList&lid=" + searchID + "&Lname=" + searchName + "&laddr=" + searchAddress + "&METERID=" + searchMeterId
                    + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {

                    try {
                        JSONObject strState = arg0;
                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_INFO");
                        //判断弹框状态
                        dialog.dismiss();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e(TAG, "object=" + object);
                            // 解析出单个json集中所有数据
                            String str = "LID:" + object.getString("LID").trim()
                                    + "L_NAME:" + object.getString("L_NAME").trim()
                                    + "L_ADDR:" + object.getString("L_ADDR").trim()
                                    + "L_GPS_X:" + object.getString("L_GPS_X").trim()
                                    + "L_GPS_Y:" + object.getString("L_GPS_Y").trim()
                                    + "L_CREATE_DT:" + object.getString("L_CREATE_DT").trim()
                                    + "L_CREATE_OP:" + object.getString("L_CREATE_OP").trim()
                                    + "L_BOX_NO:" + object.getString("L_BOX_NO").trim()
                                    + "L_BOX_TYPE:" + object.getString("L_BOX_TYPE").trim()
                                    + "KEY_VER:" + object.getString("KEY_VER")
                                    + "ZONE_NO:" + object.getString("ZONE_NO")
                                    + "PASSNUM" + object.getString("PASSNUM")
                                    + "ZONE_NAME:" + object.getString("ZONE_NAME");
                            if ((object.getString("L_GPS_Y").trim().equals("0.0")) || (object.getString("L_GPS_X").trim().equals("0.0")) ||
                                    (object.getString("L_GPS_Y").trim().equals("000000")) || (object.getString("L_GPS_X").trim().equals("000000"))) {
                            } else {
                                //将国测局坐标转换为百度地图坐标
                                double x = Double.valueOf(object.getString("L_GPS_X").trim());
                                double y = Double.valueOf(object.getString("L_GPS_Y").trim());
                                double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
                                double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
                                double bd_lon = z * Math.cos(theta) + 0.0065;
                                double bd_lat = z * Math.sin(theta) + 0.006;
                                latitude = String.valueOf(bd_lat);
                                longitude = String.valueOf(bd_lon);

                                SearchBaiDuMap searchBaiDuMap = new SearchBaiDuMap();
                                searchBaiDuMap.setLatitude(latitude);
                                searchBaiDuMap.setLongitude(longitude);
                                searchBaiDuMap.setLid((object.getString("LID").trim()).
                                        substring((object.getString("LID").trim()).length() - 8, (object.getString("LID").trim()).length()));
                                searchBaiDuMap.setLockName(object.getString("L_NAME").trim());
                                searchBaiDuMap.setType(object.getString("L_BOX_TYPE").trim());
                                searchBaiDu.add(searchBaiDuMap);
                            }
                        }
                        addOverlay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    dialog.dismiss();
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

    //给每一个点添加覆盖物
    private void addOverlay() {
        Marker marker = null;
        LatLng point = null;
        MarkerOptions option = null;
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress);
        for (SearchBaiDuMap data : searchBaiDu) {
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
            bundle.putSerializable("info", "锁ID：" + data.getLid()+"\n"+"锁名称："+data.getLockName());
            marker.setExtraInfo(bundle);
        }
        //将地图移动到最后一个标志点
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.setMapStatus(status);

        //地图状态显示到坐标点
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(6).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }
}
