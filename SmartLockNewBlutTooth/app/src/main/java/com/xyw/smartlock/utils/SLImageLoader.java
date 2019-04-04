package com.xyw.smartlock.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.xyw.smartlock.R;

/**
 * Created by Administrator on 2016/5/15.
 */
public class SLImageLoader {

    private static SLImageLoader mSLImageLoader;
    private DisplayImageOptions options;
    private DisplayImageOptions options2;
    private ImageLoader imageLoader;
    private Context context;

    private SLImageLoader() {
        initOptions();
        initOptions2();
    }

    public static SLImageLoader getInstance() {
        if (mSLImageLoader == null) {
            mSLImageLoader = new SLImageLoader();
        }
        return mSLImageLoader;
    }

    private void initOptions() {
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(true) // default
                .cacheOnDisc(true)
                .delayBeforeLoading(10)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) // default
                .bitmapConfig(Bitmap.Config.ARGB_4444) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();
    }

    private void initOptions2() {
        options2 = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.icon_error) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(true) // default
                .cacheOnDisc(true)
                .delayBeforeLoading(10)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) // default
                .bitmapConfig(Bitmap.Config.ARGB_4444) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();
    }

    public void loadImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    public void loadImagePix(String url, ImageView imageView) {

        ImageLoader.getInstance().displayImage(url, imageView, options2);
    }
}
