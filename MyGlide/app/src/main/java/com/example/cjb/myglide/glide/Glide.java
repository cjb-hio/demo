package com.example.cjb.myglide.glide;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.example.cjb.myglide.glide.cache.ArrayPool;
import com.example.cjb.myglide.glide.cache.MemoryCache;
import com.example.cjb.myglide.glide.cache.recycle.BitmapPool;
import com.example.cjb.myglide.glide.cache.recycle.DiskCache;
import com.example.cjb.myglide.glide.load.Engine;

public class Glide {

    private  Engine engine;
    private  GlideContext glideContext;
    MemoryCache memoryCache;
    BitmapPool bitmapPool;
    ArrayPool arrayPool;
    private static Glide glide;

    public RequestManagerRetriever getRequestManagerRetriever() {
        return requestManagerRetriever;
    }

    private RequestManagerRetriever requestManagerRetriever;

    public Glide(Context context, GlideBuilder builder) {
        memoryCache = builder.memoryCache;
        bitmapPool = builder.bitmapPool;
        arrayPool = builder.arrayPool;


        engine = builder.engine;
        Registry registry=new Registry();
        glideContext = new GlideContext(context, builder.defaultRequestOptions,
                engine, registry);

        requestManagerRetriever = new RequestManagerRetriever(glideContext);


    }

    static RequestManager with(FragmentActivity activity) {
        return Glide.get(activity).getRequestManagerRetriever().get(activity);
    }

    private static Glide get(Context context) {
        synchronized (Glide.class) {
            if (glide == null) {
                init(context, new GlideBuilder());
            }

            return glide;
        }

    }

    private static void init(Context context, GlideBuilder builder) {

        if (builder != null) {
            tearDown();
        }
        Context applicationContext = context.getApplicationContext();
        glide = builder.build(applicationContext);
    }

    private static void tearDown() {
        if (glide != null) {
            glide = null;
        }
    }


}
