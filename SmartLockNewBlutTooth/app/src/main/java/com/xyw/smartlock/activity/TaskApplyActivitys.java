package com.xyw.smartlock.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xyw.smartlock.MainActivity;
import com.xyw.smartlock.R;
import com.xyw.smartlock.bean.RecorderBean;
import com.xyw.smartlock.bean.TaskBean;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.Fiale_dailog;
import com.xyw.smartlock.utils.PostMultipart;
import com.xyw.smartlock.utils.PostTaskData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.xyw.smartlock.R.id.num;

public class TaskApplyActivitys extends AppCompatActivity {
    private int mDay;
    private int mYear;
    private int mMonth;
    private int mHour;
    private int mMinute;
    private int mDay1;
    private int mYear1;
    private int mMonth1;
    private int mHour1;
    private int mMinute1;
    private TextView Task_StartDate = null;
    private TextView Task_StartTime = null;
    private TextView Task_EndDate = null;
    private TextView Task_EndTime = null;
    private EditText Task_content = null;
    private TextView Task_Area = null;
    private TextView title;
    private ImageView backImageView;
    private Button task_submit;
    /**
     * 语音音量显示
     */
    private Dialog dialog;
    private TextView dialog_tv;
    private int recLen = 0;
    private String Zone_Name;
    private String area_no;
    private boolean flage;
    private boolean flagere = false;
    int dai;
    //上下
    private AdapterView.AdapterContextMenuInfo menuInfo;
    String content_area1;
    private static final int ITEM1 = Menu.FIRST;

    /**
     * 录音时间
     */
    private TextView timeText;
    /**
     * log标记
     */
    private static final String LOG_TAG = "AudioRecordTest";
    /**
     * 语音文件保存路径
     */
    private String mFileName = null;
    /**
     * 按住说话按钮
     */
    private Button mBtnVoice;
    /**
     * 用于语音播放
     */
    private MediaPlayer mPlayer = null;
    /**
     * 用于完成录音
     */
    private MediaRecorder mRecorder;
    /**
     * 显示语音列表
     */
    private ListView mVoidListView;
    /**
     * 语音列表适配器
     */
    private MyListAdapter mAdapter;
    /**
     * 语音列表
     */
    private List<RecorderBean> mVoicesList;
    /**
     * 录音存储路径
     */
    private static final String PATH = "/sdcard/MyVoiceForder/Record/";
    private String dateString;
    private String path;
    private String path2;
    //请求网络的等待弹框
    private ProgressDialog progressDialog;

