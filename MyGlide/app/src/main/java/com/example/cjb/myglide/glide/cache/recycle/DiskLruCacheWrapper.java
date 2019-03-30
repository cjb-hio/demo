package com.example.cjb.myglide.glide.cache.recycle;

import android.content.Context;

import com.example.cjb.myglide.glide.cache.Key;
import com.example.cjb.myglide.glide.diskcache.DiskLruCache;
import com.example.cjb.myglide.glide.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public class DiskLruCacheWrapper implements DiskCache {


    private MessageDigest messageDigest;
    final static int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
    final static String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";
    private DiskLruCache diskLruCache;

    public DiskLruCacheWrapper(Context context) {
        this(new File(context.getCacheDir(), DEFAULT_DISK_CACHE_DIR), DEFAULT_DISK_CACHE_SIZE);
    }

    public DiskLruCacheWrapper(File file, int defaultDiskCacheSize) {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            diskLruCache = DiskLruCache.open(file, 1, 1, defaultDiskCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getKey(Key key) {
        key.updateDiskCacheKey(messageDigest);
        return new String(Utils.sha256BytesToHex(messageDigest.digest()));
    }

    @Override
    public InputStream get(Key key) {
        String key1 = getKey(key);
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key1);
            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return inputStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void put(Key key, Writer writer) {
        String key1 = getKey(key);
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key1);
            if (snapshot != null) {
                return;
            }

            DiskLruCache.Editor edit = diskLruCache.edit(key1);
            if (edit != null) {
                OutputStream outputStream = edit.newOutputStream(0);
                if (writer.write(outputStream)) {
                    edit.commit();
                } else {
                    edit.abortUnlessCommitted();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Key key) {
        String key1 = getKey(key);
        try {
            diskLruCache.remove(key1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
