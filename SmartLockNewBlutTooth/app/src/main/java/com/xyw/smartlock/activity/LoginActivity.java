package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.xyw.smartlock.MainActivity;
import com.xyw.smartlock.MorePdActivity;
import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.MyService;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.MD5Utils;
import com.xyw.smartlock.utils.PermissionsChecker;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.view.CustomEditText;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 登陆界面
 *
 * @author AZ
 */

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    //二维码扫描
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private Button login_button1;
    private ImageView backImg;
    private TextView title, login_forget, login_regist;
    private CustomEditText login_account;
    private CustomEditText login_password;
    private String str;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    //请求网络的等待弹框
    private LoadingDialog dialog;
    private String version;
    private String maxVersion;
    private PackageInfo info;
    private String strArg0;
    private DemoApplication demoApplication;
    private String strState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplicationContext();
        intiview();
        // 判断当前网络是否可用

        // 缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();

//        requestPermissions();
    }

    private PermissionsChecker mPermissionsChecker;//检查权限
    private static final int PERMISSION_REQUEST_CODE = 0;        // 系统权限返回码
    //危险权限（运行时权限）
    static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.INTERNET
    };

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            mPermissionsChecker = new PermissionsChecker(this);
            //权限没有授权，进入授权界面
            if (mPermissionsChecker.judgePermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * 判断当前网络是否可用
     */

    private boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void intiview() {
        //获取版本信息
        try {
            PackageManager manager = this.getPackageManager();

            info = manager.getPackageInfo(this.getPackageName(), 0);
            //获取版本号
            version = String.valueOf(info.versionCode);
            //获取版本名
            maxVersion = info.versionName;
            Log.e("version", version);
        } catch (Exception e) {
            e.printStackTrace();

        }

        // 隐藏返回键
        backImg = (ImageView) findViewById(R.id.common_title_back);
        backImg.setVisibility(View.GONE);
        // 设置标题栏的名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.login);

        // 绑定控件
        login_forget = (TextView) findViewById(R.id.login_forget);
        login_regist = (TextView) findViewById(R.id.login_regist);
        login_account = (CustomEditText) findViewById(R.id.login_account);
        login_password = (CustomEditText) findViewById(R.id.login_password);
        login_button1 = (Button) findViewById(R.id.login_btnLogin);
        login_button1.setOnClickListener(this);
        login_forget.setOnClickListener(this);
        login_regist.setOnClickListener(this);
        login_password.setOnClickListener(this);
        //重写enter事件,当输入账号和密码时,点击enter直接登录
        login_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                     @Override
                                                     public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                                                         if (actionId == EditorInfo.IME_ACTION_DONE) {
                                                             if (login_account.getEtInput().toString().trim().length() < 11) {
                                                                 ToastUtil.MyToast(LoginActivity.this, "请输入正确的登陆账号");
                                                                 login_account.setEtInput("");
                                                                 login_account.requestFocus();
                                                             }
                                                             if (login_password.getEtInput().toString().trim().length() < 6) {
                                                                 ToastUtil.MyToast(LoginActivity.this, "密码至少6位数");
                                                                 login_password.setEtInput("");
                                                                 login_password.requestFocus();
                                                             }
                                                         }

                                                         if (isNetworkAvailable(LoginActivity.this)) {
                                                             volley_post();
                                                         } else {
                                                             if (ACache.get(LoginActivity.this).getAsObject("LoginInfo") != null) {
                                                                 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                 startActivity(intent);
                                                                 finish();
                                                                 ToastUtil.MyToast(LoginActivity.this, "当前没有网络，只能离线开锁15次");
                                                             } else {
                                                                 Toast.makeText(LoginActivity.this, "请先在有网情况下注册并登录", Toast.LENGTH_SHORT).show();
                                                             }

                                                         }
                                                         return false;
                                                     }
                                                 }

        );

        //当点击登录时获取文本框的password和account,将他们进行存储
        SharedPreferences remdname = getPreferences(Activity.MODE_PRIVATE);//实例化SharedPreferences
        SharedPreferences.Editor edit = remdname.edit();//实例化Editor
        edit.putString("name", login_account.getEtInput().toString().trim());//获取文本框的信息
        edit.putString("pass", login_password.getEtInput().toString().trim());
        String name_str = remdname.getString("name", "");//每次进入软件时,读取存储的账号密码信息
        String pass_str = remdname.getString("pass", "");
        login_account.setEtInput(name_str);
        login_password.setEtInput(pass_str);

        dialog = new LoadingDialog(LoginActivity.this, R.style.dailogStyle);
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 控件的监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btnLogin:
                if (login_account.getEtInput().toString().trim().length() < 11) {
                    ToastUtil.MyToast(LoginActivity.this, "请输入正确的登陆账号");
                    clearEdittext(login_account);
                    return;
                }
                if (login_password.getEtInput().toString().trim().length() < 6) {
                    ToastUtil.MyToast(LoginActivity.this, "密码至少6位数");
                    clearEdittext(login_password);
                    return;
                }

                if (isNetworkAvailable(LoginActivity.this)) {
                    volley_post();
                } else {
                    if (ACache.get(LoginActivity.this).getAsObject("LoginInfo") != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        ToastUtil.MyToast(LoginActivity.this, "当前没有网络，只能离线开锁15次");
                    } else {
                        Toast.makeText(LoginActivity.this, "请先在有网情况下注册并登录", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.login_regist:
                Intent intent2 = new Intent(LoginActivity.this, RegistActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.login_forget:
                Intent intent3 = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                intent3.putExtra("phone", login_account.getEtInput().toString().trim());
                this.startActivity(intent3);
                break;
            default:
                break;
        }
    }

    private void clearEdittext(CustomEditText et) {
        et.setEtInput("");
        et.requestFocus();
    }

    /**
     * 通过post向服务端发送数据
     */

    private void volley_post() {
        try {
            //等待网络的D
            dialog.show();
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);
            // MD5加密
            String srcString = login_password.getEtInput().toString().trim();

            // 2.POST请求参数
            final String URL = HttpServerAddress.BASE_URL + "?m=Login" + "&op_no="
                    + login_account.getEtInput().toString().trim() + "&version=" + version + "&op_pass=" + MD5Utils.encryptByMD5(srcString) + "&OP_FLAG=A";

            Log.e("TAG", URL);
            // 3.json post请求处理
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    Log.e("onResponse", "onResponse");
                    try {
                        System.out.println("------------" + arg0 + "--------------");
                        strState = arg0.getString("result");
                        System.out.println("------------" + strState + "--------------");
                        //判断弹框状态
                        dialog.dismiss();

                        if (strState != null && strState.equalsIgnoreCase("true")) {
                            //请求后台若返回true,保存用户名和密码信息,之后跳转页面
                            SharedPreferences remdname = getPreferences(Activity.MODE_PRIVATE);
                            SharedPreferences.Editor edit = remdname.edit();
                            edit.putString("name", login_account.getEtInput().toString().trim());
                            edit.putString("pass", login_password.getEtInput().toString().trim());
                            edit.commit();

                            str = "OP_DT:" + arg0.getString("OP_DT") + "\n"
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
                            // 缓存数据
//                            //获取系统当前时间,然后存入缓存
//                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date startTime = new Date(System.currentTimeMillis());
                            acacheUserBean.setAccount(login_account.getEtInput().toString().trim());
                            acacheUserBean.setMAXVER(arg0.getString("MAXVER"));
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
                            acacheUserBean.setUSER_CONTEXT(arg0.getString("USER_CONTEXT"));

                            aCache.put("LoginInfo", acacheUserBean);// 缓存文件名称
                            // // 读取缓存数据
//                            acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                            final String stringd = arg0.getString("ROLE_ID");

                            if (Double.valueOf(arg0.getString("MAXVER")) > Double.valueOf(maxVersion)) {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("版本升级")
                                        .setMessage("是否下载最新版本替代" + info.versionName)
                                        //当点确定按钮时从服务器上下载 新的apk 然后安装
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                downLoadApk();
                                            }
                                        })
                                        //当点取消按钮时进行登录
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub
                                                //跳转到主界面
                                                intomain(stringd);
                                            }
                                        }).setCancelable(false)
                                        .show();
                            } else {
                                intomain(stringd);
                            }
                        } else if (arg0.getString("result").equals("版本过低，请升级软件!")) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("版本升级")
                                    .setMessage("是否下载最新版本替代" + info.versionName)
                                    //当点确定按钮时从服务器上下载 新的apk 然后安装
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            downLoadApk();
                                        }
                                    })
                                    //当点取消按钮时进行登录
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).setCancelable(false)
                                    .show();
                            return;
                        } else {
                            ToastUtil.MyToast(getApplicationContext(), strState);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.MyToast(getApplicationContext(), "请检查网络");
                    }
                }

                private void intomain(String stringd) {
                    if (stringd.equals("4")) {
                        Intent intentService = new Intent();
                        intentService.setClass(LoginActivity.this, MyService.class);
                        startService(intentService);
                        Intent intent2 = new Intent(LoginActivity.this, MorePdActivity.class);
                        startActivity(intent2);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    ToastUtil.MyToast(getApplicationContext(), strState);
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

    /*
 * 从服务器中下载APK
 */
    protected void downLoadApk() {
        Uri uri = Uri.parse("http://www.xywlock.com:81/Download/xywlock.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
//        final ProgressDialog pd;    //进度条对话框
//        pd = new ProgressDialog(this);
//        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pd.setMessage("正在下载更新");
//        pd.show();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
////                    File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);
//                    sleep(30000);
////                    installApk(file);
//                    pd.dismiss(); //结束掉进度条对话框
//                } catch (Exception e) {
////                    Message msg = new Message();
////                    msg.what = DOWN_ERROR;
////                    handler.sendMessage(msg);
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

}
