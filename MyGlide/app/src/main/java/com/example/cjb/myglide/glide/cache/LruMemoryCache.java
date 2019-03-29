package com.example.cjb.myglide.glide.cache;

import android.os.Build;
import android.util.LruCache;

import com.example.cjb.myglide.glide.cache.recycle.Resource;

public class LruMemoryCache extends LruCache<Key,Resource> implements MemoryCache{

    private MemoryCache.ResourceRemoveListener resourceRemoveListener;
    public LruMemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT){
            return value.getBitmap().getAllocationByteCount();
        }else {
            return value.getBitmap().getByteCount();
        }
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        resourceRemoveListener.onResourceRemoved(oldValue);
    }

    @Override
    public void setResourceRemoveListener(ResourceRemoveListener listener) {
        this.resourceRemoveListener=listener;
    }



}
