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

package com.galaxy.lemon.framework.schedule.batch;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import com.galaxy.lemon.common.condition.OnBatchCondition;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class BatchSchedulingConfiguration{
    public static final String BATCH_SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalBatchScheduledAnnotationProcessor";
    
    @Bean(name = BatchSchedulingConfiguration.BATCH_SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Conditional(OnBatchCondition.class)
    public BatchScheduledAnnotationBeanPostProcessor batchScheduledAnnotationProcessor() {
        return new BatchScheduledAnnotationBeanPostProcessor();
    }
   
}
