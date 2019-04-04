package com.xyw.smartlock.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.LockRecord;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnlockRecordActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> unLockList = new ArrayList<HashMap<String, Object>>();
    private HashMap<String, Object> hashMap_text = new HashMap<String, Object>();
    private MyImgAdapter myadapter;
    private ListView mylistview;
    private TextView title;
    private ImageView imageback, search;
    private EditText ed_id;
    private String account, personNumber;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String itemLockId = null;
    private String lockAction = null;
    private String result = null;
    private String objectS, strS;
    private String l_OPTYPE;
    private EditText unlockrecord_startDate = null;
    private EditText unlockrecord_endDate = null;
    private String unLock_startDate, unLock_endDate;
    private String number = "";
    private String myLid;
    private static final int SHOW_DAPATICK = 0;
    private static final int DATE_DIALOG_ID = 1;
    private static final int DATE_DIALOG_ID1 = 2;
    private int mYear;
    private int mMonth;
    private int mDay;
    //经度和纬度
    private String longitude;
    private String latitude;
    private String phoneNumber;
    private String operatorName;
    private String dateTime;

    //请求网络的等待弹框
    private LoadingDialog dialog;

    //操作数据库
    private DateBaseUtil dateBaseUtil;
    private LockRecord lockRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlockrecord);
        getSupportActionBar().hide();

        //初始化数据库
        dateBaseUtil = new DateBaseUtil(UnlockRecordActivity.this);
        lockRecord = new LockRecord();
        mylistview = (ListView) findViewById(R.id.unlockrecord_listview);
        // 绑定adapter数据
        myadapter = new MyImgAdapter(this);
        mylistview.setAdapter(myadapter);
        // 刷新列表
        myadapter.notifyDataSetChanged();
        // 获取缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();
        account = LoginInfo.getOP_NO().toString().trim();
        // 初始化控件
        initview();
        volley_post();
        // 请求网络
    }

    /**
     * 初始化控件
     */
    private void initview() {
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.unlockrecord_tv_title);
        title.setText(R.string.unlockrecord);
        ed_id = (EditText) findViewById(R.id.ed_id);
        // 设置标题栏返回按钮
        imageback = (ImageView) findViewById(R.id.unlockrecord_title_back);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        // 设置标题栏的搜索按钮
        search = (ImageView) findViewById(R.id.unlockrecord_title_search);
        search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //alertDialog();
                startActivityForResult(new Intent(UnlockRecordActivity.this, QueryActivity.class), 1);
            }
        });
        // 监听listview,点击会进入listview的详细界面
        mylistview = (ListView) findViewById(R.id.unlockrecord_listview);
        mylistview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 获得当前listview的内容,将获取的内容存入HashMap集合中
                HashMap<String, String> clickitem = (HashMap<String, String>) mylistview.getItemAtPosition(position);
                System.out.println("ckickitem=" + clickitem);

                // 遍历HashMap,找到想要的值
                Set<?> set = clickitem.entrySet();
                Iterator<?> itor = set.iterator();// 遍历的类
                while (itor.hasNext()) {// 存在下一个值
                    HashMap.Entry entry = (HashMap.Entry) itor.next();
                    // 找到所有key-value对集合
                    if (entry.getKey().equals("ItemLockID")) {
                        // 获取value值与所知道的value比较
                        itemLockId = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的ID ：" + itemLockId);
                    }
                    if (entry.getKey().equals("ItemLockAction")) {
                        // 获取value值与所知道的value比较
                        lockAction = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的操作类型 ：" + lockAction);
                    }
                    if (entry.getKey().equals("ItemResult")) {
                        // 获取value值与所知道的value比较
                        result = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的操作结果 ：" + result);
                    }
                    if (entry.getKey().equals("ItemDateTime")) {
                        // 获取value值与所知道的value比较
                        dateTime = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的操作时间 ：" + dateTime);
                    }
                    if (entry.getKey().equals("ItemLongitude")) {
                        // 获取value值与所知道的value比较
                        longitude = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的经度 ：" + longitude);
                    }
                    if (entry.getKey().equals("ItemLatitude")) {
                        // 获取value值与所知道的value比较
                        latitude = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的纬度 ：" + latitude);
                    }
                    if (entry.getKey().equals("ItemName")) {
                        // 获取value值与所知道的value比较
                        operatorName = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的操作员 ：" + operatorName);
                    }
                    if (entry.getKey().equals("ItemPhoneNumbere")) {
                        // 获取value值与所知道的value比较
                        phoneNumber = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的操作员电话 ：" + phoneNumber);
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("LockTD", itemLockId);
                intent.putExtra("lockAction", lockAction);
                intent.putExtra("result", result);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("operatorName", operatorName);
                intent.putExtra("dateTime", dateTime);
                intent.setClass(UnlockRecordActivity.this, DetailInforActivity.class);
                startActivity(intent);
            }
        });
    }


    // 通过查询向服务端发起请求
    private void choose_volley_post() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(UnlockRecordActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockLogListD&OP_NO=" + account
                    + "&Begin_time=" + unLock_startDate + "&end_time=" + unLock_endDate + "&USER_CONTEXT="
                    + personNumber + "&lid=" + myLid + "&op_number=" + number;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;
                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析
                        System.out.println("strState=" + strState);
                        JSONArray array = strState.getJSONArray("LOCK_LOG");
                        System.out.println("array=" + array);
                        for (int i = 0; i < array.length(); i++) {
                            System.out.println("array=" + array);
                            JSONObject object = array.getJSONObject(i);
                            // System.out.println("object" + object);
                            // array数组下解析出的json集合累加
                            objectS += object + "\n";
                            // 解析出单个json集中所有数据
                            String str = "OP_NAME:" + object.getString("OP_NAME").trim()
                                    + "L_RET:" + object.getString("L_RET").trim()
                                    + "L_OPTYPE:" + object.getString("L_OPTYPE").trim()
                                    + "L_GPS_X:" + object.getString("L_GPS_X").trim()
                                    + "L_GPS_Y:" + object.getString("L_GPS_Y").trim()
                                    + "L_CREATE_DT:" + object.getString("L_CREATE_DT").trim()
                                    + "LID:" + object.getString("LID").trim()
                                    + "L_CREATE_OP:" + object.getString("L_CREATE_OP").trim();
                            //将得到的数据存入数据库表中
                            HashMap<String, Object> hashMap_text = new HashMap<String, Object>();

                            hashMap_text.put("ItemDateTime", object.getString("L_CREATE_DT").trim());
                            hashMap_text.put("ItemLockID", object.getString("LID").trim());
                            hashMap_text.put("ItemName", object.getString("OP_NAME").trim());
                            if (object.getInt("L_OPTYPE") == 1) {
                                l_OPTYPE = "开锁";
                            } else if (object.getInt("L_OPTYPE") == 2) {
                                l_OPTYPE = "落锁";
                            } else if (object.getInt("L_OPTYPE") == 3) {
                                l_OPTYPE = "下装密钥";
                            }

                            hashMap_text.put("ItemLockAction", l_OPTYPE);
                            hashMap_text.put("ItemResult", object.getString("L_RET").trim());
                            hashMap_text.put("ItemLatitude", object.getString("L_GPS_Y").trim());
                            hashMap_text.put("ItemLongitude", object.getString("L_GPS_X").trim());
                            hashMap_text.put("ItemPhoneNumbere", object.getString("L_CREATE_OP").trim());

                            unLockList.add(hashMap_text);
                            strS += str + "\n";
                            // TODO
                            System.out.println("str=" + str);
                        }
                        myadapter.notifyDataSetChanged();
                        dialog.dismiss();
                        // json中的所有json集合
                        System.out.println("+++++" + "objectS:" + objectS);
                        // json数据中的所有数据
                        System.out.println("+++++" + "strS:" + strS);
                    } catch (Exception e) {
                        e.printStackTrace();
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

    // 向服务端发起post请求
    private Object volley_post1() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(UnlockRecordActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockLogList&Op_no=" + account
                    + "&USER_CONTEXT=" + personNumber;
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;
                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析
                        System.out.println("strState=" + strState);
                        JSONArray array = strState.getJSONArray("LOCK_LOG");
                        System.out.println("array=" + array);

                        for (int i = 0; i < array.length(); i++) {
                            System.out.println("array=" + array);
                            JSONObject object = array.getJSONObject(i);
                            // System.out.println("object" + object);
                            // array数组下解析出的json集合累加
                            objectS += object + "\n";
                            // 解析出单个json集中所有数据
                            String str = "OP_NAME:" + object.getString("OP_NAME").trim()
                                    + "L_RET:" + object.getString("L_RET").trim()
                                    + "L_OPTYPE:" + object.getString("L_OPTYPE").trim()
                                    + "L_GPS_X:" + object.getString("L_GPS_X").trim()
                                    + "L_GPS_Y:" + object.getString("L_GPS_Y").trim()
                                    + "L_CREATE_DT:" + object.getString("L_CREATE_DT").trim()
                                    + "LID:" + object.getString("LID").trim()
                                    + "L_CREATE_OP:" + object.getString("L_CREATE_OP").trim();
                            HashMap<String, Object> hashMap_text = null;
                            hashMap_text = new HashMap<String, Object>();

//插入数据
//                            lockRecord.setOP_NAME(object.getString("OP_NAME"));
//                            lockRecord.setL_RET(object.getString("L_RET"));
//                            lockRecord.setL_OPTYPE(object.getString("L_OPTYPE"));
//                            lockRecord.setL_GPS_X(object.getString("L_GPS_X"));
//                            lockRecord.setL_GPS_Y(object.getString("L_GPS_Y"));
//                            lockRecord.setL_CREATE_DT(object.getString("L_CREATE_DT"));
//                            lockRecord.setLID(object.getString("LID"));
//                            lockRecord.setL_CREATE_OP(object.getString("L_CREATE_OP"));
//                            dateBaseUtil.Insert2(lockRecord);
                            hashMap_text.put("ItemDateTime", object.getString("L_CREATE_DT").trim());
                            hashMap_text.put("ItemLockID", object.getString("LID").trim());
                            hashMap_text.put("ItemName", object.getString("OP_NAME").trim());
                            if (object.getInt("L_OPTYPE") == 1) {
                                l_OPTYPE = "开锁";
                            } else if (object.getInt("L_OPTYPE") == 2) {
                                l_OPTYPE = "落锁";
                            } else if (object.getInt("L_OPTYPE") == 3) {
                                l_OPTYPE = "下装密钥";
                            }

                            hashMap_text.put("ItemLockAction", l_OPTYPE);
                            hashMap_text.put("ItemResult", object.getString("L_RET").trim());
                            hashMap_text.put("ItemResult", object.getString("L_RET").trim());
                            hashMap_text.put("ItemLatitude", object.getString("L_GPS_Y").trim());
                            hashMap_text.put("ItemLongitude", object.getString("L_GPS_X").trim());
                            hashMap_text.put("ItemPhoneNumbere", object.getString("L_CREATE_OP").trim());
                            unLockList.add(hashMap_text);
                            myadapter.notifyDataSetChanged();

                            strS += str + "\n";
                            // TODO
                            System.out.println("str=" + str);
                        }
                        dialog.dismiss();
                        // json中的所有json集合
                        System.out.println("+++++" + "objectS:" + objectS);
                        // json数据中的所有数据
                        System.out.println("+++++" + "strS:" + strS);

                    } catch (Exception e) {
                        e.printStackTrace();
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
        return myadapter;
    }

    // 向服务端发起post请求
    private void volley_post() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(UnlockRecordActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockLogList&Op_no=" + account
                    + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;
                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析
                        System.out.println("strState=" + strState);
                        JSONArray array = strState.getJSONArray("LOCK_LOG");
                        System.out.println("array=" + array);

                        for (int i = 0; i < array.length(); i++) {
                            System.out.println("array=" + array);
                            JSONObject object = array.getJSONObject(i);
                            // System.out.println("object" + object);
                            // array数组下解析出的json集合累加
                            objectS += object + "\n";
                            // 解析出单个json集中所有数据
                            String str = "OP_NAME:" + object.getString("OP_NAME").trim()
                                    + "L_RET:" + object.getString("L_RET").trim()
                                    + "L_OPTYPE:" + object.getString("L_OPTYPE").trim()
                                    + "L_GPS_X:" + object.getString("L_GPS_X").trim()
                                    + "L_GPS_Y:" + object.getString("L_GPS_Y").trim()
                                    + "L_CREATE_DT:" + object.getString("L_CREATE_DT").trim()
                                    + "LID:" + object.getString("LID").trim()
                                    + "L_CREATE_OP:" + object.getString("L_CREATE_OP").trim();
                            HashMap<String, Object> hashMap_text = new HashMap<String, Object>();
                            hashMap_text.put("ItemDateTime", object.getString("L_CREATE_DT").trim());
                            hashMap_text.put("ItemLockID", object.getString("LID").trim());
                            hashMap_text.put("ItemName", object.getString("OP_NAME").trim());
                            if (object.getInt("L_OPTYPE") == 1) {
                                l_OPTYPE = "开锁";
                            } else if (object.getInt("L_OPTYPE") == 2) {
                                l_OPTYPE = "落锁";
                            } else if (object.getInt("L_OPTYPE") == 3) {
                                l_OPTYPE = "下装密钥";
                            }

                            hashMap_text.put("ItemLockAction", l_OPTYPE);
                            hashMap_text.put("ItemResult", object.getString("L_RET").trim());
//                            hashMap_text.put("ItemResult", object.getString("L_RET").trim());

                            hashMap_text.put("ItemLatitude", object.getString("L_GPS_Y").trim());
                            hashMap_text.put("ItemLongitude", object.getString("L_GPS_X").trim());
                            hashMap_text.put("ItemPhoneNumbere", object.getString("L_CREATE_OP").trim());
                            unLockList.add(hashMap_text);
                            strS += str + "\n";
                            // TODO
                            System.out.println("str=" + str);
                        }
                        myadapter.notifyDataSetChanged();
                        dialog.dismiss();
                        // json中的所有json集合
                        System.out.println("+++++" + "objectS:" + objectS);
                        // json数据中的所有数据
                        System.out.println("+++++" + "strS:" + strS);
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

    private class MyImgAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        /*
         * 构造函数
         */
        public MyImgAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return unLockList.size();// 返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return unLockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 书中详细解释该方法
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            String str2;
            // 观察convertView随ListView滚动情况
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_unlockrecord_item, null);
                holder = new ViewHolder();
                /** 得到各个控件的对象 */
                holder.dateTime = (TextView) convertView.findViewById(R.id.unlockrecord_DateTime);
                holder.lockID = (TextView) convertView.findViewById(R.id.unlockrecord_lockID);
                holder.lockAction = (TextView) convertView.findViewById(R.id.unlockrecord_action);
                holder.result = (TextView) convertView.findViewById(R.id.unlockrecord_result);
                holder.name = (TextView) convertView.findViewById(R.id.unlockrecord_name);
                holder.latitude = (TextView) convertView.findViewById(R.id.unlockrecord_latitude);
                holder.longitude = (TextView) convertView.findViewById(R.id.unlockrecord_longitude);
                holder.phoneNumber = (TextView) convertView.findViewById(R.id.unlockrecord_phoneNumber);
                holder.ll_record_item = (LinearLayout) convertView.findViewById(R.id.ll_record_item);

                convertView.setTag(holder);// 绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
            }
            /** 设置TextView显示的内容，即我们存放在动态数组中的数据 */
            String str = unLockList.get(position).get("ItemLockID").toString();
            if (str.length() > 1 && str.length() == 16) {
                str2 = str.substring(8, 16);
            } else {
                str2 = "ID有误";
            }
            holder.dateTime.setText(unLockList.get(position).get("ItemDateTime").toString());
            holder.lockID.setText(str2);
            holder.lockAction.setText(unLockList.get(position).get("ItemLockAction").toString());
            String result = unLockList.get(position).get("ItemResult").toString();
            if (result.equals("手开申请")) {
                holder.ll_record_item.setBackgroundResource(R.drawable.tk_ing_bg);
                setTextColor(holder.dateTime, holder.lockID, holder.lockAction, holder.result, holder.name, getResources().getColor(R.color.black));
            } else if (result.equals("手开成功")) {
                holder.ll_record_item.setBackgroundResource(R.drawable.tk_true_bg);
                setTextColor(holder.dateTime, holder.lockID, holder.lockAction, holder.result, holder.name, getResources().getColor(R.color.result_view));
            } else if (result.equals("解锁成功")) {
                holder.ll_record_item.setBackgroundResource(R.drawable.tk_true_bg);
                setTextColor(holder.dateTime, holder.lockID, holder.lockAction, holder.result, holder.name, getResources().getColor(R.color.result_view));
            } else if (result.equals("解锁失败")) {
                holder.ll_record_item.setBackgroundResource(R.drawable.tk_false_bg);
                setTextColor(holder.dateTime, holder.lockID, holder.lockAction, holder.result, holder.name, getResources().getColor(R.color.white));
            } else if (result.equals("落锁成功")) {
                holder.ll_record_item.setBackgroundResource(R.drawable.tk_true_bg);
                setTextColor(holder.dateTime, holder.lockID, holder.lockAction, holder.result, holder.name, getResources().getColor(R.color.result_view));
            } else if (result.equals("落锁失败")) {
                holder.ll_record_item.setBackgroundResource(R.drawable.tk_false_bg);
                setTextColor(holder.dateTime, holder.lockID, holder.lockAction, holder.result, holder.name, getResources().getColor(R.color.white));
            }
            holder.result.setText(result);
            holder.name.setText(unLockList.get(position).get("ItemName").toString());
            holder.latitude.setText(unLockList.get(position).get("ItemLatitude").toString());
            holder.longitude.setText(unLockList.get(position).get("ItemLongitude").toString());
            holder.phoneNumber.setText(unLockList.get(position).get("ItemPhoneNumbere").toString());

            return convertView;
        }

        private void setTextColor(TextView tv1, TextView tv2, TextView tv3, TextView tv4, TextView tv5, int color) {
            tv1.setTextColor(color);
            tv2.setTextColor(color);
            tv3.setTextColor(color);
            tv4.setTextColor(color);
            tv5.setTextColor(color);
        }
    }

    /**
     * 存放控件
     */
    private class ViewHolder {
        TextView dateTime;
        TextView lockID;
        TextView lockAction;
        TextView result;
        TextView name;
        TextView longitude;
        TextView latitude;
        TextView phoneNumber;
        LinearLayout ll_record_item;
    }

    /**
     * 设置开始日期
     */
    private void startDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatstart();
    }

    /**
     * 设置结束日期
     */
    private void endDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatend();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplatstart() {
        unlockrecord_startDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    private void updateDateDisplatend() {
        unlockrecord_endDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    /**
     * 日期控件事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplatstart();
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplatend();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            case DATE_DIALOG_ID1:
                return new DatePickerDialog(this, mDateSetListener1, mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
            case DATE_DIALOG_ID1:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }

    /**
     * 处理日期和时间控件的Handler
     */
    Handler dateandtimeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UnlockRecordActivity.SHOW_DAPATICK:
                    showDialog(DATE_DIALOG_ID);
                    break;
                case UnlockRecordActivity.CONTEXT_RESTRICTED:
                    showDialog(DATE_DIALOG_ID1);
                    break;
            }
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        myLid = "";
        if (data != null) {
            unLock_startDate = data.getStringExtra("start_date");
            unLock_endDate = data.getStringExtra("end_date");
            myLid = data.getStringExtra("ed_id");
            number = data.getStringExtra("number");
            if (!unLock_startDate.equals("CANCEL")) {
                hashMap_text.clear();
                unLockList.removeAll(unLockList);
                // 绑定adapter数据
                mylistview.setAdapter(myadapter);
                // 刷新列表
                myadapter.notifyDataSetChanged();
                choose_volley_post();
            } else {
                //获取数据库数据
                getDbData();
            }

        }
    }

    public void getDbData() {
        List<LockRecord> list = dateBaseUtil.queryAll_LockRecord();
        Log.e("queryALl", list.toString());
        if (list.size() != 0) {
            List<Map<String, Object>> templist = new ArrayList<Map<String, Object>>();
            String str2;
            for (LockRecord lockRecord : list) {
                if (lockRecord.getL_OPTYPE().equals("1")) {
                    l_OPTYPE = "开锁";
                } else if (lockRecord.getL_RET().equals("2")) {
                    l_OPTYPE = "落锁";
                } else if (lockRecord.getL_RET().equals("3")) {
                    l_OPTYPE = "下装密钥";
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("op_name", lockRecord.getOP_NAME());
                map.put("l_ret", lockRecord.getL_RET());
                map.put("l_optype", l_OPTYPE);
                map.put("l_gps_x", lockRecord.getL_GPS_X());
                map.put("l_gps_y", lockRecord.getL_GPS_Y());
                map.put("l_create_dt", lockRecord.getL_CREATE_DT());
                String str = lockRecord.getLID();
                if (str.length() > 1) {
                    str2 = str.substring(8, 16);
                } else {
                    str2 = "";
                }
                map.put("lid", str2);
                map.put("l_create_op", lockRecord.getL_CREATE_OP());
                templist.add(map);
            }
            //添加到ListView
            myadapter.notifyDataSetChanged();
            mylistview.setAdapter(new SimpleAdapter(UnlockRecordActivity.this,
                    templist, R.layout.activity_unlockrecord_item,
                    new String[]{"op_name", "lid", "l_create_dt", "l_optype", "l_ret"},
                    new int[]{R.id.unlockrecord_name, R.id.unlockrecord_lockID, R.id.unlockrecord_DateTime, R.id.unlockrecord_action, R.id.unlockrecord_result}));
            Toast.makeText(getApplicationContext(), "数据库的数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "无相关记录", Toast.LENGTH_SHORT).show();
        }
    }
}
