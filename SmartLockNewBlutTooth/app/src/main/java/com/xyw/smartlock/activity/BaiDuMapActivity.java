package com.xyw.smartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.ToastUtil;

import java.util.List;

public class BaiDuMapActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    private Context mContext;
    private LatLng cenpt;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 反地址编码,范围搜索
    private GeoCoder mSearch = null;
    private ListView lv_poi;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    //经纬度
    private double latitude;
    private double longitude;
    private String lockRegistID;
    private TextView title;
    private ImageView titleBack;
    private String areaAddress, areaName;
    private String TypeNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidumapview_activity);
        getSupportActionBar().hide();
        mContext = BaiDuMapActivity.this;
        mMapView = (MapView) findViewById(R.id.baidumapview);
        lv_poi = (ListView) findViewById(R.id.baidumapview_list);
        mBaiduMap = mMapView.getMap();
        Intent intent = getIntent();
        TypeNumber = intent.getStringExtra("TypeNumber");
        lockRegistID = intent.getStringExtra("ID");
        lv_poi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view.findViewById(R.id.baidumapview_text2);
                TextView tv2 = (TextView) view.findViewById(R.id.baidumapview_text1);
                areaAddress = tv.getText().toString();
                areaName = tv2.getText().toString();
                Intent intent = new Intent(BaiDuMapActivity.this, LockRegisteredActivity.class);
                intent.putExtra("areaAddress", areaAddress);
                intent.putExtra("areaName", areaName);
                intent.putExtra("ID", lockRegistID);
                intent.putExtra("TypeNumber", TypeNumber);
//                startActivity(intent);
                BaiDuMapActivity.this.setResult(RESULT_OK, intent);
                BaiDuMapActivity.this.finish();
                System.out.println("str=" + areaAddress);
            }
        });
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.addressinfo);
        titleBack = (ImageView) findViewById(R.id.common_title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaiDuMapActivity.this, LockRegisteredActivity.class);
//                startActivity(intent);
                BaiDuMapActivity.this.setResult(RESULT_OK, intent);
                BaiDuMapActivity.this.finish();
            }
        });

//        Intent intent = getIntent();
//        latitude = intent.getDoubleExtra("latitude", 0);
//        longitude = intent.getDoubleExtra("longitude", 0);
//        addOverlay();
//        cenpt = new LatLng(latitude, longitude);
//        mSearch = GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(this);
//
        //当前地点标识物
        addOverlay();
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        // 设置先关参数
        InitLocation();
        mLocationClient.start();


    }

    /**
     * 添加覆盖物的方法
     */
    private void addOverlay() {
        Marker marker = null;
        LatLng point = null;
        MarkerOptions option = null;
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.baidumapaddress);
        point = new LatLng(latitude, longitude);
        option = new MarkerOptions().position(point).icon(bitmap);
        marker = (Marker) mBaiduMap.addOverlay(option);
        //Bundle用于通信
        Bundle bundle = new Bundle();
        marker.setExtraInfo(bundle);//将bundle值传入marker中，给baiduMap设置监听时可以得到它
        //将地图移动到最后一个标志点
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.setMapStatus(status);

    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }


    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
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

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append(location.getDirection());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            }
            if (location != null) {
                mLocationClient.stop();
            }

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("TAG", String.valueOf(latitude));
            Log.e("TAG", String.valueOf(longitude));
            Log.i("BaiduLocationApiDem", sb.toString());
            LatLng cenpt = new LatLng(location.getLatitude(), location.getLongitude());
            //创建mSearch实例对象
            mSearch = GeoCoder.newInstance();
            //发起反地理位置请求
            ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
            //设置反地理位置坐标
            reverseGeoCodeOption.location(new LatLng(location.getLatitude(), location.getLongitude()));
            mSearch.reverseGeoCode(reverseGeoCodeOption);
            addOverlay();
            // 定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            // 改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);

            completeLis();
            //设置查询结果监听者
            mSearch.setOnGetGeoCodeResultListener(BaiDuMapActivity.this);
        }

    }

    public void completeLis() {
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
//                // TODO Auto-generated method stub
//                Toast.makeText(BaiDuMapActivity.this, "加载完成", Toast.LENGTH_SHORT)
//                        .show();
//                 nearbySearch(cenpt.latitude, cenpt.longitude);
//                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                // 移动地图后结果
                cenpt = mBaiduMap.getMapStatus().target;
                System.out.println("*****************lat = " + cenpt.latitude);
                System.out.println("*****************lng = " + cenpt.longitude);
                // nearbySearch(cenpt.latitude, cenpt.longitude);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

        List<PoiInfo> list_poi = result.getPoiList();
        if (result.getPoiList() != null) {
            PoiAdapter pa = new PoiAdapter(BaiDuMapActivity.this, list_poi);
            lv_poi.setAdapter(pa);
        } else {
            try {
                Thread.sleep(5000);
                Intent intent = new Intent(BaiDuMapActivity.this, LockRegisteredActivity.class);
                startActivity(intent);
                BaiDuMapActivity.this.setResult(RESULT_OK, intent);
                BaiDuMapActivity.this.finish();
                ToastUtil.MyToast(BaiDuMapActivity.this, "无法定位到信息，请检查网络或者重启手机");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    //填充istview的adapter
    public class PoiAdapter extends BaseAdapter {

        private Context mContext;
        private List<PoiInfo> list_poi;

        public PoiAdapter(Context c, List<PoiInfo> list) {
            this.list_poi = list;
            this.mContext = c;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list_poi.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return list_poi.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.baidumapview_activity_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.baidumapview_text1);
                holder.address = (TextView) convertView.findViewById(R.id.baidumapview_text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list_poi.get(arg0).name);
            holder.address.setText(list_poi.get(arg0).address);
            System.out.println(list_poi.get(arg0).address);

            return convertView;
        }

        class ViewHolder {
            TextView name, address;
        }
    }

    // 绑定手机返回键按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(BaiDuMapActivity.this, LockRegisteredActivity.class);
//            startActivity(intent);
            BaiDuMapActivity.this.setResult(RESULT_OK, intent);
            BaiDuMapActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mLocationClient.stop();
        mSearch.destroy();
    }
}
