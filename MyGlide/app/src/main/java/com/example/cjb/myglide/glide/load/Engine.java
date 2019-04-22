package com.example.cjb.myglide.glide.load;

import android.util.LruCache;

import com.example.cjb.myglide.glide.cache.MemoryCache;
import com.example.cjb.myglide.glide.cache.recycle.BitmapPool;
import com.example.cjb.myglide.glide.cache.recycle.DiskCache;
import com.example.cjb.myglide.glide.cache.recycle.Resource;

import java.util.concurrent.ThreadPoolExecutor;

public class Engine implements MemoryCache.ResourceRemoveListener {

    public Engine(MemoryCache memoryCache, DiskCache diskCache, BitmapPool bitmapPool, ThreadPoolExecutor executor) {

    }

    @Override
    public void onResourceRemoved(Resource resource) {

    }
}
