package com.xyw.smartlock;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xyw.smartlock.activity.ChangePasswordActivity;
import com.xyw.smartlock.activity.GuideActivity;
import com.xyw.smartlock.activity.LockRegisteredActivity;
import com.xyw.smartlock.activity.LoginActivity;
import com.xyw.smartlock.activity.MoreMuchActivity;
import com.xyw.smartlock.activity.SearchActivity;
import com.xyw.smartlock.activity.SetMsActivity;
import com.xyw.smartlock.activity.TaskAndSendActivity;
import com.xyw.smartlock.activity.TaskAndSendListActivity;
import com.xyw.smartlock.activity.TaskApplyActivitys;
import com.xyw.smartlock.activity.UnlockRecordActivity;
import com.xyw.smartlock.activity.UserInfoActivity;
import com.xyw.smartlock.admodle.ADInfo;
import com.xyw.smartlock.admodle.CycleViewPager;
import com.xyw.smartlock.admodle.ViewFactory;
import com.xyw.smartlock.bean.MyService;
import com.xyw.smartlock.common.BroadcastKey;
import com.xyw.smartlock.common.GossipItem;
import com.xyw.smartlock.common.GossipView;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.common.XCRoundImageView;
import com.xyw.smartlock.db.DateBaseUtil;
import com.xyw.smartlock.db.UnLock;
import com.xyw.smartlock.nfctest.UnlockActivity;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.GetImgHead;
import com.xyw.smartlock.utils.PermissionsChecker;
import com.xyw.smartlock.utils.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "MainActivity";

    private boolean show = false;
    private XCRoundImageView userInfo;
    private ImageButton imgBtnAdd;
    private LinearLayout layout1, layout2, layout3, layout4;
    private LinearLayout mainTitle;
    private static final int REQUEST_ENABLE_BT = 2;
    public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
    //蓝牙图标
    private ImageView img_bt;
    DemoApplication demoApplication;
    private BluetoothAdapter mBtAdapter;
    private boolean toConnect = false;
    //广告模块
    private ViewPager adViewPager;
    private List<ImageView> imageViews;// 滑动的图片集合

    private List<View> dots; // 图片标题正文的那些点
    private List<View> dotList;

    private TextView tv_date, tv_title, tv_topic_from, tv_topic;
    private int currentItem = 0; // 当前图片的索引号

    // 异步加载图片
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    // 轮播banner的数据
    private PopupWindow popupWindow;
    protected EditText et_text;
//    private String[] strs = {"锁注册", "锁档案", "更多", "任务列表", "任务申请", "记录查询"};
    private String[] strs;
    //判断有没有NFC功能
    private NfcAdapter mAdapter;
    //操作数据库
    private DateBaseUtil dateBaseUtil;
    private List<UnLock> list = new ArrayList<UnLock>();

    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private boolean btqx = false;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            adViewPager.setCurrentItem(currentItem);
        }
    };
    private boolean btimgbg = true;
    private List<ImageView> views = new ArrayList<ImageView>();
    private List<ADInfo> infos = new ArrayList<ADInfo>();
    private CycleViewPager cycleViewPager;
    private Intent myIntent;
    private Intent intentService;
    private LoadingDialog dialog;
    private String userCounext, maxver, address, account, keyvalue, areaID;
    private String BeginTime, EndTime;
    private String strBeginTime, strEndTime;
    private static String path = "/sdcard/myHead/";// sd路径
    String url;

    private String[] imageUrls = {HttpServerAddress.BASE_IMAGE + "top1.png",
            HttpServerAddress.BASE_IMAGE + "top2.png",
            HttpServerAddress.BASE_IMAGE + "top3.png",
            HttpServerAddress.BASE_IMAGE + "top4.png",
            HttpServerAddress.BASE_IMAGE + "top5.png",};
    //创建广播接收器，接收广播，更新头像
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadcastKey.MSG_HEADPIX_UPLOAD_SUCCESS.equals(intent.getAction())) {
                ImageLoader.getInstance().clearDiscCache();
                ImageLoader.getInstance().clearMemoryCache();
                loadHeadPixImage();
                Log.e("TAG", "mainactivity_Broadcast");
            }
        }
    };
    private BroadcastReceiver upbtimg = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
                    img_bt.setImageDrawable(getResources().getDrawable(R.mipmap.bluet));
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    }, 200);
                    //每次连接读一次ID
