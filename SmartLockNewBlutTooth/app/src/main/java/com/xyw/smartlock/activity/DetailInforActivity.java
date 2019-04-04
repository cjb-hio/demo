package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.view.LayoutItem1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class DetailInforActivity extends AppCompatActivity {

    private TextView title;
    private ImageView imageback;
    private String personNumber;
    private LayoutItem1 operator_name_text1, lock_id_text2, open_lock_adress_text3,
            lock_time_text5, open_L_BOX_NO__text6, open_lock_L_BOX_TYPE_text7,
            Area_ZONE_NAME_text9, lockperson_text10, lock_option_text11, operating_results_text12,
            open_Areanumber_text13, lock_positioning_image14;
    private String lockID, lockAction, result, lockName, lockAdress;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String strL_BOX_TYPE;
    //经度和纬度
    private String longitude;
    private String latitude;
    private String phoneNumber;
    private String operatorName;
    private String dateTime;
    //请求网络的等待弹框
    private LoadingDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailinfor);
        getSupportActionBar().hide();
        // 获取缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");

        // 初始化控件
        initview();

        // 通过网络发送请求获取数据
        volley_post();
    }

    private void initview() {
        // 设置title标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.detailinfor);
        // 设置title返回按钮
        imageback = (ImageView) findViewById(R.id.common_title_back);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 绑定控件
        operator_name_text1 = (LayoutItem1) findViewById(R.id.operator_name_text1);
        lock_id_text2 = (LayoutItem1) findViewById(R.id.lock_id_text2);
        open_lock_adress_text3 = (LayoutItem1) findViewById(R.id.open_lock_adress_text3);
        lock_time_text5 = (LayoutItem1) findViewById(R.id.lock_time_text5);
        open_L_BOX_NO__text6 = (LayoutItem1) findViewById(R.id.open_L_BOX_NO__text6);
        open_lock_L_BOX_TYPE_text7 = (LayoutItem1) findViewById(R.id.open_lock_L_BOX_TYPE_text7);
        Area_ZONE_NAME_text9 = (LayoutItem1) findViewById(R.id.Area_ZONE_NAME_text9);
        lockperson_text10 = (LayoutItem1) findViewById(R.id.lockperson_phone_text10);
        lock_option_text11 = (LayoutItem1) findViewById(R.id.lock_option_text11);
        operating_results_text12 = (LayoutItem1) findViewById(R.id.operating_results_text12);
        open_Areanumber_text13 = (LayoutItem1) findViewById(R.id.open_Areanumber_text13);
        lock_positioning_image14 = (LayoutItem1) findViewById(R.id.lock_positioning_image14);
        lock_positioning_image14.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((latitude.equals("0.0")) || (longitude.equals("0.0")) || (latitude.equals("000000")) || (longitude.equals("000000"))) {
                    ToastUtil.MyToast(DetailInforActivity.this, "没有定位信息，无法打开地图");
                } else {
                    baiduMap();
                }
            }
        });

        // 得到传过来的操作类型
        Intent intent = getIntent();
        lockAction = intent.getStringExtra("lockAction");
        result = intent.getStringExtra("result");
        phoneNumber = intent.getStringExtra("phoneNumber");
        operatorName = intent.getStringExtra("operatorName");
        dateTime = intent.getStringExtra("dateTime");
        lockID = intent.getStringExtra("LockTD");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        Log.e("TAG", latitude);
        Log.e("TAG", longitude);
        Log.e("TAG", dateTime);
        Log.e("TAG", phoneNumber);
        lock_id_text2.setTextView2Text(lockID);
        lock_time_text5.setTextView2Text(dateTime);
        lock_option_text11.setTextView2Text(lockAction);
        operating_results_text12.setTextView2Text(result);
        lockperson_text10.setTextView2Text(phoneNumber + "(" + operatorName + ")");

    }

    // 通过网络发送post请求发送数据
    private void volley_post() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(DetailInforActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            personNumber = acacheUserBean.getUSER_CONTEXT();
            System.out.println("personNumber=" + personNumber);
            // 得到传过来的ID
            Intent intent = getIntent();
            lockID = intent.getStringExtra("LockTD");

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockInfo&lid=" + lockID + "&Lname=" + lockName
                    + "&laddr=" + lockAdress + "&USER_CONTEXT=" + personNumber;
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

                        JSONArray array = strState.getJSONArray("LOCK_INFO");
                        System.out.println("array=" + array);
                        //判断弹框状态
                        dialog.dismiss();
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);

                            System.out.println("object" + object);

                            String str = "LID:" + object.getString("LID") + "L_NAME:" + object.getString("L_NAME")
                                    + "L_ADDR:" + object.getString("L_ADDR") + "L_GPS_X:" + object.getString("L_GPS_X")
                                    + "L_GPS_Y:" + object.getString("L_GPS_Y") + "L_CREATE_DT:"
                                    + object.getString("L_CREATE_DT") + "L_CREATE_OP:" + object.getString("L_CREATE_OP")
                                    + "L_BOX_NO:" + object.getString("L_BOX_NO") + "L_BOX_TYPE:"
                                    + object.getString("L_BOX_TYPE") + "ZONE_NO:" + object.getString("ZONE_NO")
                                    + "ZONE_NAME:" + object.getString("ZONE_NAME");
