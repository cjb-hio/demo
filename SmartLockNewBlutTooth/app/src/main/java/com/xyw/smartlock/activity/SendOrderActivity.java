package com.xyw.smartlock.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.SendOrder;
import com.xyw.smartlock.utils.SendOrderList;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SendOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mYear1;
    private int mMonth1;
    private int mDay1;
    private int mHour1;
    private int mMinute1;
    private TextView beginDate, beginTime, endDate, endTime;

    //声明时间日期变量
    private TextView titleText;
    private EditText sendOrderDemo, textAddSafetyMatters;
    private ImageView titleBack;
    private Button but_submit;
    private TextView text_send_work, text_send_head, text_send_driver, addSend_edText_area;
    private ImageView imageAddButton, imageAddProductionTools, imageAddSafetyMatters;
    private String number;
    private String random_number;
    private ACache aCache;
    private AcacheUserBean LoginInfo;
    private TextView unit_selectName, team_selectName, text_send_traffic_tools;
    private ArrayList<String> dataList3 = new ArrayList<>();
    private TextView textAddProductionTools;
    private LoadingDialog dialog;
    private SendOrder sendOrder = new SendOrder();
    private ArrayList<SendOrderList> sendOrderLists = new ArrayList<>();
    private SendOrderList sendOrderList;
    private TextView addSend_edText, addSend_statistics_number;
    private List list = new ArrayList();
    private EditText textAddProductionTools2;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendorder);
        getSupportActionBar().hide();

        // 缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");

        init();
    }

    private void init() {
        titleText = (TextView) findViewById(R.id.common_tv_title);
        titleText.setText(R.string.SendWorkApply);
        titleBack = (ImageView) findViewById(R.id.common_tv_back);
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        but_submit = (Button) findViewById(R.id.button_submit);

        text_send_work = (TextView) findViewById(R.id.text_send_work);
        text_send_work.setText(LoginInfo.getOP_NO());
        text_send_head = (TextView) findViewById(R.id.text_send_head);
        imageAddButton = (ImageView) findViewById(R.id.imageAddButton);
        imageAddProductionTools = (ImageView) findViewById(R.id.imageAddProductionTools);
        imageAddSafetyMatters = (ImageView) findViewById(R.id.imageAddSafetyMatters);
        text_send_driver = (TextView) findViewById(R.id.text_send_driver);

        but_submit.setOnClickListener(this);
        text_send_head.setOnClickListener(this);
        text_send_work.setOnClickListener(this);
        imageAddButton.setOnClickListener(this);
        text_send_driver.setOnClickListener(this);
        imageAddProductionTools.setOnClickListener(this);
        imageAddSafetyMatters.setOnClickListener(this);
        addSend_edText = (TextView) findViewById(R.id.addSend_edText);
        addSend_edText_area = (TextView) findViewById(R.id.addSend_edText_area);

        unit_selectName = (TextView) findViewById(R.id.unit_selectName);
        team_selectName = (TextView) findViewById(R.id.team_selectName);
        text_send_traffic_tools = (TextView) findViewById(R.id.text_send_traffic_tools);
        unit_selectName.setOnClickListener(this);
        team_selectName.setOnClickListener(this);
        text_send_traffic_tools.setOnClickListener(this);
        addSend_edText_area.setOnClickListener(this);
        textAddProductionTools = (TextView) findViewById(R.id.textAddProductionTools);
        sendOrderDemo = (EditText) findViewById(R.id.sendOrderDemo);
        textAddSafetyMatters = (EditText) findViewById(R.id.textAddSafetyMatters);
        addSend_statistics_number = (TextView) findViewById(R.id.addSend_statistics_number);
        textAddProductionTools2 = (EditText) findViewById(R.id.textAddProductionTools2);

        //时间日期的点击事件初始化
        dateTimeOnclick();
        startDate();
        startTime();
        endDate();
        endTime();

    }

    private void dateTimeOnclick() {
        beginDate = (TextView) findViewById(R.id.beginDate);
        beginTime = (TextView) findViewById(R.id.beginTime);
        endDate = (TextView) findViewById(R.id.endDate);
        endTime = (TextView) findViewById(R.id.endTime);

        ActivityUtils.getInstance().setTextUnderLine(beginDate);
        ActivityUtils.getInstance().setTextUnderLine(beginTime);
        ActivityUtils.getInstance().setTextUnderLine(endDate);
        ActivityUtils.getInstance().setTextUnderLine(endTime);

        beginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendShowDateDialog(beginDate, SendOrderActivity.SHOW_DAPATICK);
                new DatePickerDialog(SendOrderActivity.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });

        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendShowDateDialog(beginTime, SendOrderActivity.SHOW_TIMEPICK);
                new TimePickerDialog(SendOrderActivity.this, mTimeSetListener, mHour, mMinute, true).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendShowDateDialog(endDate, SendOrderActivity.CONTEXT_RESTRICTED);
                new DatePickerDialog(SendOrderActivity.this, mDateSetListener1, mYear1, mMonth1, mDay1).show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendShowDateDialog(endTime, SendOrderActivity.CONTEXT_INCLUDE_CODE);
                new TimePickerDialog(SendOrderActivity.this, mTimeSetListener1, mHour1, mMinute1, true).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_send_work:
                random_number = "1";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectUserrActivity.class), 1);
                break;
            case R.id.text_send_head:
                random_number = "2";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectUserrActivity.class), 2);
                break;
            case R.id.imageAddButton:
                random_number = "3";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectUserrActivity.class), 3);
                break;
            case R.id.text_send_driver:
                random_number = "4";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectUserrActivity.class), 4);
                break;
            case R.id.imageAddProductionTools:
                random_number = "5";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectionProductionActivity.class), 5);
                break;
            case R.id.imageAddSafetyMatters:
                random_number = "6";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectionSafetyActivity.class), 6);
                break;
            case R.id.unit_selectName:
                random_number = "7";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectionUnitActivity.class), 7);
                break;
            case R.id.team_selectName:
                random_number = "8";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectionTeamActivity.class), 8);
                break;
            case R.id.text_send_traffic_tools:
                random_number = "9";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectionTrafficActivity.class), 9);
                break;
            case R.id.addSend_edText_area:
                random_number = "10";
                startActivityForResult(new Intent(SendOrderActivity.this, SelectAreaManagementActivity.class), 10);
                break;

            case R.id.button_submit:
                //进行判断，派工人和工作负责人不能为空
                if (unit_selectName.getText().toString().trim().equals("请选择")) {
                    Toast.makeText(this, "请选择单位信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (team_selectName.getText().toString().trim().equals("请选择")) {
                    Toast.makeText(this, "请选择班组信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (text_send_work.getText().toString().trim().equals("请选择")) {
                    Toast.makeText(this, "请选择派工人", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (text_send_traffic_tools.getText().toString().trim().equals("请选择")) {
                    Toast.makeText(this, "请选择交通工具", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (text_send_head.getText().toString().trim().equals("请选择")) {
                    Toast.makeText(this, "请选择工作负责人", Toast.LENGTH_SHORT).show();
                    return;
                }
                //创建一个对象，将整个表单的信息存储在对象中
                try {
                    SendOrder();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //点击提交派工单信息
                volley_post1();
                break;
            default:
                break;
        }
    }

    private void volley_post1() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(SendOrderActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=insertlocktask"
                    + "&ZONE_NO=" + sendOrder.getR_ZONE_NO()
                    + "&BEGINDATETIME=" + sendOrder.getBeginDate() + "%20" + sendOrder.getBeginTime()
                    + "&ENDDATETIME=" + sendOrder.getEndDate() + "%20" + sendOrder.getEndTime()
                    + "&DEMO=" + sendOrder.getSendOrderDemo()
                    + "&R_OP_NO=" + sendOrder.getText_send_head_number()
                    + "&RET_OP_NO=" + sendOrder.getText_send_work_number()
                    + "&AUDIO_PATH1=" + "&AUDIO_PATH2=" + "&have_info=1"
                    + "&user_context=" + sendOrder.getUserContext();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            volley_post2(arg0.getString("TASK_NO"), sendOrder.getText_send_head_number(), sendOrder.getText_send_work_number());
                        } else {
                            Toast.makeText(SendOrderActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    private void volley_post2(String task_no, final String Text_send_head_number, final String text_send_work_number) {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            String str2;
            if (addSend_statistics_number.getText().toString().trim().equals("")) {
                str2 = "0";
            } else {
                String str1 = sendOrder.getAddSend_statistics_number().substring(0, sendOrder.getAddSend_statistics_number().length() - 1);
                str2 = str1.substring(1, str1.length());
            }

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreinfofun"
                    + "&funstr=add"
                    + "&TASK_NO=" + task_no
                    + "&DEP_NAME=" + sendOrder.getUnit_selectName()
                    + "&CLASS_NAME=" + sendOrder.getTeam_selectName()
                    + "&TRANSPORT=" + sendOrder.getText_send_traffic_tools()
                    + "&TOOLS_LIST=" + sendOrder.getTextAddProductionTools() + sendOrder.getTextAddProductionTools2()
                    + "&ATTENTION=" + sendOrder.getTextAddSafetyMatters()
                    + "&DRIVER=" + sendOrder.getText_send_driver()
                    + "&W_NUMBERS=" + str2
                    + "&W_BEGINTIME=" + sendOrder.getBeginDate() + "%20" + sendOrder.getBeginTime()
                    + "&W_ENDTIME=" + sendOrder.getEndDate() + "%20" + sendOrder.getEndTime()
                    + "&W_DEMO=" + ""
                    + "&user_context=" + sendOrder.getUserContext();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            if (sendOrder.getText_send_driver_number() != null) {
                                sendOrderList = new SendOrderList();
                                sendOrderList.setDataUserNumber(sendOrder.getText_send_driver_number());
                                sendOrderList.setDataUserName(sendOrder.getText_send_driver());
                                sendOrderLists.add(sendOrderList);
                            }
                            sendOrderList = new SendOrderList();
                            sendOrderList.setDataUserNumber(Text_send_head_number);
                            sendOrderList.setDataUserName(URLEncoder.encode(text_send_head.getText().toString().trim(), "UTF-8"));
                            sendOrderLists.add(sendOrderList);
                            sendOrderList = new SendOrderList();
                            sendOrderList.setDataUserNumber(text_send_work_number);
                            sendOrderList.setDataUserName(URLEncoder.encode(text_send_work.getText().toString().trim(), "UTF-8"));
                            sendOrderLists.add(sendOrderList);

                            for (SendOrderList sendOrderList : sendOrderLists) {
                                volley_post3(arg0.getString("TASK_NO"), sendOrderList.getDataUserName(), sendOrderList.getDataUserNumber());
                            }

                            dialog.dismiss();
                            Toast.makeText(SendOrderActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                            SendOrderActivity.this.finish();
                            TaskAndSendActivity.instance.finish();

                        } else {
                            Toast.makeText(SendOrderActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    private void volley_post3(String task_no, String dataUserName, String dataUserNumber) {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreuserfun"
                    + "&funstr=add"
                    + "&TASK_NO=" + task_no
                    + "&OP_NO=" + dataUserNumber
                    + "&OP_NAME=" + dataUserName
                    + "&user_context=" + sendOrder.getUserContext();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {

                        } else {
                            Toast.makeText(SendOrderActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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


    //获取当前页面的所有内容，存到对象里面
    private void SendOrder() throws UnsupportedEncodingException {

        sendOrder.setUnit_selectName(URLEncoder.encode(unit_selectName.getText().toString().trim(), "UTF-8"));
        sendOrder.setTeam_selectName(URLEncoder.encode(team_selectName.getText().toString().trim(), "UTF-8"));
        sendOrder.setText_send_work(URLEncoder.encode(text_send_work.getText().toString().trim(), "UTF-8"));
        sendOrder.setText_send_head(URLEncoder.encode(text_send_head.getText().toString().trim(), "UTF-8"));
        sendOrder.setAddSend_edText(addSend_edText.getText().toString().trim());
        sendOrder.setBeginDate(beginDate.getText().toString().trim());
        sendOrder.setBeginTime(beginTime.getText().toString().trim());
        sendOrder.setEndDate(endDate.getText().toString().trim());
        sendOrder.setEndTime(endTime.getText().toString().trim());
        sendOrder.setSendOrderDemo(URLEncoder.encode(sendOrderDemo.getText().toString().trim(), "UTF-8"));
        sendOrder.setText_send_traffic_tools(URLEncoder.encode(text_send_traffic_tools.getText().toString().trim(), "UTF-8"));
        String driver = text_send_driver.getText().toString().trim();
        if (driver.equals("请选择")) {
            sendOrder.setText_send_driver(URLEncoder.encode("无驾驶员", "UTF-8"));
        } else {
            sendOrder.setText_send_driver(URLEncoder.encode(text_send_driver.getText().toString().trim(), "UTF-8"));
        }
        sendOrder.setTextAddProductionTools(URLEncoder.encode(textAddProductionTools.getText().toString().trim(), "UTF-8"));
        sendOrder.setTextAddSafetyMatters(URLEncoder.encode(textAddSafetyMatters.getText().toString().trim(), "UTF-8"));
        sendOrder.setZONE_NAME(URLEncoder.encode(addSend_edText_area.getText().toString().trim(), "UTF-8"));

        sendOrder.setAddSend_statistics_number(addSend_statistics_number.getText().toString().trim());
        sendOrder.setTextAddProductionTools2(URLEncoder.encode("\r\n" + textAddProductionTools2.getText().toString().trim(), "UTF-8"));

        sendOrder.setR_ZONE_NO(LoginInfo.getArea_id());
        sendOrder.setUserContext(LoginInfo.getUSER_CONTEXT());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (data != null && random_number.equals("1")) {
                    text_send_work.setText(data.getStringExtra("username"));
                    number = data.getStringExtra("usernumber");
                    sendOrder.setText_send_work_number(data.getStringExtra("usernumber"));
                }
                break;
            case 2:
                if (data != null && random_number.equals("2")) {
                    if (!data.getStringExtra("usernumber").equals(sendOrder.getText_send_work_number())) {
                        text_send_head.setText(data.getStringExtra("username"));
                        sendOrder.setText_send_head_number(data.getStringExtra("usernumber"));
                        Log.e("TAG", data.getStringExtra("usernumber"));
                    } else {
                        Toast.makeText(this, "派工人与工作负责人不能相同", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 3:
                if (data != null && random_number.equals("3")) {
                    String dateStr = data.getStringExtra("username");
                    String dataUserNumber = data.getStringExtra("usernumber");

                    String[] strs = addSend_edText.getText().toString().trim().split("，") ;
                    for (String str : strs) {
                        if (str.equals(dateStr)) {
                            Toast.makeText(this, "该工作人员已添加", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    dataList3.add(dateStr);
                    addSend_statistics_number.setText("共" + dataList3.size() + "人");
                    String strSend = addSend_edText.getText().toString().trim();
                    sendOrderList = new SendOrderList();
                    try {
                        sendOrderList.setDataUserName(URLEncoder.encode(dateStr, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sendOrderList.setDataUserNumber(dataUserNumber);
                    sendOrderLists.add(sendOrderList);
                    if (strSend.equals("")) {
                        addSend_edText.setText(data.getStringExtra("username"));
                    } else {
                        addSend_edText.setText(strSend + "，" + data.getStringExtra("username"));
                    }
                }
                break;
            case 4:
                if (data != null && random_number.equals("4")) {
                    String username = data.getStringExtra("username");
                    String[] ss = addSend_edText.getText().toString().trim().split("，") ;
                    boolean isContains = false;
                    for (String str : ss) {
                        if (str.equals(text_send_driver.getText().toString().trim())) {
                            isContains = true;
                            break;
                        }
                    }
                    if (!isContains) {
                        dataList3.remove(text_send_driver.getText().toString().trim());
                    }
                    text_send_driver.setText(username);
                    if (dataList3.contains(username)) {
                        addSend_statistics_number.setText("共" + dataList3.size() + "人");
                    } else {
                        dataList3.add(data.getStringExtra("username"));
                        addSend_statistics_number.setText("共" + dataList3.size() + "人");
                    }
                    try {
                        sendOrder.setText_send_driver_number(URLEncoder.encode(data.getStringExtra("usernumber"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 5:
                if (data != null && random_number.equals("5")) {
                    String dataUserName = data.getStringExtra("username");
                    list.add(dataUserName);
                    Map map = new HashMap();
                    for (Object temp : list) {
                        Integer count = (Integer) map.get(temp);
                        map.put(temp, (count == null) ? 1 : count + 1);
                    }
                    String str = new String();
                    for (Object key : map.keySet()) {
                        str += key + "*" + map.get(key) + "    ";
                    }
                    textAddProductionTools.setText(str);
                }
                break;
            case 6:
                if (data != null && random_number.equals("6")) {
                    String textAddSafetyMattersStr = textAddSafetyMatters.getText().toString().trim();
                    if (textAddSafetyMattersStr.equals("")) {
                        textAddSafetyMatters.setText(data.getStringExtra("username"));
                    } else {
                        String[] strings = textAddSafetyMatters.getText().toString().trim().split("\n");
                        for (String str : strings) {
                            if (str.equals(data.getStringExtra("username"))) {
                                Toast.makeText(this, "该安全事项已添加", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        textAddSafetyMatters.setText(textAddSafetyMattersStr + "\n" + data.getStringExtra("username"));
                        for (String str : textAddSafetyMatters.getText().toString().split("\r\n")) {
                            Log.e("SendOrderActivity", "onActivityResult: str = " + str);
                        }
                    }
                }
                break;
            case 7:
                if (data != null && random_number.equals("7")) {
                    unit_selectName.setText(data.getStringExtra("username"));
                }
                break;
            case 8:
                if (data != null && random_number.equals("8")) {
                    team_selectName.setText(data.getStringExtra("username"));
                }
                break;
            case 9:
                if (data != null && random_number.equals("9")) {
                    text_send_traffic_tools.setText(data.getStringExtra("username"));
                }
                break;
            case 10:
                if (data != null && random_number.equals("10")) {
                    addSend_edText_area.setText(data.getStringExtra("Zone_Name")); //得到新Activity 关闭后返回的数据
                    sendOrder.setR_ZONE_NO(data.getStringExtra("Zone_No"));
                }
                break;
        }
    }

    /*--------------------------------------------------*/

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
        mYear1 = c.get(Calendar.YEAR);
        mMonth1 = c.get(Calendar.MONTH);
        mDay1 = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatend();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplatstart() {
        beginDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                .append("-").append((mDay < 10) ? "0" + mDay : mDay));
    }

    private void updateDateDisplatend() {
        endDate.setText(new StringBuilder().append(mYear1).append("-")
                .append((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1) : (mMonth1 + 1))
                .append("-").append((mDay1 < 10) ? "0" + mDay1 : mDay1));
    }

    /**
     * 日期控件事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplatstart();
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear1 = year;
            mMonth1 = monthOfYear;
            mDay1 = dayOfMonth;
            updateDateDisplatend();
        }
    };

    /**
     * 设置开始时间
     */
    private void startTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        updateTimeDisplaystart();
    }

    /**
     * 设置结束时间
     */

    private void endTime() {
        final Calendar c = Calendar.getInstance();
        mHour1 = c.get(Calendar.HOUR_OF_DAY);
        mMinute1 = c.get(Calendar.MINUTE);
        updateTimeDisplayend();
    }

    /**
     * 更新时间显示
     */
    private void updateTimeDisplaystart() {
        beginTime.setText(new StringBuilder().append(mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute));
    }

    private void updateTimeDisplayend() {
        endTime.setText(new StringBuilder().append(mHour1).append(":")
                .append((mMinute1 < 10) ? "0" + mMinute1 : mMinute1));
    }

    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateTimeDisplaystart();
        }
    };
    private TimePickerDialog.OnTimeSetListener mTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour1 = hourOfDay;
            mMinute1 = minute;
            updateTimeDisplayend();
        }

    };

    /*@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);

            case DATE_DIALOG_ID1:
                return new DatePickerDialog(this, mDateSetListener1, mYear1, mMonth1, mDay1);

            case TIME_DIALOG_ID1:
                return new TimePickerDialog(this, mTimeSetListener1, mHour1, mMinute1, true);

        }
        return null;
    }

    private void sendShowDateDialog(TextView tv, int what) {
        tv.setInputType(InputType.TYPE_NULL);// 关闭软键盘
        Message msg = new Message();
        msg.what = what;
        SendOrderActivity.this.dateandtimeHandler.sendMessage(msg);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID1:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
            case TIME_DIALOG_ID1:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
        }
    }

    *//**
     * 处理日期和时间控件的Handler
     *//*
    Handler dateandtimeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SendOrderActivity.SHOW_DAPATICK:
                    showDialog(DATE_DIALOG_ID);
                    break;
                case SendOrderActivity.SHOW_TIMEPICK:
                    showDialog(TIME_DIALOG_ID);
                    break;
                case SendOrderActivity.CONTEXT_RESTRICTED:
                    showDialog(DATE_DIALOG_ID1);
                    break;
                case SendOrderActivity.CONTEXT_INCLUDE_CODE:
                    showDialog(TIME_DIALOG_ID1);
                    break;

            }
        }

    };*/

}