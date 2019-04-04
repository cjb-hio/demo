package com.xyw.smartlock.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by HP on 2017/7/20.
 */

public class ModifyUserNameFragment extends DialogFragment implements View.OnClickListener {

    private EditText et_rename;
    private Button btn_submit, btn_cancel;
    private TextView tv_dialog_title;
    private AlertDialog alertDialog;

    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private LoadingDialog dialog;

    public interface OnSubmitResultListener {
        void onSubmit(boolean isSuccess, String result);
    }

    private OnSubmitResultListener mOnSubmitResultListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mOnSubmitResultListener = (OnSubmitResultListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDeviceSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 缓存数据
        aCache = ACache.get(getContext());
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.rename_username_dialog, null);
        tv_dialog_title = (TextView) dialogView.findViewById(R.id.tv_dialog_title);
        et_rename = (EditText) dialogView.findViewById(R.id.et_rename);
        btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);
        btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        tv_dialog_title.setText("修改用户名");
        et_rename.setHint("请输入用户名");
        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                alertDialog.cancel();
                break;
            case R.id.btn_submit:
                if (!et_rename.getText().toString().trim().equals("")) {
                    try {
                        volley_get(URLEncoder.encode(et_rename.getText().toString().trim(), "UTF-8"), acacheUserBean.getOP_NO(), acacheUserBean.getUSER_CONTEXT());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "名称不能为空!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void volley_get(final String op_name, String op_no, String user_context) {
        try {
            //等待网络的D
            dialog = new LoadingDialog(getContext(), R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());


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
                            Toast.makeText(getContext(), "用户名更新成功", Toast.LENGTH_SHORT).show();
                            UpDateAcache(URLDecoder.decode(op_name, "UTF-8"));
                            //跳转会个人信息页面
                            if (mOnSubmitResultListener != null)
                                mOnSubmitResultListener.onSubmit(true, op_name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dialog.dismiss();
                        alertDialog.cancel();
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
                        alertDialog.cancel();
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
