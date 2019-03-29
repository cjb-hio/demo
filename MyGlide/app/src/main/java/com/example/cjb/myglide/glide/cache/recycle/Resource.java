package com.example.cjb.myglide.glide.cache.recycle;

import android.graphics.Bitmap;

import com.example.cjb.myglide.glide.cache.Key;

public class Resource {


    private int acquired;
    private Bitmap bitmap;
    private ResourceRelease releaseListener;
    private Key key;
    private Resource resource;

    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * 计数器+1
     */
    public void acquire() {
        if(bitmap.isRecycled()){
            throw new IllegalStateException("资源已经回收,无法计数器加1");
        }
        acquired++;
    }

    /**
     * 计数器-1
     */
    public void release() {
        if(--acquired==0){
            //引用计数器为0
            releaseListener.onResourceRelease(key,resource);
        }
    }

    public interface ResourceRelease{
        public void onResourceRelease(Key key,Resource resource);
    }

    public void setReleaseListener(ResourceRelease releaseListener) {
        this.releaseListener = releaseListener;
    }
}
