package com.xyw.smartlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.adapter.SelectUserListAdapter;
import com.xyw.smartlock.bean.UserBean;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.GetUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectUserrActivity extends Activity {
    private ListView lv_user;
    private List<UserBean> userlist;
    private List<UserBean> mlist = new ArrayList<UserBean>();
    private SelectUserListAdapter adapter;
    private String path;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Handler handler;
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_userr);
        lv_user = (ListView) findViewById(R.id.lv_select_user);
        for (int i=0;i<1;i++){
            UserBean bean = new UserBean();
            bean.setName("正在获取....");
            bean.setNumber("00000000000");
            mlist.add(bean);
        }
        adapter = new SelectUserListAdapter(this,mlist);
        lv_user.setAdapter(adapter);
        aCache = ACache.get(this);
        acacheUserBean = new AcacheUserBean();
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        path = HttpServerAddress.BASE_URL
                + "?m=GetOpList&op_no=" + acacheUserBean.getOP_NO()
                + "&user_context=" + acacheUserBean.getUSER_CONTEXT();
        //网络获取
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                List<UserBean> list = (List<UserBean>) msg.obj;
                userlist = list;
                adapter.upData(userlist);
            }
        };
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.obj = new GetUser().run(path);
                    msg.setTarget(handler);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lv_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (userlist.size()>1){
                    intent.putExtra("username", userlist.get(position).getName());
                    intent.putExtra("usernumber",userlist.get(position).getNumber());
                }else {
                    intent.putExtra("username", "全部");
                    intent.putExtra("usernumber","");
                }
                SelectUserrActivity.this.setResult(RESULT_OK, intent);
                // 关闭Activity
                SelectUserrActivity.this.finish();
            }
        });
    }
    Thread thread = new Thread(){
        @Override
        public void run() {
            super.run();
            try {
                userlist = new GetUser().run(path);
                if (!userlist.get(0).getName().equals("正在获取....")){
                    lv_user.setAdapter(new SelectUserListAdapter(SelectUserrActivity.this,userlist));
                   // handler.sendEmptyMessage(0);
                   // adapter.notifyDataSetChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    /*Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };*/
}