//                    demoApplication.ReadIdBegin(handler);
                    break;
                case "UPBTIMG_DIS":
                    img_bt.setImageDrawable(getResources().getDrawable(R.mipmap.bluef));
                    break;
            }
        }
    };
    //创建接收器对象，并在onReceive方法中接收键值为id的数据
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //String val=intent.getStringExtra("process");

            final Dialog dialog2 = new Dialog(MainActivity.this, R.style.dailogStyle);
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = dialog2.getWindow().getAttributes();
            lp.alpha = 0.9f;//透明度
            View view1 = getLayoutInflater().inflate(R.layout.logout_dialog, null);
            dialog2.setContentView(view1);
            Button sure = (Button) view1.findViewById(R.id.bt_sure);
            Button sure_no = (Button) view1.findViewById(R.id.bt_sure_no);
            dialog2.setCanceledOnTouchOutside(false);
            sure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    show = false;
                    dialog2.dismiss();
                    stopService(intentService);
                    finish();
                }
            });
            sure_no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    show = false;
                    stopService(intentService);
                    Intent intent1 = new Intent();
                    intent1.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    stopService(intentService);
                    finish();
                }
            });
            if (!show) {
                dialog2.show();
                show = true;
            }
        }
    };

    //权限相关
    private static final int REQUEST_CODE = 0; // 请求码
    private boolean isRequireCheck; // 是否需要系统权限检测3
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
            android.Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE
    };
    private PermissionsChecker mPermissionsChecker;//检查权限
    private static final int PERMISSION_REQUEST_CODE = 0;        // 系统权限返回码
    private static final String PACKAGE_URL_SCHEME = "package:";

    private TextView main_title_TextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityUtils.getInstance().showSetPermissionDialog("location", MainActivity.this);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
