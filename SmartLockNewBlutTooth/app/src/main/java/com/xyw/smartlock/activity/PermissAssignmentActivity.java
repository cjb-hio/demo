package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
import com.xyw.smartlock.utils.Fiale_dailog;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.utils.Volley_Default_Time;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;


/**
 * MrZhong
 * 2016-05-18 16:16
 */

public class PermissAssignmentActivity extends AppCompatActivity implements
        OnTouchListener, OnClickListener {
    private TextView title, name, dt;
    private String strName, strDt;
    private ImageView imageback;
    private Button permissassignment_bt1;
    private TextView etArea, startDate, startTime, endDate, endTime;
    private Spinner sp_type, sp_area;
    private ArrayAdapter<CharSequence> mAdapter;

    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    protected int sp_type_ID;
    protected String itemstr;
    private String Zone_Name;
    private String strAccount;
    private String endstrArea_id;
    private String area_no;
    private ProgressDialog progressDialog;
    private String strV_beginTime;
    private String strV_endTime;
    private String strRole_id;
    private String strType;
    private String strArea_id;

    private int backid; //服务器传来的用户类型值 1234；
    private int newid; //spinner的下标值
    private Boolean flag = false;
    private LoadingDialog dialog;
    private String strState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissassignment);
        getSupportActionBar().hide();
        // 初始化按钮
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        initview();
    }

    private void setIntentResult() {
        setResult(Activity.RESULT_OK);
    }

    private void volley_requestData() {
        try {
            dialog.show();

            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            // 请求地址
            if (area_no == null) {
                endstrArea_id = strArea_id;
            } else {
                endstrArea_id = area_no;
            }
            final String URL = HttpServerAddress.SETOPZONE + "&op_no="
                    + strAccount.toString() + "&zone_no=" + endstrArea_id
                    + "&begintime=" + startDate.getText().toString().trim() + "%20"
                    + startTime.getText().toString().trim() + ":00" + "&endtime="
                    + endDate.getText().toString().trim() + "%20"
                    + endTime.getText().toString().trim() + ":00" + "&user_context="
                    + acacheUserBean.getUSER_CONTEXT();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("onResponse", arg0.toString());
                    try {
                        strState = arg0.getString("result");
                        dialog.dismiss();
                        if (strState != null && strState.equalsIgnoreCase("true")) {
                            ToastUtil.MyToast(getApplicationContext(), "提交成功");
//                            Intent intent = new Intent(PermissAssignmentActivity.this, JurisdictionActivity.class);
//                            startActivity(intent);
                            if (strAccount.equals(acacheUserBean.getOP_NO())) {
                                requestData();
                            } else {
                                setIntentResult();
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        permissassignment_bt1.setClickable(true);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    new Fiale_dailog(PermissAssignmentActivity.this, R.style.dailogStyle).show();
                    ToastUtil.MyToast(PermissAssignmentActivity.this, strState);
                    dialog.dismiss();
                    permissassignment_bt1.setClickable(true);
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse arg0) {
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
            Volley_Default_Time.setDefaultRetryPolicy(jsonObjectRequest);
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化按钮并绑定控件
     */
    private void initview() {
        dialog = new LoadingDialog(PermissAssignmentActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.permissionassignment);
        name = (TextView) findViewById(R.id.permission_name);
        dt = (TextView) findViewById(R.id.permission_dt);
        etArea = (TextView) findViewById(R.id.permission_area);
        startDate = (TextView) findViewById(R.id.permission_startDate);
        startTime = (TextView) findViewById(R.id.permission_startTime);
        endDate = (TextView) findViewById(R.id.permission_endDate);
        endTime = (TextView) findViewById(R.id.permission_endTime);

        ActivityUtils.getInstance().setTextUnderLine(startDate);
        ActivityUtils.getInstance().setTextUnderLine(startTime);
        ActivityUtils.getInstance().setTextUnderLine(endDate);
        ActivityUtils.getInstance().setTextUnderLine(endTime);

        sp_type = (Spinner) findViewById(R.id.permission_userType_sp);
        mAdapter = new ArrayAdapter<CharSequence>(PermissAssignmentActivity.this, R.layout.permiss_spinner_item, getResources().getStringArray(R.array.userspinner));
        //设置样式
//        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(mAdapter);

        Intent intent = getIntent();

        strAccount = intent.getStringExtra("OP_NO");
        strName = intent.getStringExtra("NAME");
        strDt = intent.getStringExtra("DT");
        strRole_id = intent.getStringExtra("ROLE_ID");
        strV_beginTime = intent.getStringExtra("V_BEGINTIME");
        strV_endTime = intent.getStringExtra("V_ENDTIME");
        etArea.setText(intent.getStringExtra("ZONE_NAME"));
        strArea_id = intent.getStringExtra("AREA_ID");


        //用户类型的改变
        backid = Integer.parseInt(strRole_id);
        newid = backid - 1;
        sp_type.setSelection(newid);
        sp_type.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sp_type_ID = position + 1;
                itemstr = sp_type.getSelectedItem().toString();
                strType = String.valueOf(sp_type_ID);
                if (strType.equals(strRole_id) && newid == position) {
                    return;
                } else {
                    dialog(newid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        // 设置标题栏返回按钮
        imageback = (ImageView) findViewById(R.id.common_title_back);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PermissAssignmentActivity.this, JurisdictionActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        //显示时间
        String[] s1 = strV_beginTime.split(" ");//以" "为分隔符，截取的字符串
        String[] s2 = strV_endTime.split(" ");
        if (s1.length == 2) {
            startDate.setText(s1[0].replace("/", "-"));
            startTime.setText(s1[1].substring(0, s1[1].length() - 3));
        } else {
            startDate.setText("");
            startTime.setText("");
        }
        if (s2.length == 2) {
            endDate.setText(s2[0].replace("/", "-"));
            endTime.setText(s2[1].substring(0, s2[1].length() - 3));
        } else {
            endDate.setText("");
            endTime.setText("");
        }
        name.setText(strName);
        dt.setText(strDt);

//        etArea.setOnTouchListener(this);
//        startDate.setOnTouchListener(this);
//        startTime.setOnTouchListener(this);
//        endDate.setOnTouchListener(this);
//        endTime.setOnTouchListener(this);

        etArea.setOnClickListener(this);
        startDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endTime.setOnClickListener(this);

        permissassignment_bt1 = (Button) findViewById(R.id.permissassignment_bt1);
        permissassignment_bt1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                permissassignment_bt1.setClickable(false);
                volley_requestData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_area:
                etArea.setInputType(InputType.TYPE_NULL);
                OpentAreManagement();
                break;
            case R.id.permission_startDate:
                startDate.setInputType(InputType.TYPE_NULL);
                createStartDateDialog();
                break;
            case R.id.permission_startTime:
                startTime.setInputType(InputType.TYPE_NULL);
                createStartTimeDialog();
                break;
            case R.id.permission_endDate:
                endDate.setInputType(InputType.TYPE_NULL);
                createEndDateDialog();
                break;
            case R.id.permission_endTime:
                endTime.setInputType(InputType.TYPE_NULL);
                createEndTimeDialog();
                break;
        }
    }

    protected int dialog(final int newid) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你真的要设为" + itemstr).setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        volley_requestType();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        sp_type.setSelection(newid);

                    }
                });
        AlertDialog alert = builder.show();
        return newid;
    }

    protected void volley_requestType() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            // 请求地址
            final String URL = HttpServerAddress.SETOPTYPE + "&op_no="
                    + strAccount + "&role_id="
                    + sp_type_ID + "&user_context="
                    + acacheUserBean.getUSER_CONTEXT();
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("onResponse", arg0.toString());
                    try {
                        strState = arg0.getString("result");
                        if (strState != null
                                && strState.equalsIgnoreCase("true")) {
                            ToastUtil.MyToast(getApplicationContext(), "修改成功");
                            newid = sp_type_ID - 1;
                        } else {
                            sp_type.setSelection(newid);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(),strState);
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(arg0.data, "UTF-8"));
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

    private void requestData() {
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            final String userCounext = LoginInfo.getUSER_CONTEXT().toString().trim();
            final String account = LoginInfo.getAccount().toString().trim();
            final String address = LoginInfo.getAddress().toString().trim();
            final String maxver = LoginInfo.getMAXVER().toString().trim();

            // 2.POST请求参数

            final String URL = HttpServerAddress.BASE_URL + "?m=GetLoginInfo" + "&op_no="
                    + account + "&USER_CONTEXT=" + userCounext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------arg0=" + arg0 + "--------------");
                        String strState = arg0.getString("result");
                        System.out.println("------------" + strState + "--------------");
                        //判断弹框状态
                        dialog.dismiss();

                        if (strState.equalsIgnoreCase("true")) {
                            String str = "OP_DT:" + arg0.getString("OP_DT") + "\n"
                                    + "OP_PHONE:" + arg0.getString("OP_PHONE") + "\n"
                                    + "result:" + arg0.getString("result") + "\n"
                                    + "VBEGINTIME:" + arg0.getString("VBEGINTIME") + "\n"
                                    + "VENDTIME:" + arg0.getString("VENDTIME") + "\n"
                                    + "OP_NAME:" + arg0.getString("OP_NAME") + "\n"
                                    + "AREA_NAME:" + arg0.getString("AREA_NAME") + "\n"
                                    + "KEYVALUE:" + arg0.getString("KEYVALUE") + "\n"
                                    + "AREA_ID:" + arg0.getString("AREA_ID") + "\n"
                                    + "ROLE_ID:" + arg0.getString("ROLE_ID") + "\n"
                                    + "USER_CONTEXT:" + arg0.getString("USER_CONTEXT") + "\n";
                            System.out.println("json解析后数据:" + "\n" + str);
                            //将获取的数据替换缓存中的数据
                            acacheUserBean.setMAXVER(maxver);
                            acacheUserBean.setOP_DT(arg0.getString("OP_DT"));
                            acacheUserBean.setOP_NO(arg0.getString("OP_PHONE"));
                            acacheUserBean.setResult(arg0.getString("result"));
                            acacheUserBean.setBeginTime(arg0.getString("VBEGINTIME"));
                            acacheUserBean.setEndTime(arg0.getString("VENDTIME"));
                            acacheUserBean.setOP_NAME(arg0.getString("OP_NAME"));
                            acacheUserBean.setAREA_NAME(arg0.getString("AREA_NAME"));
                            acacheUserBean.setKEYVALUE(arg0.getString("KEYVALUE"));
                            acacheUserBean.setArea_id(arg0.getString("AREA_ID"));
                            acacheUserBean.setROLE_ID(arg0.getString("ROLE_ID"));
                            acacheUserBean.setUSER_CONTEXT(userCounext);
                            acacheUserBean.setAddress(address);
                            acacheUserBean.setAccount(account);

                            acacheUserBean.setBeginTime(arg0.getString("VBEGINTIME").trim());
                            acacheUserBean.setEndTime(arg0.getString("VENDTIME").trim());

                            aCache.put("LoginInfo", acacheUserBean);// 缓存文件名称

                            setIntentResult();
                            finish();//更新完数据后关闭Activity
                            //获取混村数据，在判断是否有NFC功能
                        } else {
//                            BeginTime=LoginInfo.getBeginTime().toString();
//                            EndTime=LoginInfo.getEndTime().toString();
//                            dateTime();
                            ToastUtil.MyToast(PermissAssignmentActivity.this, strState);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
//                    BeginTime=LoginInfo.getBeginTime().toString();
//                    EndTime=LoginInfo.getEndTime().toString();
//                    dateTime();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.permission_area:
//                        etArea.setInputType(InputType.TYPE_NULL);
//                        OpentAreManagement();
                        break;
                    case R.id.permission_startDate:
//                        startDate.setInputType(InputType.TYPE_NULL);
//                        createStartDateDialog();
                        break;
                    case R.id.permission_startTime:
//                        startTime.setInputType(InputType.TYPE_NULL);
//                        createStartTimeDialog();
                        break;
                    case R.id.permission_endDate:
//                        endDate.setInputType(InputType.TYPE_NULL);
//                        createEndDateDialog();
                        break;
                    case R.id.permission_endTime:
//                        endTime.setInputType(InputType.TYPE_NULL);
//                        createEndTimeDialog();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void OpentAreManagement() {
        startActivityForResult(new Intent(PermissAssignmentActivity.this, SelectAreaManagementActivity.class), 1);
    }

    private void createEndTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        Dialog dialog = null;
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime.setText(hourOfDay + ":" + minute);
            }

        };
        dialog = new TimePickerDialog(this, timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.DAY_OF_MONTH), true);
        dialog.show();
    }

    private void createEndDateDialog() {
        // TODO Auto-generated method stub
        Calendar calendar = Calendar.getInstance();
        Dialog dialog = null;
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year,
                                  int monthOfYear, int dayOfMonth) {
                String month = "0" + (monthOfYear + 1);
                String day = "0" + dayOfMonth;
                String seldate = year + "-"
                        + month.substring(month.length() - 2) + "-"
                        + day.substring(day.length() - 2);
                endDate.setText(seldate);
            }
        };
        dialog = new DatePickerDialog(this, dateListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void createStartTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        Dialog dialog = null;
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.setText(hourOfDay + ":" + minute);
            }

        };
        dialog = new TimePickerDialog(this, timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.DAY_OF_MONTH), true);
        dialog.show();
    }

    private void createStartDateDialog() {
        Calendar calendar = Calendar.getInstance();
        Dialog dialog = null;
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year,
                                  int monthOfYear, int dayOfMonth) {
                String month = "0" + (monthOfYear + 1);
                String day = "0" + dayOfMonth;
                String seldate = year + "-"
                        + month.substring(month.length() - 2) + "-"
                        + day.substring(day.length() - 2);
                startDate.setText(seldate);
            }
        };
        dialog = new DatePickerDialog(this, dateListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Zone_Name = data.getStringExtra("Zone_Name");// 得到新Activity 关闭后返回的数据
            area_no = data.getStringExtra("Zone_No");
            Log.e("onActivityResult", Zone_Name);
            etArea.setText(Zone_Name);
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(PermissAssignmentActivity.this, JurisdictionActivity.class);
//        startActivity(intent);
//        finish();
//    }
}