package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.xyw.smartlock.nfc.Ntag_I2C_Demo;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.view.LayoutItem1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;


public class LockFileActivity extends AppCompatActivity implements OnClickListener, SensorEventListener {

    private static final String TAG = "LockFileActivity";

    private View fl_cancel_bg;
    private LayoutItem1 lockfile_number_relativelayout, lockfile_address, lockfile_linearlayout, lockfile_village_address;
    private TextView title;
    private ImageView backImage, lockfile_search;
    private String latitude, longitude, lockID, Used, lockName, lockAdress, lockfile_lockState_str;
    private LayoutItem1 lockfile_lockId, lockfile_lockname, lockfile_registAddress, lockfile_type,
            lockfile_areaname, lockfile_operator, lockfile_dateTime, lockfile_lockState;
    private Button lock_cancellation1, lock_cancellation2, lock_delete;
    private String strL_boxType, Lock_keyState;
    //提取缓存参数
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private String personNumber;

    private AlertDialog alertDialog;
    private TextView lockFile_title;
    private EditText lockFile_Id;
    private String lockFileID;
    private Button lockFile_button1, lockFile_button2;
    private String strState;
    //NFC
    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private String areaID;
    private String areaNumber;
    private String strResult;
    private DemoApplication demoApplication;
    private LoadingDialog mLoadingDialog;
    //摇一摇
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;
    private ImageView bluebg;
    private boolean btimgbg = true;

