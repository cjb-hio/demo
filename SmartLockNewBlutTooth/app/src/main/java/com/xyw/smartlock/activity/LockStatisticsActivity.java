package com.xyw.smartlock.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class LockStatisticsActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> lockStatList = new ArrayList<HashMap<String, Object>>();
    private HashMap<String, Object> lockStatItemList = new HashMap<String, Object>();
    private TextView title;
    private ImageView titleBack, titleSearch;
    private ListView lockStatListview;
    private MyAdapter adapter;
    private LoadingDialog dialog;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String userContex;
    private String startDateTime, endDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayreport);
        getSupportActionBar().hide();
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        userContex = acacheUserBean.getUSER_CONTEXT();
        title = (TextView) findViewById(R.id.lockstat_tv_title);
        title.setText(R.string.lockstatistics);
        titleBack = (ImageView) findViewById(R.id.lockstat_title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleSearch = (ImageView) findViewById(R.id.lockstat_title_search);
        titleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LockStatisticsActivity.this, DateTimeActivity.class), 1);
            }
        });
        //添加整个布局
        lockStatListview = (ListView) findViewById(R.id.dayreportlistview);
        adapter = new MyAdapter(this);
        lockStatListview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void vooleyGet() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(LockStatisticsActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.POST请求参数

            final String URL = HttpServerAddress.BASE_URL + "?m=getzonelocklognum&begin_time=" + startDateTime +
                    "&end_time=" + endDateTime + "&user_context=" + userContex;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;
                        System.out.println("------------" + "LOCK_LOCKLOGNUM=" + strState + "--------------");
                        JSONArray array = strState.getJSONArray("LOCK_LOCKLOGNUM");
                        System.out.println("array=" + array);
                        dialog.dismiss();
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);
                                HashMap<String, Object> lockStatItemList = null;
                                lockStatItemList = new HashMap<String, Object>();
                                lockStatItemList.put("DayReportItemAreaName", object.getString("ZONE_NAME").trim());
                                lockStatItemList.put("DayReportItemOperator", object.getString("OP_NAME").trim());
                                lockStatItemList.put("DayReportItemLockNumber", object.getString("LOCK_NUM").trim());
                                lockStatList.add(lockStatItemList);
                                adapter.notifyDataSetChanged();
                            }
                            System.out.println("lockStatList=" + lockStatList);
                        } else {
                            Toast.makeText(LockStatisticsActivity.this, "暂时没有开锁信息!", Toast.LENGTH_SHORT).show();
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
                    ToastUtil.MyToast(LockStatisticsActivity.this, "这个时间段内无开锁信息");

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


    //获取从弹框传回来的时间信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) return;
        startDateTime = data.getStringExtra("start_date");
        endDateTime = data.getStringExtra("end_date");

        if (startDateTime != null && endDateTime != null) {
            if (startDateTime.equals("CANCEL") || endDateTime.equals("CANCEL")) {

            } else {
                lockStatList.removeAll(lockStatList);
                adapter.notifyDataSetChanged();
                vooleyGet();
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        Context context;

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return lockStatList.size();
        }

        @Override
        public Object getItem(int position) {
            return lockStatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_dayreport_item, null);
                holder = new ViewHolder();
                holder.dayreportitem_areaName = (TextView) convertView.findViewById(R.id.activity_dayreport_name);
                holder.dayreportitem_operator = (TextView) convertView.findViewById(R.id.activity_dayreport_successful);
                holder.dayreportitem_lockNumber = (TextView) convertView.findViewById(R.id.activity_dayreport_failures);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.dayreportitem_areaName.setText(lockStatList.get(position).get("DayReportItemAreaName").toString());
            holder.dayreportitem_operator.setText(lockStatList.get(position).get("DayReportItemOperator").toString());
            holder.dayreportitem_lockNumber.setText(lockStatList.get(position).get("DayReportItemLockNumber").toString());

            return convertView;
        }

    }

    private class ViewHolder {
        TextView dayreportitem_areaName;
        TextView dayreportitem_operator;
        TextView dayreportitem_lockNumber;

    }


}