//                            longitude = object.getString("L_GPS_X");
//                            latitude = object.getString("L_GPS_Y");
//                            Log.e("TAG", object.getString("L_GPS_X"));
//                            Log.e("TAG", object.getString("L_GPS_Y"));
                            Area_ZONE_NAME_text9.setTextView2Text(object.getString("ZONE_NAME").trim());
                            open_Areanumber_text13.setTextView2Text(object.getString("ZONE_NO").trim());
                            operator_name_text1.setTextView2Text(object.getString("L_NAME").trim());
                            lock_id_text2.setTextView2Text(object.getString("LID").trim());
                            open_lock_adress_text3.setTextView2Text(object.getString("L_ADDR").trim());
                            open_L_BOX_NO__text6.setTextView2Text(object.getString("L_BOX_NO").trim());
                            if (object.getString("L_BOX_TYPE").trim().equals("1")) {
                                strL_BOX_TYPE = "表箱";
                            } else if (object.getString("L_BOX_TYPE").trim().equals("2")) {
                                strL_BOX_TYPE = "变压器室";
                            } else if (object.getString("L_BOX_TYPE").trim().equals("3")) {
                                strL_BOX_TYPE = "台变配电室";
                            } else if (object.getString("L_BOX_TYPE").trim().equals("4")) {
                                strL_BOX_TYPE = "台变计量箱";
                            }
                            open_lock_L_BOX_TYPE_text7.setTextView2Text(strL_BOX_TYPE);
                            //contact_ZONE_NO_text8.setTextView2Text(object.getString("ZONE_NO").trim());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
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

    /**
     * 调起百度地图
     */
    private void baiduMap() {
        Intent intent = null;
        try {// 如果有安装百度地图 就启动百度地图
            StringBuffer sbs = new StringBuffer();
            sbs.append(
                    "intent://map/geocoder?location=" + latitude + "," + longitude + "&coord_type=gcj02&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            try {
//                intent = Intent.getIntent(sbs.toString());
                //上面方法已过时，尝试使用这个方法
                intent = Intent.parseUri(sbs.toString(), Intent.URI_ANDROID_APP_SCHEME);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        } catch (Exception e) {// 没有百度地图则弹出网页端
            StringBuffer sb = new StringBuffer();
            sb.append("http://api.map.baidu.com/geocoder?location=" + latitude + "," + longitude + "&coord_type=gcj02&output=html");
//            Uri uri = Uri.parse(sb.toString());
//            intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                intent = Intent.parseUri(sb.toString(), Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
            startActivity(intent);
        }
    }
}
