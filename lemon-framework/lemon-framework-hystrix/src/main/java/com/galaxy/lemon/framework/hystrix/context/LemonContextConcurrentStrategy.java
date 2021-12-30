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

package com.galaxy.lemon.framework.hystrix.context;

import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * resolve context for lemon framework when using hystrix thread pool mode
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonContextConcurrentStrategy extends HystrixConcurrencyStrategy {

    private static final String HYSTRIX_COMPONENT = "hystrix";
    private static final Logger log = LoggerFactory.getLogger(LemonContextConcurrentStrategy.class);

    private HystrixConcurrencyStrategy delegate;

    public LemonContextConcurrentStrategy() {
        try {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof LemonContextConcurrentStrategy) {
                return;
            }
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins
                    .getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance()
                    .getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance()
                    .getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance()
                    .getPropertiesStrategy();
            logCurrentStateOfHysrixPlugins(eventNotifier, metricsPublisher,
                    propertiesStrategy);
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
            HystrixPlugins.getInstance()
                    .registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        }
        catch (Exception e) {
            log.error("Failed to register Lemon Hystrix Concurrency Strategy", e);
        }
    }

    private void logCurrentStateOfHysrixPlugins(HystrixEventNotifier eventNotifier,
                                                HystrixMetricsPublisher metricsPublisher,
                                                HystrixPropertiesStrategy propertiesStrategy) {
        if (log.isDebugEnabled()) {
            log.debug("Current Hystrix plugins configuration is [" + "concurrencyStrategy ["
                    + this.delegate + "]," + "eventNotifier [" + eventNotifier + "],"
                    + "metricPublisher [" + metricsPublisher + "]," + "propertiesStrategy ["
                    + propertiesStrategy + "]," + "]");
            log.debug("Registering Lemon Hystrix Concurrency Strategy.");
        }
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        if (callable instanceof LemonContextConcurrentStrategy.HystrixLemonCallable) {
            return callable;
        }
        Callable<T> wrappedCallable = this.delegate != null
                ? this.delegate.wrapCallable(callable) : callable;
        if (wrappedCallable instanceof LemonContextConcurrentStrategy.HystrixLemonCallable) {
            return wrappedCallable;
        }
        return new LemonContextConcurrentStrategy.HystrixLemonCallable<>(wrappedCallable);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixThreadPoolProperties threadPoolProperties) {
        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return this.delegate.getRequestVariable(rv);
    }

    // Visible for testing
    static class HystrixLemonCallable<S> implements Callable<S> {

        private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

        private LemonContext lemonContext;
        private BaseLemonData lemonData;
        private Callable<S> callable;

        public HystrixLemonCallable(LemonContext lemonContext, BaseLemonData lemonData,
                                    Callable<S> callable) {
            this.lemonContext = lemonContext;
            this.lemonData = lemonData;
            this.callable = callable;
        }

        public HystrixLemonCallable(Callable<S> callable) {
            this.lemonContext = LemonContext.getCurrentContext();
            this.lemonData = LemonDataHolder.getLemonData();
            this.callable = callable;
        }


        @Override
        public S call() throws Exception {
            LemonContext existingLemonContext = LemonContext.getCurrentContext();
            BaseLemonData existLemonData = LemonDataHolder.getLemonData();
            try {
                LemonContext.setCurrentContext(this.lemonContext);
                LemonDataHolder.setLemonData(this.lemonData);
                if (logger.isDebugEnabled()) {
                    logger.debug("Set LemonContext and LemonDataHolder in HystrixConcurrencyStrategy");
                }
                return callable.call();
            }
            finally {
                LemonContext.setCurrentContext(existingLemonContext);
                LemonDataHolder.setLemonData(existLemonData);
                if (logger.isDebugEnabled()) {
                    logger.debug("Set Original LemonContext and LemonDataHolder in HystrixConcurrencyStrategy");
                }
            }
        }

    }
}
