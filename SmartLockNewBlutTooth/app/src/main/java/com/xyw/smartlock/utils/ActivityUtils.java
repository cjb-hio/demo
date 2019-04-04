package com.xyw.smartlock.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.activity.BTselectActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HP on 2017/5/10.
 */

public class ActivityUtils {

    private PermissionsChecker mPermissionsChecker;//检查权限

    private static ActivityUtils mActivityUtils;
    public static final int REQUEST_SELECT_DEVICE = 100;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int PERMISSION_REQUEST_CODE = 5;

    public static ActivityUtils getInstance() {
        if (null == mActivityUtils) {
            synchronized (ActivityUtils.class) {
                if (null == mActivityUtils) {
                    mActivityUtils = new ActivityUtils();
                }
            }
        }
        return mActivityUtils;
    }

    protected ActivityUtils(){
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param colorResId
     */
    public void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSelecctBluetoothDialog(Activity context, DemoApplication demoApplication) {
        if (BluetoothUtils.getInstance().isHasBluetooth()) {
            if (!BluetoothUtils.getInstance().isOpenBluetooth()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
            if (demoApplication.getConnect() == 0) {
                Intent newIntent = new Intent(context, BTselectActivity.class);
                context.startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
            }
        } else {
            Toast.makeText(context, "不支持蓝牙设备", Toast.LENGTH_SHORT).show();
        }
    }



    public Uri getUri(Context context, File file) {
        Uri photoGraphUri = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            photoGraphUri = Uri.fromFile(file);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            photoGraphUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//            photoGraphUri = FileProvider.getUriForFile(context, "com.xyw.mysmartlock.fileprovider", file);
        }
        return photoGraphUri;
    }

    public boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void showSetPermissionDialog(String permission, final Context context) {
        new AlertDialog.Builder(context).setMessage(permission + context.getString(R.string.permission_prohibit))
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAppDetailSettingIntent(context);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    /**
     * 转换 MAC 值
     *
     * @param mac
     * @return
     */
    public String getEnterMac(String mac) {
        String enterMac = mac.substring(0, 2) + mac.substring(3, 5) + mac.substring(6, 8)
                + mac.substring(9, 11) + mac.substring(12, 14) + mac.substring(15, 17);
        return enterMac;
    }

    public String getTimeScale() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String timeScale = sdf.format(date);
        Log.e("ActivityUtils", "getTimeScale: timeScale = " + timeScale);
        return timeScale;
    }

    /**
     * '
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            } else {
                NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
                if (infos == null) {
                    return false;
                } else {
                    for (NetworkInfo info : infos) {
                        if (info.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置textview下划线
     * @param textView
     */
    public void setTextUnderLine(TextView textView) {
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        textView.getPaint().setAntiAlias(true);//抗锯齿
        textView.getPaint().setUnderlineText(false);
    }

    public boolean checkPermission(Activity activity) {
        mPermissionsChecker = new PermissionsChecker(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //权限没有授权，进入授权界面
            if (mPermissionsChecker.judgePermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取系统当前时间
     * @param pattern
     * @return
     */
    public String getCurrentTime(String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(pattern);
        return fmt.format(new Date());
    }
}
