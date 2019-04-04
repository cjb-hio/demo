package com.xyw.smartlock.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.common.Phone;
import com.xyw.smartlock.utils.MD5Utils;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.utils.Volley_Default_Time;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ForgetPasswordActivity extends AppCompatActivity implements OnClickListener {
    private Button forgetSubmit, forget_btnCode;
    private TextView title;
    private ImageView backImg;
    // 账号
    private String strPhone = "";
    // 验证码
    private String strCode;
    // 密码
    private TimeCount time;
    private String pwd = "";
    // 重复密码块
    private String repwd = "";
    private EditText phonenumber;
    private EditText code;
    private EditText password;
    private EditText ForgetrepeatPassword;

    private int reqidNo;
    private LoadingDialog dialog;
    private String strState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        getSupportActionBar().hide();
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.retrieve_password);
        // 设置返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        backImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        initview();
        forgetSubmit.setClickable(false);
        forgetSubmit.setBackgroundColor(getResources().getColor(R.color.button_grey));
    }

    /**
     * 监听控件
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_btnCode:
                strPhone = phonenumber.getText().toString().trim();

                if (!Phone.isMobileNO(strPhone)) {
                    ToastUtil.MyToast(getApplicationContext(), "手机号有误");
                    phonenumber.requestFocus();
                    return;
                } else {
                    forget_btnCode.setClickable(false);
                }


                try {
                    // 1.创建请求队列
                    RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

                    // 2.POST请求参数
                    final String URL = HttpServerAddress.BASE_URL + "?m=GetNumber&reqid=" + reqidNo + "&phone=" + strPhone + "&isExist=1";
                    Log.e("TAG",URL);
                    // 3.json post请求处理
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            Log.e("onResponse", arg0.toString());
                            try {
                                strState = arg0.getString("result");
                                if (strState != null && strState.equalsIgnoreCase("true")) {
                                    String json = arg0.toString();
                                    JSONObject jsonObject = new JSONObject(json);
                                    strCode = jsonObject.getString("number");
                                    time = new TimeCount(60000, 1000);
                                    time.start();
                                    ToastUtil.MyToast(getApplicationContext(), "Sending verification code, please wait....");
                                    forgetSubmit.setClickable(true);
                                    forgetSubmit.setBackgroundResource(R.drawable.btn_selector);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            ToastUtil.MyToast(getApplication(), strState);
                            forget_btnCode.setClickable(true);
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
                break;
            case R.id.forgetpassword_complete:
                pwd = password.getText().toString();
                repwd = ForgetrepeatPassword.getText().toString();
                if (!Phone.isMobileNO(phonenumber.getText().toString())) {
                    return;
                }
                if (!strCode.equals(code.getText().toString())) {
                    ToastUtil.MyToast(getApplicationContext(), "验证码有误");
                    return;
                }
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(repwd)) {
                    ToastUtil.MyToast(getApplicationContext(), "密码不能为空");
                    return;
                }
                if (pwd.length() < 6) {
                    ToastUtil.MyToast(getApplicationContext(), "密码至少为6位");
                    return;
                }
                if (!pwd.equals(repwd)) {
                    ToastUtil.MyToast(getApplicationContext(), "两次密码不一致");
                    ForgetrepeatPassword.requestFocus();
                    return;
                }
                ChangPwd();
                finish();
                break;
            default:
                break;
        }
    }

    private void ChangPwd() {
        dialog = new LoadingDialog(ForgetPasswordActivity.this, R.style.dailogStyle);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // MD5 加密
            pwd = password.getText().toString();
            String pass = MD5Utils.encryptByMD5(pwd);
            // 请求地址
            final String URL = HttpServerAddress.RESTPASS + "&op_no="
                    + strPhone + "&op_pass=" + pass + "&reqid=" + reqidNo + "&number=" + strCode;

            Log.e("URL", URL);

            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("onResponse", arg0.toString());
                    try {
                        strState = arg0.getString("result");
                        if (strState != null && strState.equalsIgnoreCase("true")) {

                            String json = arg0.toString();
                            JSONObject jsonObject = new JSONObject(json);
                            strCode = jsonObject.getString("number");
                            Log.i("state", "request success!");
                            ToastUtil.MyToast(getApplicationContext(), "提交成功");
                            dialog.cancel();
                            finish();
                        } else if (strState.equalsIgnoreCase("无此用户！")) {
                            Log.i("state", "request failed!");
                            dialog.dismiss();
                            ToastUtil.MyToast(getApplicationContext(), "用户不存在");
                            Log.e("", strState + "hello");
                        } else {
                            dialog.dismiss();
                            ToastUtil.MyToast(getApplicationContext(), "用户不存在");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.cancel();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    Log.i("state", "request failed2!");
                    dialog.cancel();
                    ToastUtil.MyToast(getApplicationContext(), strState);
                    Log.e("ErrorListener", arg0.toString());
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
            Volley_Default_Time.setDefaultRetryPolicy(jsonObjectRequest);
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 初始化控件
     */
    private void initview() {
        // 监听控件
        forgetSubmit = (Button) findViewById(R.id.forgetpassword_complete);
        forget_btnCode = (Button) findViewById(R.id.forget_btnCode);
        forgetSubmit.setOnClickListener(this);
        forget_btnCode.setOnClickListener(this);
        phonenumber = (EditText) findViewById(R.id.forgetpassword_phonenumber);
        code = (EditText) findViewById(R.id.forgetpassword_code);
        password = (EditText) findViewById(R.id.forgetpassword_password);
        ForgetrepeatPassword = (EditText) findViewById(R.id.forgetpassword_Repeatpassword);
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            forget_btnCode.setClickable(false);
            forget_btnCode.setBackgroundColor(getResources().getColor(R.color.button_grey));
            forget_btnCode.setText(millisUntilFinished / 1000 + "秒");

        }

        @Override
        public void onFinish() {
            forget_btnCode.setClickable(true);
            forget_btnCode.setBackgroundResource(R.drawable.btn_selector);
            forget_btnCode.setText("重新获取验证码");
        }
    }

}
