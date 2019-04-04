package com.xyw.smartlock.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.xyw.smartlock.activity.LoginActivity;
import com.xyw.smartlock.adapter.MenuAdapter;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by HP on 2017/5/19.
 */

public class JurisdictionActivityUtil {
    private List<JurisdictBean> mList;
    private MenuAdapter mAdapter;
    private LoadingDialog dialog;
    private Context mContext;

    public JurisdictionActivityUtil(Context context, List<JurisdictBean> list, MenuAdapter adapter) {
        this.mList = list;
        this.mAdapter = adapter;
        this.mContext = context;
        //等待网络的D
        dialog = new LoadingDialog(mContext, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);
    }


    public void deleteList(final String account, final int position, String userContext, final String op_number) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(mContext);
            final String URL = HttpServerAddress.BASE_URL + "?m=deleteop&op_no=" + account + "&user_context=" + userContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        String strState = arg0.getString("result");
                        if (strState.equals("true")) {
                            mList.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        } else {
                            Toast.makeText(mContext, "你没有删除此用户权限!", Toast.LENGTH_SHORT).show();
                        }
                        if (account.equals(op_number)) {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
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


    //重命名文件
    public void rename(final String op_name, String op_no, final int position, String user_context) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(mContext);
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
                            Toast.makeText(mContext, "用户名更新成功", Toast.LENGTH_SHORT).show();
                            JurisdictBean jurisdictBean = mList.get(position);
                            jurisdictBean.setName(URLDecoder.decode(op_name, "UTF-8"));
                            mAdapter.notifyItemChanged(position);
                        } else {
                            Toast.makeText(mContext, strState, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
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
}
