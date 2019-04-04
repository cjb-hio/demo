package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.xyw.smartlock.utils.MyImgAdapter;
import com.xyw.smartlock.utils.SearchListObj;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView backimage, search_image, search_title_baidumap;
    private TextView lockfile_title;
    private List<SearchListObj> myListObj = new ArrayList<SearchListObj>();
    private ListView mylistview;
    private MyImgAdapter myadapter;
    private AcacheUserBean LoginInfo;
    private ACache aCache;
    private String personNumber;
    private String objectS, strS;
    //纬度和经度
    private String latitude;
    private String longitude;
    //操作状态
    private String l_Result, userID;
    //对话框控件
    private TextView searchDialog_title;
    private EditText searchDialog_Id, searchDialog_Name, searchDialog_Address, searchDialog_meter;
    private String searchID, searchName, searchAddress, searchMeterId;
    private Button searchDialog_button1, searchDialog_button2;
    private AlertDialog areatDialog;
    private String itemLockId = null;
    private String itemUsed = null;
    //请求网络的等待弹框
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        mylistview = (ListView) findViewById(R.id.search_listview);
        // 获取缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();
        // 初始化控件

        initview();
    }

    private static final int REQ_COAD_LOCK_FILE = 1;
    /**
     * 初始化控件
     */
    private void initview() {
        // 绑定控件
        lockfile_title = (TextView) findViewById(R.id.search_tv_title);
        lockfile_title.setText(R.string.lockfile);
        search_image = (ImageView) findViewById(R.id.search_title_search);

        dialog = new LoadingDialog(SearchActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);

        // 绑定adapter数据
        myadapter = new MyImgAdapter(SearchActivity.this, myListObj);
        mylistview.setAdapter(myadapter);

        search_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
        search_title_baidumap = (ImageView) findViewById(R.id.search_title_baidumap);
        search_title_baidumap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, SearchBaiDuMapActivity.class);
                intent.putExtra("searchID", searchID);
                intent.putExtra("searchName", searchName);
                intent.putExtra("searchAddress", searchAddress);
                intent.putExtra("searchMeterId", searchMeterId);
                startActivity(intent);
            }
        });

        // 设置返回按钮
        backimage = (ImageView) findViewById(R.id.search_title_back);
        backimage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 监听listview,点击会进入listview的详细界面
        mylistview = (ListView) findViewById(R.id.search_listview);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //点击获取ID和USED
                TextView tv1 = (TextView) view.findViewById(R.id.search_lockID);
                TextView tv2 = (TextView) view.findViewById(R.id.search_used);
                itemLockId = tv1.getText().toString();
                itemUsed = tv2.getText().toString();
                Intent intent = new Intent(SearchActivity.this, LockFileActivity.class);
                intent.putExtra("LockID", itemLockId);
                intent.putExtra("Used", itemUsed);
                startActivityForResult(intent, REQ_COAD_LOCK_FILE);
            }


        });
    }

    //获取从区域弹框得到的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_COAD_LOCK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                myListObj.removeAll(myListObj);
                //获得返回数据后开始请求数据,修改vlurekey的值和区域id
                volley_post();
            }
        } else if (requestCode == REQ_SEARCH_REQUIREMENT) {
            if (resultCode == Activity.RESULT_OK) {
                searchID = data.getStringExtra("searchID");
                searchName = data.getStringExtra("searchName");
                searchAddress = data.getStringExtra("searchAddress");
                searchMeterId = data.getStringExtra("searchMeterId");
                myListObj.removeAll(myListObj);
                // 绑定adapter数据
                mylistview.setAdapter(myadapter);
                volley_post();
            }
        }
//        if (data != null) {
//            myListObj.removeAll(myListObj);
////            searchID = "";
////            searchName = "";
////            searchAddress = "";
//            //获得返回数据后开始请求数据,修改vlurekey的值和区域id
//            volley_post();
//        }
    }


    /**
     * 向后台请求数据,得到锁注册记录列表
     */
    private void volley_post() {
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockInfoList&lid=" + searchID + "&Lname=" + searchName + "&laddr=" + searchAddress + "&METERID=" + searchMeterId
                    + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {

                    try {
                        JSONObject strState = arg0;
                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_INFO");
                        //判断弹框状态
                        dialog.dismiss();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Log.e("TAG", "object=" + object);
                            //用一个对象存数据
                            SearchListObj searchListObj = new SearchListObj();
                            searchListObj.setDateTime(object.getString("L_CREATE_DT").trim());
                            searchListObj.setUsed(object.getString("USED").trim());
                            searchListObj.setLockID(object.getString("LID").trim());
                            if (object.getString("KEY_VER").equals("0")) {
                                l_Result = "未下装";
                            } else if (object.getString("KEY_VER").equals("1")) {
                                l_Result = "下装成功";
                            }
                            searchListObj.setResult(l_Result);
                            searchListObj.setSreaName(object.getString("ZONE_NAME").trim());
                            myListObj.add(searchListObj);
                        }
                        myadapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), "请检查网络");
                    dialog.dismiss();
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


    private static final int REQ_SEARCH_REQUIREMENT = 6;
    /**
     * 点击按钮弹出菜单
     */
    private void alertDialog() {
        startActivityForResult(new Intent(SearchActivity.this, SearchDialogActivity.class), REQ_SEARCH_REQUIREMENT);
        /*LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.search_customdialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setView(layout);
        areatDialog = builder.show();
        areatDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("TAG", "onDismiss: ");
            }
        });
        searchDialog_title = (TextView) layout.findViewById(R.id.searchDialog_title);
        searchDialog_title.setText(R.string.enter_conditSearch);
        //绑定控件
        searchDialog_Id = (EditText) layout.findViewById(R.id.searchDialog_Id);
        searchDialog_Name = (EditText) layout.findViewById(R.id.searchDialog_Name);
        searchDialog_Address = (EditText) layout.findViewById(R.id.searchDialog_Address);
        searchDialog_meter = (EditText) layout.findViewById(R.id.searchDialog_meter);
        searchDialog_button1 = (Button) layout.findViewById(R.id.searchDialog_button1);
        searchDialog_button2 = (Button) layout.findViewById(R.id.searchDialog_button2);
        searchDialog_button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到文本框里面的值
                searchID = searchDialog_Id.getText().toString().trim();
                searchName = searchDialog_Name.getText().toString().trim();
                searchAddress = searchDialog_Address.getText().toString().trim();
                searchMeterId = searchDialog_meter.getText().toString().trim();
                myListObj.removeAll(myListObj);
                // 绑定adapter数据
                mylistview.setAdapter(myadapter);
                volley_post();
                areatDialog.dismiss();
            }
        });
        searchDialog_button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                areatDialog.dismiss();
            }
        });*/
    }
}
