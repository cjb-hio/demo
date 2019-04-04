package com.xyw.smartlock.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.MD5Utils;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ChangePasswordActivity extends AppCompatActivity implements OnClickListener {
    private EditText old_password, new_password, new_password_again;
    private Button btn_changepassword;
    private TextView title;
    private ImageView imageback;
    private ProgressDialog progressDialog;
    private ACache aCache;
    private AcacheUserBean acacheUserBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        getSupportActionBar().hide();
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        // 初始化控件
        initview();
    }

    //dianjishijian
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_changepassword:
                String password = old_password.getText().toString();
                String new_pwd = new_password.getText().toString();
                String repPassword = new_password_again.getText().toString();
                if (password.length() < 6) {
                    ToastUtil.MyToast(getApplicationContext(), "请输入不少于6个字符的原密码");
                    return;
                }
                if (new_pwd.length() < 6) {
                    ToastUtil.MyToast(getApplicationContext(), "密码至少为6个");
                    return;
                }
                if (!new_pwd.equals(repPassword)) {
                    ToastUtil.MyToast(getApplicationContext(), "两次密码不一样");
                    new_password_again.requestFocus();
                    return;
                }

                ChangePwd();
                break;

            default:
                break;
        }

    }

    private void ChangePwd() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在提交数据,请稍后...");
        try {
            progressDialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // MD5 加密
            String old_pass = MD5Utils.encryptByMD5(old_password.getText()
                    .toString());
            String new_pass = MD5Utils.encryptByMD5(new_password.getText()
                    .toString());
            // 请求地址
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            Log.e("Acache", acacheUserBean.toString());
            final String URL = HttpServerAddress.MODPASS + "&op_no="
                    + acacheUserBean.getOP_NO() + "&op_pass_old=" + old_pass
                    + "&op_pass=" + new_pass + "&USER_CONTEXT="
                    + acacheUserBean.getUSER_CONTEXT();

            Log.e("URL", URL);

            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("onResponse", arg0.toString());
                    try {
                        String strState = arg0.getString("result");
                        if (strState != null
                                && strState.equalsIgnoreCase("true")) {

                            progressDialog.cancel();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.cancel();

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    Log.i("state", "request failed2!");
                    progressDialog.cancel();
                    ToastUtil.MyToast(getApplicationContext(),
                            arg0.toString());
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(
                                arg0.data, "UTF-8"));
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(arg0));
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initview() {
        // 设置标题栏的名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.change_password);
        // 设置标题栏返回按钮
        imageback = (ImageView) findViewById(R.id.common_title_back);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_changepassword = (Button) findViewById(R.id.btn_changepassword);
        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        new_password_again = (EditText) findViewById(R.id.new_password_again);
        btn_changepassword.setOnClickListener(this);
    }

}
