package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xyw.smartlock.R;
import com.xyw.smartlock.adapter.AuditGridViewAdapter;
import com.xyw.smartlock.bean.UserBean;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.Audit;
import com.xyw.smartlock.utils.ImageTools1;
import com.xyw.smartlock.utils.PostImageHead;
import com.xyw.smartlock.utils.SLImageLoader;
import com.xyw.smartlock.utils.SendOrderList;
import com.xyw.smartlock.utils.Volley_Default_Time;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class AuditActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "AuditActivity";
    private ImageLoader imageLoader;


    private TextView tv_title;
    private ImageView titleBack;
    private TextView Audit_Unit, audit_team, Audit_Send_workers, Audit_head, audit_person_name, Audit_beginDateTime,
            Audit_endDateTime, Audit_Demo, Audit_send_traffic_tools, Audit_send_driver, Audit_AddProductionTools,
            Audit_AddSafetyMatters, audit_team_number, Audit_Send_workers_number;
    private Button Audit_AgreedAndSignature, Audit_UnAgreed, Audit_Review_Success;
    private LinearLayout LinearLayout11;

    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String UserContext;
    private String TASK_NO;
    private List<SendOrderList> SendOrderLists = new ArrayList<>();
    private List<Audit> AuditLists = new ArrayList<>();
    private Audit audit;
    private SendOrderList sendOrderList;
    private String RET_OP_NO, R_OP_NO, R_OP_NAME;
    private List<UserBean> userBeanList = new ArrayList<UserBean>();
    private String role_id, ret_v;
    //请求网络的等待界面
    private LoadingDialog dialog;
    private Bitmap mSignBitmap;
    //    private ImageView Audit_picture_ImageView;
    private Button Audit_picture_Button;

    private ImageView relativeLayout_001_ImageView1, relativeLayout_001_ImageView2, relativeLayout_002_ImageView1,
            relativeLayout_002_ImageView2, relativeLayout_003_ImageView1, relativeLayout_003_ImageView2;
    private String ImageViewStr;

    private File file;
    private static final int SCALE = 4;//照片缩小比例


    private String photoSavePath;//保存路径
    private String photoSaveName;//图片名
    private String WorkImg1PhotoSaveName;//备注1的图片
    private String WorkImg2PhotoSaveName;//备注2的图片
    private String WorkImg3PhotoSaveName;//备注3的图片


    private PostImageHead postImageHead;
    private AlertDialog alertDialog;

    private GridView Audit_picture_GridView;
    private AuditGridViewAdapter auditGridViewAdapter;
    private TextView relativeLayout_001_TextView2, relativeLayout_002_TextView2, relativeLayout_003_TextView2;
    private TextView Audit_actual_beginDate, Audit_actual_beginTime, Audit_actual_endDate, Audit_actual_endTime, Audit_statistics_number, Audit_Note_picture;
    private Button note_button;

    private ActivityUtils mActivityUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
        getSupportActionBar().hide();
        mActivityUtils = ActivityUtils.getInstance();
        // 初始化标题栏和返回按钮
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        UserContext = acacheUserBean.getUSER_CONTEXT();
        Intent intent = getIntent();
        TASK_NO = intent.getStringExtra("TASK_NO");
        role_id = intent.getStringExtra("role_id");
        ret_v = intent.getStringExtra("ret_v");

        init();

        Audit_head.setText(intent.getStringExtra("name"));
        Audit_beginDateTime.setText(intent.getStringExtra("startTime"));
        Audit_endDateTime.setText(intent.getStringExtra("endTime"));
        Audit_Demo.setText(intent.getStringExtra("DEMO"));
        RET_OP_NO = intent.getStringExtra("RET_OP_NO");
        R_OP_NO = intent.getStringExtra("R_OP_NO");
        R_OP_NAME = intent.getStringExtra("name");
        Log.e("RET_OP_NO", RET_OP_NO);

        volley_GetData1();
        volley_GetData3(RET_OP_NO);
        volley_GetData4();

        if (RET_OP_NO.equals(acacheUserBean.getOP_NO())) {
            if (ret_v.equals("审核中")) {
                LinearLayout11.setVisibility(View.VISIBLE);
            } else if (ret_v.equals("审核成功")) {
                LinearLayout11.setVisibility(View.GONE);
            } else if (ret_v.equals("审核失败")) {
                LinearLayout11.setVisibility(View.GONE);
            }
        } else {
            LinearLayout11.setVisibility(View.GONE);
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String str = sdf.format(date);
        photoSavePath = Environment.getExternalStorageDirectory() + "/SmartLock/image/";
        photoSaveName = "sign" + str + ".png";
        WorkImg1PhotoSaveName = "WorkImg1" + str + ".png";
        WorkImg2PhotoSaveName = "WorkImg2" + str + ".png";
        WorkImg3PhotoSaveName = "WorkImg3" + str + ".png";

        //加载图片需要先进行初始化
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(AuditActivity.this));
    }


    private void volley_GetData3(final String ret_op_no) {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL + "?m=GetOpList" + "&user_context=" + UserContext + "&op_no=" + acacheUserBean.getOP_NO();

            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        Log.e("ARG0", String.valueOf(arg0));
                        JSONArray array = arg0.getJSONArray("LOCK_OP");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (ret_op_no.equals(object.getString("OP_NO"))) {
                                    Log.e("TAG", ret_op_no);
                                    Log.e("TAG", object.getString("OP_NAME"));
                                    Audit_Send_workers.setText(object.getString("OP_NAME"));
                                }
                            }
                            volley_GetData2();
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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
            Volley_Default_Time.setDefaultRetryPolicy(jsonObjectRequest);
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.common_tv_title);
        tv_title.setText(R.string.dispatching_application);
        titleBack = (ImageView) findViewById(R.id.common_title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Audit_Unit = (TextView) findViewById(R.id.Audit_Unit);
        audit_team = (TextView) findViewById(R.id.audit_team);
        Audit_Send_workers = (TextView) findViewById(R.id.Audit_Send_workers);
        Audit_head = (TextView) findViewById(R.id.Audit_head);
        audit_person_name = (TextView) findViewById(R.id.audit_person_name);
        Audit_beginDateTime = (TextView) findViewById(R.id.Audit_beginDateTime);
        Audit_endDateTime = (TextView) findViewById(R.id.Audit_endDateTime);
        Audit_Demo = (TextView) findViewById(R.id.Audit_Demo);
        Audit_send_traffic_tools = (TextView) findViewById(R.id.Audit_send_traffic_tools);
        Audit_send_driver = (TextView) findViewById(R.id.Audit_send_driver);
        Audit_AddProductionTools = (TextView) findViewById(R.id.Audit_AddProductionTools);
        Audit_AddSafetyMatters = (TextView) findViewById(R.id.Audit_AddSafetyMatters);
        audit_team_number = (TextView) findViewById(R.id.audit_team_number);

        Audit_AgreedAndSignature = (Button) findViewById(R.id.Audit_Agreed);
        Audit_UnAgreed = (Button) findViewById(R.id.Audit_UnAgreed);
        LinearLayout11 = (LinearLayout) findViewById(R.id.LinearLayout11);

        Audit_AgreedAndSignature.setOnClickListener(this);
        Audit_UnAgreed.setOnClickListener(this);
        Audit_picture_Button = (Button) findViewById(R.id.Audit_picture_Button);
        Audit_picture_Button.setOnClickListener(this);

        dialog = new LoadingDialog(AuditActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);

        relativeLayout_001_ImageView1 = (ImageView) findViewById(R.id.relativeLayout_001_ImageView1);
        relativeLayout_001_ImageView2 = (ImageView) findViewById(R.id.relativeLayout_001_ImageView2);
        relativeLayout_002_ImageView1 = (ImageView) findViewById(R.id.relativeLayout_002_ImageView1);
        relativeLayout_002_ImageView2 = (ImageView) findViewById(R.id.relativeLayout_002_ImageView2);
        relativeLayout_003_ImageView1 = (ImageView) findViewById(R.id.relativeLayout_003_ImageView1);
        relativeLayout_003_ImageView2 = (ImageView) findViewById(R.id.relativeLayout_003_ImageView2);
        relativeLayout_001_TextView2 = (TextView) findViewById(R.id.relativeLayout_001_TextView2);
        relativeLayout_002_TextView2 = (TextView) findViewById(R.id.relativeLayout_002_TextView2);
        relativeLayout_003_TextView2 = (TextView) findViewById(R.id.relativeLayout_003_TextView2);
        relativeLayout_001_ImageView2.setOnClickListener(this);
        relativeLayout_002_ImageView2.setOnClickListener(this);
        relativeLayout_003_ImageView2.setOnClickListener(this);
        relativeLayout_001_ImageView1.setOnClickListener(this);
        relativeLayout_002_ImageView1.setOnClickListener(this);
        relativeLayout_003_ImageView1.setOnClickListener(this);

        Audit_picture_GridView = (GridView) findViewById(R.id.Audit_picture_GridView);
        auditGridViewAdapter = new AuditGridViewAdapter(this, AuditLists);
        Audit_picture_GridView.setAdapter(auditGridViewAdapter);
        Audit_picture_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Audit audit = AuditLists.get(position);
                Intent intent = new Intent(AuditActivity.this, ImageShowerActivity.class);
                intent.putExtra("SIGNIMG", audit.getSIGNIMG());
                startActivity(intent);
            }
        });

        Audit_Note_picture = (TextView) findViewById(R.id.Audit_Note_picture);
        note_button = (Button) findViewById(R.id.note_button);
        note_button.setOnClickListener(this);

        Audit_statistics_number = (TextView) findViewById(R.id.Audit_statistics_number);
        Audit_actual_beginDate = (TextView) findViewById(R.id.Audit_actual_beginDate);
        Audit_actual_beginTime = (TextView) findViewById(R.id.Audit_actual_beginTime);
        Audit_actual_endDate = (TextView) findViewById(R.id.Audit_actual_endDate);
        Audit_actual_endTime = (TextView) findViewById(R.id.Audit_actual_endTime);
    }


    private void signatureDialog(final String str1) {
        WritePadDialog writeTabletDialog = new WritePadDialog(AuditActivity.this, new DialogListener() {
            @Override
            public void refreshActivity(Object object) {

                mSignBitmap = (Bitmap) object;
                            /*
                             * 将bitmap保存到本地.jpg
							 */
                file = new File(Environment.getExternalStorageDirectory(), "SmartLock/image");
                FileOutputStream Out = null;
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(photoSavePath + photoSaveName);

                try {
                    file.createNewFile();
                    Out = new FileOutputStream(file);
                    mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, Out);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } finally {
                    try {
                        Out.flush();
                        Out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (R_OP_NO.equals(acacheUserBean.getOP_NO())) {
                    ArrayList<String> myList = new ArrayList<>();
                    for (Audit audit : AuditLists) {
                        if (!audit.getDataUserNumber().equals(acacheUserBean.getOP_NO())) {
                            myList.add(audit.getSIGN());
                        }
                    }

                    HashSet hSet = new HashSet(myList);
                    myList.clear();
                    myList.addAll(hSet);
                    if (myList.size() != 1 || myList.get(0).equals("0")) {
                        Toast.makeText(AuditActivity.this, "请先确认小组成员签名", Toast.LENGTH_SHORT).show();
                    } else if (myList.size() == 1 || myList.get(0).equals("1")) {
                        ImageViewPostUrl(photoSavePath, photoSaveName, str1);
                    }
                } else {
                    ImageViewPostUrl(photoSavePath, photoSaveName, str1);
                }

            }
        });
        writeTabletDialog.show();
    }


    private void volley_GetData1() {
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreinfofun&funstr=getlist&TASK_NO=" + TASK_NO + "&user_context=" + UserContext;

            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        Log.e("ARG0", String.valueOf(arg0));
                        JSONArray array = arg0.getJSONArray("LOCK_TASKINFO");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                Audit_Unit.setText(object.getString("DEP_NAME"));
                                audit_team.setText(object.getString("CLASS_NAME"));
                                audit_team_number.setText(object.getString("TASK_NO"));
                                Audit_send_traffic_tools.setText(object.getString("TRANSPORT"));
                                Audit_send_driver.setText(object.getString("DRIVER"));
                                Audit_AddProductionTools.setText(object.getString("TOOLS_LIST"));
                                Audit_AddSafetyMatters.setText(object.getString("ATTENTION"));
                                Audit_statistics_number.setText("共" + object.getString("W_NUMBERS") + "人");

                                Audit_Note_picture.setText(object.getString("W_DEMO"));

                                if (object.getString("W_DEMO") == null || object.getString("W_DEMO").equals("")) {
                                    note_button.setText(R.string.submit);
                                } else {
                                    note_button.setText("更新备注");
                                }

                                Audit_actual_beginDate.setText(object.getString("W_BEGINTIME").substring(0, object.getString("W_BEGINTIME").indexOf(" ")));
                                Audit_actual_beginTime.setText(object.getString("W_BEGINTIME").substring(object.getString("W_BEGINTIME").indexOf(" ") + 1));
                                Audit_actual_endDate.setText(object.getString("W_ENDTIME").substring(0, object.getString("W_ENDTIME").indexOf(" ")));
                                Audit_actual_endTime.setText(object.getString("W_ENDTIME").substring(object.getString("W_ENDTIME").indexOf(" ") + 1));
//                                Audit_actual_endTime.setText(object.getString("W_BEGINTIME").substring(object.getString("W_ENDTIME").indexOf("20%"), object.getString("W_ENDTIME").length()));
                                if (!object.getString("WORKIMG1").equals("")) {
                                    relativeLayout_001_TextView2.setText(object.getString("WORKIMG1"));
                                    loadHeadPixImage(object.getString("WORKIMG1"), relativeLayout_001_ImageView1);
                                }
                                if (!object.getString("WORKIMG2").equals("")) {
                                    relativeLayout_002_TextView2.setText(object.getString("WORKIMG2"));
                                    loadHeadPixImage(object.getString("WORKIMG2"), relativeLayout_002_ImageView1);
                                }
                                if (!object.getString("WORKIMG3").equals("")) {
                                    relativeLayout_003_TextView2.setText(object.getString("WORKIMG3"));
                                    loadHeadPixImage(object.getString("WORKIMG3"), relativeLayout_003_ImageView1);
                                }
                            }
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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
            Volley_Default_Time.setDefaultRetryPolicy(jsonObjectRequest);
            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadHeadPixImage(String str1, ImageView imageView) {
        String url = HttpServerAddress.UPLOADS + str1;
        SLImageLoader.getInstance().loadImagePix(url, imageView);
    }

    private void volley_GetData2() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreuserfun&funstr=getlist&TASK_NO=" + TASK_NO + "&user_context=" + UserContext;

            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        Log.e("ARG0", String.valueOf(arg0));
                        JSONArray array = arg0.getJSONArray("LOCK_TASKOPLIST");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                sendOrderList = new SendOrderList();
                                sendOrderList.setDataUserName(object.getString("OP_NAME"));
                                sendOrderList.setSIGNIMG(object.getString("SIGNIMG"));
                                sendOrderList.setDataUserNumber(object.getString("OP_NO"));
                                SendOrderLists.add(sendOrderList);
                            }
                            removeHashSet1(SendOrderLists);
                            for (SendOrderList sendOrderList : SendOrderLists) {
                                String Audit_Person_Name = audit_person_name.getText().toString();
                                if (!sendOrderList.getDataUserName().equals(Audit_head.getText().toString().trim())
                                        && !sendOrderList.getDataUserName().equals(Audit_Send_workers.getText().toString().trim())) {
                                    if (Audit_Person_Name.equals("")) {
                                        audit_person_name.setText(sendOrderList.getDataUserName());
                                    } else {
                                        audit_person_name.setText(Audit_Person_Name + "," + sendOrderList.getDataUserName());
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        dialog.dismiss();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Audit_Agreed:
                String str1;
                if (acacheUserBean.getOP_NO().equals(RET_OP_NO)) {
                    str1 = "true";
                    signatureDialog(str1);
                } else {
                    Toast.makeText(this, "请派工人签发此单", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Audit_UnAgreed:
                String str2 = "false";
                audit2(str2);
                break;
            case R.id.Audit_picture_Button:
                signatureDialog("");
                break;
            case R.id.relativeLayout_001_ImageView2:
                ImageViewStr = "1";
                showPicturePicker(ImageViewStr);
                break;
            case R.id.relativeLayout_002_ImageView2:
                ImageViewStr = "2";
                showPicturePicker(ImageViewStr);
                break;
            case R.id.relativeLayout_003_ImageView2:
                ImageViewStr = "3";
                showPicturePicker(ImageViewStr);
                break;
            case R.id.relativeLayout_001_ImageView1:
                Intent intent1 = new Intent(AuditActivity.this, ImageShowerActivity.class);
                intent1.putExtra("SIGNIMG", relativeLayout_001_TextView2.getText().toString().trim());
                startActivity(intent1);
                break;
            case R.id.relativeLayout_002_ImageView1:
                Intent intent2 = new Intent(AuditActivity.this, ImageShowerActivity.class);
                intent2.putExtra("SIGNIMG", relativeLayout_002_TextView2.getText().toString().trim());
                startActivity(intent2);
                break;
            case R.id.relativeLayout_003_ImageView1:
                Intent intent3 = new Intent(AuditActivity.this, ImageShowerActivity.class);
                intent3.putExtra("SIGNIMG", relativeLayout_003_TextView2.getText().toString().trim());
                startActivity(intent3);
                break;
            case R.id.note_button:
                updateDemo(Audit_Note_picture.getText().toString().trim());
                break;

            default:
                break;
        }
    }

    private void ImageViewPostUrl(final String photoSavePath, final String photoSaveName, final String str1) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postImageHead = new PostImageHead();
                try {
                    //等待网络的D
                    String result = postImageHead.run(HttpServerAddress.UPLOADFILE + "&USER_CONTEXT=" + UserContext, photoSavePath + photoSaveName);
                    Log.e("result", result);
                    if (result.equalsIgnoreCase("true")) {
                        if (RET_OP_NO.equals(acacheUserBean.getOP_NO())) {
                            audit(str1);
                        } else {
                            signature();
                        }
                    } else {
                        Toast.makeText(AuditActivity.this, "签名失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //个人签名
    private void signature() {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreuserfun"
                    + "&funstr=sign&TASK_NO=" + TASK_NO + "&OP_NO=" + acacheUserBean.getOP_NO() + "&SIGNIMG=" + photoSaveName
                    + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            beginTime();

                            Audit_picture_GridView.setAdapter(auditGridViewAdapter);
                            AuditLists.removeAll(AuditLists);
                            auditGridViewAdapter.notifyDataSetChanged();
                            volley_GetData4();
                            Toast.makeText(AuditActivity.this, "签名成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    //审核
    private void audit(String str1) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=checklocktask&Task_no="
                    + TASK_NO + "&check_result=" + str1
                    + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            signature();
                            Toast.makeText(AuditActivity.this, "审核成功", Toast.LENGTH_SHORT).show();
                            setBackResult();
                            finish();
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    //审核
    private void audit2(String str1) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=checklocktask&Task_no="
                    + TASK_NO + "&check_result=" + str1
                    + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            Toast.makeText(AuditActivity.this, "审核成功", Toast.LENGTH_SHORT).show();
                            setBackResult();
                            finish();
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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


    private void volley_GetData4() {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreuserfun&funstr=getlist&TASK_NO=" + TASK_NO + "&user_context=" + UserContext;

            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        Log.e("ARG0", String.valueOf(arg0));
                        JSONArray array = arg0.getJSONArray("LOCK_TASKOPLIST");
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                audit = new Audit();
                                audit.setDataUserName(object.getString("OP_NAME"));
                                audit.setDataUserNumber(object.getString("OP_NO"));
                                audit.setSIGN(object.getString("SIGN"));
                                audit.setTASK_NO(object.getString("TASK_NO"));
                                audit.setSIGNIMG(object.getString("SIGNIMG"));
                                AuditLists.add(audit);
                            }
                            removeHashSet(AuditLists);
                            Audit_picture_GridView.setAdapter(auditGridViewAdapter);
                            auditGridViewAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
                    try {
                        dialog.dismiss();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //通过判断签名状况，如果是最后一个人签名，就把当前时间确认为实际开始时间
    //工作负责人最后签字，如果不是工作负责人签字，每次都要上传签字信息，每签一次字上传一次，最后一个签字的人就是任务开始时间
    private void beginTime() {
        Log.e(TAG + "--R_OP_NO", R_OP_NO);//工作负责人
        Log.e(TAG + "-RET_OP_NO", RET_OP_NO);//派工人
        Log.e(TAG + "--OP_NO", acacheUserBean.getOP_NO());
        if (R_OP_NO.equals(acacheUserBean.getOP_NO()) || RET_OP_NO.equals(acacheUserBean.getOP_NO())) {

        } else {
            //获取系统时间
            DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间

            Log.e(TAG + "--date--", fmt.format(curDate));
            Log.e(TAG + "--date--", fmt.format(curDate).substring(0, fmt.format(curDate).indexOf(" ")));
            Log.e(TAG + "--time--", fmt.format(curDate).substring(fmt.format(curDate).indexOf(" ") + 1, fmt.format(curDate).length()));

            String Date = fmt.format(curDate).substring(0, fmt.format(curDate).indexOf(" "));
            String Time = fmt.format(curDate).substring(fmt.format(curDate).indexOf(" ") + 1, fmt.format(curDate).length());


            Audit_actual_beginDate.setText(Date);
            Audit_actual_beginTime.setText(Time);

            updateDateTime(Date + "%20" + Time,
                    Audit_actual_endDate.getText().toString().trim() + "%20" + Audit_actual_endTime.getText().toString().trim());
        }

    }


    //去掉重复对象
    public static void removeHashSet1(List<SendOrderList> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getDataUserNumber().equals(list.get(i).getDataUserNumber())) {
                    list.remove(j);
                }
            }
        }
    }

    //去掉重复对象
    public static void removeHashSet(List<Audit> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getDataUserNumber().equals(list.get(i).getDataUserNumber())) {
                    list.remove(j);
                }
            }
        }
    }


    public void showPicturePicker(String ImageViewStr) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.photo_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AuditActivity.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.show();
        TextView searchDialog_title = (TextView) layout.findViewById(R.id.dialog_title);
        searchDialog_title.setText(R.string.photo_dialog_title);
        //绑定控件
        Button photo_dialog1 = (Button) layout.findViewById(R.id.photo_dialog1);
        Button photo_dialog2 = (Button) layout.findViewById(R.id.photo_dialog2);
        Button photo_dialog3 = (Button) layout.findViewById(R.id.photo_dialog3);
        Button photo_dialog4 = (Button) layout.findViewById(R.id.photo_dialog4);
        Button photo_dialog5 = (Button) layout.findViewById(R.id.photo_dialog5);
        Button photo_dialog6 = (Button) layout.findViewById(R.id.photo_dialog6);
        Button photo_button = (Button) layout.findViewById(R.id.photo_button);
        photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (ImageViewStr == "2") {
            photo_dialog1.setVisibility(View.GONE);
            photo_dialog2.setVisibility(View.GONE);
            photo_dialog3.setVisibility(View.VISIBLE);
            photo_dialog4.setVisibility(View.VISIBLE);
            photo_dialog5.setVisibility(View.GONE);
            photo_dialog6.setVisibility(View.GONE);
        } else if (ImageViewStr == "3") {
            photo_dialog1.setVisibility(View.GONE);
            photo_dialog2.setVisibility(View.GONE);
            photo_dialog3.setVisibility(View.GONE);
            photo_dialog4.setVisibility(View.GONE);
            photo_dialog5.setVisibility(View.VISIBLE);
            photo_dialog6.setVisibility(View.VISIBLE);
        }


        photo_dialog1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri1 = mActivityUtils.getUri(AuditActivity.this, new File(Environment.getExternalStorageDirectory(), "image.png"));
//                Uri imageUri1 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.png"));
                alertDialog.dismiss();
                //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1);
                startActivityForResult(openCameraIntent1, 0);
            }
        });

        photo_dialog2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAlbumIntent1 = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent1.setType("image/*");
                alertDialog.dismiss();
                startActivityForResult(openAlbumIntent1, 1);
            }
        });

        photo_dialog3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri2 = mActivityUtils.getUri(AuditActivity.this, new File(Environment.getExternalStorageDirectory(), "image.png"));
