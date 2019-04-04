package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.adapter.MyRecordAdapter;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.GetFromRecord;
import com.xyw.smartlock.utils.PostTaskCheck;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskInformation extends AppCompatActivity {
    /**
     * 录音存储路径
     */
    private static final String PATH = "/sdcard/MyVoiceForder/";
    /**
     * 用于语音播放
     */
    private MediaPlayer mPlayer = null;
    private String PathCheck;
    private String task_no;
    private TextView tv_name;
    private TextView fromArea;
    private TextView fromStartDate;
    private TextView fromEndDate;
    private TextView fromContent;
    private TextView tv_zhuangtai;
    private ImageView backImg;
    private Button bt_true;
    private Button bt_false;
    private String Path;
    private String path1 = null;
    private String path2 = null;
    private String ret_v;
    private String mName1;
    private String mName2;
    private String role_id;
    private ListView lv_from;
    private TextView title;

    private String user_context_number;
    private AcacheUserBean acacheUserBean;
    private ACache mCache;
    private List<String> mList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_information);
        getSupportActionBar().hide();
        File destDir = new File("/sdcard/MyVoiceForder");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        initView();
        Intent intent = getIntent();
        if (null != intent) {
            tv_name.setText(intent.getStringExtra("name"));
            fromArea.setText(intent.getStringExtra("area"));
            fromStartDate.setText(intent.getStringExtra("startTime"));
            fromEndDate.setText(intent.getStringExtra("endTime"));
            fromContent.setText(intent.getStringExtra("content"));
            task_no = intent.getStringExtra("taskno");
            ret_v = intent.getStringExtra("ret_v");
            role_id = intent.getStringExtra("role_id");
            if (role_id.equals("1")) {
                if (!ret_v.equals("审核中")) {
                    bt_true.setVisibility(View.GONE);
                    bt_false.setVisibility(View.GONE);
                    tv_zhuangtai.setVisibility(View.VISIBLE);
                    if (ret_v.equals("审核失败")) {
                        tv_zhuangtai.setText("申请失败");
                        tv_zhuangtai.setBackgroundResource(R.drawable.zhuangtai_false_bg);
                        tv_zhuangtai.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        tv_zhuangtai.setText("审核通过");
                        tv_zhuangtai.setBackgroundResource(R.drawable.zhuangtai_true_bg);
                        tv_zhuangtai.setTextColor(getResources().getColor(R.color.zhuangtai_true));
                    }
                } else {
                    bt_true.setVisibility(View.GONE);
                    bt_false.setVisibility(View.GONE);
                    tv_zhuangtai.setVisibility(View.VISIBLE);
                    tv_zhuangtai.setText("等待审核");
                    tv_zhuangtai.setBackgroundResource(R.drawable.zhuangtai_wait_bg);
                    tv_zhuangtai.setTextColor(getResources().getColor(R.color.zhuangtai_wait));
                }
            } else {
                if (!ret_v.equals("审核中")) {
                    bt_true.setVisibility(View.GONE);
                    bt_false.setVisibility(View.GONE);
                    tv_zhuangtai.setVisibility(View.VISIBLE);
                    if (ret_v.equals("审核失败")) {
                        tv_zhuangtai.setText("申请失败");
                        tv_zhuangtai.setBackgroundResource(R.drawable.zhuangtai_false_bg);
                        tv_zhuangtai.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        tv_zhuangtai.setText("审核通过");
                        tv_zhuangtai.setBackgroundResource(R.drawable.zhuangtai_true_bg);
                        tv_zhuangtai.setTextColor(getResources().getColor(R.color.zhuangtai_true));
                    }
                }
            }
        }
        PathCheck = HttpServerAddress.CHECKLOCKTASK;
        // 缓存数据
        mCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) mCache.getAsObject("LoginInfo");
        user_context_number = acacheUserBean.getUSER_CONTEXT().toString();
        System.out.println("user_context_number=" + user_context_number);
        mName1 = intent.getStringExtra("path1");
        mName2 = intent.getStringExtra("path2");
        //mName1 = mName1.replace(" ", "");
        //mName2 = mName2.replace(" ", "");
        if (mName1.length()>1) {
            mList.add(mName1);
            if (mName2.length()>1) {
                mList.add(mName2);
            } else {
                System.out.println("第二个空");
            }
        } else {
            System.out.println("第一个空");
        }
        Path = HttpServerAddress.UPLOADS;
        path1 = Path + mName1;
        path2 = Path + mName2;
        lv_from.setAdapter(new MyRecordAdapter(TaskInformation.this, mList));
        lv_from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(PATH + mList.get(position));
                    mPlayer.prepare();
                    mPlayer.start();
                    //Toast.makeText(getApplicationContext(), mVoicesList.get(position).getRecorder(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    //Log.e(LOG_TAG, "播放失败");
                    //Toast.makeText(getApplicationContext(), mVoicesList.get(position).getRecorder(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        initRecord();
    }

    private void initView() {
        mPlayer = new MediaPlayer();
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText("任务详情");
        // 监听返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent3 = new Intent();
//                intent3.setClass(TaskInformation.this, TaskListActivityNew.class);
//                startActivity(intent3);
                finish();
            }
        });
        tv_name = (TextView) findViewById(R.id.tv_name);
        fromArea = (TextView) findViewById(R.id.tv_from_area);
        fromStartDate = (TextView) findViewById(R.id.task_from_startdate);
        fromEndDate = (TextView) findViewById(R.id.task_from_enddate);
        fromContent = (TextView) findViewById(R.id.task_from_content_application);
        tv_zhuangtai = (TextView) findViewById(R.id.tv_zhuangtai);
        bt_true = (Button) findViewById(R.id.tasktrue);
        bt_false = (Button) findViewById(R.id.taskfalse);
        lv_from = (ListView) findViewById(R.id.lv_from);
        lv_from.setAdapter(new MyRecordAdapter(TaskInformation.this, mList));
    }

    private void initRecord() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                getRecord(path1, mName1);
                getRecord(path2, mName2);
            }
        };
        thread.start();
    }

    private void getRecord(final String mpath1, final String mpath2) {
        GetFromRecord getFromRecord = new GetFromRecord();
        try {
            getFromRecord.run(mpath1,
                    mpath2);
            //handler.sendEmptyMessage(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void taskTrue(View view) {
        final String pathx = PathCheck + "&Task_no=" + task_no + "&check_result=" + "true" + "&user_context=" + user_context_number;
        final PostTaskCheck taskCheck = new PostTaskCheck();
        final Thread thread2 = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String result = taskCheck.run(pathx);
                    if (result.equals("true")) {
//                        Intent intent2 = new Intent();
//                        intent2.setClass(TaskInformation.this, TaskListActivityNew.class);
//                        startActivity(intent2);
                        setIntentResult();
                        finish();
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        final Dialog dialog = new Dialog(TaskInformation.this, R.style.dailogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 0.9f;//透明度
        View view1 = getLayoutInflater().inflate(R.layout.dailog_sure, null);
        dialog.setContentView(view1);
        Button sure = (Button) view1.findViewById(R.id.bt_sure);
        Button sure_no = (Button) view1.findViewById(R.id.bt_sure_no);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                thread2.start();
            }
        });
        sure_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void taskFalse(View view) {
        final PostTaskCheck taskCheck = new PostTaskCheck();
        final Thread thread3 = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String result = taskCheck.run(PathCheck + "&Task_no=" + task_no + "&check_result=" + "false" + "&user_context=" + user_context_number);
                    if (result.equals("true")) {
//                        Intent intent2 = new Intent();
//                        intent2.setClass(TaskInformation.this, TaskListActivityNew.class);
//                        startActivity(intent2);
                        setIntentResult();
                        finish();
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        final Dialog dialog2 = new Dialog(TaskInformation.this, R.style.dailogStyle);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = dialog2.getWindow().getAttributes();
        lp.alpha = 0.9f;//透明度
        View view1 = getLayoutInflater().inflate(R.layout.dailog_sure, null);
        dialog2.setContentView(view1);
        Button sure = (Button) view1.findViewById(R.id.bt_sure);
        Button sure_no = (Button) view1.findViewById(R.id.bt_sure_no);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                thread3.start();
            }
        });
        sure_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(TaskInformation.this, "请检查网络", Toast.LENGTH_SHORT);
        }
    };

    private void setIntentResult() {
        setResult(Activity.RESULT_OK);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent2 = new Intent();
//        intent2.setClass(TaskInformation.this, TaskListActivityNew.class);
//        startActivity(intent2);
//    }
}
