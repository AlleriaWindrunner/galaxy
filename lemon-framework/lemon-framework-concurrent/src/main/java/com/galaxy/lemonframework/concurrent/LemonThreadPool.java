/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.galaxy.lemonframework.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * thread pool
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonThreadPool extends ThreadPoolExecutor {
    private final static ConcurrentHashMap threadPoolMap = new ConcurrentHashMap();

    private String name;

    public LemonThreadPool(String name) {
        super(5, 50, 60L, TimeUnit.SECONDS, new SynchronousQueue(),
                namedThreadFactory(name));
        this.name = name;
        threadPoolMap.put(name, this);
    }

    private LemonThreadPool(String name, int min, int max) {
        super(min, max, 60L, TimeUnit.SECONDS, new SynchronousQueue(),
                namedThreadFactory(name));
        this.name = name;
        threadPoolMap.put(name, this);
    }

    private LemonThreadPool(String name, int min, int max, int queue) {
        super(min, max, 60L, TimeUnit.SECONDS, queue == 0 ? new SynchronousQueue() : new ArrayBlockingQueue(queue),
                namedThreadFactory(name));
        this.name = name;
        threadPoolMap.put(name, this);
    }

    private LemonThreadPool(String name, int min, int max, long keepAliveTime, int queue) {
        super(min, max, keepAliveTime, TimeUnit.SECONDS, queue == 0 ? new SynchronousQueue() : new ArrayBlockingQueue(queue),
                namedThreadFactory(name));
        this.name = name;
        threadPoolMap.put(name, this);
    }

    public static LemonThreadPool createThreadPool(String name) {
        return new LemonThreadPool(name);
    }

    public static LemonThreadPool createThreadPool(String name, int minThreads,
                                                int maxThreads) {
        return new LemonThreadPool(name, minThreads, maxThreads);
    }

    public static LemonThreadPool createThreadPool(String name, int minThreads,
                                                int maxThreads, int queueSize) {
        return new LemonThreadPool(name, minThreads, maxThreads, queueSize);
    }

    public static LemonThreadPool createThreadPool(String name, int minThreads,
                                                int maxThreads, long keepAliveTime, int queueSize) {
        return new LemonThreadPool(name, minThreads, maxThreads, keepAliveTime, queueSize);
    }

    public static ThreadFactory namedThreadFactory(final String name) {
        SecurityManager s = System.getSecurityManager();
        final ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread
                .currentThread().getThreadGroup();
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix = "lemon-pool-" + name + "-thread-";

        return r -> {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            return t;
        };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
