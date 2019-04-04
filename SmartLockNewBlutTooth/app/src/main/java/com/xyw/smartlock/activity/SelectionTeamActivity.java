package com.xyw.smartlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
import com.xyw.smartlock.adapter.SelectionListAdapter;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.SelectionListBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class SelectionTeamActivity extends Activity {
    private SelectionListBean selectionListBean = null;
    private List<SelectionListBean> SelectionList = new ArrayList<>();
    ;
    private ListView selection_listView;
    private SelectionListAdapter selectionListAdapter;
    private LoadingDialog dialog;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String user_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_list);
        // 缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        user_context = acacheUserBean.getUSER_CONTEXT();

        VolleyGet();
        initView();
    }

    private void initView() {
        selection_listView = (ListView) findViewById(R.id.selection_listView);
        selectionListAdapter = new SelectionListAdapter(this, SelectionList);
        selection_listView.setAdapter(selectionListAdapter);
        selection_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
//                if (SelectionList.size() > 1) {
                    intent.putExtra("username", SelectionList.get(position).getSelectionBean1());
                    intent.putExtra("userID", SelectionList.get(position).getSelectionBean2());

//                } else {
//                    intent.putExtra("username", "全部");
//                    intent.putExtra("userID", "");
//                }
                Log.e("TAG", SelectionList.get(position).getSelectionBean1());
                Log.e("TAG", SelectionList.get(position).getSelectionBean2());
                SelectionTeamActivity.this.setResult(RESULT_OK, intent);
                // 关闭Activity
                SelectionTeamActivity.this.finish();
            }
        });

    }

    //获取提交的数据
    private void VolleyGet() {

        try {
            //等待网络的D
            dialog = new LoadingDialog(SelectionTeamActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL1 = HttpServerAddress.BASE_URL + "?m=taskstringfun" + "&funstr=getlist" + "&strtype=bz"
                    + "&user_context=" + user_context;

            Log.e("TAG", URL1);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL1, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        JSONObject strState = arg0;
                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_STRING");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e("json", object.toString());
                            // 解析出单个json集中所有数
                            selectionListBean = new SelectionListBean();
                            selectionListBean.setSelectionBean1(object.getString("STR_DEMO"));
                            selectionListBean.setSelectionBean2(object.getString("STR_ID"));
                            SelectionList.add(selectionListBean);
                        }
                        selectionListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
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
}
