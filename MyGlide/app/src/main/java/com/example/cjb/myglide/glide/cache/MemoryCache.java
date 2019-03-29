package com.example.cjb.myglide.glide.cache;

import com.example.cjb.myglide.glide.cache.recycle.Resource;

public interface MemoryCache {

    public interface ResourceRemoveListener{
        void onResourceRemoved(Resource resource);
    }
    //public Resource put(Key key, Resource resource);

    public void setResourceRemoveListener(ResourceRemoveListener listener);

}
