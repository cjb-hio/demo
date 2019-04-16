package com.example.cjb.myglide.glide;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;

import com.example.cjb.myglide.glide.cache.ArrayPool;
import com.example.cjb.myglide.glide.cache.LruArrayPool;
import com.example.cjb.myglide.glide.cache.LruMemoryCache;
import com.example.cjb.myglide.glide.cache.MemoryCache;
import com.example.cjb.myglide.glide.cache.recycle.BitmapPool;
import com.example.cjb.myglide.glide.cache.recycle.DiskCache;
import com.example.cjb.myglide.glide.cache.recycle.DiskLruCacheWrapper;
import com.example.cjb.myglide.glide.cache.recycle.LruBitmapPool;
import com.example.cjb.myglide.glide.load.Engine;

import java.util.concurrent.ThreadPoolExecutor;

public class GlideBuilder {


    MemoryCache memoryCache;
    DiskCache diskCache;
    BitmapPool bitmapPool;
    Engine engine;
    ArrayPool arrayPool;
    ThreadPoolExecutor executor;
    RequestOption defaultRequestOptions;


    public Glide build(Context context){

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        //Glide缓存最大可用内存大小
        int maxSize = getMaxSize(activityManager);

        if (null == arrayPool){
            arrayPool = new LruArrayPool();
        }
        //减去数组缓存后的可用内存大小
        int availableSize = maxSize - arrayPool.getMaxSize();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        // 获得一个屏幕大小的argb所占的内存大小
        int screenSize = widthPixels * heightPixels * 4;

        //bitmap复用占 4份
        float bitmapPoolSize = screenSize * 4.0f;
        //内存缓存占 2份
        float memoryCacheSize = screenSize * 2.0f;

        if (bitmapPoolSize + memoryCacheSize <= availableSize) {
            bitmapPoolSize = Math.round(bitmapPoolSize);
            memoryCacheSize = Math.round(memoryCacheSize);
        } else {
            //把总内存分成 6分
            float part = availableSize / 6.0f;
            bitmapPoolSize = Math.round(part * 4);
            memoryCacheSize = Math.round(part * 2);
        }
        //bitmap复用池
        if (null == bitmapPool) {
            bitmapPool = new LruBitmapPool((int) bitmapPoolSize);
        }
        //内存缓存
        if (null == memoryCache) {
            memoryCache = new LruMemoryCache((int) memoryCacheSize);
        }
        //磁盘缓存
        if (null == diskCache) {
            diskCache = new DiskLruCacheWrapper(context);
        }


        if (executor == null) {
            executor = GlideExecutor.newExecutor();
        }
        engine = new Engine(memoryCache, diskCache, bitmapPool, executor);
        memoryCache.setResourceRemoveListener(engine);
        return new Glide(context, this);



    }

    private int getMaxSize(ActivityManager activityManager) {
        int size = activityManager.getMemoryClass() * 1024 * 1024;
        return (int) Math.round(size*0.4);
    }
}
