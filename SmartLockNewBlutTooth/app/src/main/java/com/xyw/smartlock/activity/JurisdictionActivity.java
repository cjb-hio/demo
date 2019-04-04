package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.xyw.smartlock.adapter.MenuAdapter;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.listener.OnItemClickListener;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.JurisdictBean;
import com.xyw.smartlock.utils.JurisdictionActivityUtil;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.view.ListViewDecoration;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class JurisdictionActivity extends AppCompatActivity implements OnClickListener {
    private TextView title;
    private ImageView imageback, common_tv_search;
    private List<JurisdictBean> jurisdictionList;
    private JurisdictBean jurisdictBean;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String UserContext;
    private LoadingDialog dialog;
    private AlertDialog areatDialog;
    private EditText dialog_userName, dialog_account;

    private SwipeMenuRecyclerView jurisdiction_recycler_view;
    private SwipeRefreshLayout jurisdiction_swipe_layout;
    private MenuAdapter mMenuAdapter;
    private JurisdictionActivityUtil mUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_jurisdiction);
        getSupportActionBar().hide();
        dialog = new LoadingDialog(JurisdictionActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);
        // 初始化标题栏和返回按钮
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        UserContext = acacheUserBean.getUSER_CONTEXT();
        //初始化对象集合
        jurisdictionList = new ArrayList<>();

        initview();
        if (ActivityUtils.getInstance().isNetworkAvailable(JurisdictionActivity.this)) {
            dialog.show();
            volley_requestData();
        } else {
            Toast.makeText(JurisdictionActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化标题栏,返回按钮,和绑定控件
     */
    private void initview() {
        // 设置标题栏
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.jurisdictionmanagement);
        // 定义返回按钮
        imageback = (ImageView) findViewById(R.id.common_tv_back);
        imageback.setVisibility(View.VISIBLE);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        common_tv_search = (ImageView) findViewById(R.id.common_tv_search);
        common_tv_search.setVisibility(View.VISIBLE);
        common_tv_search.setOnClickListener(this);

        initSwipeMenuRecyclerView();
    }

    private static final int REQ_COAD_PERMISS_ASSIGNMENT = 3;
    private void jumpToPermissAssignment(int position) {
        JurisdictBean jurisdictBean = jurisdictionList.get(position);

        Intent intent = new Intent();
        intent.putExtra("OP_NO", jurisdictBean.getOp_No());
        intent.putExtra("NAME", jurisdictBean.getName());
        intent.putExtra("DT", jurisdictBean.getDt());
        intent.putExtra("ROLE_ID", jurisdictBean.getRole_Id());
        intent.putExtra("AREA_ID", jurisdictBean.getArea_Id());
        intent.putExtra("V_BEGINTIME", jurisdictBean.getV_BEGINTIME());
        intent.putExtra("V_ENDTIME", jurisdictBean.getV_ENDTIME());
        intent.putExtra("ZONE_NAME", jurisdictBean.getZONE_NAME());
        intent.setClass(JurisdictionActivity.this, PermissAssignmentActivity.class);
        startActivityForResult(intent, REQ_COAD_PERMISS_ASSIGNMENT);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COAD_PERMISS_ASSIGNMENT) {
            if (resultCode == Activity.RESULT_OK) {
                jurisdictionList.clear();
                mMenuAdapter.notifyDataSetChanged();
                if (ActivityUtils.getInstance().isNetworkAvailable(JurisdictionActivity.this)) {
                    volley_requestData();
                } else {
                    Toast.makeText(JurisdictionActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initSwipeMenuRecyclerView() {
        jurisdiction_swipe_layout = (SwipeRefreshLayout) findViewById(R.id.jurisdiction_swipe_layout);
        jurisdiction_swipe_layout.setOnRefreshListener(mOnRefreshListener);

        jurisdiction_recycler_view = (SwipeMenuRecyclerView) findViewById(R.id.jurisdiction_recycler_view);
        jurisdiction_recycler_view.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        jurisdiction_recycler_view.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        jurisdiction_recycler_view.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        jurisdiction_recycler_view.addItemDecoration(new ListViewDecoration(JurisdictionActivity.this));// 添加分割线。

        // 设置菜单创建器。
        jurisdiction_recycler_view.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        jurisdiction_recycler_view.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(jurisdictionList);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        jurisdiction_recycler_view.setAdapter(mMenuAdapter);
        mUtil = new JurisdictionActivityUtil(JurisdictionActivity.this, jurisdictionList, mMenuAdapter);
    }

    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (ActivityUtils.getInstance().isNetworkAvailable(JurisdictionActivity.this)) {
                volley_requestData();
            } else {
                jurisdiction_swipe_layout.setRefreshing(false);
                Toast.makeText(JurisdictionActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 创建右侧侧滑按钮
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            //设置菜单栏宽高
            int width = getResources().getDimensionPixelSize(R.dimen.margin_60);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(JurisdictionActivity.this)
                        .setBackgroundDrawable(R.drawable.selector_green)
                        .setText("重命名")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到右侧菜单。

                SwipeMenuItem deleteItem = new SwipeMenuItem(JurisdictionActivity.this)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.del_icon_normal)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    /**
     * 侧滑按钮点击事件
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, @SwipeMenuRecyclerView.DirectionMode int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 1) {
                    new AlertDialog.Builder(JurisdictionActivity.this)
                            .setTitle("删除区域")
                            .setMessage("你确定要删除这条记录吗")// 设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件
                                    mUtil.deleteList(jurisdictionList.get(adapterPosition).getOp_No(), adapterPosition, UserContext, acacheUserBean.getOP_NO());
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {// 响应事件
                        }
                    }).show();// 在按键响应事件中显示此对话框
                } else if (menuPosition == 0) {// 加载输入框的布局文件
                    LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final LinearLayout layout1 = (LinearLayout) inflater1.inflate(R.layout.customdialog_add, null);
                    final EditText inputStringr = (EditText) layout1.findViewById(R.id.dialog_add_text);
                    inputStringr.setHint("请输入操作员名称");
                    // 弹出的对话框
                    new AlertDialog.Builder(JurisdictionActivity.this)
                        /* 设置弹出窗口的信息 */
                            .setTitle("重命名")
                            .setView(layout1)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    String string_rename = inputStringr.getText().toString().trim();
                                    if (string_rename.equals("")) {
                                        Toast.makeText(JurisdictionActivity.this, "输入的名称不能为空", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            mUtil.rename(URLEncoder.encode(string_rename, "UTF-8"), jurisdictionList.get(adapterPosition).getOp_No(), adapterPosition, UserContext);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                        public void onClick(DialogInterface dialoginterface, int i) {}
                    }).show();
                }
//                Toast.makeText(JurisdictionActivity.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            jumpToPermissAssignment(position);
        }
    };

    private void volley_requestData() {
        try {
            jurisdictionList.clear();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL
                    + "?m=GetOpList&op_no=" + acacheUserBean.getOP_NO()
                    + "&user_context=" + acacheUserBean.getUSER_CONTEXT();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        jurisdictionList.clear();
                        mMenuAdapter.notifyDataSetChanged();

                        JSONObject strState = arg0;
                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_OP");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e("json", object.toString());
                            // 解析出单个json集中所有数
                            jurisdictBean = new JurisdictBean();
                            jurisdictBean.setName(object.getString("OP_NAME"));
                            jurisdictBean.setOp_No(object.getString("OP_NO"));
                            jurisdictBean.setRole_Id(object.getString("ROLE_ID"));//每个人的权限
                            jurisdictBean.setArea_Id(object.getString("AREA_ID"));
                            jurisdictBean.setDt(object.getString("OP_DT"));
                            jurisdictBean.setV_BEGINTIME(object.getString("V_BEGINTIME"));
                            jurisdictBean.setV_ENDTIME(object.getString("V_ENDTIME"));
                            jurisdictBean.setZONE_NAME(object.getString("ZONE_NAME"));

                            jurisdictionList.add(jurisdictBean);
                            dialog.dismiss();
                        }
                        updateMenuAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    dialog.dismiss();
                    ToastUtil.MyToast(getApplicationContext(), "请检查网络");
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
        } finally {
            jurisdiction_swipe_layout.setRefreshing(false);
        }
    }

    private void updateMenuAdapter() {
        mMenuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_tv_search:
                alertDialog();
                break;
            default:
                break;
        }
    }

    private void alertDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.jurisdiction_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(JurisdictionActivity.this);
        builder.setView(layout);
        builder.setCancelable(false);
        areatDialog = builder.show();
        TextView searchDialog_title = (TextView) layout.findViewById(R.id.dialog_title);
        searchDialog_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        searchDialog_title.setText(R.string.enter_conditSearch);
        //绑定控件
        dialog_userName = (EditText) layout.findViewById(R.id.dialog_userName);
        dialog_account = (EditText) layout.findViewById(R.id.dialog_account);

        Button dialog_button1 = (Button) layout.findViewById(R.id.dialog_button1);
        Button dialog_button2 = (Button) layout.findViewById(R.id.dialog_button2);
        dialog_button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialog_userName_text = dialog_userName.getText().toString().trim();
                String dialog_account_text = dialog_account.getText().toString().trim();
                if (dialog_userName_text.equals("") && dialog_account_text.equals("")) {
                    Toast.makeText(JurisdictionActivity.this, "搜索条件不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityUtils.getInstance().isNetworkAvailable(JurisdictionActivity.this)) {
                        volley_requestData2(dialog_userName_text, dialog_account_text);
                    } else {
                        Toast.makeText(JurisdictionActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
                    }
                    areatDialog.dismiss();
                }
            }
        });
        dialog_button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                areatDialog.dismiss();
            }
        });
    }

    private void volley_requestData2(final String dialog_userName_text, final String dialog_account_text) {
        try {
            dialog.show();
            //清空对象集合
            jurisdictionList.clear();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL
                    + "?m=GetOpList&op_no=" + acacheUserBean.getOP_NO()
                    + "&user_context=" + acacheUserBean.getUSER_CONTEXT();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        jurisdictionList.clear();
                        mMenuAdapter.notifyDataSetChanged();

                        JSONObject strState = arg0;
                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_OP");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e("json", object.toString());
                            // 解析出单个json集中所有数
                            if (dialog_userName_text.equals(object.getString("OP_NAME")) || dialog_account_text.equals(object.getString("OP_NO"))) {
                                jurisdictBean = new JurisdictBean();
                                jurisdictBean.setName(object.getString("OP_NAME"));
                                jurisdictBean.setOp_No(object.getString("OP_NO"));
                                jurisdictBean.setRole_Id(object.getString("ROLE_ID"));//每个人的权限
                                jurisdictBean.setArea_Id(object.getString("AREA_ID"));
                                jurisdictBean.setDt(object.getString("OP_DT"));
                                jurisdictBean.setV_BEGINTIME(object.getString("V_BEGINTIME"));
                                jurisdictBean.setV_ENDTIME(object.getString("V_ENDTIME"));
                                jurisdictBean.setZONE_NAME(object.getString("ZONE_NAME"));

                                jurisdictionList.add(jurisdictBean);
                            }
                        }
                        updateMenuAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    dialog.dismiss();
                    ToastUtil.MyToast(getApplicationContext(), "请检查网络");
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

