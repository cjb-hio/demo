package com.xyw.smartlock.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AreaManagementActivity extends AppCompatActivity {
    protected static final Context Context = null;
    private HashMap<String, String> lockMap = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> AreaListNameID = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> HashMap_AreaNameId = new HashMap<String, String>();
    private ArrayList<String> AreList = new ArrayList<String>();
    private ArrayList<String> treeList = new ArrayList<String>();
    private TextView title;
    private ImageView backImage;
    private ListView areamanagementlistview;
    private static final int ITEM1 = Menu.FIRST;
    private static final int ITEM2 = Menu.FIRST + 1;
    private static final int ITEM3 = Menu.FIRST + 2;
    private MyAreaAdapter adapter;
    private String strS, strinputStringr;
    private String objectS;
    private String string_p_zone_no_number;
    private String user_context_number;
    private String content_area1, string_rename;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private AdapterContextMenuInfo menuInfo;
    private int id;
    private String AreaNameID;
    private String strAreaNameID, strAreaName;
    private LinearLayout area_linear;
    private TextView area_text;
    //请求网络的等待界面
    private LoadingDialog dialog;
    private String numberOfLocks;
    private String strState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areamanagement);
        getSupportActionBar().hide();
        areamanagementlistview = (ListView) findViewById(R.id.areamanagement_listview);
        // 为所有的条目注册上下文菜单ContextMen
        registerForContextMenu(areamanagementlistview);// 注册上下文菜单

        adapter = new MyAreaAdapter();

        // 缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        user_context_number = acacheUserBean.getUSER_CONTEXT().toString();
        //向服务端请求数据
        string_p_zone_no_number = "0000";//第一次运行时的区域编码
        initview();
        vooleyGet();


    }

    private void initview() {

        area_linear = (LinearLayout) findViewById(R.id.area_linear);
        area_text = (TextView) findViewById(R.id.area_text);
        // 设置返回按钮
        backImage = (ImageView) findViewById(R.id.common_title_back);
        backImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                area_linear.setVisibility(View.VISIBLE);
                area_text.setVisibility(View.INVISIBLE);
                areamanagementlistview.setAdapter(adapter);
                AreaListNameID.removeAll(AreaListNameID);
                adapter.notifyDataSetChanged();

                treeList = (ArrayList<String>) getNewList(treeList);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~treeList=" + treeList + "~~~~~~~~~~~~~~~~");

                System.out.println(treeList.size() - 1);
                if ((treeList.size() - 1) == 0) {
                    finish();
                } else {
                    treeList.remove(treeList.size() - 1);
                    string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                    System.out.println("string_p_zone_no_number=" + string_p_zone_no_number);
                    volley_OnClick();
                }
            }
        });

        // 初始化标题栏和返回按钮
        // 设置标题栏
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.Areamanagement);

        // 监听每一条listview
        areamanagementlistview = (ListView) findViewById(R.id.areamanagement_listview);
        areamanagementlistview.setAdapter(adapter);
        areamanagementlistview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> clickitem = (HashMap<String, String>) areamanagementlistview.getItemAtPosition(position);

                System.out.println("ckickitem=" + clickitem);

                // 遍历HashMap,找到想要的值
                Set set = clickitem.entrySet();
                Iterator<?> itor = set.iterator();// 遍历的类
                while (itor.hasNext()) {// 存在下一个值
                    HashMap.Entry entry = (HashMap.Entry) itor.next();
                    // 找到所有key-value对集合
                    if (entry.getKey().equals("AreaNameId")) {//
                        // 获取value值与所知道的value比较
                        AreaNameID = entry.getValue().toString().trim();// 取得key值
                        System.out.println("我要找的ID ：" + AreaNameID);
                    }
                }
                areamanagementlistview.setAdapter(adapter);
                AreaListNameID.removeAll(AreaListNameID);
                adapter.notifyDataSetChanged();
                string_p_zone_no_number = AreaNameID.toString().trim();
                volley_post();
            }
        });
    }


    /**
     * 定义一个去除arrayList的方法.
     */
    public List<String> getNewList(List<String> li) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < li.size(); i++) {
            String str = li.get(i); // 获取传入集合对象的每一个元素
            if (!list.contains(str)) { // 查看新集合中是否有指定的元素，如果没有则加入
                list.add(str);
            }
        }
        return list; // 返回集合
    }


    /**
     * 通过post向服务端发送请求(加载整个页面)
     */

    private void volley_post() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(AreaManagementActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=getzonelist&p_zone_no=" + string_p_zone_no_number
                    + "&user_context=" + user_context_number;
            Log.e("TAG",URL);

            treeList.add(string_p_zone_no_number);
            System.out.println("treeList=" + treeList);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;

                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析

                        System.out.println("strState=" + strState);

                        JSONArray array = strState.getJSONArray("ZONE");

                        System.out.println("array=" + array);
                        //判断弹框状态

                        if (array.length() == 0) {
                            dialog.dismiss();
                            area_linear.setVisibility(View.GONE);
                            area_text.setVisibility(View.VISIBLE);

                            LayoutInflater inflater = (LayoutInflater) AreaManagementActivity.this
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.customdialog_add,
                                    null);

                            // 弹出的对话框

                            new AlertDialog.Builder(AreaManagementActivity.this)
                                    .setTitle("添加区域")
                                    /* 设置弹出窗口的信息 */
                                    .setMessage("请输入区域名称").setView(layout)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {

                                            EditText inputStringr = (EditText) layout
                                                    .findViewById(R.id.dialog_add_text);

                                            strinputStringr = inputStringr.getText().toString();

                                            if (strinputStringr == null || strinputStringr.equals("")) {
                                                area_linear.setVisibility(View.VISIBLE);
                                                area_text.setVisibility(View.INVISIBLE);
                                                areamanagementlistview.setAdapter(adapter);
                                                AreList.removeAll(AreList);
                                                adapter.notifyDataSetChanged();
                                                treeList = (ArrayList<String>) getNewList(treeList);
                                                treeList.remove(treeList.size() - 1);
                                                string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString()
                                                        .trim();

                                                volley_OnClick();


                                                Toast.makeText(getApplicationContext(), "添加的内容不能为空", Toast.LENGTH_SHORT)
                                                        .show();
                                            } else {
                                                area_linear.setVisibility(View.VISIBLE);
                                                area_text.setVisibility(View.INVISIBLE);
                                                volley_post_add();

                                            }
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    area_linear.setVisibility(View.VISIBLE);
                                    area_text.setVisibility(View.INVISIBLE);
                                    areamanagementlistview.setAdapter(adapter);
                                    AreList.removeAll(AreList);
                                    adapter.notifyDataSetChanged();

                                    treeList = (ArrayList<String>) getNewList(treeList);
                                    treeList.remove(treeList.size() - 1);
                                    string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();

                                    volley_OnClick();
                                }
                            }).show();
                        } else {
                            dialog.dismiss();
                        }


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            System.out.println("object" + object);

                            // array数组下解析出的json集合累加
                            objectS += object + "\n";

                            // 解析出单个json集中所有数据
                            String str = "ZONE_NO:" + object.getString("ZONE_NO") + "ZONE_NAME:"
                                    + object.getString("ZONE_NAME") + "P_ZONE_NO:" + object.getString("P_ZONE_NO");
                            //将解析出来的数据添加到集合中
                            HashMap<String, String> HashMap_AreaNameId = null;
                            HashMap_AreaNameId = new HashMap<String, String>();

                            if ((lockMap.get(object.getString("ZONE_NO"))) == null) {
                                numberOfLocks = "0";
                            } else {
                                numberOfLocks = lockMap.get(object.getString("ZONE_NO"));
                            }
                            HashMap_AreaNameId.put("AreaName", object.getString("ZONE_NAME") + "(" + numberOfLocks + ")");
                            HashMap_AreaNameId.put("AreaNameId", object.getString("ZONE_NO"));

                            AreaListNameID.add(HashMap_AreaNameId);
                            // 刷新列表
                            adapter.notifyDataSetChanged();

                            strS += str + "\n";
                            // TODO
                            System.out.println("str=" + str);

                        }
                        // json中的所有json集合
                        System.out.println("+++++" + "objectS:" + objectS);
                        // json数据中的所有数据
                        System.out.println("+++++" + "strS:" + strS);

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

    /**
     * 返回时请求数据的页面
     */

    private void volley_OnClick() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(AreaManagementActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            JSONObject params = new JSONObject();
            final String URL = HttpServerAddress.BASE_URL + "?m=getzonelist&p_zone_no=" + string_p_zone_no_number
                    + "&user_context=" + user_context_number;

            Log.e("TAG",URL);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;

                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析

                        System.out.println("strState=" + strState);

                        JSONArray array = strState.getJSONArray("ZONE");
                        System.out.println("array=" + array);
                        //判断弹框状态
                        //判断弹框状态
                        if (array.length() != 0) {
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }

                        for (int i = 0; i < array.length(); i++) {

                            System.out.println("array=" + array);

                            JSONObject object = array.getJSONObject(i);

                            System.out.println("object" + object);

                            // array数组下解析出的json集合累加
                            objectS += object + "\n";


                            // 解析出单个json集中所有数据
                            String str = "ZONE_NO:" + object.getString("ZONE_NO") + "ZONE_NAME:"
                                    + object.getString("ZONE_NAME") + "P_ZONE_NO:" + object.getString("P_ZONE_NO");
                            //将解析出来的数据添加到集合中
                            HashMap<String, String> HashMap_AreaNameId = null;
                            HashMap_AreaNameId = new HashMap<String, String>();
                            if ((lockMap.get(object.getString("ZONE_NO"))) == null) {
                                numberOfLocks = "0";
                            } else {
                                numberOfLocks = lockMap.get(object.getString("ZONE_NO"));
                            }
                            HashMap_AreaNameId.put("AreaName", object.getString("ZONE_NAME") + "(" + numberOfLocks + ")");
                            HashMap_AreaNameId.put("AreaNameId", object.getString("ZONE_NO"));

                            AreaListNameID.add(HashMap_AreaNameId);
                            // 刷新列表
                            adapter.notifyDataSetChanged();

                        }
                        // json中的所有json集合
                        System.out.println("+++++" + "objectS:" + objectS);
                        // json数据中的所有数据
                        System.out.println("+++++" + "strS:" + strS);

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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, ITEM1, 0, "添加");
        menu.add(0, ITEM2, 0, "删除");
        menu.add(0, ITEM3, 0, "重命名");
    }

    /**
     * 通过长按条目激活上下文菜单
     */

    // 上下文菜单菜单单击事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        content_area1 = adapter.getItem(menuInfo.position).toString();
        HashMap<String, String> content_area1 = (HashMap<String, String>) adapter.getItem(menuInfo.position);

        // 遍历HashMap,找到想要的值
        Set set = content_area1.entrySet();
        Iterator itor = set.iterator();// 遍历的类
        while (itor.hasNext()) {// 存在下一个值
            HashMap.Entry entry = (HashMap.Entry) itor.next();//
            // 找到所有key-value对集合
            if (entry.getKey().equals("AreaNameId")) {//
                // 获取value值与所知道的value比较
                strAreaNameID = entry.getValue().toString().trim();// 取得key值
                System.out.println("我要找的区域编号 ：" + strAreaNameID);
            }
            if (entry.getKey().equals("AreaName")) {//
                // 获取value值与所知道的value比较
                strAreaName = entry.getValue().toString().trim();// 取得key值
                System.out.println("我要找的区域名 ：" + strAreaName);
            }
        }


        switch (item.getItemId()) {
            case ITEM1:
                // 加载输入框的布局文件
                LayoutInflater inflater = (LayoutInflater) AreaManagementActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.customdialog_add, null);

                // 弹出的对话框

                new AlertDialog.Builder(this)
                         /* 弹出窗口的最上头文字 */
                        .setTitle("添加区域")
                        /* 设置弹出窗口的信息 */
                        .setMessage("请输入区域名称")
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {

                                EditText inputStringr = (EditText) layout.findViewById(R.id.dialog_add_text);

                                strinputStringr = inputStringr.getText().toString();

                                if (strinputStringr == null || strinputStringr.equals("")) {

                                    Toast.makeText(getApplicationContext(), "添加的内容不能为空", Toast.LENGTH_SHORT).show();
                                } else {

                                    area_linear.setVisibility(View.VISIBLE);
                                    area_text.setVisibility(View.INVISIBLE);
                                    volley_post_add();

                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
                break;
            case ITEM2:

                new AlertDialog.Builder(this)
                        .setTitle("删除区域")
                        .setMessage("你确定要删除这条记录吗")// 设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 添加确定按钮

                            @Override

                            public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件
                                treeList = (ArrayList<String>) getNewList(treeList);

                                System.out.println(treeList.size() - 1);

                                if ((treeList.size() - 1) == 0) {
                                    string_p_zone_no_number = "0000";
                                } else {
                                    string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                                    System.out.println("string_p_zone_no_number=" + string_p_zone_no_number);

                                }
                                volley_post_romove();

                            }

                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 添加返回按钮

                    @Override

                    public void onClick(DialogInterface dialog, int which) {// 响应事件

                    }

                }).show();// 在按键响应事件中显示此对话框

                break;
            case ITEM3:
                // 加载输入框的布局文件
                LayoutInflater inflater1 = (LayoutInflater) AreaManagementActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout layout1 = (LinearLayout) inflater1.inflate(R.layout.customdialog_add, null);

                // 当前listivew的id
                AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
                id = (int) menuInfo.id;
                if (-1 == id) {
                    super.onContextItemSelected(item);
                }

                // 弹出的对话框

                new AlertDialog.Builder(this)
                        /* 设置弹出窗口的信息 */
                        .setTitle("重命名区域")
                        .setMessage("请输入区域")
                        .setView(layout1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {

                                EditText inputStringr = (EditText) layout1.findViewById(R.id.dialog_add_text);

                                string_rename = inputStringr.getText().toString().trim();

                                if (string_rename == null || string_rename.equals("")) {

                                    Toast.makeText(getApplicationContext(), "输入的区域不能为空", Toast.LENGTH_SHORT).show();
                                } else {

                                    treeList = (ArrayList<String>) getNewList(treeList);
                                    System.out.println(treeList.size() - 1);
                                    if ((treeList.size() - 1) == 0) {
                                        string_p_zone_no_number = "0000";
                                    } else {
                                        string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                                        System.out.println("string_p_zone_no_number=" + string_p_zone_no_number);
                                    }
                                    volley_post_rename();

                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
                break;
        }
        return true;
    }

    /**
     * 通过post向服务端发送请求(添加)
     */

    private void volley_post_add() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(AreaManagementActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            //得到父级的的区域编号
            treeList = (ArrayList<String>) getNewList(treeList);
            System.out.println(treeList.size() - 1);
            if ((treeList.size() - 1) == 0) {
                string_p_zone_no_number = "0000";
            } else {
                string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                System.out.println("string_p_zone_no_number=" + string_p_zone_no_number);
            }
            // 2.服务器网址
            final String URL1 = HttpServerAddress.BASE_URL + "?m=" + "insertzone&zone_name=" + URLEncoder.encode(strinputStringr)
                    + "&p_zone_no=" + string_p_zone_no_number + "&user_context=" + user_context_number;

            Log.e("TAG",URL1);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL1, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            try {
                                System.out.println("------------" + "arg0=" + arg0 + "--------------");
                                String strState = arg0.getString("result");

                                // json数据解析
                                if (strState != null && strState.equalsIgnoreCase("true")) {
                                    dialog.dismiss();
                                    areamanagementlistview.setAdapter(adapter);
                                    AreaListNameID.removeAll(AreaListNameID);
                                    adapter.notifyDataSetChanged();
                                    volley_post();
                                } else {
                                    dialog.dismiss();

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
                    ToastUtil.MyToast(AreaManagementActivity.this, strState);
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

    /**
     * 通过post向服务端发送请求(删除)
     */
    private void volley_post_romove() {
        try {

            //等待网络的D
            dialog = new LoadingDialog(AreaManagementActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            JSONObject params = new JSONObject();


            // 2.服务器网址
            final String URL2 = HttpServerAddress.BASE_URL + "?m=deletezone&zone_name=" + strAreaName + "&zone_no="
                    + strAreaNameID + "&user_context=" + user_context_number;
            Log.e("TAG",URL2);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL2, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            try {
                                System.out.println("------------" + "arg0=" + arg0 + "--------------");
                                String strState = arg0.getString("result");
                                //判断弹框状态
                                if (strState != null && strState.equalsIgnoreCase("true")) {
                                    dialog.dismiss();
                                    areamanagementlistview.setAdapter(adapter);
                                    AreaListNameID.removeAll(AreaListNameID);
                                    adapter.notifyDataSetChanged();
                                    volley_post();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    dialog.dismiss();
                    ToastUtil.MyToast(AreaManagementActivity.this, strState);

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

    /**
     * 通过post向服务端发送请求(重命名)
     */
    private void volley_post_rename() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(AreaManagementActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            JSONObject params = new JSONObject();


            // 2.服务器网址
            final String URL3 = HttpServerAddress.BASE_URL + "?m=modifyzone&zone_name=" + string_rename + "&zone_no="
                    + strAreaNameID + "&user_context=" + user_context_number;

            Log.e("TAG",URL3);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL3, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            try {
                                System.out.println("------------" + "arg0=" + arg0 + "--------------");
                                String strState = arg0.getString("result");

                                // json数据解析
                                if (strState != null && strState.equalsIgnoreCase("true")) {
                                    dialog.dismiss();
                                    areamanagementlistview.setAdapter(adapter);
                                    AreaListNameID.removeAll(AreaListNameID);
                                    adapter.notifyDataSetChanged();
                                    volley_post();
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
                    ToastUtil.MyToast(AreaManagementActivity.this, strState);
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

    //查询区域里有多少把锁
    private void vooleyGet() {
        try {

            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.POST请求参数

            final String URL = HttpServerAddress.BASE_URL + "?m=getzonelocknum&user_context=" + user_context_number;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;
                        System.out.println("------------" + "LOCK_LOCKNUM=" + strState + "--------------");
                        JSONArray array = strState.getJSONArray("LOCK_LOCKNUM");
                        System.out.println("array=" + array);
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                lockMap.put(object.getString("ZONE_NO").trim(), object.getString("LOCK_NUM").trim());
                            }
                            System.out.println("lockMap=" + lockMap);
                        }
                        volley_post();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(AreaManagementActivity.this, "无法获得锁信息和区域信息，请检查网络或者稍后再试");
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


    private class MyAreaAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAreaAdapter() {
            mInflater = LayoutInflater.from(AreaManagementActivity.this);
        }

        @Override
        public int getCount() {
            return AreaListNameID.size();
        }

        @Override
        public Object getItem(int position) {
            return AreaListNameID.get(position);
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
                convertView = mInflater.inflate(R.layout.activity_areamanagement_item, null);
                holder = new ViewHolder();
                holder.areaName = (TextView) convertView.findViewById(R.id.areamanagement_currentarea);
                holder.areaNameId = (TextView) convertView.findViewById(R.id.areamanagement_currentareaID);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.areaName.setText(AreaListNameID.get(position).get("AreaName").toString());
            holder.areaNameId.setText(AreaListNameID.get(position).get("AreaNameId").toString());
            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView areaName;
        public TextView areaNameId;
    }

    // 关闭activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 关闭上下文菜单
    @Override
    public void onContextMenuClosed(Menu menu) {
        // TODO Auto-generated method stub
        super.onContextMenuClosed(menu);
    }

    // 绑定手机返回键按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            area_linear.setVisibility(View.VISIBLE);
            area_text.setVisibility(View.INVISIBLE);
            areamanagementlistview.setAdapter(adapter);
            AreaListNameID.removeAll(AreaListNameID);
            adapter.notifyDataSetChanged();
            treeList = (ArrayList<String>) getNewList(treeList);
            System.out.println(treeList.size() - 1);
            if ((treeList.size() - 1) == 0) {
                finish();
                System.out.println("---------------finish()运行了!-----------------");
            } else {
                treeList.remove(treeList.size() - 1);
                string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                System.out.println("string_p_zone_no_number=" + string_p_zone_no_number);
                volley_OnClick();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
