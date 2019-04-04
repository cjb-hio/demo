package com.xyw.smartlock.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xyw.smartlock.adapter.UnitSwipeAdapter;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.OnItemClickListener;
import com.xyw.smartlock.utils.PersonMainBean;
import com.xyw.smartlock.view.ListViewDecoration;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TrafficActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private ImageView titleBack, traffic_frame_ImageView;
//    private ListView traffic_ListView;
    private List<PersonMainBean> teamList = new ArrayList<>();
    private PersonMainBean personMainBean = null;
//    private TrafficAdapter trafficAdapter;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String user_context;
    private LoadingDialog dialog;

    private SwipeRefreshLayout traffic_refreshable_view;
    private SwipeMenuRecyclerView traffic_listview;
    private UnitSwipeAdapter mUnitSwipeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        getSupportActionBar().hide();

        // 缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        user_context = acacheUserBean.getUSER_CONTEXT();

        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.traffic_tools);
        titleBack = (ImageView) findViewById(R.id.common_tv_back);
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
        traffic_frame_ImageView = (ImageView) findViewById(R.id.traffic_frame_ImageView);
        traffic_frame_ImageView.setOnClickListener(this);
//        traffic_ListView = (ListView) findViewById(traffic_ListView);
//        trafficAdapter = new TrafficAdapter(this, teamList, user_context);
//        traffic_ListView.setAdapter(trafficAdapter);

        traffic_refreshable_view = (SwipeRefreshLayout) findViewById(R.id.traffic_refreshable_view);
        traffic_refreshable_view.setOnRefreshListener(onRefreshListener);
        traffic_listview = (SwipeMenuRecyclerView) findViewById(R.id.traffic_listview);
        traffic_listview.setLayoutManager(new LinearLayoutManager(TrafficActivity.this));// 布局管理器。
        traffic_listview.setHasFixedSize(true);//高度设置为自定义
        traffic_listview.setItemAnimator(new DefaultItemAnimator());//设置默认动画，加也行，不加也行
        traffic_listview.addItemDecoration(new ListViewDecoration(getApplicationContext()));// 添加分割线。

        mUnitSwipeAdapter = new UnitSwipeAdapter(TrafficActivity.this, teamList);
        mUnitSwipeAdapter.setOnItemClickListener(onItemClickListener);
        traffic_listview.setAdapter(mUnitSwipeAdapter);
        //设置菜单创建器
        traffic_listview.setSwipeMenuCreator(swipeMenuCreator);
        //设置菜单Item点击监听
        traffic_listview.setSwipeMenuItemClickListener(nenuItemCliclListemer);

        dialog = new LoadingDialog(TrafficActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);

        volley_getList();
    }

    /**
     * 刷新监听
     */
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            if (ActivityUtils.getInstance().isNetworkAvailable(TrafficActivity.this)) {
                getList();
            } else {
                Toast.makeText(TrafficActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
                traffic_refreshable_view.setRefreshing(false);
            }
        }
    };

    /**
     * 菜单监听事件
     */
    private OnSwipeMenuItemClickListener nenuItemCliclListemer = new OnSwipeMenuItemClickListener() {

        @Override
        public void onItemClick(com.yanzhenjie.recyclerview.swipe.Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();//关闭被点击的菜单
            if (menuPosition == 0) {//删除按钮被点击
                personDelete(teamList.get(adapterPosition).getPersonMainID(), adapterPosition);
            }
        }
    };

    /**
     * 菜单创建器，在Item要创建菜单时调用
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.controls_80);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            //添加右侧菜单
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(TrafficActivity.this)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.del_icon_normal)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);

                swipeRightMenu.addMenuItem(deleteItem);
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {

        }
    };


    private void volley_getList() {
        try {
            //等待网络的D
            dialog.show();
            getList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getList() {
        teamList.clear();
        // 1.创建请求队列
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        // 2.服务器网址
        final String URL1 = HttpServerAddress.BASE_URL + "?m=taskstringfun&funstr=getlist&strtype=jtog"
                + "&user_context=" + user_context;

        Log.e("TAG", URL1);
        // 3.json get请求处理
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL1, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.e("URL", URL1);
                try {
                    JSONObject strState = arg0;
                    // json数据解析
                    JSONArray array = strState.getJSONArray("LOCK_STRING");
                    if (array.length() != 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e("json", object.toString());
                            // 解析出单个json集中所有数
                            personMainBean = new PersonMainBean();
                            personMainBean.setPersonMainStr(object.getString("STR_DEMO"));
                            personMainBean.setPersonMainID(object.getString("STR_ID"));
                            teamList.add(personMainBean);
                        }
//                            trafficAdapter.notifyDataSetChanged();
                        mUnitSwipeAdapter.notifyDataSetChanged();
                    } else {
                        AddProduction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    closeRefresh();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                dialog.dismiss();
                closeRefresh();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse arg0) {
                try {
                    dialog.dismiss();
                    closeRefresh();
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
    }

    private void closeRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                traffic_refreshable_view.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_tv_back:
                finish();
                break;
            case R.id.traffic_frame_ImageView:
                AddProduction();
                break;
            default:
                break;
        }
    }

    //生产工具名称
    private void AddProduction() {

        // 加载输入框的布局文件
        LayoutInflater inflater = (LayoutInflater) TrafficActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.customdialog_add, null);
        final EditText unitString = (EditText) layout.findViewById(R.id.dialog_add_text);
        unitString.setHint("请输入交通工具名称");

        new AlertDialog.Builder(this)
                         /* 弹出窗口的最上头文字 */
                .setTitle("添加交通工具")
                        /* 设置弹出窗口的信息 */
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        //添加方法请求
                        try {
                            if (unitString.getText().toString().trim().equals("")) {
                                Toast.makeText(TrafficActivity.this, "內容不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                volley_post(URLEncoder.encode(unitString.getText().toString().trim(), "UTF-8"));
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
            public void onClick(DialogInterface dialoginterface, int i) {
            }
        }).show();
    }


    private void volley_post(final String unitString) {
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL1 = HttpServerAddress.BASE_URL + "?m=" + "taskstringfun&funstr=add&strtype=jtog"
                    + "&strdemo=" + unitString + "&user_context=" + user_context;

            Log.e("TAG", URL1);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL1, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        String strState = arg0.getString("result");

                        // json数据解析
                        if (strState != null && strState.equals("true")) {
                            personMainBean = new PersonMainBean();
                            personMainBean.setPersonMainStr(URLDecoder.decode(unitString, "utf-8"));
                            personMainBean.setPersonMainID(arg0.getString("STR_ID"));
                            teamList.add(personMainBean);
//                            trafficAdapter.notifyDataSetChanged();
                            mUnitSwipeAdapter.notifyItemInserted(teamList.size());
                            Toast.makeText(TrafficActivity.this, "添加成功!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
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


    private void personDelete(String getPersonMainID, final int position) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(TrafficActivity.this);


            final String URL = HttpServerAddress.BASE_URL + "?m=taskstringfun&funstr=del" + "&strid=" + getPersonMainID + "&user_context=" + user_context;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {

                    try {
                        String strState = arg0.getString("result");
                        if (strState.equals("true")) {
                            teamList.remove(position);
                            mUnitSwipeAdapter.notifyItemRemoved(position);
                            Toast.makeText(TrafficActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TrafficActivity.this, "你没有删除的权限！", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
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
