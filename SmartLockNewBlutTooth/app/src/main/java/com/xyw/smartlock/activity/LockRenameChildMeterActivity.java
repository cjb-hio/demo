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
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.zxing.qrcodactivity.QrcodeActivityCapture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LockRenameChildMeterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

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


    private EditText rechildMeterOne, rechildMeterTwo, rechildMeterThree, rechildMeterFour, rechildMeterFive,
            rechildMeterSix, rechildMeterServen, rechildMeterEight, rechildMeterNine, rechildMeterTen,
            rechildMeterEle, rechildMeterTwe, rechildMeterThi, rechildMeterFou, rechildMeterFif;
    private TextView title;
    private Button relockqecode_button;
    private ImageView ImageBack, imageview_qrcode_button;
    private String lockID, qrcode_one, qrcode_two, qrcode_three, qrcode_four, qrcode_five, qrcode_six, qrcode_seven,
            qrcode_eight, qrcode_nine, qrcode_ten, qrcode_ele, qrcode_twe, qrcode_thi, qrcode_fou, qrcode_fif;
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

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_rename_child_meter);
        getSupportActionBar().hide();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 获取缓存数据
        aCache = ACache.get(this);
        LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        //初始化控件
        initview();
    }

    //初始化控件
    private void initview() {
        title = (TextView) findViewById(R.id.lockregistersdchildMeter_tv_title);
        Intent intent = getIntent();
        if (null != intent) {
            lockID = intent.getStringExtra("ID");
            //设置标题栏
            title.setText(intent.getStringExtra("LockType"));
        }
        //设置返回按钮
        ImageBack = (ImageView) findViewById(R.id.lockrenamechildMeter_title_back);
        ImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //二维码按钮
        imageview_qrcode_button = (ImageView) findViewById(R.id.remeter_imageview_qrcode_button);
        imageview_qrcode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        //设置完成按钮
        relockqecode_button = (Button) findViewById(R.id.relockqecode_button);
        relockqecode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLast()) {
                    volley_post();
                }
            }
        });

        rechildMeterOne = (EditText) findViewById(R.id.rechildMeterOne);
        rechildMeterTwo = (EditText) findViewById(R.id.rechildMeterTwo);
        rechildMeterThree = (EditText) findViewById(R.id.rechildMeterThree);
        rechildMeterFour = (EditText) findViewById(R.id.rechildMeterFour);
        rechildMeterFive = (EditText) findViewById(R.id.rechildMeterFive);
        rechildMeterSix = (EditText) findViewById(R.id.rechildMeterSix);
        rechildMeterServen = (EditText) findViewById(R.id.rechildMeterServen);
        rechildMeterEight = (EditText) findViewById(R.id.rechildMeterEight);
        rechildMeterNine = (EditText) findViewById(R.id.rechildMeterNine);
        rechildMeterTen = (EditText) findViewById(R.id.rechildMeterTen);
        rechildMeterEle = (EditText) findViewById(R.id.rechildMeterEle);
        rechildMeterTwe = (EditText) findViewById(R.id.rechildMeterTwe);
        rechildMeterThi = (EditText) findViewById(R.id.rechildMeterThi);
        rechildMeterFou = (EditText) findViewById(R.id.rechildMeterFou);
        rechildMeterFif = (EditText) findViewById(R.id.rechildMeterFif);
        rechildMeterOne.setOnFocusChangeListener(this);
        rechildMeterTwo.setOnFocusChangeListener(this);
        rechildMeterThree.setOnFocusChangeListener(this);
        rechildMeterFour.setOnFocusChangeListener(this);
        rechildMeterFive.setOnFocusChangeListener(this);
        rechildMeterSix.setOnFocusChangeListener(this);
        rechildMeterServen.setOnFocusChangeListener(this);
        rechildMeterEight.setOnFocusChangeListener(this);
        rechildMeterNine.setOnFocusChangeListener(this);
        rechildMeterTen.setOnFocusChangeListener(this);
        rechildMeterEle.setOnFocusChangeListener(this);
        rechildMeterTwe.setOnFocusChangeListener(this);
        rechildMeterThi.setOnFocusChangeListener(this);
        rechildMeterFou.setOnFocusChangeListener(this);
        rechildMeterFif.setOnFocusChangeListener(this);

        vooley_get();
    }

    private boolean checkLast() {
        String lastData = null;
        EditText et = null;
        if (qrcodeOnFocus == 1) {
            lastData = rechildMeterOne.getText().toString().trim();
            et = rechildMeterOne;
        } else if (qrcodeOnFocus == 2) {
            lastData = rechildMeterTwo.getText().toString().trim();
            et = rechildMeterTwo;
        } else if (qrcodeOnFocus == 3) {
            lastData = rechildMeterThree.getText().toString().trim();
            et = rechildMeterThree;
        } else if (qrcodeOnFocus == 4) {
            lastData = rechildMeterFour.getText().toString().trim();
            et = rechildMeterFour;
        } else if (qrcodeOnFocus == 5) {
            lastData = rechildMeterFive.getText().toString().trim();
            et = rechildMeterFive;
        } else if (qrcodeOnFocus == 6) {
            lastData = rechildMeterSix.getText().toString().trim();
            et = rechildMeterSix;
        } else if (qrcodeOnFocus == 7) {
            lastData = rechildMeterServen.getText().toString().trim();
            et = rechildMeterServen;
        } else if (qrcodeOnFocus == 8) {
            lastData = rechildMeterEight.getText().toString().trim();
            et = rechildMeterEight;
        } else if (qrcodeOnFocus == 9) {
            lastData = rechildMeterNine.getText().toString().trim();
            et = rechildMeterNine;
        } else if (qrcodeOnFocus == 10) {
            lastData = rechildMeterTen.getText().toString().trim();
            et = rechildMeterTen;
        } else if (qrcodeOnFocus == 11) {
            lastData = rechildMeterEle.getText().toString().trim();
            et = rechildMeterEle;
        } else if (qrcodeOnFocus == 12) {
            lastData = rechildMeterTwe.getText().toString().trim();
            et = rechildMeterTwe;
        } else if (qrcodeOnFocus == 13) {
            lastData = rechildMeterThi.getText().toString().trim();
            et = rechildMeterThi;
        } else if (qrcodeOnFocus == 14) {
            lastData = rechildMeterFou.getText().toString().trim();
            et = rechildMeterFou;
        } else if (qrcodeOnFocus == 15) {
            lastData = rechildMeterFif.getText().toString().trim();
            et = rechildMeterFif;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            switch (requestCode) {
                case SCANNIN_GREQUEST_CODE1:
                    meterOne = bundle.getString("result");
                    if (!mScanList.contains(meterOne)) {
                        //显示扫描到的内容
                        rechildMeterOne.setText(meterOne);
                        mScanList.add(meterOne);
                        setEdittext(2, rechildMeterTwo);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(1, rechildMeterOne);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE2:
                    meterTwo = bundle.getString("result");
                    if (!mScanList.contains(meterTwo)) {
                        //显示扫描到的内容
                        rechildMeterTwo.setText(meterTwo);
                        mScanList.add(meterTwo);
                        setEdittext(3, rechildMeterThree);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(2, rechildMeterTwo);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE3:
                    meterThree = bundle.getString("result");
                    if (!mScanList.contains(meterThree)) {
                        //显示扫描到的内容
                        rechildMeterThree.setText(meterThree);
                        mScanList.add(meterThree);
                        setEdittext(4, rechildMeterFour);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(3, rechildMeterThree);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE4:
                    meterFour = bundle.getString("result");
                    if (!mScanList.contains(meterFour)) {
                        //显示扫描到的内容
                        rechildMeterFour.setText(meterFour);
                        mScanList.add(meterFour);
                        setEdittext(5, rechildMeterFive);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(4, rechildMeterFour);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE5:
                    meterFive = bundle.getString("result");
                    if (!mScanList.contains(meterFive)) {
                        //显示扫描到的内容
                        rechildMeterFive.setText(meterFive);
                        mScanList.add(meterFive);
                        setEdittext(6, rechildMeterSix);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(5, rechildMeterFive);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE6:
                    meterSix = bundle.getString("result");
                    if (!mScanList.contains(meterSix)) {
                        //显示扫描到的内容
                        rechildMeterSix.setText(meterSix);
                        mScanList.add(meterSix);
                        setEdittext(7, rechildMeterServen);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(6, rechildMeterSix);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE7:
                    meterSeven = bundle.getString("result");
                    if (!mScanList.contains(meterSeven)) {
                        //显示扫描到的内容
                        rechildMeterServen.setText(meterSeven);
                        mScanList.add(meterSeven);
                        setEdittext(8, rechildMeterEight);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(7, rechildMeterServen);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE8:
                    meterEight = bundle.getString("result");
                    if (!mScanList.contains(meterEight)) {
                        //显示扫描到的内容
                        rechildMeterEight.setText(meterEight);
                        mScanList.add(meterEight);
                        setEdittext(9, rechildMeterNine);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(8, rechildMeterEight);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE9:
                    meterNine = bundle.getString("result");
                    if (!mScanList.contains(meterNine)) {
                        //显示扫描到的内容
                        rechildMeterNine.setText(meterNine);
                        mScanList.add(meterNine);
                        setEdittext(10, rechildMeterTen);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(9, rechildMeterNine);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE10:
                    meterTen = bundle.getString("result");
                    if (!mScanList.contains(meterTen)) {
                        //显示扫描到的内容
                        rechildMeterTen.setText(meterTen);
                        mScanList.add(meterTen);
                        setEdittext(11, rechildMeterEle);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(10, rechildMeterTen);
                    }
                    //显示
//                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                    break;
                case SCANNIN_GREQUEST_CODE11:
                    meterEle = bundle.getString("result");
                    if (!mScanList.contains(meterEle)) {
                        //显示扫描到的内容
                        rechildMeterEle.setText(meterEle);
                        mScanList.add(meterEle);
                        setEdittext(12, rechildMeterTwe);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(11, rechildMeterEle);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE12:
                    meterTwe = bundle.getString("result");
                    if (!mScanList.contains(meterTwe)) {
                        //显示扫描到的内容
                        rechildMeterTwe.setText(meterTwe);
                        mScanList.add(meterTwe);
                        setEdittext(13, rechildMeterThi);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(12, rechildMeterTwe);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE13:
                    meterThi = bundle.getString("result");
                    if (!mScanList.contains(meterThi)) {
                        //显示扫描到的内容
                        rechildMeterThi.setText(meterThi);
                        mScanList.add(meterThi);
                        setEdittext(14, rechildMeterFou);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(13, rechildMeterThi);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE14:
                    meterFou = bundle.getString("result");
                    if (!mScanList.contains(meterFou)) {
                        //显示扫描到的内容
                        rechildMeterFou.setText(meterFou);
                        mScanList.add(meterFou);
                        setEdittext(15, rechildMeterFif);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(14, rechildMeterFou);
                    }
                    break;
                case SCANNIN_GREQUEST_CODE15:
                    meterFif = bundle.getString("result");
                    if (!mScanList.contains(meterFif)) {
                        //显示扫描到的内容
                        rechildMeterFif.setText(meterFif);
                        mScanList.add(meterFif);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    } else {
                        Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                        setEdittext(15, rechildMeterFif);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setEdittext(int qrcode, EditText et) {
        qrcodeOnFocus = qrcode;
        edittextReuqesFocus(et);
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
            mScanList.remove(et.getText().toString().trim());
        }
    }

    private void edittextReuqesFocus(final EditText et) {
        et.setText("");
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }

    private Handler mHandler = new Handler();

    private void changeFocus(String str, final EditText et) {
        str = et.getText().toString().trim();
        if (!str.equals("")) {
            if (!mScanList.contains(str)) {
                mScanList.add(str);
            } else {
                Toast.makeText(this, R.string.data_contains, Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        edittextReuqesFocus(et);
                    }
                }, 200);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.rechildMeterOne:
                    qrcodeOnFocus = 1;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterTwo:
                    qrcodeOnFocus = 2;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterThree:
                    qrcodeOnFocus = 3;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterFour:
                    qrcodeOnFocus = 4;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterFive:
                    qrcodeOnFocus = 5;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterSix:
                    qrcodeOnFocus = 6;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterServen:
                    qrcodeOnFocus = 7;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterEight:
                    qrcodeOnFocus = 8;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterNine:
                    qrcodeOnFocus = 9;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterTen:
                    qrcodeOnFocus = 10;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterEle:
                    qrcodeOnFocus = 11;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterTwe:
                    qrcodeOnFocus = 12;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterThi:
                    qrcodeOnFocus = 13;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterFou:
                    qrcodeOnFocus = 14;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterFif:
                    qrcodeOnFocus = 15;
                    removeEdittext(rechildMeterOne);
                    break;
                default:
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.rechildMeterOne:
                    qrcodeOnFocus = 1;
                    removeEdittext(rechildMeterOne);
                    break;
                case R.id.rechildMeterTwo:
                    qrcodeOnFocus = 2;
                    break;
                case R.id.rechildMeterThree:
                    qrcodeOnFocus = 3;
                    break;
                case R.id.rechildMeterFour:
                    qrcodeOnFocus = 4;
                    break;
                case R.id.rechildMeterFive:
                    qrcodeOnFocus = 5;
                    break;
                case R.id.rechildMeterSix:
                    qrcodeOnFocus = 6;
                    break;
                case R.id.rechildMeterServen:
                    qrcodeOnFocus = 7;
                    break;
                case R.id.rechildMeterEight:
                    qrcodeOnFocus = 8;
                    break;
                case R.id.rechildMeterNine:
                    qrcodeOnFocus = 9;
                    break;
                case R.id.rechildMeterTen:
                    qrcodeOnFocus = 10;
                    break;
                case R.id.rechildMeterEle:
                    qrcodeOnFocus = 11;
                    break;
                case R.id.rechildMeterTwe:
                    qrcodeOnFocus = 12;
                    break;
                case R.id.rechildMeterThi:
                    qrcodeOnFocus = 13;
                    break;
                case R.id.rechildMeterFou:
                    qrcodeOnFocus = 14;
                    break;
                case R.id.rechildMeterFif:
                    qrcodeOnFocus = 15;
                    break;
                default:
                    break;
            }
        }
    }

    private void test() {
        //二维码扫描功能
        Intent intent = new Intent();
        intent.setClass(LockRenameChildMeterActivity.this, QrcodeActivityCapture.class);
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
        }
    }

    //当获取到设备编码后，把数据上传到服务器
    private void volley_post() {
        //获取文本框里面的值
        qrcode_one = rechildMeterOne.getText().toString().trim();
        qrcode_two = rechildMeterTwo.getText().toString().trim();
        qrcode_three = rechildMeterThree.getText().toString().trim();
        qrcode_four = rechildMeterFour.getText().toString().trim();
        qrcode_five = rechildMeterFive.getText().toString().trim();
        qrcode_six = rechildMeterSix.getText().toString().trim();
        qrcode_seven = rechildMeterServen.getText().toString().trim();
        qrcode_eight = rechildMeterEight.getText().toString().trim();
        qrcode_nine = rechildMeterNine.getText().toString().trim();
        qrcode_ten = rechildMeterTen.getText().toString().trim();
        qrcode_ele = rechildMeterEle.getText().toString().trim();
        qrcode_twe = rechildMeterTwe.getText().toString().trim();
        qrcode_thi = rechildMeterThi.getText().toString().trim();
        qrcode_fou = rechildMeterFou.getText().toString().trim();
        qrcode_fif = rechildMeterFif.getText().toString().trim();
        try {
            //等待网络的D
            dialog = new LoadingDialog(LockRenameChildMeterActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

//            JSONObject params = new JSONObject();
            // 读取缓存数据
            personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();

            // 提交的参数数据
            // 2.服务器网址
            final String URL = HttpServerAddress.BASE_URL + "?m=insertboxinfo&LID=" + lockID + "&BOX_1=" + qrcode_one
                    + "&BOX_2=" + qrcode_two + "&BOX_3=" + qrcode_three + "&BOX_4=" + qrcode_four + "&BOX_5="
                    + qrcode_five + "&BOX_6=" + qrcode_six + "&BOX_7=" + qrcode_seven + "&BOX_8=" + qrcode_eight + "&BOX_9=" +
                    qrcode_nine + "&BOX_10=" + qrcode_ten + "&BOX_11=" + qrcode_ele + "&BOX_12=" + qrcode_twe + "&BOX_13=" + qrcode_thi
                    + "&BOX_14=" + qrcode_fou + "&BOX_15=" + qrcode_fif + "&USER_CONTEXT=" + personNumber;
            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    try {
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        Log.e("TAG", "" + String.valueOf(arg0));
                        if ((arg0.getString("result")).equals("true")) {
                            Intent intent = new Intent();
                            LockRenameChildMeterActivity.this.setResult(RESULT_OK, intent);
                            LockRenameChildMeterActivity.this.finish();
                        } else {
                            ToastUtil.MyToast(LockRenameChildMeterActivity.this, "上传失败，请重新上传");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        ToastUtil.MyToast(LockRenameChildMeterActivity.this, strResult);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(LockRenameChildMeterActivity.this, strResult);
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
            dialog = new LoadingDialog(LockRenameChildMeterActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 读取缓存数据
            personNumber = LoginInfo.getUSER_CONTEXT().toString().trim();
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
                        System.out.println("------------" + "arg0=" + arg0 + "--------------");
                        JSONObject strState = arg0;

                        System.out.println("------------" + "ZONE=" + strState + "--------------");
                        // json数据解析
                        System.out.println("strState=" + strState);

                        JSONArray array = strState.getJSONArray("LOCK_BOX");
                        System.out.println("array=" + array);
                        dialog.dismiss();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            System.out.println("object" + object);
                            setTextView(rechildMeterOne, "BOX_SUBID10", object);
                            setTextView(rechildMeterTwo, "BOX_SUBID11", object);
                            setTextView(rechildMeterThree, "BOX_SUBID12", object);
                            setTextView(rechildMeterFour, "BOX_SUBID13", object);
                            setTextView(rechildMeterFive, "BOX_SUBID14", object);
                            setTextView(rechildMeterSix, "BOX_SUBID15", object);
                            setTextView(rechildMeterServen, "BOX_SUBID16", object);
                            setTextView(rechildMeterEight, "BOX_SUBID17", object);
                            setTextView(rechildMeterNine, "BOX_SUBID18", object);
                            setTextView(rechildMeterTen, "BOX_SUBID19", object);
                            setTextView(rechildMeterEle, "BOX_SUBID20", object);
                            setTextView(rechildMeterTwe, "BOX_SUBID21", object);
                            setTextView(rechildMeterThi, "BOX_SUBID22", object);
                            setTextView(rechildMeterFou, "BOX_SUBID23", object);
                            setTextView(rechildMeterFif, "BOX_SUBID24", object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
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

    private void setTextView(TextView tv, String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field))
            tv.setText(jsonObject.getString(field));
        else
            tv.setText("");
    }
}
