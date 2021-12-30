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

package com.galaxy.lemon.framework.autoconfigure;


import com.galaxy.lemon.common.exception.DefaultExceptionConversionService;
import com.galaxy.lemon.framework.validation.BeanValidationInvalidHelper;
import com.galaxy.lemon.framework.valuable.ValuableHelper;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonBackgroundPreinitializer implements ApplicationListener<SpringApplicationEvent> {

    private static final AtomicBoolean preinitializationStarted = new AtomicBoolean(
            false);

    private static final CountDownLatch preinitializationComplete = new CountDownLatch(1);

    @Override
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            if (preinitializationStarted.compareAndSet(false, true)) {
                performPreinitialization();
            }
        }
        if ((event instanceof ApplicationReadyEvent
                || event instanceof ApplicationFailedEvent)
                && preinitializationStarted.get()) {
            try {
                preinitializationComplete.await();
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void performPreinitialization() {
        try {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    runSafely(new ExceptionConversionInitializer());
                    runSafely(new BeanValidationInvalidInitializer());
                    runSafely(new ValuableInitializer());
                    preinitializationComplete.countDown();
                }

                public void runSafely(Runnable runnable) {
                    try {
                        runnable.run();
                    }
                    catch (Throwable ex) {
                        // Ignore
                    }
                }

            }, "background-preinit");
            thread.start();
        }
        catch (Exception ex) {
            // This will fail on GAE where creating threads is prohibited. We can safely
            // continue but startup will be slightly slower as the initialization will now
            // happen on the main thread.
            preinitializationComplete.countDown();
        }
    }

    /**
     * Early initializer for exception conversion
     */
    private static class ExceptionConversionInitializer implements Runnable {
        @Override
        public void run() {
            new DefaultExceptionConversionService();
        }
    }

    /**
     * Early initializer for BeanValidationInvalid
     */
    private static class BeanValidationInvalidInitializer implements Runnable {
        @Override
        public void run() {
            BeanValidationInvalidHelper.resolveBeanValidationInvalidIfNecessary(null);
        }
    }

    /**
     * Early initializer for ValuableInitializer
     */
    private static class ValuableInitializer implements Runnable {
        public void run() {
            ValuableHelper.getValuableClasses();
        }
    }


}
