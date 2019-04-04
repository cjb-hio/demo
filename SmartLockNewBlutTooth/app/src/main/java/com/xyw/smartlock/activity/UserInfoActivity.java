package com.xyw.smartlock.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.xyw.smartlock.common.BroadcastKey;
import com.xyw.smartlock.common.ClipActivity;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.RoundImageView;
import com.xyw.smartlock.fragment.ModifyUserNameFragment;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.SLImageLoader;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class UserInfoActivity extends AppCompatActivity implements OnClickListener, ModifyUserNameFragment.OnSubmitResultListener {

    private TextView title;
    private ImageView imageback;

    private TextView userName, account, phone, dt, areaNumber, area, userType, startDate,
            endDate;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private PopupWindow popWindow;
    private RoundImageView headPhoto;
    private Button btnCamera, btnAlbum, btn_big, btnCancel;
    public static final int PHOTOZOOM = 0; // 相册/拍照
    public static final int PHOTOTAKE = 1; // 相册/拍照
    public static final int IMAGE_COMPLETE = 2; // 结果
    public static final int CROPREQCODE = 3; // 截取
    public static final int  INFOMODIFYNAME=4;
    private String photoSavePath;//保存路径
    private String photoSaveName;//图pian名
    private String path;//图片全路径
    private String strState;

    private ImageView userInfo_Image;
    private TextView userInfo_userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        getSupportActionBar().hide();
//        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
//        ImageLoader.getInstance().init(imageLoaderConfiguration);
        // 初始化控件
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        initview();

//        requestData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastKey.MSG_HEADPIX_UPLOAD_SUCCESS);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void loadHeadPixImage() {
        String url = HttpServerAddress.UPLOADS + acacheUserBean.getOP_NO() + ".png";
        SLImageLoader.getInstance().loadImagePix(url, headPhoto);
    }

    private void requestData() {
        try {
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            // 请求地址
            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            Log.e("UserInfoAcache", acacheUserBean.toString());
            final String URL = HttpServerAddress.USERINFO + "&op_no="
                    + acacheUserBean.getOP_NO() + "&USER_CONTEXT="
                    + acacheUserBean.getUSER_CONTEXT();

            Log.e("UserInfoURL", URL);

            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("UserInfoonResponse", arg0.toString());
                    try {
                        strState = arg0.getString("result");
                        if (strState.equals("true")) {
                            Log.e("UserInfostate", "request success!" + arg0.toString());
                            ToastUtil.MyToast(getApplicationContext(), "获取成功");
                            userName.setText(arg0.getString("OP_NAME"));
                            phone.setText(arg0.getString("OP_PHONE"));
                            dt.setText(arg0.getString("OP_DT"));
                            areaNumber.setText(arg0.getString("AREA_ID"));
                            area.setText(arg0.getString("AREA_NAME"));
                            String type = arg0.getString("ROLE_ID");
                            if ("1".equals(type)) {
                                userType.setText("操作员");
                            } else if ("2".equals(type)) {
                                userType.setText("区域管理员");
                            } else if ("3".equals(type)) {
                                userType.setText("超级管理员");
                            } else {
                                userType.setText("生产测试员");
                            }
                            startDate.setText(arg0.getString("VBEGINTIME").replaceAll("/", "-"));
                            endDate.setText(arg0.getString("VENDTIME").replaceAll("/", "-"));
                            account.setText(acacheUserBean.getOP_NO());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), strState);
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(
                        NetworkResponse arg0) {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 初始化并绑定控件
     */
    private void initview() {
        userInfo_Image = (ImageView) findViewById(R.id.userInfo_Image);
        userInfo_userName = (TextView) findViewById(R.id.userInfo_userName);
        userInfo_Image.setOnClickListener(this);
        userInfo_userName.setOnClickListener(this);

        File file = new File(Environment.getExternalStorageDirectory(), "SmartLock/image");
        if (!file.exists())
            file.mkdirs();
        photoSavePath = Environment.getExternalStorageDirectory() + "/SmartLock/image/";
        photoSaveName = acacheUserBean.getOP_NO() + ".png";
        // 设置标题栏
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.persondata);

        headPhoto = (RoundImageView) findViewById(R.id.headPhoto);
        userName = (TextView) findViewById(R.id.userInfo_userName);
        account = (TextView) findViewById(R.id.userInfo_account);
        phone = (TextView) findViewById(R.id.userInfo_phone);
        dt = (TextView) findViewById(R.id.userInfo_dt);
        areaNumber = (TextView) findViewById(R.id.userInfo_areaNumber);
        area = (TextView) findViewById(R.id.userInfo_area);
        userType = (TextView) findViewById(R.id.userInfo_userType);
        startDate = (TextView) findViewById(R.id.userInfo_startDate);
        endDate = (TextView) findViewById(R.id.userInfo_endDate);
        // 设置标题栏返回控件
        imageback = (ImageView) findViewById(R.id.common_title_back);
        imageback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //加载头像
        loadHeadPixImage();
        userName.setText(acacheUserBean.getOP_NAME());
        phone.setText(acacheUserBean.getOP_NO());
        dt.setText(acacheUserBean.getOP_DT());
        areaNumber.setText(acacheUserBean.getArea_id());
        area.setText(acacheUserBean.getAREA_NAME());
        String type = acacheUserBean.getROLE_ID();
        if ("1".equals(type)) {
            userType.setText("操作员");
        } else if ("2".equals(type)) {
            userType.setText("区域管理员");
        } else if ("3".equals(type)) {
            userType.setText("超级管理员");
        } else {
            userType.setText("生产测试员");
        }

        startDate.setText(acacheUserBean.getBeginTime().replaceAll("/", "-"));
        endDate.setText(acacheUserBean.getEndTime().replaceAll("/", "-"));
        account.setText(acacheUserBean.getOP_NO());
//        headPhoto.setImageBitmap(getLoacalBitmap(path));
        headPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                View popView = View.inflate(UserInfoActivity.this, R.layout.userinfo_pop_menu, null);

                btnCamera = (Button) popView.findViewById(R.id.btn_camera_pop_camera);
                btnAlbum = (Button) popView.findViewById(R.id.btn_camera_pop_album);
                btn_big = (Button) popView.findViewById(R.id.btn_camera_pop_big);
                btnCancel = (Button) popView.findViewById(R.id.btn_camera_pop_cancel);

                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;

                popWindow = new PopupWindow(popView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.AnimBottom);
                popWindow.setFocusable(true);
                popWindow.setOutsideTouchable(false);// 设置允许在外点击消失

                OnClickListener listener = new OnClickListener() {
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.btn_camera_pop_camera:
                                popWindow.dismiss();
//                                photoSaveName = acacheUserBean.getOP_NO() + ".png";
                                Uri imageUri = null;
                                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File photoFile = new File(photoSavePath, photoSaveName);
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    imageUri = Uri.fromFile(photoFile);
                                } else {
                                    ContentValues contentValues = new ContentValues(1);
                                    contentValues.put(MediaStore.Images.Media.DATA, photoFile.getAbsolutePath());
                                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                                }
                                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(openCameraIntent, PHOTOTAKE);
                                break;
                            case R.id.btn_camera_pop_album:
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, PHOTOZOOM);
                                break;
                            case R.id.btn_camera_pop_big:
                                Intent bigIntent = new Intent(UserInfoActivity.this, BigPictureActivity.class);
                                headPhoto.setDrawingCacheEnabled(true);
                                bigIntent.putExtra("BitMap", headPhoto.getDrawingCache());
                                startActivity(bigIntent);
                                break;
                            case R.id.btn_camera_pop_cancel:
                                break;
                        }
                        popWindow.dismiss();
                    }
                };

                btnCamera.setOnClickListener(listener);
                btnAlbum.setOnClickListener(listener);
                btn_big.setOnClickListener(listener);
                btnCancel.setOnClickListener(listener);

                ColorDrawable dw = new ColorDrawable(0x30000000);
                popWindow.setBackgroundDrawable(dw);
                popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }


        });
    }

    /**
     * 图片选择及拍照结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Uri uri = null;
        switch (requestCode) {
            case PHOTOZOOM://相册
                if (data == null) {
                    return;
                }
                uri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(uri, proj, null, null, null);
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);// 图片在的路径
                Intent intent3 = new Intent(UserInfoActivity.this, ClipActivity.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);
                break;
            case PHOTOTAKE://拍照
                path = photoSavePath + photoSaveName;
                uri = Uri.fromFile(new File(path));
                Intent intent2 = new Intent(UserInfoActivity.this, ClipActivity.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE:
                final String temppath = data.getStringExtra("path");
                headPhoto.setImageBitmap(getLoacalBitmap(temppath));
                break;
            case INFOMODIFYNAME:
                if(data!=null){
                    String userInfoModifyName=data.getStringExtra("userInfo_Name");
                    userInfo_userName.setText(userInfoModifyName);
                }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadcastKey.MSG_HEADPIX_UPLOAD_SUCCESS.equals(intent.getAction())) {
                String path = intent.getStringExtra("pathUpload");
                Log.e("path", path);
                SLImageLoader.getInstance().loadImage("file://" + path, headPhoto);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private ModifyUserNameFragment mModifyUserNameFragment;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userInfo_userName:
            case R.id.userInfo_Image:
//                Intent intent=new Intent(UserInfoActivity.this, ModifyNameActivity.class);
//                startActivityForResult(intent, INFOMODIFYNAME);
                mModifyUserNameFragment = new ModifyUserNameFragment();
                mModifyUserNameFragment.show(getSupportFragmentManager(), "rename");

                break;
            default:
                break;
        }
    }

    @Override
    public void onSubmit(boolean isSuccess, String result) {
        if (isSuccess) {
            userInfo_userName.setText(result);
        }
    }
}
