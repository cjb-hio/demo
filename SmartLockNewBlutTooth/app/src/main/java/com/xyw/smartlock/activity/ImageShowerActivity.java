package com.xyw.smartlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.ImageLoadingDialog;


/**
 * Created by 19428 on 2016/11/5.
 */
public class ImageShowerActivity extends Activity implements View.OnClickListener {
    private ImageView imageShower;
    private ImageLoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageshower);
        Intent intent = getIntent();

        imageShower = (ImageView) findViewById(R.id.imageShower);
        dialog = new ImageLoadingDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        if (null != intent) {
            loadHeadPixImage(intent.getStringExtra("SIGNIMG"));
        }

//        // 两秒后关闭后dialog
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//            }
//        }, 1000 * 1);
//        imageShower.setOnClickListener(this);
    }


    private void loadHeadPixImage(String str1) {
        String url = HttpServerAddress.UPLOADS + str1;
//        SLImageLoader.getInstance().loadImagePix(url, imageShower);
        ImageLoader.getInstance().displayImage(url, imageShower, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                dialog.show();
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                dialog.dismiss();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                dialog.dismiss();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
//        finish();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                ImageShowerActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageShower:
                ImageShowerActivity.this.finish();
                break;
        }
    }
}
