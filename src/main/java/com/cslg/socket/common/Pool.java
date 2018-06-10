package com.cslg.socket.common;

import java.util.concurrent.*;

public class Pool {

    public static ThreadPoolExecutor threadPool;

    public static ThreadPoolExecutor singleThreadPool;

    static {
        threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());

        singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    static class MyThreadFactory implements ThreadFactory {

        private String name;

        private MyThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, name + "-" + r.hashCode());
        }
    }
}