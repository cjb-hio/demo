package com.xyw.smartlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.xyw.smartlock.view.CustomItemTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LockFileChildMeter extends AppCompatActivity {
    private TextView title;
    private ImageView titleBack;
    private CustomItemTextView lockfilechildMeterOne, lockfilechildMeterTwo, lockfilechildMeterThree, lockfilechildMeterFour, lockfilechildMeterFive,
            lockfilechildMeterSix, lockfilechildMeterSeven, lockfilechildMeterEight, lockfilechildMeterNine, lockfilechildMeterTen,
            lockfilechildMeterEle, lockfilechildMeterTwe, lockfilechildMeterThi, lockfilechildMeterFou, lockfilechildMeterFif;
    //提取缓存参数
    private AcacheUserBean LoginInfo;
    private ACache aCache;
    private String personNumber;
    private String lockID;
    //请求网络的等待界面
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockfile_childmeter);
        getSupportActionBar().hide();

        // 获取缓存数据
        aCache = ACache.get(this);
        LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        personNumber = LoginInfo.getUSER_CONTEXT().toString();
        Intent intent = getIntent();
        lockID = intent.getStringExtra("ID");
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(intent.getStringExtra("lockfile_type"));
        titleBack = (ImageView) findViewById(R.id.common_title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initview();
        vooley_get();
    }

    //绑定控件
    private void initview() {
        lockfilechildMeterOne = (CustomItemTextView) findViewById(R.id.lockfilechildMeterOne);
        lockfilechildMeterTwo = (CustomItemTextView) findViewById(R.id.lockfilechildMeterTwo);
        lockfilechildMeterThree = (CustomItemTextView) findViewById(R.id.lockfilechildMeterThree);
        lockfilechildMeterFour = (CustomItemTextView) findViewById(R.id.lockfilechildMeterFour);
        lockfilechildMeterFive = (CustomItemTextView) findViewById(R.id.lockfilechildMeterFive);
        lockfilechildMeterSix = (CustomItemTextView) findViewById(R.id.lockfilechildMeterSix);
        lockfilechildMeterSeven = (CustomItemTextView) findViewById(R.id.lockfilechildMeterServen);
        lockfilechildMeterEight = (CustomItemTextView) findViewById(R.id.lockfilechildMeterEight);
        lockfilechildMeterNine = (CustomItemTextView) findViewById(R.id.lockfilechildMeterNine);
        lockfilechildMeterTen = (CustomItemTextView) findViewById(R.id.lockfilechildMeterTen);
        lockfilechildMeterEle = (CustomItemTextView) findViewById(R.id.lockfilechildMeterEle);
        lockfilechildMeterTwe = (CustomItemTextView) findViewById(R.id.lockfilechildMeterTwe);
        lockfilechildMeterThi = (CustomItemTextView) findViewById(R.id.lockfilechildMeterThi);
        lockfilechildMeterFou = (CustomItemTextView) findViewById(R.id.lockfilechildMeterFou);
        lockfilechildMeterFif = (CustomItemTextView) findViewById(R.id.lockfilechildMeterFif);
    }

    //请求数据
    private void vooley_get() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(LockFileChildMeter.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=getboxinfo&LID=" + lockID + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {

                    try {
                        JSONObject strState = arg0;
                        // json数据解析
                        System.out.println("strState=" + strState);
                        JSONArray array = strState.getJSONArray("LOCK_BOX");
                        dialog.dismiss();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            System.out.println("object" + object);
                            if (object.has("BOX_SUBID10") && object.has("BOX_SUBID19")) {
                                if (object.getString("BOX_SUBID10").equals(object.getString("BOX_SUBID19"))) {
                                    object.put("BOX_SUBID19", "");
                                }
                            }
                            setTextView(lockfilechildMeterOne, "BOX_SUBID10", object);
                            setTextView(lockfilechildMeterTwo, "BOX_SUBID11", object);
                            setTextView(lockfilechildMeterThree, "BOX_SUBID12", object);
                            setTextView(lockfilechildMeterFour, "BOX_SUBID13", object);
                            setTextView(lockfilechildMeterFive, "BOX_SUBID14", object);
                            setTextView(lockfilechildMeterSix, "BOX_SUBID15", object);
                            setTextView(lockfilechildMeterSeven, "BOX_SUBID16", object);
                            setTextView(lockfilechildMeterEight, "BOX_SUBID17", object);
                            setTextView(lockfilechildMeterNine, "BOX_SUBID18", object);
                            setTextView(lockfilechildMeterTen, "BOX_SUBID19", object);
                            setTextView(lockfilechildMeterEle, "BOX_SUBID20", object);
                            setTextView(lockfilechildMeterTwe, "BOX_SUBID21", object);
                            setTextView(lockfilechildMeterThi, "BOX_SUBID22", object);
                            setTextView(lockfilechildMeterFou, "BOX_SUBID23", object);
                            setTextView(lockfilechildMeterFif, "BOX_SUBID24", object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
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
                        dialog.dismiss();
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
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

    private void setTextView(CustomItemTextView tv, String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field))
            tv.setContent(jsonObject.getString(field));
        else
            tv.setContent("");
    }

}
