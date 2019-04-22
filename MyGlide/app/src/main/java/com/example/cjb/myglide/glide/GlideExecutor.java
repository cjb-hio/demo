package com.example.cjb.myglide.glide;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GlideExecutor {
    private static int bestThreadCount;

    public static int calculateBestThreadCount() {
        if (bestThreadCount == 0) {
            bestThreadCount = Math.min(4, Runtime.getRuntime().availableProcessors());
        }
        return bestThreadCount;
    }


    public static ThreadPoolExecutor newExecutor() {
        int threadCount = calculateBestThreadCount();
        return new ThreadPoolExecutor(threadCount,threadCount,0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory());
    }

    private static class DefaultThreadFactory implements ThreadFactory{

        private int threadNum;
        @Override
        public Thread newThread(Runnable r) {
            final Thread result = new Thread(r, "glide-thread-" + threadNum);
            threadNum++;
            return result;
        }
    }
}
