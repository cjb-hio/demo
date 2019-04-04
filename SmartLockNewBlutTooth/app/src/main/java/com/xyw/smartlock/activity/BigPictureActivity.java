package com.xyw.smartlock.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.common.HttpServerAddress;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.SLImageLoader;

public class BigPictureActivity extends AppCompatActivity {
    private AcacheUserBean acacheUserBean;
    private ACache aCache;
    private ImageView big_headPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_picture);
        getSupportActionBar().hide();
        acacheUserBean = new AcacheUserBean();
        aCache = ACache.get(this);
        acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
        big_headPhoto = (ImageView) findViewById(R.id.big_headPhoto);
//        Intent intent = getIntent();
//        Bitmap bitmap = intent.getParcelableExtra("BitMap");
        String url = HttpServerAddress.UPLOADS + acacheUserBean.getOP_NO() + ".png";
        SLImageLoader.getInstance().loadImagePix(url, big_headPhoto);
//        big_headPhoto.setImageBitmap(bitmap);
    }

}
