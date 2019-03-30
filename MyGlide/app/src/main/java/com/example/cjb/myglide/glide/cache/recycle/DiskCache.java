package com.example.cjb.myglide.glide.cache.recycle;

import com.example.cjb.myglide.glide.cache.Key;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface DiskCache {
    interface Writer {
        boolean write(OutputStream file);
    }

    InputStream get(Key key);
    void put(Key key,Writer writer);
    void delete(Key key);
    void clear();
}