    private String user_context_number;
    private String user_op_name;
    private AcacheUserBean acacheUserBean;
    private ACache mCache;
    /**
     * 请求内的数据
     */
    TaskBean taskBean = new TaskBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_apply_activitys);
        getSupportActionBar().hide();
        initData();
        initView();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        startDate();
        startTime();
        endDate();
        endTime();

        // 缓存数据
        mCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        AcacheUserBean LoginInfo = (AcacheUserBean) mCache.getAsObject("LoginInfo");
        user_context_number = LoginInfo.getUSER_CONTEXT().toString();
        user_op_name = LoginInfo.getOP_NAME().toString();
        System.out.println("用户名是什么样子讷讷嗯嗯嗯=" + user_op_name);
        //System.out.println("user_context_number="+user_context_number);
        // 为所有的条目注册上下文菜单ContextMen
        registerForContextMenu(mVoidListView);// 注册上下文菜单
    }

    /*private void sendShowDateDialog(TextView tv, int what) {
        tv.setInputType(InputType.TYPE_NULL);// 关闭软键盘
        Message msg = new Message();
        msg.what = what;
        TaskApplyActivitys.this.dateandtimeHandler.sendMessage(msg);
    }*/

    /**
     * 初始化控件
     */

    private void initView() {
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.task_apply);
        // 返回上一页按钮监听
        backImageView = (ImageView) findViewById(R.id.common_title_back);
        backImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 绑定控件
        Task_StartDate = (TextView) findViewById(R.id.task_startdate);
        Task_StartTime = (TextView) findViewById(R.id.task_starttime);
        Task_EndDate = (TextView) findViewById(R.id.task_enddate);
        Task_EndTime = (TextView) findViewById(R.id.task_endtime);

        ActivityUtils.getInstance().setTextUnderLine(Task_StartDate);
        ActivityUtils.getInstance().setTextUnderLine(Task_StartTime);
        ActivityUtils.getInstance().setTextUnderLine(Task_EndDate);
        ActivityUtils.getInstance().setTextUnderLine(Task_EndTime);

        Task_Area = (TextView) findViewById(R.id.tv_area);
        task_submit = (Button) findViewById(R.id.task_submit);
        task_submit.setFocusable(true);
        Task_content = (EditText) findViewById(R.id.task_content_application);
        Task_content.setSingleLine(false);
        Task_content.setHorizontallyScrolling(false);
        Task_StartDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TaskApplyActivitys.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        Task_StartTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(TaskApplyActivitys.this, mTimeSetListener, mHour, mMinute, true).show();
            }
        });
        Task_EndDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TaskApplyActivitys.this, mDateSetListener1, mYear1, mMonth1, mDay1).show();
            }
        });
        Task_EndTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(TaskApplyActivitys.this, mTimeSetListener1, mHour1, mMinute1, true).show();
            }
        });


        mVoidListView = (ListView) findViewById(R.id.voidList);
        mAdapter = new MyListAdapter(this);
        mVoidListView.setAdapter(mAdapter);
        mVoidListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(mVoicesList.get(position).getRecorder());
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "播放失败");
                }
            }
        });

        mBtnVoice = (Button) findViewById(R.id.bt_recorder);
        mBtnVoice.setText("按住说话");
        mBtnVoice.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (ActivityUtils.getInstance().checkPermission(TaskApplyActivitys.this, Manifest.permission.RECORD_AUDIO)) {
                            Log.e("TaskApplyActivitys", "onTouch: 申请RECORD_AUDIO权限");
                            //申请RECORD_AUDIO权限
                            ActivityCompat.requestPermissions(TaskApplyActivitys.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                        } else {
                            Log.e("TaskApplyActivitys", "onTouch: 开始录音");
                            startVoice();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopVoice();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(runnable);
                        recLen = 0;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private static final int RECORD_AUDIO = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功的操作
                    startVoice();
                } else {
                    // 权限请求失败的操作
                }
                break;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mVoicesList = new ArrayList<RecorderBean>();
        mPlayer = new MediaPlayer();
    }

    boolean isRecord = false; //判断录音权限是否被禁止后开始录音
    /**
     * 开始录音
     */
    private void startVoice() {
        // 设置录音保存路径
        SimpleDateFormat format = new SimpleDateFormat("yyMMddmmss");
        dateString = format.format(new Date());
        mFileName = PATH + "s" + dateString + ".mp3";
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD Card is not mounted,It is  " + state + ".");
        }
        File directory = new File(mFileName).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            Log.d(LOG_TAG, "Path to file could not be created");
        } else if (directory.exists()) {
            Log.d(LOG_TAG, directory.getPath().toString());

        }
        showVoiceDialog();
        Log.d(LOG_TAG, "开始录音");
        // TODO 防止开权限后崩溃
        if (mRecorder != null) {
            mRecorder.reset();
        } else {
            mRecorder = new MediaRecorder();
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        try {
            //当权限被拒绝后，录音产生异常，用try catch 捕获异常并跳转到权限设置界面对权限设置进行修改
            isRecord = true;
            mRecorder.start();
        } catch (Exception e) {
            isRecord = false;
            ActivityUtils.getInstance().showSetPermissionDialog(getString(R.string.record), TaskApplyActivitys.this);
            e.printStackTrace();
        }
        // 计时线程
        //myThread();
    }

    /**
     * 显示正在录音的图标
     */
    private void showVoiceDialog() {
        dialog = new Dialog(TaskApplyActivitys.this, R.style.DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.talks_layout);
        dialog_tv = (TextView) dialog.findViewById(R.id.talk_tv);
        handler.postDelayed(runnable, 1000);
        dialog.show();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen++;
            dialog_tv.setText(recLen + "S");
            handler.postDelayed(this, 1000);
        }
    };


    /**
     * 停止录音
     */
    private void stopVoice() {
        RecorderBean bean = new RecorderBean();
        if (mVoicesList.size() < 2) {
            if (isRecord) {
                bean.setRecorder(mFileName);
                bean.setTime(recLen);
                mVoicesList.add(bean);
            }
        } else {
            Toast.makeText(getApplicationContext(), "超过条数限制", Toast.LENGTH_SHORT).show();
        }
        mAdapter = new MyListAdapter(TaskApplyActivitys.this);
        mVoidListView.setAdapter(mAdapter);
        dialog.dismiss();
        handler.removeCallbacks(runnable);
        recLen = 0;
        if (mRecorder != null)
            mRecorder.reset();
        //Toast.makeText(getApplicationContext(), "保存录音" + mFileName, Toast.LENGTH_SHORT).show();
    }

    /*--------------------------------------------------*/

    /**
     * 设置开始日期
     */
    private void startDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatstart();
    }

    /**
     * 设置结束日期
     */
    private void endDate() {
        final Calendar c = Calendar.getInstance();
        mYear1 = c.get(Calendar.YEAR);
        mMonth1 = c.get(Calendar.MONTH);
        mDay1 = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatend();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplatstart() {
        Task_StartDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                .append("-").append((mDay < 10) ? "0" + mDay : mDay));
    }

    private void updateDateDisplatend() {
        Task_EndDate.setText(new StringBuilder().append(mYear1).append("-")
                .append((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1) : (mMonth1 + 1))
                .append("-").append((mDay1 < 10) ? "0" + mDay1 : mDay1));
    }

    /**
     * 日期控件事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplatstart();
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mYear1 = year;
            mMonth1 = monthOfYear;
            mDay1 = dayOfMonth;
            updateDateDisplatend();
        }
    };

    /**
     * 设置开始时间
     */
    private void startTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        updateTimeDisplaystart();

    }

    /**
     * 设置结束时间
     */

    private void endTime() {
        final Calendar c = Calendar.getInstance();
        mHour1 = c.get(Calendar.HOUR_OF_DAY);
        mMinute1 = c.get(Calendar.MINUTE);
        updateTimeDisplayend();
    }

    /**
     * 更新时间显示
     */
    private void updateTimeDisplaystart() {
        Task_StartTime.setText(new StringBuilder().append(mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute));

    }

    private void updateTimeDisplayend() {
        Task_EndTime.setText(new StringBuilder().append(mHour1).append(":")
                .append((mMinute1 < 10) ? "0" + mMinute1 : mMinute1));

    }

    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;

            updateTimeDisplaystart();
        }
    };
    private TimePickerDialog.OnTimeSetListener mTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour1 = hourOfDay;
            mMinute1 = minute;

            updateTimeDisplayend();
        }

    };

    boolean flage2;

    public void Desubmit() {
        path = HttpServerAddress.UPLOADFILE + "&user_context=" + user_context_number;
        path2 = HttpServerAddress.INSETTLOCKTASK + "&user_context=" + user_context_number;
        taskBean.setArea(Task_Area.getText().toString());
        taskBean.setStartTime(Task_StartDate.getText().toString() + "%20" + Task_StartTime.getText().toString() + ":00");
        taskBean.setEndTime(Task_EndDate.getText().toString() + "%20" + Task_EndTime.getText().toString() + ":00");
        taskBean.setContent(Task_content.getText().toString() + "");
        flage2 = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                PostMultipart postMultipart = new PostMultipart();
                PostTaskData postTaskData = new PostTaskData();
                try {
                    switch (mVoicesList.size()) {
                        case 0:
                            String resultdata = postTaskData.run(path2 + "&ZONE_NO=" + area_no
                                    + "&BEGINDATETIME=" + taskBean.getStartTime()
                                    + "&ENDDATETIME=" + taskBean.getEndTime()
                                    + "&DEMO=" + taskBean.getContent()
                                    + "&AUDIO_PATH1=" + taskBean.getPath1()
                                    + "&AUDIO_PATH2=" + taskBean.getPath2());
                            if (resultdata.equals("true")) {
                                flage = true;
                            } else {
                                flage = false;
                            }
                            break;
                        case 1:
                            File file = new File(mVoicesList.get(0).getRecorder());
                            taskBean.setPath1(file.getName());
                            String result = postMultipart.run(path, mVoicesList.get(0).getRecorder());
                            if (result.equals("true")) {
                                String resultDate1 = postTaskData.run(path2 + "&ZONE_NO=" + area_no
                                        + "&BEGINDATETIME=" + taskBean.getStartTime()
                                        + "&ENDDATETIME=" + taskBean.getEndTime()
                                        + "&DEMO=" + taskBean.getContent()
                                        + "&AUDIO_PATH1=" + taskBean.getPath1()
                                        + "&AUDIO_PATH2=" + taskBean.getPath2());
                                if (resultDate1.equals("true")) {
                                    flage = true;
                                } else {
                                    flage = false;
                                }
                            }
                            break;
                        case 2:
                            File file0 = new File(mVoicesList.get(0).getRecorder());
                            File file1 = new File(mVoicesList.get(1).getRecorder());
                            taskBean.setPath1(file0.getName());
                            taskBean.setPath2(file1.getName());
                            String result1 = postMultipart.run(path, mVoicesList.get(0).getRecorder());
                            String result2 = postMultipart.run(path, mVoicesList.get(1).getRecorder());
                            if (result1.equals("true") && result1.equals(result2)) {
                                String resultDate2 = postTaskData.run(path2 + "&ZONE_NO=" + area_no
                                        + "&BEGINDATETIME=" + taskBean.getStartTime()
                                        + "&ENDDATETIME=" + taskBean.getEndTime()
                                        + "&DEMO=" + taskBean.getContent()
                                        + "&AUDIO_PATH1=" + taskBean.getPath1()
                                        + "&AUDIO_PATH2=" + taskBean.getPath2());
                                if (resultDate2.equals("true")) {
                                    flage = true;
                                } else {
                                    flage = false;
                                }
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                flage2 = true;
            }
        };
        thread.start();
    }

    private Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //progressDialog.dismiss();
            // TODO Auto-generated method stub
            Toast.makeText(TaskApplyActivitys.this, "提交成功", Toast.LENGTH_SHORT).show();
        }

    };
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //progressDialog.dismiss();
            // TODO Auto-generated method stub
            Fiale_dailog dailog = new Fiale_dailog(TaskApplyActivitys.this, R.style.dailogStyle);
            dailog.show();
        }

    };

    public void jump() {
        if (flage) {
            //    progressDialog.dismiss();
            // Toast.makeText(TaskApplyActivitys.this,"提交成功",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(TaskApplyActivitys.this, MainActivity.class);
            startActivity(intent);
            handler3.sendEmptyMessage(0);
            finish();
        } else {
            handler2.sendEmptyMessage(0);
        }
    }

    Thread thread2 = new Thread() {
        @Override
        public void run() {
            super.run();
            // Looper.prepare();
            //jump();
        }
    };

    public void dosub() {
        Desubmit();
        while (!flage2) {
            try {
                thread2.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //thread2.start();
        new Thread(DownloadRunnable).start();
    }

    public void submit(View view) {
        if (Task_Area.getText().toString().equals("请选择")) {
            final Dialog dialogtip = new Dialog(TaskApplyActivitys.this, R.style.dailogStyle);
            dialogtip.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = dialogtip.getWindow().getAttributes();
            lp.alpha = 0.9f;//透明度
            View view1 = getLayoutInflater().inflate(R.layout.dailog_tip_layout, null);
            dialogtip.setContentView(view1);
            Button bt_tip = (Button) view1.findViewById(R.id.bt_tip);
            bt_tip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogtip.dismiss();
                }
            });
            dialogtip.show();
        } else {
            view.setFocusable(false);//无法点击
            final Dialog dialog2 = new Dialog(TaskApplyActivitys.this, R.style.dailogStyle);
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = dialog2.getWindow().getAttributes();
            lp.alpha = 0.9f;//透明度
            View view1 = getLayoutInflater().inflate(R.layout.dailog_sure, null);
            dialog2.setContentView(view1);
            Button sure = (Button) view1.findViewById(R.id.bt_sure);
            Button sure_no = (Button) view1.findViewById(R.id.bt_sure_no);

            sure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                    dosub();
                }
            });
            sure_no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                }
            });
            dialog2.show();
        }
        //等待小弹框
        /*progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在提交数据,请稍后...");
        progressDialog.show();*/

    }

    public void selectArea(View view) {
        startActivityForResult(new Intent(TaskApplyActivitys.this, SelectAreaManagementActivity.class), 1);
    }


    /**
     * 通过长按条目激活上下文菜单
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // 添加菜单项
        super.onCreateContextMenu(menu, v, menuInfo);
        TextView tv = (TextView) v.findViewById(num);
        dai = Integer.parseInt((String) tv.getText());
        menu.add(0, ITEM1, 0, "删除");
    }

    // 上下文菜单菜单单击事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        content_area1 = mAdapter.getItem(menuInfo.position).toString();
        switch (item.getItemId()) {
            case ITEM1:
                File defile = new File(mVoicesList.get(dai).getRecorder());
                defile.delete();
                mVoicesList.remove(dai);
                break;
        }
        handlerx.sendEmptyMessage(0);
        return true;
    }

    private Handler handlerx = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 语音列表适配器
     */
    private class MyListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public MyListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mVoicesList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_voicelist, null);
                holder = new Holder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv_armName);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.num = (TextView) convertView.findViewById(R.id.num);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.num.setText(position + "");
            holder.tv.setText("消息" + (position + 1));
            holder.time.setText(mVoicesList.get(position).getTime() + "″");
            return convertView;
        }
        class Holder {
            TextView tv;
            TextView time;
            TextView num;
        }
    }

    Runnable DownloadRunnable = new Runnable() {
        @Override
        public void run() {
            jump();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        task_submit.setFocusable(true);
        if (data != null) {
            Zone_Name = data.getStringExtra("Zone_Name");//得到新Activity 关闭后返回的数据
            area_no = data.getStringExtra("Zone_No");
            Task_Area.setText(Zone_Name);
        }
    }
}
