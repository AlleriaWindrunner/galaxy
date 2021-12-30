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

import com.galaxy.lemon.common.condition.OnBatchCondition;
import com.galaxy.lemon.framework.schedule.batch.BatchScheduled;
import com.galaxy.lemon.framework.schedule.batch.EnableBatchScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ScheduleAutoConfiguration {

    @Configuration
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnClass({CustomizedSchedulingConfigurer.class})
    @Conditional(RequiredCreatingScheduleThreadPoolCondition.class)
    @EnableConfigurationProperties(ScheduleProperties.class)
    public static class ScheduleThreadPool {
        private static final Logger logger = LoggerFactory.getLogger(ScheduleThreadPool.class);

        public static final String TASK_SCHEDULER_BEAN_NAME = "taskScheduler";

        private static final int DEFAULT_POOL_SIZE = 10;
        private static final int DEFAULT_AWAIT_TERMINATION_SECONDS = 30;
        private static final boolean DEFAULT_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = true;
        private static final String TASK_THREAD_NAME_PREFIX = "lemon-task-";

        @Bean
        @ConditionalOnMissingBean(name = TASK_SCHEDULER_BEAN_NAME)
        public ThreadPoolTaskScheduler taskScheduler(ScheduleProperties scheduleProperties) {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(Optional.ofNullable(scheduleProperties).map(ScheduleProperties::getThreadPool).map(t -> t.getPoolSize()).orElse(DEFAULT_POOL_SIZE));
            scheduler.setErrorHandler(t -> {
                if (logger.isErrorEnabled()) {
                    logger.error("Unexpected error occurred at scheduling task.", t);
                }
                if (t instanceof StoppedExecutionException) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Stopped scheduling task because a \"StoppedExecutionException\" exception raise.");
                    }
                    throw (StoppedExecutionException) t;
                }
            });
            scheduler.setThreadNamePrefix(TASK_THREAD_NAME_PREFIX);
            scheduler.setAwaitTerminationSeconds(Optional.ofNullable(scheduleProperties).map(ScheduleProperties::getThreadPool).map(t -> t.getAwaitTerminationSeconds()).orElse(DEFAULT_AWAIT_TERMINATION_SECONDS));
            scheduler.setWaitForTasksToCompleteOnShutdown(Optional.ofNullable(scheduleProperties).map(ScheduleProperties::getThreadPool).map(t -> t.getWaitForTasksToCompleteOnShutdown()).orElse(DEFAULT_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN));
            scheduler.setRejectedExecutionHandler((r, executor) -> {
                if (logger.isErrorEnabled()) {
                    logger.error("Please increase the task thread pool size using param \"lemon.schedule.threadPool.poolSize\".");
                }
                throw new RejectedExecutionException("Task \"" + r.toString() + "\" rejected execution from \"" + executor.toString() + "\".");
            });
            return scheduler;
        }

        @Bean
        @ConditionalOnBean(name = TASK_SCHEDULER_BEAN_NAME)
        public SchedulingConfigurer schedulingConfigurer(@Autowired @Qualifier(TASK_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler taskScheduler) {
            return new CustomizedSchedulingConfigurer(taskScheduler);
        }

    }

    @Configuration
    @ConditionalOnClass(Scheduled.class)
    @ConditionalOnProperty(value = "lemon.scheduling.enabled", matchIfMissing = true)
    @EnableScheduling
    public static class Schedule {

    }

    @Configuration
    @ConditionalOnClass(BatchScheduled.class)
    @Conditional(OnBatchCondition.class)
    @EnableBatchScheduling
    public static class BatchSchedule {

    }

    /**
     * creating task scheduler if necessary
     * @author yuzhou
     * @date 2018/3/27
     * @time 14:05
     * @since 2.0.0
     */
    @Order
    public static class RequiredCreatingScheduleThreadPoolCondition extends AnyNestedCondition {
        public RequiredCreatingScheduleThreadPoolCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(value = "lemon.scheduling.enabled", matchIfMissing = true)
        static class SchedulingCondition {

        }

        @Conditional(OnBatchCondition.class)
        static class BatchSchedulingCondition {

        }

    }


}
