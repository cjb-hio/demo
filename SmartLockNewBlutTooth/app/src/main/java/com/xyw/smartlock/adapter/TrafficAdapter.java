package com.xyw.smartlock.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.xyw.smartlock.utils.PersonMainBean;
import com.xyw.smartlock.utils.SliderView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class TrafficAdapter extends BaseAdapter {
    private Context context;
    private List<PersonMainBean> mList;
    private String user_context;

    public TrafficAdapter(Context context, List<PersonMainBean> list, String user_context) {
        this.context = context;
        this.mList = list;
        this.user_context = user_context;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        SliderView slideView = (SliderView) view;
        if (slideView == null) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.activity_production_item, null);
            slideView = new SliderView(context);
            slideView.setContentView(itemView);
            holder = new ViewHolder(slideView);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        final PersonMainBean personMainBean = mList.get(position);
        slideView.shrink();
        holder.production_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete area")
                        .setMessage("Are you sure you want to delete this record?")// 设置显示的内容
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {// 添加确定按钮

                            @Override

                            public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件

                                //删除当前列
                                personDelete(personMainBean.getPersonMainID(), position, user_context);

                            }

                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {// 添加返回按钮

                    @Override

                    public void onClick(DialogInterface dialog, int which) {// 响应事件

                    }

                }).show();// 在按键响应事件中显示此对话框


            }
        });
        holder.production_item_TextView1.setText(personMainBean.getPersonMainStr());
        holder.production_item_TextView2.setText(personMainBean.getPersonMainID());
        return slideView;
    }


    private static class ViewHolder {
        public TextView production_item_TextView1;
        public TextView production_item_TextView2;
        public ViewGroup production_holder;


        ViewHolder(View view) {
            production_item_TextView1 = (TextView) view.findViewById(R.id.production_item_TextView1);
            production_item_TextView2 = (TextView) view.findViewById(R.id.production_item_TextView2);
            production_holder = (ViewGroup) view.findViewById(R.id.holder);

        }
    }


    private void personDelete(String getPersonMainID, final int position, String user_context) {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(context);


            final String URL = HttpServerAddress.BASE_URL + "?m=taskstringfun&funstr=del" + "&strid=" + getPersonMainID + "&user_context=" + user_context;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {

                    try {
                        String strState = arg0.getString("result");
                        if (strState.equals("true")) {
                            mList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "delete success！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "You do not have permission to delete！", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
