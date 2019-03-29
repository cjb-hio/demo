package com.example.cjb.myglide.glide.cache;

import com.example.cjb.myglide.glide.cache.recycle.Resource;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class ActiveResource {
    private boolean isShutdown;
    private Resource.ResourceRelease resourceRelease;
    private ReferenceQueue<Resource> queue;
    private HashMap<Key, ResourceWeakReference> activeMap = new HashMap<>();
    private Thread queueThread;


    /**
     * 将资源放入活动缓存
     *
     * @param key
     * @param resource
     */
    public void activate(Key key, Resource resource) {
        resource.setReleaseListener(resourceRelease);
        activeMap.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    /**
     * 将资源移除活动缓存
     *
     * @param key
     * @return
     */
    public Resource deactivate(Key key) {
        ResourceWeakReference remove = activeMap.remove(key);
        if (remove != null) {
            return remove.get();
        }
        return null;

    }

    public void setResourceRelease(Resource.ResourceRelease resourceRelease) {
        this.resourceRelease = resourceRelease;
    }

    private ReferenceQueue getReferenceQueue() {
        if (null == queue) {
            queue = new ReferenceQueue<>();
            queueThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!isShutdown) {
                        try {
                            ResourceWeakReference remove = (ResourceWeakReference) queue.remove();
                            activeMap.remove(remove.key);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            queueThread.start();
        }
        return queue;

    }

    private class ResourceWeakReference extends WeakReference<Resource> {

        Key key;

        public ResourceWeakReference(Key key, Resource referent,
                                     ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.key = key;
        }
    }
}
