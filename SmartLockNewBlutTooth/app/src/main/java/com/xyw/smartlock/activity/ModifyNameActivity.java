package com.xyw.smartlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;



/**
 * Created by 19428 on 2016/9/19.
 */
public class ModifyNameActivity extends Activity implements View.OnClickListener {
    private TextView modifyName_tv_title;
    private TextView modifyName_tv_determine;
    private ImageView modifyName_tv_back;
    private EditText modifyName_editText;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyname);
        // 缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        initView();
    }

    private void initView() {
        modifyName_tv_title = (TextView) findViewById(R.id.modifyName_tv_title);
        modifyName_tv_title.setText(R.string.Modify_Name);
        modifyName_tv_determine = (TextView) findViewById(R.id.modifyName_tv_determine);
        modifyName_tv_back = (ImageView) findViewById(R.id.modifyName_tv_back);
        modifyName_editText = (EditText) findViewById(R.id.modifyName_editText);
        modifyName_tv_determine.setOnClickListener(this);
        modifyName_tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modifyName_tv_determine:
                // 读取缓存数据
                AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                String op_no = LoginInfo.getOP_NO();
                String user_context = LoginInfo.getUSER_CONTEXT();
                String op_name = modifyName_editText.getText().toString().trim();
                if (!op_name.equals("")) {
                    try {
                        volley_get(URLEncoder.encode(op_name, "UTF-8"), op_no, user_context);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "名称不能为空!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.modifyName_tv_back:
                finish();
                break;
            default:
                break;
        }

    }

    private void volley_get(final String op_name, String op_no, String user_context) {
        try {
            //等待网络的D
            dialog = new LoadingDialog(ModifyNameActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);


            final String URL = HttpServerAddress.BASE_URL + "?m=setopname&op_no=" + op_no + "&op_name=" +
                    op_name + "&user_context=" + user_context;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {

                    try {
                        String strState = arg0.getString("result");
                        if (strState.equals("true")) {
                            Toast.makeText(ModifyNameActivity.this, "用户名更新成功", Toast.LENGTH_SHORT).show();
                            UpDateAcache(URLDecoder.decode(op_name, "UTF-8"));
                            //跳转会个人信息页面
                            Intent intent = new Intent();
                            intent.putExtra("userInfo_Name", URLDecoder.decode(op_name, "UTF-8"));
                            ModifyNameActivity.this.setResult(RESULT_OK, intent);
                            // 关闭Activity
                            ModifyNameActivity.this.finish();
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

    private void UpDateAcache(String op_name) {
        Log.e("TAG", op_name);
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        String Account1 = LoginInfo.getAccount();
        String OP_DT1 = LoginInfo.getOP_DT();
        String OP_NO1 = LoginInfo.getOP_NO();
        String result1 = LoginInfo.getResult();
        String BeginTime1 = LoginInfo.getBeginTime();
        String EndTime1 = LoginInfo.getEndTime();
        String AREA_NAME1 = LoginInfo.getAREA_NAME();
        String KEYVALUE1 = LoginInfo.getKEYVALUE();
        String Area_id1 = LoginInfo.getArea_id();
        String ROLE_ID1 = LoginInfo.getROLE_ID();
        String USER_CONTEXT1 = LoginInfo.getUSER_CONTEXT();
        String MAXVER1 = LoginInfo.getMAXVER();
        String Address1 = LoginInfo.getAddress();


        acacheUserBean.setAccount(Account1);
        acacheUserBean.setMAXVER(MAXVER1);
        acacheUserBean.setOP_DT(OP_DT1);
        acacheUserBean.setOP_NO(OP_NO1);
        acacheUserBean.setResult(result1);
        acacheUserBean.setBeginTime(BeginTime1);
        acacheUserBean.setEndTime(EndTime1);
        acacheUserBean.setOP_NAME(op_name);
        acacheUserBean.setAREA_NAME(AREA_NAME1);
        acacheUserBean.setKEYVALUE(KEYVALUE1);
        acacheUserBean.setArea_id(Area_id1);
        acacheUserBean.setROLE_ID(ROLE_ID1);
        acacheUserBean.setUSER_CONTEXT(USER_CONTEXT1);
        acacheUserBean.setAddress(Address1);
        aCache.put("LoginInfo", acacheUserBean);// 缓存文件名称

    }

}

