package com.example.cjb.myglide.glide.cache.recycle;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.NavigableMap;
import java.util.TreeMap;

public class LruBitmapPool extends LruCache<Integer,Bitmap> implements BitmapPool {

    NavigableMap<Integer, Integer> map = new TreeMap<>();

    private final static int MAX_OVER_SIZE_MULTIPLE = 2;
    private boolean isRemoved;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    public void put(Bitmap bitmap) {
        int size;
        if (!bitmap.isMutable()) {
            bitmap.recycle();
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            size = bitmap.getAllocationByteCount();
        } else {
            size = bitmap.getByteCount();
        }

        if (size >= size()) {
            bitmap.recycle();
            return;
        }
        put(size, bitmap);
        map.put(size, 0);
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        int size = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);

        Integer key = map.ceilingKey(size);
        if (key != null && key < size * MAX_OVER_SIZE_MULTIPLE) {
            isRemoved=true;
            Bitmap remove = remove(key);
            isRemoved=false;
            return remove;
        }
        return null;
    }
}
