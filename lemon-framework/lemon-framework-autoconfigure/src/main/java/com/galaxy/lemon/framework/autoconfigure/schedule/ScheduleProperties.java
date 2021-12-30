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

package com.galaxy.lemon.framework.autoconfigure.schedule;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties("lemon.schedule")
public class ScheduleProperties {
    
    private ThreadPoolProperties threadPool;
    
    public ThreadPoolProperties getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPoolProperties threadPool) {
        this.threadPool = threadPool;
    }

    public static class ThreadPoolProperties {
        private Integer poolSize;
        private Integer awaitTerminationSeconds;
        private Boolean waitForTasksToCompleteOnShutdown;
        public Integer getPoolSize() {
            return poolSize;
        }
        public void setPoolSize(Integer poolSize) {
            this.poolSize = poolSize;
        }
        public Integer getAwaitTerminationSeconds() {
            return awaitTerminationSeconds;
        }
        public void setAwaitTerminationSeconds(Integer awaitTerminationSeconds) {
            this.awaitTerminationSeconds = awaitTerminationSeconds;
        }
        public Boolean getWaitForTasksToCompleteOnShutdown() {
            return waitForTasksToCompleteOnShutdown;
        }
        public void setWaitForTasksToCompleteOnShutdown(
                Boolean waitForTasksToCompleteOnShutdown) {
            this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        }
        
    }
}
