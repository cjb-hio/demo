package com.xyw.smartlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.adapter.TaskListAdapter;
import com.xyw.smartlock.bean.TaskBean;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.GetTaskList;
import com.xyw.smartlock.utils.OnItemClickListener;
import com.xyw.smartlock.utils.PostCancelTask;
import com.xyw.smartlock.view.ListViewDecoration;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TaskListActivityNew extends AppCompatActivity {
    private TextView title;
    private ImageView backImg;
    private String op_no;
    private String user_context_number;
    private AcacheUserBean acacheUserBean;
    private ACache mCache;
    private String datapath;
    private List<TaskBean> mlist;
    private SwipeMenuRecyclerView lv_task;
    private TaskListAdapter adapter;
    //请求网络的等待弹框
    private String role_id;
    private LoadingDialog loadingDialog;
    private SwipeRefreshLayout refreshableView;
    private DemoApplication demoApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_tasklist);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplication();

        // 缓存数据
        mCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        // 读取缓存数据
        acacheUserBean = (AcacheUserBean) mCache.getAsObject("LoginInfo");
        op_no = acacheUserBean.getOP_NO();
        user_context_number = acacheUserBean.getUSER_CONTEXT();
        role_id = acacheUserBean.getROLE_ID();
        mlist = new ArrayList<>();
        datapath = HttpServerAddress.GETLOCKTASKLIST + "&op_no=" + op_no + "&user_context=" + user_context_number;

        // 初始化标题栏和返回按钮
        initview();
    }

    private void getData() {
        loadingDialog.show();
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                GetTaskList taskList = new GetTaskList();
                try {
                    mlist.clear();
                    mlist.addAll(taskList.run(datapath));
                    loadingDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        thread.start();
    }

    private void initview() {
        // 设置标题栏名称
        title = (TextView) findViewById(R.id.common_tv_title);
        title.setText(R.string.taskList);
        // 监听返回按钮
        backImg = (ImageView) findViewById(R.id.common_title_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //绑定列表
        lv_task = (SwipeMenuRecyclerView) findViewById(R.id.tasklist_listview);
        refreshableView = (SwipeRefreshLayout) findViewById(R.id.refreshable_view);

        lv_task.setLayoutManager(new LinearLayoutManager(TaskListActivityNew.this));
        lv_task.setHasFixedSize(true);//高度设置为自定义
        lv_task.setItemAnimator(new DefaultItemAnimator());//设置默认动画，加也行，不加也行
        lv_task.addItemDecoration(new ListViewDecoration(getApplicationContext()));// 添加分割线。
        refreshableView.setOnRefreshListener(onRefreshListener);

        adapter = new TaskListAdapter(TaskListActivityNew.this, mlist);
        adapter.setOnItemClickListener(onItemClickListener);
        lv_task.setAdapter(adapter);

        //设置菜单创建器
        lv_task.setSwipeMenuCreator(swipeMenuCreator);
        //设置菜单Item点击监听
        lv_task.setSwipeMenuItemClickListener(nenuItemCliclListemer);

        //等待小弹框
        loadingDialog = new LoadingDialog(this, R.style.dailogStyle);
        loadingDialog.setCanceledOnTouchOutside(false);

        //获取列表信息
        if (ActivityUtils.getInstance().isNetworkAvailable(TaskListActivityNew.this)) {
            getData();
        } else {
            Toast.makeText(TaskListActivityNew.this, R.string.net_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 菜单创建器，在Item要创建菜单时调用
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.controls_80);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            //添加右侧菜单
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(TaskListActivityNew.this)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.del_icon_normal)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);

                swipeRightMenu.addMenuItem(deleteItem);
            }
        }
    };

    public static final int REQ_CODE_TASK_INFORMATION = 3;
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            TaskBean bean = mlist.get(position);
            Intent intent = new Intent();
            intent.putExtra("area", bean.getArea());
            intent.putExtra("startTime", bean.getStartTime());
            intent.putExtra("endTime", bean.getEndTime());
            intent.putExtra("content", bean.getContent());
            intent.putExtra("path1", bean.getPath1());

            intent.putExtra("path2", bean.getPath2());
            intent.putExtra("taskno", bean.getTask_no());
            intent.putExtra("ret_v", bean.getRET_V());
            intent.putExtra("role_id", role_id);
            intent.putExtra("name", bean.getMyName());
            intent.setClass(TaskListActivityNew.this, TaskInformation.class);
            startActivityForResult(intent,REQ_CODE_TASK_INFORMATION);
//            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_TASK_INFORMATION) {
            if (resultCode == Activity.RESULT_OK) {
                mlist.clear();
                adapter.notifyDataSetChanged();
                loadingDialog.show();
                demoApplication.handlerPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mlist.addAll(new GetTaskList().run(datapath));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 刷新监听
     */
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            if (ActivityUtils.getInstance().isNetworkAvailable(TaskListActivityNew.this)) {
                demoApplication.handlerPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final List<TaskBean> list = new GetTaskList().run(datapath);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mlist.clear();
                                    adapter.notifyDataSetChanged();
                                    mlist.addAll(list);
                                    refreshableView.setRefreshing(false);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Toast.makeText(TaskListActivityNew.this, R.string.net_error, Toast.LENGTH_SHORT).show();
                refreshableView.setRefreshing(false);
            }
        }
    };

    /**
     * 菜单监听事件
     */
    private OnSwipeMenuItemClickListener nenuItemCliclListemer = new OnSwipeMenuItemClickListener() {

        @Override
        public void onItemClick(com.yanzhenjie.recyclerview.swipe.Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();//关闭被点击的菜单
            final TaskBean taskBean = mlist.get(adapterPosition);
            if (menuPosition == 0) {//删除按钮被点击
                if (taskBean.getRET_V().equals("审核中")) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            PostCancelTask task = new PostCancelTask();
                            try {
                                String org0 = task.run(HttpServerAddress.DELETELOCKTASK + "&Task_no=" + taskBean.getTask_no() + "&user_context=" + user_context_number);
                                if (org0.equals("true")) {
                                    Looper.prepare();
                                    mlist.remove(adapterPosition);
                                    adapter.notifyItemRemoved(adapterPosition);
                                    Toast.makeText(TaskListActivityNew.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                } else {
                    Toast.makeText(TaskListActivityNew.this, "该任务已成立", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