    private LockUtil mLockUtil;
    //使用Handler更新UI
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if (demoApplication.getMS() == 2) {
                        if (msg.obj != null) {
                            byte[] lock_data = (byte[]) msg.obj;
                            byte[] lockId = new byte[8];
                            for (int i = 0; i < 8; i++) {
                                lockId[i] = lock_data[i];
                            }
                            bluebg.setImageResource(R.mipmap.bluet);
                            lockID = mLockUtil.bytes2HexString(lockId);
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~lockID=" + lockID + "~~~~~~~~~~~~~~~~~~~~~");
                            if (lockfile_lockState.getTextView2Text().toString().trim().equals("正使用") || lockfile_lockState.getTextView2Text().toString().trim().equals("--")) {
                                lockfile_lockId.setTextView2Text(lockID);
                                volley_post();
                            } else {
                                ToastUtil.MyToast(LockFileActivity.this, "请在另外的页面读取ID");
                            }
                        }
                    } else {
                        String lid = (String) msg.obj;
                        System.out.println("lid=" + lid);
//                        bluebg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bluet));
                        bluebg.setImageResource(R.mipmap.bluet);
                        lockID = lid;
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~lockID=" + lockID + "~~~~~~~~~~~~~~~~~~~~~");
                        if (lockfile_lockState.getTextView2Text().toString().trim().equals("正使用") || lockfile_lockState.getTextView2Text().toString().trim().equals("--")) {
                            lockfile_lockId.setTextView2Text(lockID);
                            volley_post();
                        } else {
                            ToastUtil.MyToast(LockFileActivity.this, "请在另外的页面读取ID");
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };
    private BroadcastReceiver upbtimg = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            bluebg.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
                    bluebg.setVisibility(View.VISIBLE);
                    bluebg.setImageResource(R.mipmap.bluet);
                    break;
                case "UPBTIMG_DIS":
                    bluebg.setVisibility(View.VISIBLE);
                    bluebg.setImageResource(R.mipmap.bluef);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockfile);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        mLockUtil = LockUtil.getInstance();
        registerReceiver(upbtimg, new IntentFilter("UPBTIMG"));
        bluebg = (ImageView) findViewById(R.id.bluebg);
        // 判断是否有Nfc功能
        if (demoApplication.getMS() == 0) {
            setNfcForeground();
            mAdapter = NfcAdapter.getDefaultAdapter(this);
        } else {
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            bluebg.setVisibility(View.VISIBLE);
            bluebg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //demoApplication.connect();
                    if (demoApplication.getConnect() == 1) {

                    } else {
                        if (btimgbg) {
                            demoApplication.connect();
                            btimgbg = false;
                        }
                    }
                }
            });
            if (demoApplication.getConnect() == 1) {
                bluebg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bluet));
            } else {
                bluebg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bluef));
            }
        }
        //获取从锁注册页面得到的值
        Intent intent = getIntent();
        lockID = intent.getStringExtra("LockID");
        if ((intent.getStringExtra("Used") != null)) {
            if (intent.getStringExtra("Used").equals("1")) {
                Used = "已注销";
            } else if (intent.getStringExtra("Used").equals("0")) {
                Used = "正使用";
            }
        } else {
            Used = "正使用";
            if (demoApplication.getMS() == 2) {
                if (demoApplication.getConnect() == 1) {
                    demoApplication.ReadIdBegin(handler);
                } else {
                    Toast.makeText(LockFileActivity.this, "未连接蓝牙设备", Toast.LENGTH_SHORT).show();
                }
            }
        }
        lockfile_lockId = (LayoutItem1) findViewById(R.id.lockfile_lockId);
        lockfile_lockId.setTextView2Text(lockID);
        System.out.println("Used=" + Used);
        // 获取缓存数据
        aCache = ACache.get(this);
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        personNumber = acacheUserBean.getUSER_CONTEXT();
        areaID = acacheUserBean.getArea_id();
        System.out.println("personNumber=" + personNumber);
        // 初始化控件
        initview();
    }

    /**
     * 初始标题栏和返回按钮
     */
    private void initview() {
        fl_cancel_bg = findViewById(R.id.fl_cancel_bg);
        fl_cancel_bg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        lockfile_lockId = (LayoutItem1) findViewById(R.id.lockfile_lockId);
        // 设置标题栏
        title = (TextView) findViewById(R.id.lockfile_tv_title);
        title.setText(R.string.lockfileinfo);

        //搜索按钮点击事件
        lockfile_search = (ImageView) findViewById(R.id.lockfile_search);
        lockfile_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog();

            }
        });

        // 设置返回按钮
        backImage = (ImageView) findViewById(R.id.lockfile_title_back);
        backImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 关闭Activity
                LockFileActivity.this.finish();
            }
        });

        lock_cancellation1 = (Button) findViewById(R.id.lock_cancellation1);
        lock_cancellation2 = (Button) findViewById(R.id.lock_cancellation2);
        lock_delete = (Button) findViewById(R.id.lock_delete);
        //绑定控件
        lockfile_lockname = (LayoutItem1) findViewById(R.id.lockfile_lockname);
        lockfile_registAddress = (LayoutItem1) findViewById(R.id.lockfile_registAddress);
        //点击进入百度地图定位
        lockfile_address = (LayoutItem1) findViewById(R.id.lockfile_address);
        lockfile_lockState = (LayoutItem1) findViewById(R.id.lockfile_lockState);
        lockfile_number_relativelayout = (LayoutItem1) findViewById(R.id.lockfile_number_relativelayout);
        lockfile_type = (LayoutItem1) findViewById(R.id.lockfile_type);
        lockfile_areaname = (LayoutItem1) findViewById(R.id.lockfile_areaname);
        lockfile_linearlayout = (LayoutItem1) findViewById(R.id.lockfile_linearlayout);
        lockfile_village_address = (LayoutItem1) findViewById(R.id.lockfile_village_address);
        lockfile_operator = (LayoutItem1) findViewById(R.id.lockfile_operator);
        lockfile_dateTime = (LayoutItem1) findViewById(R.id.lockfile_dateTime);

        mLoadingDialog = new LoadingDialog(LockFileActivity.this, R.style.dailogStyle);
        mLoadingDialog.setCanceledOnTouchOutside(false);

        Judge();
    }

    private void Judge() {
        lockfile_lockState_str = lockfile_lockState.getTextView2Text().toString().trim();
        //判断当前锁的状态，如果是“正使用”，没有影响，如果是“已注销”，就只显示，无法点击
        if (lockfile_lockState_str.equals("已注销")) {
            fl_cancel_bg.setVisibility(View.VISIBLE);
            lock_cancellation1.setVisibility(View.GONE);
            lock_cancellation2.setVisibility(View.GONE);
            lock_delete.setVisibility(View.GONE);
            initInfo();
        } else {
            fl_cancel_bg.setVisibility(View.GONE);
            lockfile_address.setOnClickListener(this);
            lockfile_number_relativelayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    lockfile_lockState_str = lockfile_lockState.getTextView2Text().toString().trim();
                    if (lockfile_lockState_str.equals("正使用")) {
                        Intent intent = new Intent(LockFileActivity.this, LockFileChildMeter.class);
                        intent.putExtra("lockfile_type", lockfile_type.getTextView2Text().toString().trim());
                        intent.putExtra("ID", lockfile_lockId.getTextView2Text().toString().trim());
                        startActivity(intent);
                    } else if (lockfile_lockState_str.equals("已注销")) {

                    }
                }
            });
            lockfile_linearlayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    lockfile_lockState_str = lockfile_lockState.getTextView2Text().toString().trim();
                    if (lockfile_lockState_str.equals("正使用")) {
                        if (lockfile_linearlayout.getTextView2Text().toString().trim().equals("未下装")) {
                            if (areaID.equals(areaNumber)) {
                                if (ActivityUtils.getInstance().isNetworkAvailable(LockFileActivity.this)) {
                                    startActivityForResult(new Intent(LockFileActivity.this, KeyStateActivity.class), REQ_CODE_KEY_STATE);
                                } else {
                                    Toast.makeText(LockFileActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                ToastUtil.MyToast(LockFileActivity.this, "您没有权限下装密钥，请修改您的权限！");
                            }
                        } else if (lockfile_linearlayout.getTextView2Text().toString().trim().equals("下装成功")) {
                            ToastUtil.MyToast(LockFileActivity.this, "秘钥已下装成功");
                        }
                    } else if (lockfile_lockState_str.equals("已注销")) {

                    }
                }
            });
            lockfile_village_address.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LockFileActivity.this, VillageAddressMapActivity.class));
                }
            });

            lockfile_lockState.setTextView2Text(Used);
            lock_cancellation2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!lockfile_lockId.getTextView2Text().toString().trim().equals("")) {
                        Intent intent = new Intent(LockFileActivity.this, ModifyLockFileActivity.class);
                        intent.putExtra("LockID", lockfile_lockId.getTextView2Text().toString().trim());
                        intent.putExtra("LockName", lockfile_lockname.getTextView2Text().toString().trim());
                        intent.putExtra("LockAddress", lockfile_registAddress.getTextView2Text().toString().trim());
                        intent.putExtra("LockNumber", lockfile_number_relativelayout.getTextView2Text().toString().trim());
                        intent.putExtra("LockType", lockfile_type.getTextView2Text().toString().trim());
                        startActivityForResult(intent, REQ_CODE_MODIFY_LOCK_FILE);
                    } else {
                        Toast.makeText(LockFileActivity.this, "锁信息错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            lock_cancellation1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击弹出对话框
                    cancellationLock();
                }
            });
            lock_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteLock();
                }
            });
            //从服务端获取数据
            volley_post();
        }
    }

    private void deleteLock() {
        try {
            mLoadingDialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=DeleteLockInfo&lid=" + lockID + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        mLoadingDialog.dismiss();
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;

                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_INFO");
                        System.out.println("array=" + array);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mLoadingDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), "请检查网络");
                    if (alertDialog != null)
                        alertDialog.dismiss();
                    mLoadingDialog.dismiss();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse arg0) {
                    try {
                        mLoadingDialog.dismiss();
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

    private final static int REQ_CODE_KEY_STATE = 1;
    private final static int REQ_CODE_MODIFY_LOCK_FILE = 2;

    //点击注销锁，弹出对话框，询问是否注销锁
    private void cancellationLock() {
        new AlertDialog.Builder(this)
                .setMessage("你确定要注销锁吗？这个过程不可逆转。")// 设置显示的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 添加确定按钮

                    @Override
                    public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件
                        if (lockfile_lockId.getTextView2Text().toString().trim().length() != 16) {
                            Toast.makeText(LockFileActivity.this, "锁ID错误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cancellationPost();
                    }

                }).setNegativeButton("取消", null).show();// 在按键响应事件中显示此对话框
    }

    //把绑定的控件封装起来
    private void initInfo() {
        volley_post();
    }

    //当锁ID不存在时，在再所有界面填充--
    private void noFindID() {
        lockfile_lockId.setTextView2Text(lockID);
        lockfile_lockname.setTextView2Text("--");
        lockfile_registAddress.setTextView2Text("--");
        lockfile_number_relativelayout.setTextView2Text("--");
        lockfile_type.setTextView2Text("--");
        lockfile_areaname.setTextView2Text("--");
        lockfile_linearlayout.setTextView2Text("--");
        lockfile_operator.setTextView2Text("--");
        lockfile_dateTime.setTextView2Text("--");
        lockfile_lockState.setTextView2Text("--");
    }


    // 通过网络从服务端获取锁注册信息
    private void volley_post() {
        try {
            mLoadingDialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=GetLockInfo&lid=" + lockID + "&Lname=" + lockName
                    + "&laddr=" + lockAdress + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        mLoadingDialog.dismiss();
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;

                        // json数据解析
                        JSONArray array = strState.getJSONArray("LOCK_INFO");
                        System.out.println("array=" + array);
                        //判断ID是否存在
                        if (array.length() == 0) {
                            noFindID();
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                System.out.println("object" + object);
                                String str = "LID:" + object.getString("LID") + "L_NAME:" + object.getString("L_NAME")
                                        + "L_ADDR:" + object.getString("L_ADDR") + "L_GPS_X:" + object.getString("L_GPS_X")
                                        + "L_GPS_Y:" + object.getString("L_GPS_Y") + "L_CREATE_DT:" + object.getString("L_CREATE_DT")
                                        + "L_CREATE_OP:" + object.getString("L_CREATE_OP") + "L_BOX_NO:" + object.getString("L_BOX_NO")
                                        + "L_BOX_TYPE:" + object.getString("L_BOX_TYPE") + "KEY_VER:" + object.getString("KEY_VER")
                                        + "ZONE_NO:" + object.getString("ZONE_NO") + "ZONE_NAME:" + object.getString("ZONE_NAME")
                                        + "OP_NAME:" + object.getString("OP_NAME") + "PASSNUM:" + object.getString("PASSNUM");

                                if ((object.getString("USED")).equals("1")) {
                                    lockfile_lockState.setTextView2Text("已注销");
                                    fl_cancel_bg.setVisibility(View.VISIBLE);
                                    lock_cancellation1.setVisibility(View.GONE);
                                    lock_cancellation2.setVisibility(View.GONE);
                                    lock_delete.setVisibility(View.GONE);
                                } else if ((object.getString("USED")).equals("0")) {
                                    lockfile_lockState.setTextView2Text("正使用");
                                }
                                System.out.println("str=" + str);
                                areaNumber = object.getString("ZONE_NO").toString().trim();
                                latitude = object.getString("L_GPS_Y").toString().trim();
                                longitude = object.getString("L_GPS_X").toString().trim();
                                Log.e("TAG", latitude);
                                Log.e("TAG", longitude);
                                lockfile_lockId.setTextView2Text(object.getString("LID").toString().trim());
                                lockfile_lockname.setTextView2Text(object.getString("L_NAME").toString().trim());
                                lockfile_registAddress.setTextView2Text(object.getString("L_ADDR").toString().trim());
                                lockfile_number_relativelayout.setTextView2Text(object.getString("L_BOX_NO").toString().trim());
                                if (object.getString("L_BOX_TYPE").trim().equals("1")) {
                                    strL_boxType = "表箱";
                                } else if (object.getString("L_BOX_TYPE").trim().equals("2")) {
                                    strL_boxType = "变压器室";
                                } else if (object.getString("L_BOX_TYPE").trim().equals("3")) {
                                    strL_boxType = "台变配电室";
                                } else if (object.getString("L_BOX_TYPE").trim().equals("4")) {
                                    strL_boxType = "台变计量箱";
                                } else {
                                    strL_boxType = object.getString("L_BOX_TYPE");
                                }
                                lockfile_type.setTextView2Text(strL_boxType);
                                lockfile_areaname.setTextView2Text(object.getString("ZONE_NAME").toString().trim());
                                if (object.getString("KEY_VER").trim().equals("0")) {
                                    Lock_keyState = "未下装";
                                    lockfile_linearlayout.setTextView2Text(Lock_keyState);
                                    lockfile_linearlayout.setTextView2TextColor(Color.RED);
                                } else if (object.getString("KEY_VER").trim().equals("1")) {
                                    Lock_keyState = "下装成功";
                                    lockfile_linearlayout.setTextView2Text("下装成功");
                                    lockfile_linearlayout.setTextView2TextColor(Color.GREEN);
                                }
                                lockfile_operator.setTextView2Text((object.getString("L_CREATE_OP").toString().trim()) + "(" + object.getString("OP_NAME") + ")");
                                lockfile_dateTime.setTextView2Text(object.getString("L_CREATE_DT").toString().trim());
                            }
                        }
                    } catch (Exception e) {
                        mLoadingDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), "请检查网络");
                    if (alertDialog != null)
                        alertDialog.dismiss();
                    mLoadingDialog.dismiss();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse arg0) {
                    try {
                        mLoadingDialog.dismiss();
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.lockfile_address:
                lockfile_lockState_str = lockfile_lockState.getTextView2Text().toString().trim();
                if (lockfile_lockState_str.equals("正使用")) {
                    Intent intent = null;
                    try {// 如果有安装百度地图 就启动百度地图
                        StringBuffer sbs = new StringBuffer();
                        sbs.append(
                                "intent://map/geocoder?location=" + latitude + "," + longitude + "&coord_type=gcj02&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        try {
//                            intent = Intent.getIntent(sbs.toString());
                            //上面方法已过时，尝试使用这个方法
                            intent = Intent.parseUri(sbs.toString(), Intent.URI_ANDROID_APP_SCHEME);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    } catch (Exception e) {// 没有百度地图则弹出网页端
                        StringBuffer sb = new StringBuffer();
                        sb.append("http://api.map.baidu.com/geocoder?location=" + latitude + "," + longitude + "&coord_type=gcj02&output=html");
//                        Uri uri = Uri.parse(sb.toString());
//                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            intent = Intent.parseUri(sb.toString(), Intent.URI_INTENT_SCHEME);
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                        startActivity(intent);
                    }
                } else if (lockfile_lockState_str.equals("已注销")) {
                }
                break;
            default:
                break;
        }
    }

    private void alertDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.lockfile_customdialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(LockFileActivity.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.show();
        lockFile_title = (TextView) layout.findViewById(R.id.lockFile_title);
        lockFile_title.setText(R.string.enter_lockFileSearch);
        //绑定控件
        lockFile_Id = (EditText) layout.findViewById(R.id.lockFile_Id);
        lockFile_button1 = (Button) layout.findViewById(R.id.lockFile_button1);
        lockFile_button2 = (Button) layout.findViewById(R.id.lockFile_button2);
        lockFile_button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到文本框里面的值
//                lockFileID = lockFile_Id.getText().toString().trim();
                lockFileID = getWriteId(lockFile_Id.getText().toString().trim());

                if (lockFileID.length() != 16) {
                    ToastUtil.MyToast(LockFileActivity.this, "锁ID必须是16位,请重新输入锁ID");
                } else {
                    lockID = lockFileID.toString();
                    volley_post();
                }
                alertDialog.dismiss();
            }
        });
        lockFile_button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private String getWriteId(String lid) {
        String wid = lid;
        if (TextUtils.isEmpty(wid)) {
            Toast.makeText(this, "值不能为空", Toast.LENGTH_SHORT).show();
            return "";
        }
        while (wid.length() < 16) {
            StringBuffer id = new StringBuffer();
            id.append("0").append(wid);//左补0
            wid = id.toString();
        }
        return wid;
    }

    //通过NFC功能来读取锁ID,并且请求数据,显示锁信息
    //Nfc功能
    public void setNfcForeground() {
        // Create a generic PendingIntent that will be delivered to this
        // activity. The NFC stack will fill
        // in the intent with the details of the discovered tag before
        // delivering it to this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all NDEF based dispatches
        mFilters = new IntentFilter[]{
                // new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};

        // Setup a tech list for all NFC tags
        mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
        if (demoApplication.getMS() == 1) {
            if (null == mSensorManager)
                mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            if (mAdapter != null) {
                mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
            }

            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) && newIntent == false) {
                // give the UI some time to load, then execute the Demo
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onNewIntent(getIntent());
                    }
                }, 100);
            }
            newIntent = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (demoApplication.getMS() == 1) {
            mSensorManager.unregisterListener(this);
        } else {
            if (mAdapter != null && newIntent == false) {
                mAdapter.disableForegroundDispatch(this);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent nfc_intent) {
        newIntent = true;
        super.onNewIntent(nfc_intent);
        // Set the pattern for vibration
        long pattern[] = {0, 100};

        // Vibrate on new Intent
        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
        doProcess(nfc_intent);
    }

    public void doProcess(Intent nfc_intent) {
        final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        demo = new Ntag_I2C_Demo(tag, this);
        demo.ReadIdBegin(handler);
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * <p/>
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_KEY_STATE) {
                String keyState = data.getStringExtra("keyState");// 得到新Activity 关闭后返回的数据
                System.out.println("keyState=" + keyState);
                if (keyState.equals("下装成功")) {
                    //得到缓存中的令牌信息
                    AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                    personNumber = LoginInfo.getUSER_CONTEXT().toString();
                    String lid = data.getStringExtra("Lid");
                    lockfile_lockId.setTextView2Text(lid);
                    //得到界面上的ID值
                    lockID = lockfile_lockId.getTextView2Text().toString().trim();
                    setlockkeyver_post();
                } else {
                    ToastUtil.MyToast(LockFileActivity.this, "下装失败,请重新下装");
                }
            } else if (requestCode == REQ_CODE_MODIFY_LOCK_FILE) {
                volley_post();
                setIntentResult();
            }
        }