//                Uri imageUri2 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.png"));
                alertDialog.dismiss();
                //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
                startActivityForResult(openCameraIntent2, 2);
            }
        });

        photo_dialog4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAlbumIntent2 = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent2.setType("image/*");
                alertDialog.dismiss();
                startActivityForResult(openAlbumIntent2, 3);
            }
        });

        photo_dialog5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri3 = mActivityUtils.getUri(AuditActivity.this, new File(Environment.getExternalStorageDirectory(), "image.png"));
//                Uri imageUri3 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.png"));
                alertDialog.dismiss();
                //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent3.putExtra(MediaStore.EXTRA_OUTPUT, imageUri3);
                startActivityForResult(openCameraIntent3, 4);
            }
        });

        photo_dialog6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAlbumIntent3 = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent3.setType("image/*");
                alertDialog.dismiss();
                startActivityForResult(openAlbumIntent3, 5);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                //将保存在本地的图片取出并缩小后显示在界面上
                Bitmap bitmap1 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.png");
                Bitmap newBitmap1 = ImageTools1.zoomBitmap(bitmap1, bitmap1.getWidth() / SCALE, bitmap1.getHeight() / SCALE);
                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                bitmap1.recycle();

                //将处理过的图片显示在界面上，并保存到本地
                relativeLayout_001_ImageView1.setImageBitmap(newBitmap1);
                ImageTools1.savePhotoToSDCard(newBitmap1, photoSavePath, WorkImg1PhotoSaveName);
                relativeLayout_001_TextView2.setText(WorkImg1PhotoSaveName);
                ImageViewPostUrl1(photoSavePath, WorkImg1PhotoSaveName);

                break;

            case 1:
                if (data != null) {
                    ContentResolver resolver1 = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri1 = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver1, originalUri1);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap1 = ImageTools1.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();
                            relativeLayout_001_ImageView1.setImageBitmap(smallBitmap1);
                            ImageTools1.savePhotoToSDCard(smallBitmap1, photoSavePath, WorkImg1PhotoSaveName);
                            relativeLayout_001_TextView2.setText(WorkImg1PhotoSaveName);
                            ImageViewPostUrl1(photoSavePath, WorkImg1PhotoSaveName);
                        } else {
                            return;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }

                break;
            case 2:
                //将保存在本地的图片取出并缩小后显示在界面上
                Bitmap bitmap2 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.png");
                Bitmap newBitmap2 = ImageTools1.zoomBitmap(bitmap2, bitmap2.getWidth() / SCALE, bitmap2.getHeight() / SCALE);
                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                bitmap2.recycle();

                //将处理过的图片显示在界面上，并保存到本地
                relativeLayout_002_ImageView1.setImageBitmap(newBitmap2);
                ImageTools1.savePhotoToSDCard(newBitmap2, photoSavePath, WorkImg2PhotoSaveName);
                relativeLayout_002_TextView2.setText(WorkImg2PhotoSaveName);

                ImageViewPostUrl2(photoSavePath, WorkImg2PhotoSaveName);
                break;
            case 3:
                if (data != null) {
                    ContentResolver resolver2 = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri2 = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo2 = MediaStore.Images.Media.getBitmap(resolver2, originalUri2);
                        if (photo2 != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap2 = ImageTools1.zoomBitmap(photo2, photo2.getWidth() / SCALE, photo2.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo2.recycle();
                            relativeLayout_002_ImageView1.setImageBitmap(smallBitmap2);
                            ImageTools1.savePhotoToSDCard(smallBitmap2, photoSavePath, WorkImg2PhotoSaveName);
                            relativeLayout_002_TextView2.setText(WorkImg2PhotoSaveName);
                            ImageViewPostUrl2(photoSavePath, WorkImg2PhotoSaveName);
                        } else {
                            return;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 4:
                //将保存在本地的图片取出并缩小后显示在界面上
                Bitmap bitmap3 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.png");
                Bitmap newBitmap3 = ImageTools1.zoomBitmap(bitmap3, bitmap3.getWidth() / SCALE, bitmap3.getHeight() / SCALE);
                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                bitmap3.recycle();

                //将处理过的图片显示在界面上，并保存到本地
                relativeLayout_003_ImageView1.setImageBitmap(newBitmap3);
                ImageTools1.savePhotoToSDCard(newBitmap3, photoSavePath, WorkImg3PhotoSaveName);
                relativeLayout_002_TextView2.setText(WorkImg3PhotoSaveName);
                ImageViewPostUrl3(photoSavePath, WorkImg3PhotoSaveName);
                break;
            case 5:
                if (data != null) {
                    ContentResolver resolver3 = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri3 = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo3 = MediaStore.Images.Media.getBitmap(resolver3, originalUri3);
                        if (photo3 != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools1.zoomBitmap(photo3, photo3.getWidth() / SCALE, photo3.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo3.recycle();
                            relativeLayout_003_ImageView1.setImageBitmap(smallBitmap);
                            ImageTools1.savePhotoToSDCard(smallBitmap, photoSavePath, WorkImg3PhotoSaveName);
                            relativeLayout_002_TextView2.setText(WorkImg3PhotoSaveName);
                            ImageViewPostUrl3(photoSavePath, WorkImg3PhotoSaveName);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void ImageViewPostUrl1(final String photoSavePath, final String photoSaveName1) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postImageHead = new PostImageHead();
                try {
                    String result = postImageHead.run(HttpServerAddress.UPLOADFILE + "&USER_CONTEXT=" + UserContext, photoSavePath + photoSaveName1);
                    Log.e("result", result);
                    if (result.equalsIgnoreCase("true")) {
                        signature1(photoSaveName1);
                    } else {
                        Toast.makeText(AuditActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ImageViewPostUrl2(final String photoSavePath, final String photoSaveName2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postImageHead = new PostImageHead();
                try {
                    String result = postImageHead.run(HttpServerAddress.UPLOADFILE + "&USER_CONTEXT=" + UserContext, photoSavePath + photoSaveName2);
                    Log.e("result", result);
                    if (result.equalsIgnoreCase("true")) {
                        signature2(photoSaveName2);
                    } else {
                        Toast.makeText(AuditActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void ImageViewPostUrl3(final String photoSavePath, final String photoSaveName3) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postImageHead = new PostImageHead();
                try {
                    String result = postImageHead.run(HttpServerAddress.UPLOADFILE + "&USER_CONTEXT=" + UserContext, photoSavePath + photoSaveName3);
                    Log.e("result", result);
                    if (result.equalsIgnoreCase("true")) {
                        signature3(photoSaveName3);
                    } else {
                        Toast.makeText(AuditActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void signature1(String photoSaveName1) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreimgfun"
                    + "&funstr=update1&task_no=" + TASK_NO + "&workimg=" + photoSaveName1
                    + "&imgno=1" + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            Toast.makeText(AuditActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    private void signature2(String photoSaveName2) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreimgfun"
                    + "&funstr=update1&task_no=" + TASK_NO
                    + "&workimg=" + photoSaveName2 + "&imgno=2"
                    + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            Toast.makeText(AuditActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    private void signature3(String photoSaveName3) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreimgfun"
                    + "&funstr=update1&TASK_NO=" + TASK_NO
                    + "&imgno=3"
                    + "&workimg=" + photoSaveName3 + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            Toast.makeText(AuditActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                            //将最后上传照片的时间定为实际结束时间
                            //获取系统时间
                            DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间

                            Log.e(TAG + "--date--", fmt.format(curDate));
                            Log.e(TAG + "--date--", fmt.format(curDate).substring(0, fmt.format(curDate).indexOf(" ")));
                            Log.e(TAG + "--time--", fmt.format(curDate).substring(fmt.format(curDate).indexOf(" ") + 1, fmt.format(curDate).length()));

                            String Date = fmt.format(curDate).substring(0, fmt.format(curDate).indexOf(" "));
                            String Time = fmt.format(curDate).substring(fmt.format(curDate).indexOf(" ") + 1, fmt.format(curDate).length());

                            Audit_actual_endDate.setText(Date);
                            Audit_actual_endTime.setText(Time);
                            //开始时间从页面获取，结束时间获取系统时间
                            updateDateTime(Audit_actual_beginDate.getText() + "%20" + Audit_actual_beginTime.getText(),
                                    Audit_actual_endDate.getText().toString().trim() + "%20" + Audit_actual_endTime.getText().toString().trim());
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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


    //更新日期时间
    private void updateDateTime(String beginTime, String endTime) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreinfofun"
                    + "&funstr=updatedatetime&task_no=" + TASK_NO
                    + "&W_BEGINTIME=" + beginTime
                    + "&W_ENDTIME=" + endTime
                    + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            Log.e(TAG + "--upDT--", "时间更新成功");
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    //更新备注
    private void updateDemo(String w_demo) {
        try {
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final String URL = HttpServerAddress.BASE_URL + "?m=taskmoreinfofun"
                    + "&funstr=updatedemo&task_no=" + TASK_NO
                    + "&W_DEMO=" + w_demo
                    + "&user_context=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.d("TAG", String.valueOf(arg0));
                    try {
                        if (arg0.getString("result").equals("true")) {
                            Toast.makeText(AuditActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
//                            note_button.setVisibility(View.INVISIBLE);
                            note_button.setText("更新备注");
                        } else {
                            Toast.makeText(AuditActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
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

    private void setBackResult() {
        setResult(Activity.RESULT_OK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mian, menu);
        return true;
    }


    // 绑定手机返回键按钮
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
