package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.xyw.smartlock.MorePdActivity;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.ToastUtil;
import com.xyw.smartlock.view.LayoutItem1;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MoreMuchActivity extends AppCompatActivity implements OnClickListener {
    private TextView title;
    private LayoutItem1 area, jurisdiction, quicklyunlock, pdtext, readId, versionupdating, help, switchArea,moreMuch_PersonnelMaintain, moremuch_more_unlock, moremuch_dayreport, moremuch_unlockcontrail;
    private ImageView backImage;

    private AcacheUserBean acacheUserBean;
    private ACache aCache;

    private String version;
    private String maxVersion;
    private PackageInfo info;
    //请求网络的等待弹框
    private LoadingDialog dialog;
    private String areaName, areaNameId;
    private String user_context_number;
    private String accountNo, areaNumber;
    private String strState;
    //取出所有缓存数据
    private String OP_DT, OP_PHONE, result, BeginTime, EndTime, OP_NAME, AREA_NAME, KEYVALUE, Area_id, MAXVER, ROLE_ID, USER_CONTEXT;
    private DemoApplication demoApplication;
    private View moremuch_more_unlock_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moremuch);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplication();
        // 缓存数据
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");

        // 初始化标题栏和返回按钮
        initview();
    }

    private void initview() {
        try {
            PackageManager manager = this.getPackageManager();
            info = manager.getPackageInfo(this.getPackageName(), 0);
            version = String.valueOf(info.versionCode);
            maxVersion = info.versionName;
            Log.e("version", version);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        moremuch_more_unlock_view = findViewById(R.id.moremuch_more_unlock_view);
        moremuch_more_unlock = (LayoutItem1) findViewById(R.id.moremuch_more_unlock);
        if (demoApplication.getMS() == 2) {
//            moremuch_more_unlock_view.setVisibility(View.VISIBLE);
            moremuch_more_unlock.setVisibility(View.VISIBLE);
            moremuch_more_unlock.setOnClickListener(this);
        } else {
//            moremuch_more_unlock_view.setVisibility(View.GONE);
            moremuch_more_unlock.setVisibility(View.GONE);
        }
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.more_functions);
        // 设置返回按键
        backImage = (ImageView) findViewById(R.id.common_title_back);
        backImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        // 绑定控件
        area = (LayoutItem1) findViewById(R.id.moremuch_area);
        jurisdiction = (LayoutItem1) findViewById(R.id.moremuch_jurisdiction);
        quicklyunlock = (LayoutItem1) findViewById(R.id.moremuch_quicklyunlock);
        pdtext = (LayoutItem1) findViewById(R.id.moremuch_PD);
        versionupdating = (LayoutItem1) findViewById(R.id.moremuch_versionupdating);
        readId = (LayoutItem1) findViewById(R.id.moremuch_readId);
        help = (LayoutItem1) findViewById(R.id.moremuch_help);
        switchArea = (LayoutItem1) findViewById(R.id.moremuch_switchArea);
        area.setOnClickListener(this);
        jurisdiction.setOnClickListener(this);
        quicklyunlock.setOnClickListener(this);
        pdtext.setOnClickListener(this);
        readId.setOnClickListener(this);
        versionupdating.setOnClickListener(this);
        help.setOnClickListener(this);
        switchArea.setOnClickListener(this);
        if (acacheUserBean.getROLE_ID().equals("3")) {
            pdtext.setVisibility(View.VISIBLE);
        } else {
            pdtext.setVisibility(View.GONE);
        }
        moremuch_unlockcontrail = (LayoutItem1) findViewById(R.id.moremuch_unlockcontrail);
        moremuch_unlockcontrail.setOnClickListener(this);
        moremuch_dayreport = (LayoutItem1) findViewById(R.id.moremuch_dayreport);
        moremuch_dayreport.setOnClickListener(this);
        moreMuch_PersonnelMaintain= (LayoutItem1) findViewById(R.id.moreMuch_PersonnelMaintain);
        moreMuch_PersonnelMaintain.setOnClickListener(this);
    }

    private final static int CHANGE_AREA_REQ = 1;

    /**
     * 监听控件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moremuch_area:
                Intent intent1 = new Intent(this, AreaManagementActivity.class);
                startActivity(intent1);
                break;
            case R.id.moremuch_jurisdiction:
                Intent intent2 = new Intent(this, JurisdictionActivity.class);
                startActivity(intent2);
                break;
            case R.id.moremuch_quicklyunlock:
                Intent intent = new Intent(MoreMuchActivity.this, GuideActivity.class);
                startActivity(intent);
                break;
            case R.id.moremuch_PD:
                Intent intentpd = new Intent(this, MorePdActivity.class);
                startActivity(intentpd);
                break;
            case R.id.moremuch_readId:
                Intent intent5 = new Intent(MoreMuchActivity.this, LockFileActivity.class);
                startActivity(intent5);
                break;
            case R.id.moremuch_versionupdating:
                if (Double.valueOf(acacheUserBean.getMAXVER()) > Double.valueOf(maxVersion)) {
                    new AlertDialog.Builder(MoreMuchActivity.this)
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

                                }
                            }).setCancelable(false)
                            .show();
                } else {
                    ToastUtil.MyToast(getApplicationContext(), "已是最新版本，" + maxVersion);
                }
                break;
            case R.id.moremuch_unlockcontrail:
                if (isNetworkAvailable(MoreMuchActivity.this)) {
                    Intent intent6 = new Intent(MoreMuchActivity.this, LockContrailActivity.class);
                    startActivity(intent6);
                } else {
                    ToastUtil.MyToast(MoreMuchActivity.this, "当前网络不可用，无法显示地图，请先连接网络");
                }
                break;
            case R.id.moremuch_dayreport:
                Intent intent7 = new Intent(MoreMuchActivity.this, LockStatisticsActivity.class);
                startActivity(intent7);
                break;
            case R.id.moremuch_help:
                break;
            case R.id.moremuch_switchArea:
                final AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
                user_context_number = LoginInfo.getUSER_CONTEXT();
                startActivityForResult(new Intent(MoreMuchActivity.this, UserInfoAreaManagementActivity.class), CHANGE_AREA_REQ);
                break;
            case R.id.moreMuch_PersonnelMaintain:
                Intent intent8=new Intent(MoreMuchActivity.this,PersonMaintainActivity.class);
                startActivity(intent8);
                break;
            case R.id.moremuch_more_unlock:
                if (demoApplication.getConnect() == 1) {
                    demoApplication.setISManual(true);
                    demoApplication.disconnect();
                }
                startActivity(new Intent(MoreMuchActivity.this, MoreUnlockActivity.class));
                break;
            default:
                break;
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


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public Resources getResources() {
        return super.getResources();
    }

    //从服务器获取数据替换缓存中的数据
    private void volley_get() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(MoreMuchActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            JSONObject params = new JSONObject();

            //从缓存里面获取数据
            // 读取缓存数据
            final AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            user_context_number = LoginInfo.getUSER_CONTEXT();
            //获取用户的账号
            accountNo = LoginInfo.getOP_NO().trim();
            //将缓存里面的数据都取出来
            OP_DT = LoginInfo.getOP_DT().trim();
            OP_PHONE = LoginInfo.getOP_NO().trim();
            result = LoginInfo.getResult().trim();
            BeginTime = LoginInfo.getBeginTime().trim();
            EndTime = LoginInfo.getEndTime().trim();
            OP_NAME = LoginInfo.getOP_NAME().trim();
            AREA_NAME = LoginInfo.getAREA_NAME().trim();
            KEYVALUE = LoginInfo.getKEYVALUE().trim();
            Area_id = LoginInfo.getArea_id().trim();
            MAXVER = LoginInfo.getMAXVER().trim();
            ROLE_ID = LoginInfo.getROLE_ID().trim();
            USER_CONTEXT = LoginInfo.getUSER_CONTEXT().trim();


            // 2.get请求参数
            final String URL = HttpServerAddress.BASE_URL + "?m=SwitchZone&OP_NO=" + accountNo + "&ZONE_NO="
                    + areaNameId + "&user_context=" + user_context_number;

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
                        if (strState.equals("true")) {
                            ToastUtil.MyToast(MoreMuchActivity.this, "区域切换成功!");
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                        if (strState != null && strState.equalsIgnoreCase("true")) {
                            String str = "OP_NAME:" + arg0.getString("OP_NAME") + "\n"
                                    + "AREA_NAME:" + arg0.getString("AREA_NAME") + "\n"
                                    + "AREA_ID:" + arg0.getString("AREA_ID") + "\n"
                                    + "KEYVALUE:" + arg0.getString("KEYVALUE") + "\n";
                            Log.v("str", str);
                            //将数据放进缓存里面
                            acacheUserBean.setArea_id(arg0.getString("AREA_ID"));
                            acacheUserBean.setKEYVALUE(arg0.getString("KEYVALUE"));
                            acacheUserBean.setAREA_NAME(arg0.getString("AREA_NAME"));

                            acacheUserBean.setOP_DT(OP_DT);
                            acacheUserBean.setOP_NO(OP_PHONE);
                            acacheUserBean.setResult(result);
                            acacheUserBean.setBeginTime(BeginTime);
                            acacheUserBean.setEndTime(EndTime);
                            acacheUserBean.setMAXVER(MAXVER);
                            acacheUserBean.setOP_NAME(OP_NAME);
                            acacheUserBean.setROLE_ID(ROLE_ID);
                            acacheUserBean.setUSER_CONTEXT(USER_CONTEXT);

                            aCache.put("LoginInfo", acacheUserBean);// 缓存文件名称
                            if ((arg0.getString("AREA_ID")).equals((LoginInfo.getArea_id().trim()))) {
                                ToastUtil.MyToast(MoreMuchActivity.this, "区域没有改变!");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

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
            //设置连接超时
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // 4.请求对象放入请求队列
            volleyRequestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取从区域弹框得到的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_AREA_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    areaName = data.getStringExtra("Zone_Name");// 得到新Activity 关闭后返回的数据
                    areaNameId = data.getStringExtra("Zone_No");
                    //获得返回数据后开始请求数据,修改vlurekey的值和区域id
                    if (areaName != null && areaNameId != null) {
                        volley_get();
                    }
                }
            }
        }
    }

    /**
     * 检查当前网络是否可用
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

}
