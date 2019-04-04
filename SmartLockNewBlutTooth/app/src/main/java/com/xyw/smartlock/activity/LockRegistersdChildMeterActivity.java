package com.xyw.smartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.MeterId;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.zxing.qrcodactivity.QrcodeActivityCapture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LockRegistersdChildMeterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = "LockRegistersdChildMete";

    //二维码扫描
    private final static int SCANNIN_GREQUEST_CODE1 = 1;
    private final static int SCANNIN_GREQUEST_CODE2 = 2;
    private final static int SCANNIN_GREQUEST_CODE3 = 3;
    private final static int SCANNIN_GREQUEST_CODE4 = 4;
    private final static int SCANNIN_GREQUEST_CODE5 = 5;
    private final static int SCANNIN_GREQUEST_CODE6 = 6;
    private final static int SCANNIN_GREQUEST_CODE7 = 7;
    private final static int SCANNIN_GREQUEST_CODE8 = 8;
    private final static int SCANNIN_GREQUEST_CODE9 = 9;
    private final static int SCANNIN_GREQUEST_CODE10 = 10;
    private final static int SCANNIN_GREQUEST_CODE11 = 11;
    private final static int SCANNIN_GREQUEST_CODE12 = 12;
    private final static int SCANNIN_GREQUEST_CODE13 = 13;
    private final static int SCANNIN_GREQUEST_CODE14 = 14;
    private final static int SCANNIN_GREQUEST_CODE15 = 15;
    private final static int SCANNIN_GREQUEST_CODE16 = 16;
    private final static int SCANNIN_GREQUEST_CODE17 = 17;
    private final static int SCANNIN_GREQUEST_CODE18 = 18;
    private final static int SCANNIN_GREQUEST_CODE19 = 19;
    private final static int SCANNIN_GREQUEST_CODE20 = 20;
    private final static int SCANNIN_GREQUEST_CODE21 = 21;
    private final static int SCANNIN_GREQUEST_CODE22 = 22;
    private final static int SCANNIN_GREQUEST_CODE23 = 23;
    private final static int SCANNIN_GREQUEST_CODE24 = 24;
    private final static int SCANNIN_GREQUEST_CODE25 = 25;
    private final static int SCANNIN_GREQUEST_CODE26 = 26;
    private final static int SCANNIN_GREQUEST_CODE27 = 27;
    private final static int SCANNIN_GREQUEST_CODE28 = 28;
    private final static int SCANNIN_GREQUEST_CODE29 = 29;
    private final static int SCANNIN_GREQUEST_CODE30 = 30;


    private EditText childMeterOne, childMeterTwo, childMeterThree, childMeterFour, childMeterFive,
            childMeterSix, childMeterServen, childMeterEight, childMeterNine, childMeterTen,
            childMeterEle, childMeterTwe, childMeterThi, childMeterFou, childMeterFif;
    private EditText childMeterSixt, childMeterSev, childMeterEig, childMeterNin, childMeterTwen,
            childMeterTone, childMeterTtwo, childMeterTthr, childMeterTfou, childMeterTfiv,
            childMeterTsix, childMeterTsev, childMeterTeig, childMeterTnin, childMeterThir;
    private TextView title;
    private Button childMeter_button;
    private ImageView ImageBack, imageview_qrcode_button;
    private String lockID, qrcode_one, qrcode_two, qrcode_three, qrcode_four, qrcode_five, qrcode_six, qrcode_seven,
            qrcode_eight, qrcode_nine, qrcode_ten, qrcode_ele, qrcode_twe, qrcode_thi, qrcode_fou, qrcode_fif,
            qrcode_sixt, qrcode_sev, qrcode_eig, qrcode_nin, qrcode_twen, qrcode_tone, qrcode_ttwo, qrcode_tthr,
            qrcode_tfou, qrcode_tfiv, qrcode_tsix, qrcode_tsev, qrcode_teig, qrcode_tnin, qrcode_thir;
    private String strResult;
    //请求网络的等待界面
    private LoadingDialog dialog;
    private AcacheUserBean LoginInfo;
    private ACache aCache;
    //令牌
    private String personNumber;
    //    private String strResult, strState;
    private int qrcodeOnFocus = 1;
    //    private String equipmentNumber,ID;
    private String lockType;
    private int type = 1;
    private static final int REGISTER_METER = 1;
    private static final int UPDATE_METER = 2;

    private InputMethodManager imm;
    //sqlite 操作
    private DateBaseUtil dateBaseUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockregisteredchildmeter);
        getSupportActionBar().hide();
        // 获取缓存数据
        aCache = ACache.get(this);
        LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        // 读取缓存数据
        personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();

        dateBaseUtil = new DateBaseUtil(LockRegistersdChildMeterActivity.this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //初始化控件
        initview();
    }

    //初始化控件
    private void initview() {
        title = (TextView) findViewById(R.id.lockregistersdchildMeter_tv_title);
        Intent intent = getIntent();
        if (null != intent) {
            lockID = intent.getStringExtra("ID");
            lockType = intent.getStringExtra("LockType");
            //设置标题栏
            title.setText(lockType);
            if (lockType.contains("修改")) {
                type = UPDATE_METER;
            } else {
                type = REGISTER_METER;
            }
        }

        dialog = new LoadingDialog(LockRegistersdChildMeterActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);

        //设置返回按钮
        ImageBack = (ImageView) findViewById(R.id.lockregistersdchildMeter_title_back);
        ImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //二维码按钮
        imageview_qrcode_button = (ImageView) findViewById(R.id.imageview_qrcode_button);
        imageview_qrcode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        //设置完成按钮
        childMeter_button = (Button) findViewById(R.id.lockqecode_button);
        if (type == REGISTER_METER) {
            childMeter_button.setText(R.string.complete);
        } else if (type == UPDATE_METER) {
            childMeter_button.setText(R.string.submit);
        }
        childMeter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLast()) {
                    if (ActivityUtils.getInstance().isNetworkAvailable(LockRegistersdChildMeterActivity.this)) {
                        if (checkData()) {
                            map = null;
                            if (type == REGISTER_METER) {
                                volley_post();
                            } else if (type == UPDATE_METER){
//                                volley_update();
                                volley_post();
                            }
                        } else {
                            Toast.makeText(LockRegistersdChildMeterActivity.this, "存在相同数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LockRegistersdChildMeterActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
//                            insertSqlite();
                    }
                }
            }
        });
        childMeterOne = (EditText) findViewById(R.id.childMeterOne);
        childMeterTwo = (EditText) findViewById(R.id.childMeterTwo);
        childMeterThree = (EditText) findViewById(R.id.childMeterThree);
        childMeterFour = (EditText) findViewById(R.id.childMeterFour);
        childMeterFive = (EditText) findViewById(R.id.childMeterFive);
        childMeterSix = (EditText) findViewById(R.id.childMeterSix);
        childMeterServen = (EditText) findViewById(R.id.childMeterServen);
        childMeterEight = (EditText) findViewById(R.id.childMeterEight);
        childMeterNine = (EditText) findViewById(R.id.childMeterNine);
        childMeterTen = (EditText) findViewById(R.id.childMeterTen);
        childMeterEle = (EditText) findViewById(R.id.childMeterEle);
        childMeterTwe = (EditText) findViewById(R.id.childMeterTwe);
        childMeterThi = (EditText) findViewById(R.id.childMeterThi);
        childMeterFou = (EditText) findViewById(R.id.childMeterFou);
        childMeterFif = (EditText) findViewById(R.id.childMeterFif);
        childMeterOne.setOnFocusChangeListener(this);
        childMeterTwo.setOnFocusChangeListener(this);
        childMeterThree.setOnFocusChangeListener(this);
        childMeterFour.setOnFocusChangeListener(this);
        childMeterFive.setOnFocusChangeListener(this);
        childMeterSix.setOnFocusChangeListener(this);
        childMeterServen.setOnFocusChangeListener(this);
        childMeterEight.setOnFocusChangeListener(this);
        childMeterNine.setOnFocusChangeListener(this);
        childMeterTen.setOnFocusChangeListener(this);
        childMeterEle.setOnFocusChangeListener(this);
        childMeterTwe.setOnFocusChangeListener(this);
        childMeterThi.setOnFocusChangeListener(this);
        childMeterFou.setOnFocusChangeListener(this);
        childMeterFif.setOnFocusChangeListener(this);
        initview2();
        if (type == UPDATE_METER) {
            if (ActivityUtils.getInstance().isNetworkAvailable(LockRegistersdChildMeterActivity.this)) {
                vooley_get();
            }else {
                Toast.makeText(LockRegistersdChildMeterActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
            }
        } else if (type == REGISTER_METER) {
//            querySqlite();
            vooley_get();
        }
    }

    private void initview2() {
        childMeterSixt = (EditText) findViewById(R.id.childMeterSixt);
        childMeterSev = (EditText) findViewById(R.id.childMeterSev);
        childMeterEig = (EditText) findViewById(R.id.childMeterEig);
        childMeterNin = (EditText) findViewById(R.id.childMeterNin);
        childMeterTwen = (EditText) findViewById(R.id.childMeterTwen);
        childMeterTone = (EditText) findViewById(R.id.childMeterTone);
        childMeterTtwo = (EditText) findViewById(R.id.childMeterTtwo);
        childMeterTthr = (EditText) findViewById(R.id.childMeterTthr);
        childMeterTfou = (EditText) findViewById(R.id.childMeterTfou);
        childMeterTfiv = (EditText) findViewById(R.id.childMeterTfiv);
        childMeterTsix = (EditText) findViewById(R.id.childMeterTsix);
        childMeterTsev = (EditText) findViewById(R.id.childMeterTsev);
        childMeterTeig = (EditText) findViewById(R.id.childMeterTeig);
        childMeterTnin = (EditText) findViewById(R.id.childMeterTnin);
        childMeterThir = (EditText) findViewById(R.id.childMeterThir);
        childMeterSixt.setOnFocusChangeListener(this);
        childMeterSev.setOnFocusChangeListener(this);
        childMeterEig.setOnFocusChangeListener(this);
        childMeterNin.setOnFocusChangeListener(this);
        childMeterTwen.setOnFocusChangeListener(this);
        childMeterTone.setOnFocusChangeListener(this);
        childMeterTtwo.setOnFocusChangeListener(this);
        childMeterTthr.setOnFocusChangeListener(this);
        childMeterTfou.setOnFocusChangeListener(this);
        childMeterTfiv.setOnFocusChangeListener(this);
        childMeterTsix.setOnFocusChangeListener(this);
        childMeterTsev.setOnFocusChangeListener(this);
        childMeterTeig.setOnFocusChangeListener(this);
        childMeterTnin.setOnFocusChangeListener(this);
        childMeterThir.setOnFocusChangeListener(this);
    }

    private Map<String, String> map;

    private boolean checkData() {
        String meter1 = childMeterOne.getText().toString().trim();
        String meter2 = childMeterTwo.getText().toString().trim();
        String meter3 = childMeterThree.getText().toString().trim();
        String meter4 = childMeterFour.getText().toString().trim();
        String meter5 = childMeterFive.getText().toString().trim();
        String meter6 = childMeterSix.getText().toString().trim();
        String meter7 = childMeterServen.getText().toString().trim();
        String meter8 = childMeterEight.getText().toString().trim();
        String meter9 = childMeterNine.getText().toString().trim();
        String meter10 = childMeterTen.getText().toString().trim();
        String meter11 = childMeterEle.getText().toString().trim();
        String meter12 = childMeterTwe.getText().toString().trim();
        String meter13 = childMeterThi.getText().toString().trim();
        String meter14 = childMeterFou.getText().toString().trim();
        String meter15 = childMeterFif.getText().toString().trim();

        String meter16 = childMeterSixt.getText().toString().trim();
        String meter17 = childMeterSev.getText().toString().trim();
        String meter18 = childMeterEig.getText().toString().trim();
        String meter19 = childMeterNin.getText().toString().trim();
        String meter20 = childMeterTwen.getText().toString().trim();
        String meter21 = childMeterTone.getText().toString().trim();
        String meter22 = childMeterTtwo.getText().toString().trim();
        String meter23 = childMeterTthr.getText().toString().trim();
        String meter24 = childMeterTfou.getText().toString().trim();
        String meter25 = childMeterTfiv.getText().toString().trim();
        String meter26 = childMeterTsix.getText().toString().trim();
        String meter27 = childMeterTsev.getText().toString().trim();
        String meter28 = childMeterTeig.getText().toString().trim();
        String meter29 = childMeterTnin.getText().toString().trim();
        String meter30 = childMeterThir.getText().toString().trim();
        map = new HashMap<>();
        map.put("meter1", meter1);
        map.put("meter2", meter2);
        map.put("meter3", meter3);
        map.put("meter4", meter4);
        map.put("meter5", meter5);
        map.put("meter6", meter6);
        map.put("meter7", meter7);
        map.put("meter8", meter8);
        map.put("meter9", meter9);
        map.put("meter10", meter10);
        map.put("meter11", meter11);
        map.put("meter12", meter12);
        map.put("meter13", meter13);
        map.put("meter14", meter14);
        map.put("meter15", meter15);
        map.put("meter16", meter16);
        map.put("meter17", meter17);
        map.put("meter18", meter18);
        map.put("meter19", meter19);
        map.put("meter20", meter20);
        map.put("meter21", meter21);
        map.put("meter22", meter22);
        map.put("meter23", meter23);
        map.put("meter24", meter24);
        map.put("meter25", meter25);
        map.put("meter26", meter26);
        map.put("meter27", meter27);
        map.put("meter28", meter28);
        map.put("meter29", meter29);
        map.put("meter30", meter30);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (null == value || value.equals(""))
                continue;
            if (!key.equals("meter1") && meter1.equals(value)) {
                return false;
            }
            if (key.equals("meter1"))
                continue;
            if (!key.equals("meter2") && meter2.equals(value)) {
                return false;
            }
            if (key.equals("meter2"))
                continue;
            if (!key.equals("meter3") && meter3.equals(value)) {
                return false;
            }
            if (key.equals("meter3"))
                continue;
            if (!key.equals("meter4") && meter4.equals(value)) {
                return false;
            }
            if (key.equals("meter4"))
                continue;
            if (!key.equals("meter5") && meter5.equals(value)) {
                return false;
            }
            if (key.equals("meter5"))
                continue;
            if (!key.equals("meter6") && meter6.equals(value)) {
                return false;
            }
            if (key.equals("meter6"))
                continue;
            if (!key.equals("meter7") && meter7.equals(value)) {
                return false;
            }
            if (key.equals("meter7"))
                continue;
            if (!key.equals("meter8") && meter8.equals(value)) {
                return false;
            }
            if (key.equals("meter8"))
                continue;
            if (!key.equals("meter9") && meter9.equals(value)) {
                return false;
            }
            if (key.equals("meter9"))
                continue;
            if (!key.equals("meter10") && meter10.equals(value)) {
                return false;
            }
            if (key.equals("meter10"))
                continue;
            if (!key.equals("meter11") && meter11.equals(value)) {
                return false;
            }
            if (key.equals("meter11"))
                continue;
            if (!key.equals("meter12") && meter12.equals(value)) {
                return false;
            }
            if (key.equals("meter12"))
                continue;
            if (!key.equals("meter13") && meter13.equals(value)) {
                return false;
            }
            if (key.equals("meter13"))
                continue;
            if (!key.equals("meter14") && meter14.equals(value)) {
                return false;
            }
            if (key.equals("meter14"))
                continue;
            if (!key.equals("meter15") && meter15.equals(value)) {
                return false;
            }
            if (key.equals("meter15"))
                continue;
            if (!key.equals("meter16") && meter16.equals(value)) {
                return false;
            }
            if (key.equals("meter16"))
                continue;
            if (!key.equals("meter17") && meter17.equals(value)) {
                return false;
            }
            if (key.equals("meter17"))
                continue;
            if (!key.equals("meter18") && meter18.equals(value)) {
                return false;
            }
            if (key.equals("meter18"))
                continue;
            if (!key.equals("meter19") && meter19.equals(value)) {
                return false;
            }
            if (key.equals("meter19"))
                continue;
            if (!key.equals("meter20") && meter20.equals(value)) {
                return false;
            }
            if (key.equals("meter20"))
                continue;
            if (!key.equals("meter21") && meter21.equals(value)) {
                return false;
            }
            if (key.equals("meter21"))
                continue;
            if (!key.equals("meter22") && meter22.equals(value)) {
                return false;
            }
            if (key.equals("meter22"))
                continue;
            if (!key.equals("meter23") && meter23.equals(value)) {
                return false;
            }
            if (key.equals("meter23"))
                continue;
            if (!key.equals("meter24") && meter24.equals(value)) {
                return false;
            }
            if (key.equals("meter24"))
                continue;
            if (!key.equals("meter25") && meter25.equals(value)) {
                return false;
            }
            if (key.equals("meter25"))
                continue;
            if (!key.equals("meter26") && meter26.equals(value)) {
                return false;
            }
            if (key.equals("meter26"))
                continue;
            if (!key.equals("meter27") && meter27.equals(value)) {
                return false;
            }
            if (key.equals("meter27"))
                continue;
            if (!key.equals("meter28") && meter28.equals(value)) {
                return false;
            }
            if (key.equals("meter28"))
                continue;
            if (!key.equals("meter29") && meter29.equals(value)) {
                return false;
            }
            if (key.equals("meter29"))
                continue;
            if (!key.equals("meter30") && meter30.equals(value)) {
                return false;
            }
            if (key.equals("meter30"))
                continue;
        }
        return true;
    }

    /**
     * 无网络情况下缓存数据
     */
    private void insertSqlite() {
        if (lockID != null) {
            MeterId meterId = new MeterId.Builder()
                    .Lid(lockID)
                    .meter1(childMeterOne.getText().toString().trim())
                    .meter2(childMeterTwo.getText().toString().trim())
                    .meter3(childMeterThree.getText().toString().trim())
                    .meter4(childMeterFour.getText().toString().trim())
                    .meter5(childMeterFive.getText().toString().trim())
                    .meter6(childMeterSix.getText().toString().trim())
                    .meter7(childMeterServen.getText().toString().trim())
                    .meter8(childMeterEight.getText().toString().trim())
                    .meter9(childMeterNine.getText().toString().trim())
                    .meter10(childMeterTen.getText().toString().trim())
                    .meter11(childMeterEle.getText().toString().trim())
                    .meter12(childMeterTwe.getText().toString().trim())
                    .meter13(childMeterThi.getText().toString().trim())
                    .meter14(childMeterFou.getText().toString().trim())
                    .meter15(childMeterFif.getText().toString().trim())
                    .meter16(childMeterSixt.getText().toString().trim())
                    .meter17(childMeterSev.getText().toString().trim())
                    .meter18(childMeterEig.getText().toString().trim())
                    .meter19(childMeterNin.getText().toString().trim())
                    .meter20(childMeterTwen.getText().toString().trim())
                    .meter21(childMeterTone.getText().toString().trim())
                    .meter22(childMeterTtwo.getText().toString().trim())
                    .meter23(childMeterTthr.getText().toString().trim())
                    .meter24(childMeterTfou.getText().toString().trim())
                    .meter25(childMeterTfiv.getText().toString().trim())
                    .meter26(childMeterTsix.getText().toString().trim())
                    .meter27(childMeterTsev.getText().toString().trim())
                    .meter28(childMeterTeig.getText().toString().trim())
                    .meter29(childMeterTnin.getText().toString().trim())
                    .meter30(childMeterThir.getText().toString().trim())
                    .build();
            dateBaseUtil.insert4(meterId);
        }
    }

    /**
     * 查询sqlite数据库中缓存的数据
     */
    private void querySqlite() {
        if (null != lockID) {
            MeterId meterId = dateBaseUtil.queryMeterId(lockID);
            if (meterId != null) {
                childMeterOne.setText(meterId.getMeter1());
                childMeterTwo.setText(meterId.getMeter2());
                childMeterThree.setText(meterId.getMeter3());
                childMeterFour.setText(meterId.getMeter4());
                childMeterFive.setText(meterId.getMeter5());
                childMeterSix.setText(meterId.getMeter6());
                childMeterServen.setText(meterId.getMeter7());
                childMeterEight.setText(meterId.getMeter8());
                childMeterNine.setText(meterId.getMeter9());
                childMeterTen.setText(meterId.getMeter10());
                childMeterEle.setText(meterId.getMeter11());
                childMeterTwe.setText(meterId.getMeter12());
                childMeterThi.setText(meterId.getMeter13());
                childMeterFou.setText(meterId.getMeter14());
                childMeterFif.setText(meterId.getMeter15());
                childMeterSixt.setText(meterId.getMeter16());
                childMeterSev.setText(meterId.getMeter17());
                childMeterEig.setText(meterId.getMeter18());
                childMeterNin.setText(meterId.getMeter19());
                childMeterTwen.setText(meterId.getMeter20());
                childMeterTone.setText(meterId.getMeter21());
                childMeterTtwo.setText(meterId.getMeter22());
                childMeterTthr.setText(meterId.getMeter23());
                childMeterTfou.setText(meterId.getMeter24());
                childMeterTfiv.setText(meterId.getMeter25());
                childMeterTsix.setText(meterId.getMeter26());
                childMeterTsev.setText(meterId.getMeter27());
                childMeterTeig.setText(meterId.getMeter28());
                childMeterTnin.setText(meterId.getMeter29());
                childMeterThir.setText(meterId.getMeter30());
                addList(meterId.getMeter1());
                addList(meterId.getMeter2());
                addList(meterId.getMeter3());
                addList(meterId.getMeter4());
                addList(meterId.getMeter5());
                addList(meterId.getMeter6());
                addList(meterId.getMeter7());
                addList(meterId.getMeter8());
                addList(meterId.getMeter9());
                addList(meterId.getMeter10());
                addList(meterId.getMeter11());
                addList(meterId.getMeter12());
                addList(meterId.getMeter13());
                addList(meterId.getMeter14());
                addList(meterId.getMeter15());
                addList(meterId.getMeter16());
                addList(meterId.getMeter17());
                addList(meterId.getMeter18());
                addList(meterId.getMeter19());
                addList(meterId.getMeter20());
                addList(meterId.getMeter21());
                addList(meterId.getMeter22());
                addList(meterId.getMeter23());
                addList(meterId.getMeter24());
                addList(meterId.getMeter25());
                addList(meterId.getMeter26());
                addList(meterId.getMeter27());
                addList(meterId.getMeter28());
                addList(meterId.getMeter29());
                addList(meterId.getMeter30());
            }
        }
    }

    private void addList(String meterId) {
        if (meterId != null && !meterId.equals("") && !mScanList.contains(meterId)) {
            mScanList.add(meterId);
        }
    }

    private boolean checkLast() {
        String lastData = null;
        EditText et = null;
        if (qrcodeOnFocus == 1) {
            lastData = childMeterOne.getText().toString().trim();
            et = childMeterOne;
        } else if (qrcodeOnFocus == 2) {
            lastData = childMeterTwo.getText().toString().trim();
            et = childMeterTwo;
        } else if (qrcodeOnFocus == 3) {
            lastData = childMeterThree.getText().toString().trim();
            et = childMeterThree;
        } else if (qrcodeOnFocus == 4) {
            lastData = childMeterFour.getText().toString().trim();
            et = childMeterFour;
        } else if (qrcodeOnFocus == 5) {
            lastData = childMeterFive.getText().toString().trim();
            et = childMeterFive;
        } else if (qrcodeOnFocus == 6) {
            lastData = childMeterSix.getText().toString().trim();
            et = childMeterSix;
        } else if (qrcodeOnFocus == 7) {
            lastData = childMeterServen.getText().toString().trim();
            et = childMeterServen;
        } else if (qrcodeOnFocus == 8) {
            lastData = childMeterEight.getText().toString().trim();
            et = childMeterEight;
        } else if (qrcodeOnFocus == 9) {
            lastData = childMeterNine.getText().toString().trim();
            et = childMeterNine;
        } else if (qrcodeOnFocus == 10) {
            lastData = childMeterTen.getText().toString().trim();
            et = childMeterTen;
        } else if (qrcodeOnFocus == 11) {
            lastData = childMeterEle.getText().toString().trim();
            et = childMeterEle;
        } else if (qrcodeOnFocus == 12) {
            lastData = childMeterTwe.getText().toString().trim();
            et = childMeterTwe;
        } else if (qrcodeOnFocus == 13) {
            lastData = childMeterThi.getText().toString().trim();
            et = childMeterThi;
        } else if (qrcodeOnFocus == 14) {
            lastData = childMeterFou.getText().toString().trim();
            et = childMeterFou;
        } else if (qrcodeOnFocus == 15) {
            lastData = childMeterFif.getText().toString().trim();
            et = childMeterFif;
        } else if (qrcodeOnFocus == 16) {
            lastData = childMeterSixt.getText().toString().trim();
            et = childMeterSixt;
        } else if (qrcodeOnFocus == 17) {
            lastData = childMeterSev.getText().toString().trim();
            et = childMeterSev;
        } else if (qrcodeOnFocus == 18) {
            lastData = childMeterEig.getText().toString().trim();
            et = childMeterEig;
        } else if (qrcodeOnFocus == 19) {
            lastData = childMeterNin.getText().toString().trim();
            et = childMeterNin;
        } else if (qrcodeOnFocus == 20) {
            lastData = childMeterTwen.getText().toString().trim();
            et = childMeterTwen;
        } else if (qrcodeOnFocus == 21) {
            lastData = childMeterTone.getText().toString().trim();
            et = childMeterTone;
        } else if (qrcodeOnFocus == 22) {
            lastData = childMeterTtwo.getText().toString().trim();
            et = childMeterTtwo;
        } else if (qrcodeOnFocus == 23) {
            lastData = childMeterTthr.getText().toString().trim();
            et = childMeterTthr;
        } else if (qrcodeOnFocus == 24) {
            lastData = childMeterTfou.getText().toString().trim();
            et = childMeterTfou;
        } else if (qrcodeOnFocus == 25) {
            lastData = childMeterTfiv.getText().toString().trim();
            et = childMeterTfiv;
        } else if (qrcodeOnFocus == 26) {
            lastData = childMeterTsix.getText().toString().trim();
            et = childMeterTsix;
        } else if (qrcodeOnFocus == 27) {
            lastData = childMeterTsev.getText().toString().trim();
            et = childMeterTsev;
        } else if (qrcodeOnFocus == 28) {
            lastData = childMeterTeig.getText().toString().trim();
            et = childMeterTeig;
        } else if (qrcodeOnFocus == 29) {
            lastData = childMeterTnin.getText().toString().trim();
            et = childMeterTnin;
        } else if (qrcodeOnFocus == 30) {
            lastData = childMeterThir.getText().toString().trim();
            et = childMeterThir;
        }
        if (lastData != null && !lastData.equals("")) {
            if (mScanList.contains(lastData)) {
                changeFocus(lastData, et);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private List<String> mScanList = new ArrayList<>();
    private String meterOne, meterTwo, meterThree, meterFour, meterFive, meterSix, meterSeven, meterEight, meterNine, meterTen, meterEle, meterTwe, meterThi, meterFou, meterFif;
    private String meterSixt, meterSev, meterEig, meterNin, meterTwen, meterTone, meterTtwo, meterTthr, meterTfou, meterTfiv, meterTsix, meterTsev, meterTeig, meterTnin, meterThir;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            switch (requestCode) {
                case SCANNIN_GREQUEST_CODE1:
                    meterOne = bundle.getString("result");
                    if (!mScanList.contains(meterOne)) {
                        removeEdittext(childMeterOne);
                        //显示扫描到的内容
                        childMeterOne.setText(meterOne);
                        setEdittext(2, childMeterTwo);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(1, childMeterOne);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE2:
                    meterTwo = bundle.getString("result");
                    if (!mScanList.contains(meterTwo)) {
                        removeEdittext(childMeterTwo);
                        //显示扫描到的内容
                        childMeterTwo.setText(meterTwo);
                        setEdittext(3, childMeterThree);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(2, childMeterTwo);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE3:
                    meterThree = bundle.getString("result");
                    if (!mScanList.contains(meterThree)) {
                        removeEdittext(childMeterThree);
                        //显示扫描到的内容
                        childMeterThree.setText(meterThree);
                        setEdittext(4, childMeterFour);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(3, childMeterThree);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE4:
                    meterFour = bundle.getString("result");
                    if (!mScanList.contains(meterFour)) {
                        removeEdittext(childMeterFour);
                        //显示扫描到的内容
                        childMeterFour.setText(meterFour);
                        setEdittext(5, childMeterFive);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(4, childMeterFour);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE5:
                    meterFive = bundle.getString("result");
                    if (!mScanList.contains(meterFive)) {
                        removeEdittext(childMeterFive);
                        //显示扫描到的内容
                        childMeterFive.setText(meterFive);
                        setEdittext(6, childMeterSix);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(5, childMeterFive);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE6:
                    meterSix = bundle.getString("result");
                    if (!mScanList.contains(meterSix)) {
                        removeEdittext(childMeterSix);
                        //显示扫描到的内容
                        childMeterSix.setText(meterSix);
                        setEdittext(7, childMeterServen);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(6, childMeterSix);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE7:
                    meterSeven = bundle.getString("result");
                    if (!mScanList.contains(meterSeven)) {
                        removeEdittext(childMeterServen);
                        //显示扫描到的内容
                        childMeterServen.setText(meterSeven);
                        setEdittext(8, childMeterEight);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(7, childMeterServen);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE8:
                    meterEight = bundle.getString("result");
                    if (!mScanList.contains(meterEight)) {
                        removeEdittext(childMeterEight);
                        //显示扫描到的内容
                        childMeterEight.setText(meterEight);
                        setEdittext(9, childMeterNine);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(8, childMeterEight);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE9:
                    meterNine = bundle.getString("result");
                    if (!mScanList.contains(meterNine)) {
                        removeEdittext(childMeterNine);
                        //显示扫描到的内容
                        childMeterNine.setText(meterNine);
                        setEdittext(10, childMeterTen);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(9, childMeterNine);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE10:
                    meterTen = bundle.getString("result");
                    if (!mScanList.contains(meterTen)) {
                        removeEdittext(childMeterTen);
                        //显示扫描到的内容
                        childMeterTen.setText(meterTen);
                        setEdittext(11, childMeterEle);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(10, childMeterTen);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE11:
                    meterEle = bundle.getString("result");
                    if (!mScanList.contains(meterEle)) {
                        removeEdittext(childMeterEle);
                        //显示扫描到的内容
                        childMeterEle.setText(meterEle);
                        setEdittext(12, childMeterTwe);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(11, childMeterEle);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE12:
                    meterTwe = bundle.getString("result");
                    if (!mScanList.contains(meterTwe)) {
                        removeEdittext(childMeterTwe);
                        //显示扫描到的内容
                        childMeterTwe.setText(meterTwe);
                        setEdittext(13, childMeterThi);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(12, childMeterTwe);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE13:
                    meterThi = bundle.getString("result");
                    if (!mScanList.contains(meterThi)) {
                        removeEdittext(childMeterThi);
                        //显示扫描到的内容
                        childMeterThi.setText(meterThi);
                        setEdittext(14, childMeterFou);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(13, childMeterThi);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE14:
                    meterFou = bundle.getString("result");
                    if (!mScanList.contains(meterFou)) {
                        removeEdittext(childMeterFou);
                        //显示扫描到的内容
                        childMeterFou.setText(meterFou);
                        setEdittext(15, childMeterFif);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(14, childMeterFou);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE15:
                    meterFif = bundle.getString("result");
                    if (!mScanList.contains(meterFif)) {
                        removeEdittext(childMeterFif);
                        //显示扫描到的内容
                        childMeterFif.setText(meterFif);
                        setEdittext(16, childMeterSixt);
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(15, childMeterFif);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE16:
                    meterSixt = bundle.getString("result");
                    if (!mScanList.contains(meterSixt)) {
                        removeEdittext(childMeterSixt);
                        //显示扫描到的内容
                        childMeterSixt.setText(meterSixt);
                        setEdittext(17, childMeterSev);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(16, childMeterSixt);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE17:
                    meterSev = bundle.getString("result");
                    if (!mScanList.contains(meterSev)) {
                        removeEdittext(childMeterSev);
                        //显示扫描到的内容
                        childMeterSev.setText(meterSev);
                        setEdittext(18, childMeterEig);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(17, childMeterSev);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE18:
                    meterEig = bundle.getString("result");
                    if (!mScanList.contains(meterEig)) {
                        removeEdittext(childMeterEig);
                        //显示扫描到的内容
                        childMeterEig.setText(meterEig);
                        setEdittext(19, childMeterNin);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(18, childMeterEig);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE19:
                    meterNin = bundle.getString("result");
                    if (!mScanList.contains(meterNin)) {
                        removeEdittext(childMeterNin);
                        //显示扫描到的内容
                        childMeterNin.setText(meterNin);
                        setEdittext(20, childMeterTwen);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(19, childMeterNin);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE20:
                    meterTwen = bundle.getString("result");
                    if (!mScanList.contains(meterTwen)) {
                        removeEdittext(childMeterTwen);
                        //显示扫描到的内容
                        childMeterTwen.setText(meterTwen);
                        setEdittext(21, childMeterTone);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(20, childMeterTwen);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE21:
                    meterTone = bundle.getString("result");
                    if (!mScanList.contains(meterTone)) {
                        removeEdittext(childMeterTone);
                        //显示扫描到的内容
                        childMeterTone.setText(meterTone);
                        setEdittext(22, childMeterTtwo);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(21, childMeterTone);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE22:
                    meterTtwo = bundle.getString("result");
                    if (!mScanList.contains(meterTtwo)) {
                        removeEdittext(childMeterTtwo);
                        //显示扫描到的内容
                        childMeterTtwo.setText(meterTtwo);
                        setEdittext(23, childMeterTthr);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(22, childMeterTtwo);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE23:
                    meterTthr = bundle.getString("result");
                    if (!mScanList.contains(meterTthr)) {
                        removeEdittext(childMeterTthr);
                        //显示扫描到的内容
                        childMeterTthr.setText(meterTthr);
                        setEdittext(24, childMeterTfou);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(23, childMeterTthr);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE24:
                    meterTfou = bundle.getString("result");
                    if (!mScanList.contains(meterTfou)) {
                        removeEdittext(childMeterTfou);
                        //显示扫描到的内容
                        childMeterTfou.setText(meterTfou);
                        setEdittext(25, childMeterTfiv);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(24, childMeterTfou);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE25:
                    meterTfiv = bundle.getString("result");
                    if (!mScanList.contains(meterTfiv)) {
                        removeEdittext(childMeterTfiv);
                        //显示扫描到的内容
                        childMeterTfiv.setText(meterTfiv);
                        setEdittext(26, childMeterTsix);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(25, childMeterTfiv);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE26:
                    meterTsix = bundle.getString("result");
                    if (!mScanList.contains(meterTsix)) {
                        removeEdittext(childMeterTsix);
                        //显示扫描到的内容
                        childMeterTsix.setText(meterTsix);
                        setEdittext(27, childMeterTsev);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(26, childMeterTsix);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE27:
                    meterTsev = bundle.getString("result");
                    if (!mScanList.contains(meterTsev)) {
                        removeEdittext(childMeterTsev);
                        //显示扫描到的内容
                        childMeterTsev.setText(meterTsev);
                        setEdittext(28, childMeterThi);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(27, childMeterTsev);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE28:
                    meterTeig = bundle.getString("result");
                    if (!mScanList.contains(meterTeig)) {
                        removeEdittext(childMeterTeig);
                        //显示扫描到的内容
                        childMeterTeig.setText(meterTeig);
                        setEdittext(29, childMeterTnin);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(28, childMeterTeig);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE29:
                    meterTnin = bundle.getString("result");
                    if (!mScanList.contains(meterTnin)) {
                        removeEdittext(childMeterTnin);
                        //显示扫描到的内容
                        childMeterTnin.setText(meterTnin);
                        setEdittext(30, childMeterTthr);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(29, childMeterTnin);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE30:
                    meterThir = bundle.getString("result");
                    if (!mScanList.contains(meterThir)) {
                        removeEdittext(childMeterTthr);
                        //显示扫描到的内容
                        childMeterTthr.setText(meterThir);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(30, childMeterTthr);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setEdittext(int qrcode, EditText et) {
        edittextReuqesFocus(et);
        qrcodeOnFocus = qrcode;
        if (qrcode < 10) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    test();
                }
            }, 500);
        }
    }

    private void removeEdittext(EditText et) {
        if (!et.getText().toString().trim().equals("")) {
            if (mScanList.contains(et.getText().toString().trim()))
                mScanList.remove(et.getText().toString().trim());
        }
    }

    private void edittextReuqesFocus(final EditText et) {
        removeEdittext(et);
        et.setText("");
        requestFocus(et);
    }

    private void requestFocus(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        /*InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            Log.e(TAG, "edittextReuqesFocus: 软键盘未打开");
        }*/
        imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }

    private Handler mHandler = new Handler();

    private void changeFocus(String str1, final EditText et) {
        Log.e(TAG, "changeFocus: ");
        String str = et.getText().toString().trim();
        if (!str.equals("")) {
            if (!mScanList.contains(str)) {
                mScanList.add(str);
            } else {
                et.setText("");
                Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        clearAllFocus();
                        requestFocus(et);
                    }
                }, 200);
            }
        }
    }

    private void clearAllFocus() {
        childMeterOne.clearFocus();
        childMeterTwo.clearFocus();
        childMeterThree.clearFocus();
        childMeterFour.clearFocus();
        childMeterFive.clearFocus();
        childMeterSix.clearFocus();
        childMeterServen.clearFocus();
        childMeterEight.clearFocus();
        childMeterNine.clearFocus();
        childMeterTen.clearFocus();
        childMeterEle.clearFocus();
        childMeterTwe.clearFocus();
        childMeterThi.clearFocus();
        childMeterFou.clearFocus();
        childMeterFif.clearFocus();
        childMeterSixt.clearFocus();
        childMeterSev.clearFocus();
        childMeterEig.clearFocus();
        childMeterNin.clearFocus();
        childMeterTwen.clearFocus();
        childMeterTone.clearFocus();
        childMeterTtwo.clearFocus();
        childMeterTthr.clearFocus();
        childMeterTfou.clearFocus();
        childMeterTfiv.clearFocus();
        childMeterTsix.clearFocus();
        childMeterTsev.clearFocus();
        childMeterTeig.clearFocus();
        childMeterTnin.clearFocus();
        childMeterThir.clearFocus();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.childMeterOne:
                    removeEdittext(childMeterOne);
                    qrcodeOnFocus = 1;
                    break;
                case R.id.childMeterTwo:
                    removeEdittext(childMeterTwo);
                    qrcodeOnFocus = 2;
                    break;
                case R.id.childMeterThree:
                    removeEdittext(childMeterThree);
                    qrcodeOnFocus = 3;
                    break;
                case R.id.childMeterFour:
                    removeEdittext(childMeterFour);
                    qrcodeOnFocus = 4;
                    break;
                case R.id.childMeterFive:
                    removeEdittext(childMeterFive);
                    qrcodeOnFocus = 5;
                    break;
                case R.id.childMeterSix:
                    removeEdittext(childMeterSix);
                    qrcodeOnFocus = 6;
                    break;
                case R.id.childMeterServen:
                    removeEdittext(childMeterServen);
                    qrcodeOnFocus = 7;
                    break;
                case R.id.childMeterEight:
                    removeEdittext(childMeterEight);
                    qrcodeOnFocus = 8;
                    break;
                case R.id.childMeterNine:
                    removeEdittext(childMeterNine);
                    qrcodeOnFocus = 9;
                    break;
                case R.id.childMeterTen:
                    removeEdittext(childMeterTen);
                    qrcodeOnFocus = 10;
                    break;
                case R.id.childMeterEle:
                    removeEdittext(childMeterEle);
                    qrcodeOnFocus = 11;
                    break;
                case R.id.childMeterTwe:
                    removeEdittext(childMeterTwe);
                    qrcodeOnFocus = 12;
                    break;
                case R.id.childMeterThi:
                    removeEdittext(childMeterThi);
                    qrcodeOnFocus = 13;
                    break;
                case R.id.childMeterFou:
                    removeEdittext(childMeterFou);
                    qrcodeOnFocus = 14;
                    break;
                case R.id.childMeterFif:
                    removeEdittext(childMeterFif);
                    qrcodeOnFocus = 15;
                    break;
                case R.id.childMeterSixt:
                    removeEdittext(childMeterSixt);
                    qrcodeOnFocus = 16;
                    break;
                case R.id.childMeterSev:
                    removeEdittext(childMeterSev);
                    qrcodeOnFocus = 17;
                    break;
                case R.id.childMeterEig:
                    removeEdittext(childMeterEig);
                    qrcodeOnFocus = 18;
                    break;
                case R.id.childMeterNin:
                    removeEdittext(childMeterNin);
                    qrcodeOnFocus = 19;
                    break;
                case R.id.childMeterTwen:
                    removeEdittext(childMeterTwen);
                    qrcodeOnFocus = 20;
                    break;
                case R.id.childMeterTone:
                    removeEdittext(childMeterTone);
                    qrcodeOnFocus = 21;
                    break;
                case R.id.childMeterTtwo:
                    removeEdittext(childMeterTtwo);
                    qrcodeOnFocus = 22;
                    break;
                case R.id.childMeterTthr:
                    removeEdittext(childMeterTthr);
                    qrcodeOnFocus = 23;
                    break;
                case R.id.childMeterTfou:
                    removeEdittext(childMeterTfou);
                    qrcodeOnFocus = 24;
                    break;
                case R.id.childMeterTfiv:
                    removeEdittext(childMeterTfiv);
                    qrcodeOnFocus = 25;
                    break;
                case R.id.childMeterTsix:
                    removeEdittext(childMeterTsix);
                    qrcodeOnFocus = 26;
                    break;
                case R.id.childMeterTsev:
                    removeEdittext(childMeterTsev);
                    qrcodeOnFocus = 27;
                    break;
                case R.id.childMeterTeig:
                    removeEdittext(childMeterTeig);
                    qrcodeOnFocus = 28;
                    break;
                case R.id.childMeterTnin:
                    removeEdittext(childMeterTnin);
                    qrcodeOnFocus = 29;
                    break;
                case R.id.childMeterThir:
                    removeEdittext(childMeterTnin);
                    qrcodeOnFocus = 30;
                    break;
                default:
                    break;
            }
        } else {
            Log.e(TAG, "onFocusChange: ");
            switch (v.getId()) {
                case R.id.childMeterOne:
                    changeFocus(meterOne, childMeterOne);
                    break;
                case R.id.childMeterTwo:
                    changeFocus(meterTwo, childMeterTwo);
                    break;
                case R.id.childMeterThree:
                    changeFocus(meterThree, childMeterThree);
                    break;
                case R.id.childMeterFour:
                    changeFocus(meterFour, childMeterFour);
                    break;
                case R.id.childMeterFive:
                    changeFocus(meterFif, childMeterFive);
                    break;
                case R.id.childMeterSix:
                    changeFocus(meterSix, childMeterSix);
                    break;
                case R.id.childMeterServen:
                    changeFocus(meterSeven, childMeterServen);
                    break;
                case R.id.childMeterEight:
                    changeFocus(meterEight, childMeterEight);
                    break;
                case R.id.childMeterNine:
                    changeFocus(meterNine, childMeterNine);
                    break;
                case R.id.childMeterTen:
                    changeFocus(meterTen, childMeterTen);
                    break;
                case R.id.childMeterEle:
                    changeFocus(meterEle, childMeterEle);
                    break;
                case R.id.childMeterTwe:
                    changeFocus(meterTwe, childMeterTwe);
                    break;
                case R.id.childMeterThi:
                    changeFocus(meterThi, childMeterThi);
                    break;
                case R.id.childMeterFou:
                    changeFocus(meterFou, childMeterFou);
                    break;
                case R.id.childMeterFif:
                    changeFocus(meterFif, childMeterFif);
                    break;
                case R.id.childMeterSixt:
                    changeFocus(meterSixt, childMeterSixt);
                    break;
                case R.id.childMeterSev:
                    changeFocus(meterSev, childMeterSev);
                    break;
                case R.id.childMeterEig:
                    changeFocus(meterEig, childMeterEig);
                    break;
                case R.id.childMeterNin:
                    changeFocus(meterNin, childMeterNin);
                    break;
                case R.id.childMeterTwen:
                    changeFocus(meterTwen, childMeterTwen);
                    break;
                case R.id.childMeterTone:
                    changeFocus(meterTone, childMeterTone);
                    break;
                case R.id.childMeterTtwo:
                    changeFocus(meterTtwo, childMeterTtwo);
                    break;
                case R.id.childMeterTthr:
                    changeFocus(meterTthr, childMeterTthr);
                    break;
                case R.id.childMeterTfou:
                    changeFocus(meterTfou, childMeterTfou);
                    break;
                case R.id.childMeterTfiv:
                    changeFocus(meterTfiv, childMeterTfiv);
                    break;
                case R.id.childMeterTsix:
                    changeFocus(meterTsix, childMeterTsix);
                    break;
                case R.id.childMeterTsev:
                    changeFocus(meterTsix, childMeterTsev);
                    break;
                case R.id.childMeterTeig:
                    changeFocus(meterTeig, childMeterTeig);
                    break;
                case R.id.childMeterTnin:
                    changeFocus(meterTnin, childMeterTnin);
                    break;
                case R.id.childMeterThir:
                    changeFocus(meterThir, childMeterThir);
                    break;
                default:
                    break;
            }
        }
    }

    private void test() {
        //二维码扫描功能
        Intent intent = new Intent();
        intent.setClass(LockRegistersdChildMeterActivity.this, QrcodeActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (qrcodeOnFocus == 1) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE1);
        } else if (qrcodeOnFocus == 2) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE2);
        } else if (qrcodeOnFocus == 3) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE3);
        } else if (qrcodeOnFocus == 4) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE4);
        } else if (qrcodeOnFocus == 5) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE5);
        } else if (qrcodeOnFocus == 6) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE6);
        } else if (qrcodeOnFocus == 7) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE7);
        } else if (qrcodeOnFocus == 8) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE8);
        } else if (qrcodeOnFocus == 9) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE9);
        } else if (qrcodeOnFocus == 10) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE10);
        } else if (qrcodeOnFocus == 11) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE11);
        } else if (qrcodeOnFocus == 12) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE12);
        } else if (qrcodeOnFocus == 13) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE13);
        } else if (qrcodeOnFocus == 14) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE14);
        } else if (qrcodeOnFocus == 15) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE15);
        } else if (qrcodeOnFocus == 16) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE16);
        } else if (qrcodeOnFocus == 17) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE17);
        } else if (qrcodeOnFocus == 18) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE18);
        } else if (qrcodeOnFocus == 19) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE19);
        } else if (qrcodeOnFocus == 20) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE20);
        } else if (qrcodeOnFocus == 21) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE21);
        } else if (qrcodeOnFocus == 22) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE22);
        } else if (qrcodeOnFocus == 23) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE23);
        } else if (qrcodeOnFocus == 24) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE24);
        } else if (qrcodeOnFocus == 25) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE25);
        } else if (qrcodeOnFocus == 26) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE26);
        } else if (qrcodeOnFocus == 27) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE27);
        } else if (qrcodeOnFocus == 28) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE28);
        } else if (qrcodeOnFocus == 29) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE29);
        } else if (qrcodeOnFocus == 30) {
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE30);
        }
    }

    //当获取到设备编码后，把数据上传到服务器
    private void volley_post() {
        //获取文本框里面的值
        qrcode_one = childMeterOne.getText().toString().trim();
        qrcode_two = childMeterTwo.getText().toString().trim();
        qrcode_three = childMeterThree.getText().toString().trim();
        qrcode_four = childMeterFour.getText().toString().trim();
        qrcode_five = childMeterFive.getText().toString().trim();
        qrcode_six = childMeterSix.getText().toString().trim();
        qrcode_seven = childMeterServen.getText().toString().trim();
        qrcode_eight = childMeterEight.getText().toString().trim();
        qrcode_nine = childMeterNine.getText().toString().trim();
        qrcode_ten = childMeterTen.getText().toString().trim();
        qrcode_ele = childMeterEle.getText().toString().trim();
        qrcode_twe = childMeterTwe.getText().toString().trim();
        qrcode_thi = childMeterThi.getText().toString().trim();
        qrcode_fou = childMeterFou.getText().toString().trim();
        qrcode_fif = childMeterFif.getText().toString().trim();
        qrcode_sixt = childMeterSixt.getText().toString().trim();
        qrcode_sev = childMeterSev.getText().toString().trim();
        qrcode_eig = childMeterEig.getText().toString().trim();
        qrcode_nin = childMeterNin.getText().toString().trim();
        qrcode_twen = childMeterTwen.getText().toString().trim();
        qrcode_tone = childMeterTone.getText().toString().trim();
        qrcode_ttwo = childMeterTtwo.getText().toString().trim();
        qrcode_tthr = childMeterTthr.getText().toString().trim();
        qrcode_tfou = childMeterTfou.getText().toString().trim();
        qrcode_tfiv = childMeterTfiv.getText().toString().trim();
        qrcode_tsix = childMeterTsix.getText().toString().trim();
        qrcode_tsev = childMeterTsev.getText().toString().trim();
        qrcode_teig = childMeterTeig.getText().toString().trim();
        qrcode_tnin = childMeterTnin.getText().toString().trim();
        qrcode_thir = childMeterThir.getText().toString().trim();
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 提交的参数数据

            // 2.服务器网址
            /*final String URL = HttpServerAddress.BASE_URL + "?m=insertboxinfo&LID=" + lockID + "&BOX_1=" + qrcode_one
                    + "&BOX_2=" + qrcode_two + "&BOX_3=" + qrcode_three + "&BOX_4=" + qrcode_four + "&BOX_5="
                    + qrcode_five + "&BOX_6=" + qrcode_six + "&BOX_7=" + qrcode_seven + "&BOX_8=" + qrcode_eight + "&BOX_9=" +
                    qrcode_nine + "&BOX_10=" + qrcode_ten + "&BOX_11=" + qrcode_ele + "&BOX_12=" + qrcode_twe + "&BOX_13=" + qrcode_thi
                    + "&BOX_14=" + qrcode_fou + "&BOX_15=" + qrcode_fif + "&USER_CONTEXT=" + personNumber;*/
            final String URL = HttpServerAddress.BASE_URL + "?m=insertboxinfo&LID=" + lockID +
                    "&BOX_1=" + qrcode_one + "&BOX_2=" + qrcode_two + "&BOX_3=" + qrcode_three + "&BOX_4=" + qrcode_four + "&BOX_5=" + qrcode_five +
                    "&BOX_6=" + qrcode_six + "&BOX_7=" + qrcode_seven + "&BOX_8=" + qrcode_eight + "&BOX_9=" + qrcode_nine + "&BOX_10=" + qrcode_ten +
                    "&BOX_11=" + qrcode_ele + "&BOX_12=" + qrcode_twe + "&BOX_13=" + qrcode_thi + "&BOX_14=" + qrcode_fou + "&BOX_15=" + qrcode_fif +
                    "&BOX_16=" + qrcode_sixt + "&BOX_17=" + qrcode_sev + "&BOX_18=" + qrcode_eig + "&BOX_19=" + qrcode_nin + "&BOX_20=" + qrcode_twen +
                    "&BOX_21=" + qrcode_tone + "&BOX_22=" + qrcode_ttwo + "&BOX_23=" + qrcode_tthr + "&BOX_24=" + qrcode_tfou + "&BOX_25=" + qrcode_tfiv +
                    "&BOX_26=" + qrcode_tsix + "&BOX_27=" + qrcode_tsev + "&BOX_28=" + qrcode_teig + "&BOX_29=" + qrcode_tnin + "&BOX_30=" + qrcode_thir + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        Log.e("TAG", "" + String.valueOf(arg0));
                        if ((arg0.getString("result")).equals("true")) {
                            //网络提交数据成功则删除数据，清理内存
//                            if (dateBaseUtil.queryMeterId(lockID) != null) {
//                                dateBaseUtil.delete4(lockID);
//                            }
                            Toast.makeText(LockRegistersdChildMeterActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            LockRegistersdChildMeterActivity.this.setResult(RESULT_OK, intent);
                            LockRegistersdChildMeterActivity.this.finish();
                        } else {
                            ToastUtil.MyToast(LockRegistersdChildMeterActivity.this, "上传失败，请重新上传");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        ToastUtil.MyToast(LockRegistersdChildMeterActivity.this, strResult);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(LockRegistersdChildMeterActivity.this, strResult);
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

    //当获取到设备编码后，把数据上传到服务器
    private void volley_update() {
        //获取文本框里面的值
        qrcode_one = childMeterOne.getText().toString().trim();
        qrcode_two = childMeterTwo.getText().toString().trim();
        qrcode_three = childMeterThree.getText().toString().trim();
        qrcode_four = childMeterFour.getText().toString().trim();
        qrcode_five = childMeterFive.getText().toString().trim();
        qrcode_six = childMeterSix.getText().toString().trim();
        qrcode_seven = childMeterServen.getText().toString().trim();
        qrcode_eight = childMeterEight.getText().toString().trim();
        qrcode_nine = childMeterNine.getText().toString().trim();
        qrcode_ten = childMeterTen.getText().toString().trim();
        qrcode_ele = childMeterEle.getText().toString().trim();
        qrcode_twe = childMeterTwe.getText().toString().trim();
        qrcode_thi = childMeterThi.getText().toString().trim();
        qrcode_fou = childMeterFou.getText().toString().trim();
        qrcode_fif = childMeterFif.getText().toString().trim();
        qrcode_sixt = childMeterSixt.getText().toString().trim();
        qrcode_sev = childMeterSev.getText().toString().trim();
        qrcode_eig = childMeterEig.getText().toString().trim();
        qrcode_nin = childMeterNin.getText().toString().trim();
        qrcode_twen = childMeterTwen.getText().toString().trim();
        qrcode_tone = childMeterTone.getText().toString().trim();
        qrcode_ttwo = childMeterTtwo.getText().toString().trim();
        qrcode_tthr = childMeterTthr.getText().toString().trim();
        qrcode_tfou = childMeterTfou.getText().toString().trim();
        qrcode_tfiv = childMeterTfiv.getText().toString().trim();
        qrcode_tsix = childMeterTsix.getText().toString().trim();
        qrcode_tsev = childMeterTsev.getText().toString().trim();
        qrcode_teig = childMeterTeig.getText().toString().trim();
        qrcode_tnin = childMeterTnin.getText().toString().trim();
        qrcode_thir = childMeterThir.getText().toString().trim();
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=insertboxinfo&LID=" + lockID +
                    "&BOX_1=" + qrcode_one + "&BOX_2=" + qrcode_two + "&BOX_3=" + qrcode_three + "&BOX_4=" + qrcode_four + "&BOX_5=" + qrcode_five +
                    "&BOX_6=" + qrcode_six + "&BOX_7=" + qrcode_seven + "&BOX_8=" + qrcode_eight + "&BOX_9=" + qrcode_nine + "&BOX_10=" + qrcode_ten +
                    "&BOX_11=" + qrcode_ele + "&BOX_12=" + qrcode_twe + "&BOX_13=" + qrcode_thi + "&BOX_14=" + qrcode_fou + "&BOX_15=" + qrcode_fif +
                    "&BOX_16=" + qrcode_sixt + "&BOX_17=" + qrcode_sev + "&BOX_18=" + qrcode_eig + "&BOX_19=" + qrcode_nin + "&BOX_20=" + qrcode_twen +
                    "&BOX_21=" + qrcode_tone + "&BOX_22=" + qrcode_ttwo + "&BOX_23=" + qrcode_tthr + "&BOX_24=" + qrcode_tfou + "&BOX_25=" + qrcode_tfiv +
                    "&BOX_26=" + qrcode_tsix + "&BOX_27=" + qrcode_tsev + "&BOX_28=" + qrcode_teig + "&BOX_29=" + qrcode_tnin + "&BOX_30=" + qrcode_thir + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        Log.e("TAG", "" + String.valueOf(arg0));
                        if ((arg0.getString("result")).equals("true")) {
                            //网络提交数据成功则删除数据，清理内存
//                            if (dateBaseUtil.queryMeterId(lockID) != null) {
//                                dateBaseUtil.delete4(lockID);
//                            }
                            Toast.makeText(LockRegistersdChildMeterActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            LockRegistersdChildMeterActivity.this.setResult(RESULT_OK, intent);
                            LockRegistersdChildMeterActivity.this.finish();
                        } else {
                            ToastUtil.MyToast(LockRegistersdChildMeterActivity.this, "上传失败，请重新上传");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        ToastUtil.MyToast(LockRegistersdChildMeterActivity.this, strResult);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(LockRegistersdChildMeterActivity.this, strResult);
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

    //请求数据
    private void vooley_get() {
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=getboxinfo&LID=" + lockID + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        JSONObject strState = arg0;
                        // json数据解析
                        System.out.println("strState = " + strState);

                        JSONArray array = strState.getJSONArray("LOCK_BOX");
                        System.out.println("array=" + array);
                        dialog.dismiss();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            System.out.println("object" + object);
                            if (object.has("BOX_SUBID10") && object.has("BOX_SUBID19")) {
                                if (object.getString("BOX_SUBID10").equals(object.getString("BOX_SUBID19"))) {
                                    object.put("BOX_SUBID19", "");
                                }
                            }
                            setTextView(childMeterOne, "BOX_SUBID10", object);
                            setTextView(childMeterTwo, "BOX_SUBID11", object);
                            setTextView(childMeterThree, "BOX_SUBID12", object);
                            setTextView(childMeterFour, "BOX_SUBID13", object);
                            setTextView(childMeterFive, "BOX_SUBID14", object);
                            setTextView(childMeterSix, "BOX_SUBID15", object);
                            setTextView(childMeterServen, "BOX_SUBID16", object);
                            setTextView(childMeterEight, "BOX_SUBID17", object);
                            setTextView(childMeterNine, "BOX_SUBID18", object);
                            setTextView(childMeterTen, "BOX_SUBID19", object);
                            setTextView(childMeterEle, "BOX_SUBID20", object);
                            setTextView(childMeterTwe, "BOX_SUBID21", object);
                            setTextView(childMeterThi, "BOX_SUBID22", object);
                            setTextView(childMeterFou, "BOX_SUBID23", object);
                            setTextView(childMeterFif, "BOX_SUBID24", object);
                            setTextView(childMeterSixt, "BOX_SUBID25", object);
                            setTextView(childMeterSev, "BOX_SUBID26", object);
                            setTextView(childMeterEig, "BOX_SUBID27", object);
                            setTextView(childMeterNin, "BOX_SUBID28", object);
                            setTextView(childMeterTwen, "BOX_SUBID29", object);
                            setTextView(childMeterTone, "BOX_SUBID30", object);
                            setTextView(childMeterTtwo, "BOX_SUBID31", object);
                            setTextView(childMeterTthr, "BOX_SUBID32", object);
                            setTextView(childMeterTfou, "BOX_SUBID33", object);
                            setTextView(childMeterTfiv, "BOX_SUBID34", object);
                            setTextView(childMeterTsix, "BOX_SUBID35", object);
                            setTextView(childMeterTsev, "BOX_SUBID36", object);
                            setTextView(childMeterTeig, "BOX_SUBID37", object);
                            setTextView(childMeterTnin, "BOX_SUBID38", object);
                            setTextView(childMeterThir, "BOX_SUBID39", object);

                            //获取到数据则表示
//                            childMeter_button.setText(R.string.submit);
//                            title.setText("修改" + lockType);
//                            type = UPDATE_METER;
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

    private void setTextView(TextView tv, String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field)) {
            String meterId = jsonObject.getString(field);
            tv.setText(meterId);
            if (childMeterOne != tv) {
                addList(meterId);
            }
        } else {
            tv.setText("");
        }
    }

}