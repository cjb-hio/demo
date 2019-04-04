package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by 19428 on 2016/11/8.
 */
public class ModifyLockFileActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private ImageView common_tv_back;
    private ImageView lock_rename_numberID;
    private TextView common_tv_title;
    private TextView modifyLockFile_lockId;
    private EditText modifyLockFile_name, modifyLockFile_registerAddress, modifyLockFile_number;
    private Spinner modifyLockFile_type;
    private Button modifyLockFile_complete;
    private LoadingDialog dialog;
    private ACache aCache;
    private AcacheUserBean aCacheUserBean;
    private String UserContext;
    private ArrayAdapter<String> arr_adapter;
    private ArrayList<String> typeList = new ArrayList<>();
    private String lockId, lockName, lockAddress, lockNumber, lockType;

    //NFC 蓝牙相关
    private Ntag_I2C_Demo demo;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Boolean newIntent = false;
    private DemoApplication demoApplication;
    //Sensor管理器
    private SensorManager mSensorManager = null;
    //震动
    private Vibrator mVibrator = null;
    private LockUtil mLockUtil;
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
//                            modifyLockFile_lockId.setText(mLockUtil.bytes2HexString(lockId));
                        }
                    } else {
                        String lid = (String) msg.obj;
//                        modifyLockFile_lockId.setText(lid);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifylockfile);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        mLockUtil = LockUtil.getInstance();

        initView();
        // 获取缓存数据
        aCache = ACache.get(this);
        aCacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        aCacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        UserContext = aCacheUserBean.getUSER_CONTEXT().trim();

        if (demoApplication.getMS() == 0) {
            setNfcForeground();
            // 判断是否有Nfc功能
            checkNFC();
        } else {
            initBluetooth();
        }
    }

    private void initBluetooth() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }


    private final static int REQ_CODE_SET_MS = 4;

    private void checkNFC() {
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                new AlertDialog.Builder(this).setTitle("NFC关闭了").setMessage("去设置")
                        .setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (android.os.Build.VERSION.SDK_INT >= 16) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                                } else {
                                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ModifyLockFileActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }
        } else {
            new AlertDialog.Builder(this).setTitle("没有可用的NFC。去切换当前模式。")
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ModifyLockFileActivity.this.finish();
                        }
                    }).setPositiveButton("前往", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(ModifyLockFileActivity.this, SetMsActivity.class), REQ_CODE_SET_MS);
                }
            }).setCancelable(false).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SET_MS) {
            if (resultCode == SetMsActivity.REQ_RES_SET_MS) {
                String result = data.getStringExtra(SetMsActivity.SET_MS_RESULT);
                if (result.equals("true")) {
                    initBluetooth();
                } else if (result.equals("false")) {
                    ModifyLockFileActivity.this.finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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

    //蓝牙相关
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14) {
                mVibrator.vibrate(100);
                readIdBegin();
                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
            }
        }
    }

    private void readIdBegin() {
        if (demoApplication.getConnect() == 1) {
            demoApplication.ReadIdBegin(handler);
        } else {
            Toast.makeText(ModifyLockFileActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void initView() {
        common_tv_back = (ImageView) findViewById(R.id.common_tv_back);
        common_tv_back.setVisibility(View.VISIBLE);
        common_tv_back.setOnClickListener(this);
        common_tv_title = (TextView) findViewById(R.id.common_tv_title);
        common_tv_title.setText("修改档案");

        modifyLockFile_lockId = (TextView) findViewById(R.id.modifyLockFile_lockId);
        modifyLockFile_name = (EditText) findViewById(R.id.modifyLockFile_name);
        modifyLockFile_registerAddress = (EditText) findViewById(R.id.modifyLockFile_registerAddress);
        modifyLockFile_number = (EditText) findViewById(R.id.modifyLockFile_number);
        modifyLockFile_complete = (Button) findViewById(R.id.modifyLockFile_complete);
        modifyLockFile_complete.setOnClickListener(this);
        lock_rename_numberID = (ImageView) findViewById(R.id.lock_rename_numberID);
        lock_rename_numberID.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            lockId = intent.getStringExtra("LockID");
            modifyLockFile_lockId.setText(lockId);
            lockName = intent.getStringExtra("LockName");
            modifyLockFile_name.setText(lockName);
            lockAddress = intent.getStringExtra("LockAddress");
            modifyLockFile_registerAddress.setText(lockAddress);
            lockNumber = intent.getStringExtra("LockNumber");
            modifyLockFile_number.setText(lockNumber);
            lockType = intent.getStringExtra("LockType");
        }

        modifyLockFile_type = (Spinner) findViewById(R.id.modifyLockFile_type);
        // 给下拉框添加数据
        typeList.add("表箱");
        typeList.add("变压器室");
        typeList.add("台变配电室");
        typeList.add("台变计量箱");
        // 适配器
        arr_adapter = new ArrayAdapter<>(this, R.layout.permiss_spinner_item, typeList);
        // 设置样式
//        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 加载适配器
        modifyLockFile_type.setAdapter(arr_adapter);
        int type = 0;
        if (lockType.equals("表箱")) {
            type = 0;
        } else if (lockType.equals("变压器室")) {
            type = 1;
        } else if (lockType.equals("台变配电室")) {
            type = 2;
        } else if (lockType.equals("台变计量箱")) {
            type = 3;
        }
        modifyLockFile_type.setSelection(type);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_tv_back:
                finish();
                break;
            case R.id.modifyLockFile_complete:
                volley_post();
                break;
            case R.id.lock_rename_numberID:
                Intent intent = new Intent(ModifyLockFileActivity.this, PDALockRegistersdChildMeterActivity.class);
                intent.putExtra("ID", lockId);
                intent.putExtra("LockType", "修改" + modifyLockFile_type.getSelectedItem().toString().trim());
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    // 向服务端发起锁注册请求
    private void volley_post() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(ModifyLockFileActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            lockType = modifyLockFile_type.getSelectedItem().toString().trim();
            if (lockType.equals("表箱")) {
                lockType = "1";
            } else if (lockType.equals("变压器室")) {
                lockType = "2";
            } else if (lockType.equals("台变配电室")) {
                lockType = "3";
            } else if (lockType.equals("台变计量箱")) {
                lockType = "4";
            }

            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);


            // 提交的参数数据

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=insertlockinfo&funstr=modify&LID=" + modifyLockFile_lockId.getText().toString().trim()
                    + "&L_NAME=" + URLEncoder.encode(modifyLockFile_name.getText().toString().trim(), "UTF-8")
                    + "&L_ADDR=" + URLEncoder.encode(modifyLockFile_registerAddress.getText().toString().trim(), "UTF-8")
                    + "&L_GPS_X=" + "0" + "&L_GPS_Y=" + "0"
                    + "&L_BOX_NO=" + modifyLockFile_number.getText().toString().trim()
                    + "&L_BOX_TYPE=" + URLEncoder.encode(lockType, "UTF-8") + "&USER_CONTEXT=" + UserContext;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");

                        if (arg0.getString("result").equals("true")) {
//                            Intent intent = new Intent(ModifyLockFileActivity.this, LockFileActivity.class);
//                            intent.putExtra("LockID", modifyLockFile_lockId.getText().toString().trim());
//                            startActivity(intent);
                            setIntentResult();
                            finish();
                            Toast.makeText(ModifyLockFileActivity.this, "所档案修改成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ModifyLockFileActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        try {
                            Toast.makeText(ModifyLockFileActivity.this, arg0.getString("result"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
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

    private void setIntentResult() {
        setResult(Activity.RESULT_OK);
    }
}