//        if (data != null) {
//            Intent intent = getIntent();
//            String keyState = data.getStringExtra("keyState");// 得到新Activity 关闭后返回的数据
//            String lid = data.getStringExtra("Lid");
//            lockfile_lockId = (TextView) findViewById(R.id.lockfile_lockId);
//            lockfile_lockId.setText(lid);
//            System.out.println("keyState=" + keyState);
//            if (keyState.equals("下装成功")) {
//                //得到缓存中的令牌信息
//                AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
//                personNumber = LoginInfo.getUSER_CONTEXT().toString();
//                //得到界面上的ID值
//                lockfile_lockId = (TextView) findViewById(R.id.lockfile_lockId);
//                lockID = lockfile_lockId.getText().toString().trim();
//                setlockkeyver_post();
//            } else {
//                ToastUtil.MyToast(LockFileActivity.this, "下装失败,请重新下装");
//            }
//
//        }
    }


    //下装秘钥成功时向后台发送数据
    private void setlockkeyver_post() {
        Log.e(TAG, "setlockkeyver_post: 下装秘钥成功时向后台发送数据");
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL3 = HttpServerAddress.BASE_URL + "?m=setlockkeyver&lid=" + lockID + "&KEYVER="
                    + "1" + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL3);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL3, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            try {
                                System.out.println("arg0=" + arg0);
                                strResult = arg0.toString();
                                Log.d("TAG", arg0.toString());
                                if ((arg0.getString("result")).equals("true")) {
                                    initInfo();
                                    //更新下载状态成功后返回数据
                                    setIntentResult();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(LockFileActivity.this, "信息上传失败");
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
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14) {
                mVibrator.vibrate(100);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
                if (demoApplication.getConnect() == 1) {
                    demoApplication.ReadIdBegin(handler);
                    bluebg.setImageResource(R.mipmap.blueconnect);
                } else {
                    Toast.makeText(LockFileActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //当点击注销按钮的时候想后台发送消息。
    private void cancellationPost() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.服务器网址
            final String URL3 = HttpServerAddress.BASE_URL + "?m=updatelockinfoused&lid=" + lockID + "&used="
                    + "1" + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL3);
            // 3.json get请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL3, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            System.out.println("arg0=" + arg0);
                            strResult = arg0.toString();
                            Log.d("TAG", arg0.toString());
                            try {
                                String strState = arg0.getString("result");
                                if (strState.equals("true")) {
                                    volley_post();
                                    setIntentResult();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(LockFileActivity.this, "信息上传失败");
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

    private void setIntentResult() {
        Intent intent = new Intent();
        LockFileActivity.this.setResult(Activity.RESULT_OK, intent);
    }

    // 绑定手机返回键按钮
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            setIntentResult();
//            // 关闭Activity
//            LockFileActivity.this.finish();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(upbtimg);
        super.onDestroy();
    }
}
