package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class UserInfoAreaManagementActivity extends Activity {
    private ArrayList<String> AreList = new ArrayList<String>();
    private ArrayList<String> treeList = new ArrayList<String>();
    private HashMap<String, String> hashMap_area = new HashMap<String, String>();
    private ArrayList<String> backArea = new ArrayList<String>();
    private HashMap<String, String> hashMap_area_back = new HashMap<String, String>();  //TODO 1
    private TextView title;
    private ImageView backImage;
    private ListView selectlistview;
    private MyAreaAdapter adapter;
    private String str, strS, strinputStringr;
    private String objectS;
    private String string_p_zone_no_number, string_number;
    private String user_context_number;
    private String content_area;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String arrayStr1;
    private String arrayStr2;
    private int a = 0;
    protected JSONObject backvalue;
    protected String strvalues;
    private Button btnSubmit;
    private String titleText;
    private TextView area_text, area_tv_current_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //禁用主题
        setContentView(R.layout.userinfoarea);
        selectlistview = (ListView) findViewById(R.id.select_listview);
        adapter = new MyAreaAdapter();


        // 缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        user_context_number = acacheUserBean.getUSER_CONTEXT().toString();

        initview();
        volley_post();
    }

    private void initview() {
        area_text = (TextView) findViewById(R.id.area_text);
        area_tv_current_choice = (TextView) findViewById(R.id.area_tv_current_choice);
        area_tv_current_choice.setText("全部区域");

        btnSubmit = (Button) findViewById(R.id.userinfoarea_determine);
        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent = new Intent();
                // intent.putExtra("Zone_Name", content_area);
                // // intent.putExtra("Zone_No", )
                // intent.setClass(SelectAreaManagementActivity.this,
                // PermissAssignmentActivity.class);
                // startActivity(intent);
                // finish();
                if ((treeList.size() - 1) == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("Zone_Name", "全部");
                    intent.putExtra("Zone_No", "0000");
                    UserInfoAreaManagementActivity.this.setResult(RESULT_OK, intent);
                    UserInfoAreaManagementActivity.this.finish();
                    System.out.println("---------------finish()运行了!-----------------");
                }

                Intent intent = new Intent();
                intent.putExtra("Zone_Name", content_area);
                intent.putExtra("Zone_No", string_p_zone_no_number);
                UserInfoAreaManagementActivity.this.setResult(RESULT_OK, intent);

                // 关闭Activity
                UserInfoAreaManagementActivity.this.finish();
            }
        });
        // 设置返回按钮
        backImage = (ImageView) findViewById(R.id.userinfoarea_title_back);
        backImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                area_text.setVisibility(View.INVISIBLE);
                selectlistview.setAdapter(adapter);
                AreList.removeAll(AreList);
                adapter.notifyDataSetChanged();

                treeList = (ArrayList<String>) getNewList(treeList);

                System.out.println(treeList.size() - 1);

                if ((treeList.size() - 1) == 0) {
                    Intent intent = new Intent();
//                    intent.putExtra("Zone_Name", "全部");
//                    intent.putExtra("Zone_No", "0000");

                    UserInfoAreaManagementActivity.this.setResult(RESULT_OK, intent);
                    UserInfoAreaManagementActivity.this.finish();
                    ToastUtil.MyToast(getApplication(), "没有选择任何区域!");

                    System.out.println("---------------finish()运行了!-----------------");
                } else {
                    string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                    Log.e("string_p_zone_no_number", string_p_zone_no_number);
                    treeList.remove(treeList.size() - 1);
                    setTitle();
                }
                volley_OnClick();
            }
        });

        // 初始化标题栏和返回按钮
        // 设置标题栏
        title = (TextView) findViewById(R.id.userinfoarea_tv_title);
        title.setText("切换区域");

        // 监听每一条listview
        selectlistview = (ListView) findViewById(R.id.select_listview);
        // 监听listview进入下一层

        selectlistview.setAdapter(adapter);
        selectlistview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setBackgroundColor(Color.YELLOW);
                content_area = selectlistview.getItemAtPosition(position).toString();
                selectlistview.setAdapter(adapter);
                AreList.removeAll(AreList);
                adapter.notifyDataSetChanged();

                a++;
                volley_post();
                area_tv_current_choice.setText(content_area);

            }
        });
    }

    private void volley_OnClick() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL
                    + "?m=getzonelist&p_zone_no=" + string_p_zone_no_number
                    + "&user_context=" + user_context_number;
            Log.e("TAG",URL);
            System.out.println("99999999999999999999treeList=" + treeList);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {

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

                        for (int i = 0; i < array.length(); i++) {
                            System.out.println("array=" + array);
                            JSONObject object = array.getJSONObject(i);
                            System.out.println("object" + object);
                            // array数组下解析出的json集合累加
                            objectS += object + "\n";
                            // TODO
                            backvalue = object;
                            System.out.println("111" + objectS);

                            // TODO
                            // json集中单个数据
                            // String ZONE_NAME =
                            // object.getString("ZONE_NAME");
                            arrayStr1 = object.getString("ZONE_NO");
                            arrayStr2 = object.getString("ZONE_NAME");

                            hashMap_area.put(arrayStr1, arrayStr2);
                            AreList.add(arrayStr2);
                            // 刷新列表
                            adapter.notifyDataSetChanged();
                            System.out.println(AreList);

                            // 解析出单个json集中所有数据
                            String str = "ZONE_NO:"
                                    + object.getString("ZONE_NO")
                                    + "ZONE_NAME:"
                                    + object.getString("ZONE_NAME")
                                    + "P_ZONE_NO:"
                                    + object.getString("P_ZONE_NO");

                            strS += str + "\n";
                            System.out.println("str=" + str);
                        }
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
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(
                                arg0.data, "UTF-8"));
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(arg0));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            treeList.add(string_p_zone_no_number);
            String strKey = null;
            hello(strKey);
            hashMap_area.clear();
            System.out.println("*******************************" + "a=" + a);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL
                    + "?m=getzonelist&p_zone_no=" + string_p_zone_no_number
                    + "&user_context=" + user_context_number;
            Log.e("TAG",URL);
            System.out.println("99999999999999999999treeList=" + treeList);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        JSONObject strState = arg0;
                        // json数据解析
                        System.out.println("strState=" + strState);

                        JSONArray array = strState.getJSONArray("ZONE");
                        if (array.length() == 0) {
                            //没有数据时显示其他的view
                            area_text.setVisibility(View.VISIBLE);
                            new AlertDialog.Builder(UserInfoAreaManagementActivity.this)
                                    .setMessage("已经没有更多区域,请到区域管理里面添加!")// 设置显示的内容
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 添加确定按钮

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件

                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 添加返回按钮

                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 响应事件

                                }
                            }).show();// 在按键响应事件中显示此对话框
                        }
                        for (int i = 0; i < array.length(); i++) {
                            System.out.println("array=" + array);
                            JSONObject object = array.getJSONObject(i);
                            System.out.println("object" + object);
                            // array数组下解析出的json集合累加
                            objectS += object + "\n";
                            // json集中单个数据
                            String ZONE_NAME = object
                                    .getString("ZONE_NAME");
                            arrayStr1 = object.getString("ZONE_NO");
                            arrayStr2 = object.getString("ZONE_NAME");

                            hashMap_area.put(arrayStr1, arrayStr2);
                            //TODO
                            hashMap_area_back.put(string_p_zone_no_number, content_area);
                            System.out.println("hashMap_area" + hashMap_area);
                            System.out.println("hashMap_area1111111" + hashMap_area_back);
                            System.out.println("ZONE_NAME" + ZONE_NAME);
                            AreList.add(ZONE_NAME);
                            // 刷新列表
                            adapter.notifyDataSetChanged();
                            System.out.println(AreList);

                            // 解析出单个json集中所有数据
                            String str = "ZONE_NO:"
                                    + object.getString("ZONE_NO")
                                    + "ZONE_NAME:"
                                    + object.getString("ZONE_NAME")
                                    + "P_ZONE_NO:"
                                    + object.getString("P_ZONE_NO");

                            strS += str + "\n";
                            System.out.println("str=" + str);
                        }
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
                public void onErrorResponse(VolleyError arg0) {}
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

    private void hello(String strKey) {
        if (a == 0) {
            string_p_zone_no_number = "0000";
            area_tv_current_choice.setText("全部区域");
        } else {

            // 通过查询,查询书hashMap中与点击时显示的value值相同的key

            Set set = hashMap_area.entrySet();// 新建一个不可重复的集合
            Iterator itor = set.iterator();// 遍历的类
            while (itor.hasNext()) {// 存在下一个值
                HashMap.Entry entry = (HashMap.Entry) itor.next();// 找到所有key-value对集合
                if (entry.getValue().equals(content_area)) {// 获取value值与所知道的value比较
                    strKey = (String) entry.getKey();// 取得key值
                    System.out.println("你要找的key ：" + strKey);
                }
            }
            string_p_zone_no_number = strKey.toString().trim();
            System.out.println(string_p_zone_no_number);
        }
    }

    private class MyAreaAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAreaAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final ArrayList<String> areList) {
            AreList.addAll(AreList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return AreList.size();
        }

        @Override
        public Object getItem(int position) {
            return AreList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_areamanagement_item, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.areamanagement_currentarea);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(AreList.get(position));
            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView textView;
    }

    // 关闭
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 绑定手机返回键按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            area_text.setVisibility(View.INVISIBLE);
            selectlistview.setAdapter(adapter);
            AreList.removeAll(AreList);
            adapter.notifyDataSetChanged();

            treeList = (ArrayList<String>) getNewList(treeList);

            System.out.println(treeList.size() - 1);
            if ((treeList.size() - 1) == 0) {
                Intent intent = new Intent();
//                intent.putExtra("Zone_Name", "全部");
//                intent.putExtra("Zone_No", "0000");

                UserInfoAreaManagementActivity.this.setResult(RESULT_OK, intent);
                // 关闭Activity
                UserInfoAreaManagementActivity.this.finish();
                ToastUtil.MyToast(getApplication(), "没有选择任何区域!");
                System.out.println("---------------finish()运行了!-----------------");
            } else {
                string_p_zone_no_number = (treeList.get(treeList.size() - 1)).toString().trim();
                treeList.remove(treeList.size() - 1);
                setTitle();
                volley_OnClick();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setTitle() {
        Iterator ite = hashMap_area_back.keySet().iterator();
        while (ite.hasNext()) {
            String key = (String) ite.next();   //   key
            if (key.equals(string_p_zone_no_number)) {
                strvalues = hashMap_area_back.get(key);
                area_tv_current_choice.setText(strvalues);
                break;
            }
        }
        if (string_p_zone_no_number.equals("0000")) {
            area_tv_current_choice.setText("全部区域");
        }
    }

    //点击确定时向后台请求切换区域
    @Override
    public void finish() {
        super.finish();
    }
}
