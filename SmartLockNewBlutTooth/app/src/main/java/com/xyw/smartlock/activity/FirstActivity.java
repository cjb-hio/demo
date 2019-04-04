package com.xyw.smartlock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.Splash;

public class FirstActivity extends AppCompatActivity {

    private ImageView BackImageView;
    private TextView titleText;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getSupportActionBar().hide();


        BackImageView = (ImageView) findViewById(R.id.common_title_back);
        titleText = (TextView) findViewById(R.id.common_tv_title);
        titleText.setText(R.string.login);

        BackImageView.setVisibility(View.GONE);

        //定义一个setting记录App是第几次启动
        SharedPreferences setting=getSharedPreferences("count",0);
        Boolean user_first=setting.getBoolean("FIRST",true);
        if (user_first){//第一次跳转到欢迎页面
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), NavigationActivity.class);
            startActivity(intent);
            setting.edit().putBoolean("FIRST",false).commit();
            finish();
        }else{//如果第二次启动直接跳转到广告页面
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), Splash.class);
            startActivity(intent);
            finish();
        }




//        //读取SharedPreferences中需要的数据
//        preferences = getSharedPreferences("count", MODE_WORLD_READABLE);
//        int count = preferences.getInt("count", 0);
//        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
//        if (count == 0) {
//              Intent intent = new Intent();
//            intent.setClass(getApplicationContext(), NavigationActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//              Intent intent = new Intent();
//            intent.setClass(getApplicationContext(), Splash.class);
//            startActivity(intent);
//            finish();
//        }
//
//        Editor editor = preferences.edit();
//        //存入数据
//        editor.putInt("count", ++count);
//        //提交修改
//        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
