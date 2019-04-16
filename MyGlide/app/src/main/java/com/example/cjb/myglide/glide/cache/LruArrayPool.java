package com.example.cjb.myglide.glide.cache;

import android.util.LruCache;

public class LruArrayPool implements ArrayPool {

    private final int maxSize;
    private LruCache cache;
    public static final int ARRAY_POOL_SIZE_BYTES = 4 * 1024 * 1024;
    public LruArrayPool() {
        this(ARRAY_POOL_SIZE_BYTES);
    }

    public LruArrayPool(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LruCache<Integer, byte[]>(maxSize) {
            @Override
            protected int sizeOf(Integer key, byte[] value) {
                return value.length;
            }


            @Override
            protected void entryRemoved(boolean evicted, Integer key, byte[] oldValue, byte[] newValue) {

            }
        };
    }


    @Override
    public byte[] get(int len) {
        return new byte[0];
    }

    @Override
    public void put(byte[] data) {

    }

    @Override
    public void clearMemory() {

    }

    @Override
    public void trimMemory(int level) {

    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }
}
