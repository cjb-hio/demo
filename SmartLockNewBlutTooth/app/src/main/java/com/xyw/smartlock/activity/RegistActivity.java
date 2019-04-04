package com.xyw.smartlock.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.PhoneInfo;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.common.Phone;
import com.xyw.smartlock.utils.MD5Utils;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.utils.Volley_Default_Time;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class RegistActivity extends AppCompatActivity implements OnClickListener {
    private TextView title;
    private ImageView backImageView;

    private EditText regist_etPhone, regist_etUserName, regist_etDepartment,
            regist_etCode, regist_etPsw, regist_etRepeatPsw, regist_Referees_number;

    private Button regist_btnCode, regist_btnSubmit;
    private LoadingDialog dialog;

    private TimeCount time;
    private int reqidNo;

    private String etPhone;
    private String etUserName;
    private String etCode;
    private String etPsw;
    private String etRepeatPsw;
    private String str;
    //    private TelephonyManager telephonyManager;
    private PhoneInfo simInfo;
    private String strState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        getSupportActionBar().hide();
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.regist);
        // 设置返回按钮
        backImageView = (ImageView) findViewById(R.id.common_title_back);
        backImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        simInfo = new PhoneInfo(RegistActivity.this);
        initView();
        reqidNo = (int) (Math.random() * 1000);
    }

    private void initView() {
        regist_etPhone = (EditText) findViewById(R.id.regist_etPhone);
//        regist_etPhone.setText(telephonyManager.getLine1Number());
        regist_etPhone.setText(simInfo.getNativePhoneNumber());
        regist_etUserName = (EditText) findViewById(R.id.regist_etUserName);
        regist_etDepartment = (EditText) findViewById(R.id.regist_etDepartment);
        regist_etCode = (EditText) findViewById(R.id.regist_etCode);
        regist_etPsw = (EditText) findViewById(R.id.regist_etPsw);

        regist_etRepeatPsw = (EditText) findViewById(R.id.regist_etRepeatPsw);

        regist_btnCode = (Button) findViewById(R.id.regist_btnCode);
        regist_btnSubmit = (Button) findViewById(R.id.regist);

        regist_Referees_number = (EditText) findViewById(R.id.regist_Referees_number);

        regist_etRepeatPsw.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (regist_etPsw.length() < 6) {
                        ToastUtil.MyToast(RegistActivity.this, "密码至少6位以上!");
                    }
                } else if (!regist_etPsw.getText().toString().equals(regist_etRepeatPsw.getText().toString())) {
                    ToastUtil.MyToast(RegistActivity.this, "两次密码不一致!");
                }
            }
        });
        regist_etRepeatPsw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive() && getCurrentFocus() != null) {
                        if (getCurrentFocus().getWindowToken() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                }
                return false;
            }
        });

        regist_btnCode.setOnClickListener(this);
        regist_btnSubmit.setOnClickListener(this);
        regist_btnSubmit.setClickable(false);
        regist_btnSubmit.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onClick(View v) {
        etPhone = regist_etPhone.getText().toString().trim();
        etUserName = regist_etUserName.getText().toString().trim();
        etCode = regist_etCode.getText().toString().trim();
        etPsw = regist_etPsw.getText().toString();
        etRepeatPsw = regist_etRepeatPsw.getText().toString();
        switch (v.getId()) {
            case R.id.regist_btnCode:
                if (!Phone.isMobileNO(etPhone)) {
                    ToastUtil.MyToast(getApplicationContext(), "手机号有误");
                    regist_etPhone.requestFocus();
                    return;
                } else {
                    regist_btnCode.setClickable(false);
                }

                try {
                    // 1.创建请求队列
                    RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
                    // 2.POST请求参数
                    final String URL = HttpServerAddress.BASE_URL + "?m=GetNumber&reqid=" + reqidNo + "&isExist=0" + "&phone="
                            + regist_etPhone.getText().toString();

                    Log.e("TAG", URL);
                    // 3.json post请求处理
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject arg0) {
                            Log.e("onResponse", arg0.toString());
                            try {
                                strState = arg0.getString("result");
                                if (strState != null && strState.equalsIgnoreCase("true")) {
                                    String json = arg0.toString();
                                    JSONObject jsonObject = new JSONObject(json);
                                    str = jsonObject.getString("number");
                                    time = new TimeCount(60000, 1000);
                                    time.start();
                                    regist_btnSubmit.setClickable(true);
                                    regist_btnSubmit.setBackgroundResource(R.drawable.btn_selector);
                                } else {
                                    regist_btnCode.setClickable(true);
                                    Toast.makeText(RegistActivity.this, strState, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            regist_btnCode.setClickable(true);
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
                break;
            case R.id.regist:
                if (!Phone.isMobileNO(etPhone)) {
                    ToastUtil.MyToast(this, "手机号有误");
                    return;
                }
                if (!Phone.isMobileNO(regist_Referees_number.getText().toString().trim())) {
                    Toast.makeText(this, "推荐人号码有误", Toast.LENGTH_SHORT).show();
                }
                if (etUserName.length() < 2) {
                    ToastUtil.MyToast(this, "用户名至少两个中文");
                    regist_etUserName.requestFocus();
                    return;
                }
                if (!str.equals(regist_etCode.getText().toString())) {
                    ToastUtil.MyToast(this, "验证码有误");
                    return;
                }
                if (etPsw.length() < 6) {
                    ToastUtil.MyToast(getApplicationContext(), "密码至少6位");
                    return;
                }
                if (!etPsw.equals(etRepeatPsw)) {
                    ToastUtil.MyToast(getApplicationContext(), "两次密码不一样");
                    regist_etPsw.requestFocus();
                    return;
                }
                if (!regist_etCode.getText().toString().equals(etCode)) {
                    ToastUtil.MyToast(getApplicationContext(), "验证码不对");
                    return;
                }
                CreateAccount();
                break;
            default:
                break;
        }
    }

    private void CreateAccount() {
        try {
            dialog = new LoadingDialog(RegistActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // MD5 加密
            String pass = MD5Utils.encryptByMD5(regist_etPsw.getText()
                    .toString());
            // 请求地址
            final String URL = HttpServerAddress.REGISTER + "&reqid=" + reqidNo
                    + "&number=" + str
                    + "&op_no=" + regist_etPhone.getText().toString()
                    + "&op_name=" + URLEncoder.encode(regist_etUserName.getText().toString(), "utf-8")
                    + "&op_dt=" + URLEncoder.encode(regist_etDepartment.getText().toString(), "utf-8")
                    + "&R_OP_NO" + URLEncoder.encode(regist_Referees_number.getText().toString().trim(), "UTF-8")
                    + "&op_pass=" + pass
                    + "&op_phone=" + regist_etPhone.getText().toString();
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
                            Log.i("state", "request success!");
                            ToastUtil.MyToast(getApplicationContext(), "提交成功");
                            dialog.dismiss();
                            finish();
                        } else {
                            Log.i("state", "request failed!");
                            dialog.dismiss();
                            ToastUtil.MyToast(getApplicationContext(), "用户已存在");
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

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            regist_btnCode.setClickable(false);
            regist_btnCode.setBackgroundColor(Color.parseColor("#d1d1d1"));
            regist_btnCode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            regist_btnCode.setClickable(true);
            regist_btnCode.setBackgroundResource(R.drawable.btn_selector);
            regist_btnCode.setText("重新获取验证码");
        }
    }
}