//                    return;
                }
                mPermissionsChecker = new PermissionsChecker(this);
                isRequireCheck = true;
            } catch (Exception e) {
                ActivityUtils.getInstance().showSetPermissionDialog("location", MainActivity.this);
            }

        }

        if (isRequireCheck) {
            //权限没有授权，进入授权界面
            if (mPermissionsChecker.judgePermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } else {
            isRequireCheck = true;
        }

        strs = getResources().getStringArray(R.array.strs);
        demoApplication = (DemoApplication) getApplicationContext();
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        url = HttpServerAddress.UPLOADS + acacheUserBean.getOP_NO() + ".png";
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        initView();
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastKey.MSG_HEADPIX_UPLOAD_SUCCESS);
        registerReceiver(mBroadcastReceiver, intentFilter);
        loadHeadPixImage();
        configImageLoader();
        //初始化广告
        initialize();

        //接收器的动态注册，Action必须与Service中的Action一致
        registerReceiver(br, new IntentFilter("ACTION_MY"));
        registerUpBtImg();

        circleMenu();
        //判断有没有NFC功能
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        intentService = new Intent();
        intentService.setClass(this, MyService.class);
        startService(intentService);


    }

    private void registerUpBtImg() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPBTIMG");
        filter.addAction("UPBTIMG_DIS");
        registerReceiver(upbtimg, filter);
    }

    private void initialize() {

        cycleViewPager = (CycleViewPager) getFragmentManager()
                .findFragmentById(R.id.fragment_cycle_viewpager_content);

        for (int i = 0; i < imageUrls.length; i++) {
            ADInfo info = new ADInfo();
            info.setUrl(imageUrls[i]);
            info.setContent("图片-->" + i);
            infos.add(info);
        }

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, infos.get(infos.size() - 1).getUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(this, infos.get(i).getUrl()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, infos.get(0).getUrl()));

        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true);

        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, mAdCycleViewListener);
        //设置轮播
        cycleViewPager.setWheel(true);

        // 设置轮播时间，默认5000ms
        cycleViewPager.setTime(3000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    /**
     * 标题栏点击事件
     */
    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
                //Toast.makeText(MainActivity.this, "position-->" + info.getContent(), Toast.LENGTH_SHORT).show();
            }
        }

    };

    /**
     * 配置ImageLoder
     */

    private void configImageLoader() {
        // 初始化ImageLoader
//        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.mipmap.top_banner_android) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.top_banner_android) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    private void loadHeadPixImage() {
        Bitmap bt = BitmapFactory.decodeFile(path + acacheUserBean.getOP_NO() + ".png");// 从Sd中找头像，转换成Bitmap
        if (bt != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);// 转换成drawable
            userInfo.setImageDrawable(drawable);
        } else {
            getHead();
        }

    }

    private void getHead() {
        Thread threadHead = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    new GetImgHead().run(url, acacheUserBean.getOP_NO() + ".png");
                    uphead.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        threadHead.start();
    }

    private Handler uphead = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bt = BitmapFactory.decodeFile(path + acacheUserBean.getOP_NO() + ".png");// 从Sd中找头像，转换成Bitmap
            if (bt != null) {
                @SuppressWarnings("deprecation")
                Drawable drawable = new BitmapDrawable(bt);// 转换成drawable
                userInfo.setImageDrawable(drawable);
            }
        }

    };

    private void circleMenu() {
//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//        wmParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        GossipView gossipView = (GossipView) findViewById(R.id.gossipview);
//        wm.addView(gossipView, wmParams);

        final List<GossipItem> items = new ArrayList<GossipItem>();
        for (int i = 0; i < strs.length; i++) {
            GossipItem item = new GossipItem(strs[i], 3);
            items.add(item);
        }
        gossipView.setItems(items);
        gossipView.setNumber(3);
        gossipView
                .setOnPieceClickListener(new GossipView.OnPieceClickListener() {
                    @Override
                    public void onPieceClick(int index) {
                        Intent it;
                        switch (index) {
                            case 0:
                                it = new Intent(MainActivity.this, LockRegisteredActivity.class);
                                startActivity(it);
                                break;
                            case 1:
                                it = new Intent(MainActivity.this, SearchActivity.class);
                                startActivity(it);
                                break;
                            case 2:
                                it = new Intent(MainActivity.this, MoreMuchActivity.class);
                                startActivity(it);
                                break;
                            case 3:
                                it = new Intent(MainActivity.this, TaskAndSendListActivity.class);
                                startActivity(it);
                                break;
                            case 4:
                                it = new Intent(MainActivity.this, TaskAndSendActivity.class);
                                startActivity(it);
                                break;
                            case 5:
                                it = new Intent(MainActivity.this, UnlockRecordActivity.class);
                                startActivity(it);
                                break;
                            case -1:
                                Log.e("MainActivity", "onPieceClick: demoApplication.getMS() = " + demoApplication.getMS());
                                //判断是否有网络
                                if (isNetworkAvailable(MainActivity.this)) {
                                    //判断是否有NFC功能
                                    checkNFC();
                                } else {
                                    //判断是蓝牙模式还是NFC模式
                                    if (demoApplication.getMS() == 1) {//蓝牙转发模式
                                        //判断当前权限是否可以开锁
                                        checkCurrentOpenPermiss();
                                    } else if (demoApplication.getMS() == 0) {
                                        if (mAdapter != null) {//NFV模式
                                            if (!mAdapter.isEnabled()) {
                                                new AlertDialog.Builder(getApplication()).setTitle("NFC关闭了").setMessage("去设置")
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                                            }
                                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        System.exit(0);
                                                    }
                                                }).show();
                                            }
                                            //判断当前权限是否可以开锁
                                            checkCurrentOpenPermiss();
                                        } else {
                                            new AlertDialog.Builder(MainActivity.this).setTitle("没有可用的NFC,是否进行手动解锁?")
                                                    .setNeutralButton("手动解锁", new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(MainActivity.this, GuideActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                                                public void onClick(DialogInterface dialoginterface, int i) {
                                                }
                                            }).show();
                                        }
                                    } else if (demoApplication.getMS() == 2) {//蓝牙直连模式
                                        checkCurrentOpenPermiss();
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    /**
     * 判断当前权限是否可以开锁
     */
    public void checkCurrentOpenPermiss() {
        //判断当前权限是否可以开锁
        AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        BeginTime = LoginInfo.getBeginTime().toString();
        EndTime = LoginInfo.getEndTime().toString();
        Date currentTime = new Date();
        //获取缓存数据
        String strBeginTime = BeginTime.replaceAll("/", "-");
        String strEndTime = EndTime.replaceAll("/", "-");
        Log.e(TAG, "dateTime: strBeginTime = " + strBeginTime);
        Log.e(TAG, "dateTime: strEndTime = " + strEndTime);
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strbeginDate = null;
        Date strendDate = null;
        try {
            strbeginDate = fmt.parse(strBeginTime.toString());
            strendDate = fmt.parse(strEndTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((currentTime.getTime() - strbeginDate.getTime()) > 0 &&
                (strendDate.getTime() - currentTime.getTime()) > 0) {

            //没网的状态下，可以开锁三次，把开锁结果存储在数据库中
            dateBaseUtil = new DateBaseUtil(MainActivity.this);
            list = dateBaseUtil.queryUnLock();
            if (list.size() > 14) {
                ToastUtil.MyToast(MainActivity.this, "Has been unlocked 15 times, please unlock after networking");
            } else {
                ToastUtil.MyToast(MainActivity.this, "There is currently no network, only 15 times offline.");
                if (list.size() <= 14) {
                    Intent intent = new Intent(MainActivity.this, UnlockActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            new AlertDialog.Builder(MainActivity.this).setTitle("The unlocking time has expired. " +
                    "Do you want to re-apply for permission?")
                    .setNeutralButton("重新申请", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, TaskApplyActivitys.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("取消", null).show();
        }
    }

    private void initView() {
        if (demoApplication.getMS() == 1) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBtAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
        userInfo = (XCRoundImageView) findViewById(R.id.userInfo);
        imgBtnAdd = (ImageButton) findViewById(R.id.imgBtn_Add);
        mainTitle = (LinearLayout) findViewById(R.id.mainTitle);
        img_bt = (ImageView) findViewById(R.id.img_bt);
        if (demoApplication.getMS() == 1) {
            img_bt.setVisibility(View.VISIBLE);
        } else {
            img_bt.setVisibility(View.INVISIBLE);
        }
        if (demoApplication.getConnect() == 1) {
            img_bt.setImageResource(R.mipmap.bluet);
        }
        img_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intentcon = new Intent();
//                intentcon.setAction("CONNECT");
//                sendBroadcast(intentcon);
                if (demoApplication.getConnect() == 1) {

                } else {
//                    if (btimgbg) {
//                        demoApplication.connect();
//                        btimgbg = false;
//                    }
                    ActivityUtils.getInstance().showSelecctBluetoothDialog(MainActivity.this, demoApplication);
                }
            }
        });
        userInfo.setOnClickListener(this);
        imgBtnAdd.setOnClickListener(this);

        main_title_TextView = (TextView) findViewById(R.id.main_title_TextView);
        main_title_TextView.setText("智能锁");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (demoApplication.getMS() == 1 || demoApplication.getMS() == 2) {
            img_bt.setVisibility(View.VISIBLE);
        } else {
            img_bt.setVisibility(View.INVISIBLE);
        }
    }


//    public void dzoc(View view) {
//        Intent its = new Intent(MainActivity.this, LockRegisteredActivity.class);
//        startActivity(its);
//    }

    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (adViewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadHeadPixImage();
        demoApplication = (DemoApplication) getApplicationContext();
        if (demoApplication.getMS() == 1) {
            img_bt.setVisibility(View.VISIBLE);
            if (demoApplication.getConnect() == 1) {
                img_bt.setImageResource(R.mipmap.bluet);
            } else {
                img_bt.setImageResource(R.mipmap.bluef);
            }
        } else {
            img_bt.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentService);
        unregisterReceiver(upbtimg);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userInfo:
                Intent intent1 = new Intent(this, UserInfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.imgBtn_Add:
                showPopupWindow(mainTitle);
                break;
            case R.id.linear1:
                Intent intent3 = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent3);
                popupWindow.dismiss();
                break;
            case R.id.linear2:
//                ToastUtil.MyToast(getApplicationContext(), "退出");
//                final Intent intent = new Intent();
//                intent.setAction("ITOP.MOBILE.SIMPLE.SERVICE.SENSORSERVICE");
//                stopService(intent);
//                Log.e("TAG", "服务关闭了");
                Intent intentdis = new Intent();
                intentdis.setAction("DISCONNECT");
                sendBroadcast(intentdis);
                //stopService(intentService);
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.linear3:
                ToastUtil.MyToast(getApplicationContext(), "关于");
                popupWindow.dismiss();
                break;
            case R.id.linear4:
                Intent intents = new Intent(MainActivity.this, SetMsActivity.class);
                //intents.setAction("contect");
                //sendBroadcast(intents);
                startActivity(intents);
                popupWindow.dismiss();
                //finish();
                break;
            default:
                break;
        }
    }

    private void showPopupWindow(View mainTitle) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.add_popupwindow, null);
        layout1 = (LinearLayout) contentView.findViewById(R.id.linear1);
        layout2 = (LinearLayout) contentView.findViewById(R.id.linear2);
        layout3 = (LinearLayout) contentView.findViewById(R.id.linear3);
        layout4 = (LinearLayout) contentView.findViewById(R.id.linear4);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);

        popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);// 设置允许在外点击消失
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        // 设置好参数之后再show
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        manager.getDefaultDisplay().getSize(size);
        int xpos = size.x - popupWindow.getWidth();
        // xoff,yoff基于anchor的左下角进行偏移。
        popupWindow.showAsDropDown(mainTitle, xpos, 10);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.MyToast(getApplicationContext(), "再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                if (demoApplication.getConnect() == 1) {
                    demoApplication.disconnect();
                }
                stopService(intentService);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //判断手机有没有NFC功能
    private void checkNFC() {
        if (demoApplication.getMS() == 1 || demoApplication.getMS() == 2) {
            requestData();
        } else if (demoApplication.getMS() == 0){
            if (mAdapter != null) {
                if (!mAdapter.isEnabled()) {
                    new AlertDialog.Builder(this).setTitle("NFC关闭了").setMessage("去设置")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).show();
                }
                requestData();
            } else {
                new AlertDialog.Builder(MainActivity.this).setTitle("没有可用的NFC,是否进行手动解锁?")
                        .setNeutralButton("手动解锁", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, GuideActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "蓝牙已打开 ", Toast.LENGTH_SHORT).show();
                    btqx = true;
                } else {
                    if (!btqx) {
                        //finish();
                    }
                }
                break;
            case ActivityUtils.REQUEST_SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (deviceAddress != null) {
                        demoApplication.adconnect(deviceAddress);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        System.out.println("重启MAIN");
        btimgbg = true;
        super.onStart();
    }

    //判断当前时间是否已经过期，如果过期，无法进入开锁界面
    private void dateTime() {
        //获取当前系统时间
        Date currentTime = new Date();
        //获取缓存数据
        String strBeginTime = BeginTime.replaceAll("/", "-");
        String strEndTime = EndTime.replaceAll("/", "-");
        Log.e(TAG, "dateTime: strBeginTime = " + strBeginTime);
        Log.e(TAG, "dateTime: strEndTime = " + strEndTime);
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strbeginDate = null;
        Date strendDate = null;
        try {
            strbeginDate = fmt.parse(strBeginTime.toString());
            strendDate = fmt.parse(strEndTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((currentTime.getTime() - strbeginDate.getTime()) > 0 && (strendDate.getTime() - currentTime.getTime()) > 0) {

            Intent intent = new Intent(MainActivity.this, UnlockActivity.class);
            startActivity(intent);

        } else {
            new AlertDialog.Builder(MainActivity.this).setTitle("开锁时间已经到期，是否重新申请权限？")
                    .setNeutralButton("重新申请", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, TaskApplyActivitys.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() { /* 设置跳出窗口的返回事件 */
                public void onClick(DialogInterface dialoginterface, int i) {
                }
            }).show();
        }
    }

    //每次进入开锁界面时请求请求一次网络，获取一次当前数据
    private void requestData() {
        try {
            //等待网络的D
            dialog = new LoadingDialog(MainActivity.this, R.style.dailogStyle);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            // 1.创建请求队列
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

            final AcacheUserBean LoginInfo = (AcacheUserBean) aCache.getAsObject("LoginInfo");
            userCounext = LoginInfo.getUSER_CONTEXT().toString().trim();
            maxver = LoginInfo.getMAXVER().toString().trim();
            address = LoginInfo.getAddress().toString().trim();
            account = LoginInfo.getAccount().toString().trim();
            keyvalue = LoginInfo.getKEYVALUE().toString().trim();
            areaID = LoginInfo.getArea_id().toString().trim();

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
                            acacheUserBean.setKEYVALUE(keyvalue);
                            acacheUserBean.setArea_id(areaID);
                            acacheUserBean.setROLE_ID(arg0.getString("ROLE_ID"));
                            acacheUserBean.setUSER_CONTEXT(userCounext);
                            acacheUserBean.setAddress(address);
                            acacheUserBean.setAccount(account);

                            aCache.put("LoginInfo", acacheUserBean);// 缓存文件名称


                            BeginTime = arg0.getString("VBEGINTIME").trim();
                            EndTime = arg0.getString("VENDTIME").trim();
                            LoginInfo.setBeginTime(arg0.getString("VBEGINTIME").trim());
                            LoginInfo.setEndTime(arg0.getString("VENDTIME").trim());

                            aCache.put("LoginInfo", acacheUserBean);
                            dateTime();
                            //获取混村数据，在判断是否有NFC功能
                        } else {
//                            BeginTime=LoginInfo.getBeginTime().toString();
//                            EndTime=LoginInfo.getEndTime().toString();
//                            dateTime();
                            ToastUtil.MyToast(MainActivity.this, strState);
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

/********************************************************************************************************/
    /**
     * 权限处理
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isRequireCheck) {
//            //权限没有授权，进入授权界面
//            if (mPermissionsChecker.judgePermissions(PERMISSIONS)) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
//            }
//        } else {
//            isRequireCheck = true;
//        }
//
//    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE
//                && hasAllPermissionsGranted(grantResults)
                ) {
            isRequireCheck = true;
        }
//        else {
//            isRequireCheck = false;
//            showPermissionDialog();
//        }
    }

//    // 含有全部的权限
//    private boolean hasAllPermissionsGranted(int[] grantResults) {
//        for (int grantResult : grantResults) {
//            if (grantResult == PackageManager.PERMISSION_DENIED) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * 提示对话框
     */
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("帮助");
        builder.setMessage("定位权限没有打开,这是系统必须权限。请点击\"设置\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults[0] != PackageManager.PEMISSION_GRANTED) {
//                    Toast.makeText(demoApplication, "如果不打开定位权限,无法使用定位功能.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        }
//    }
}