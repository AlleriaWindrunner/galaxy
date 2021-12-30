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

package com.galaxy.lemon.framework.autoconfigure.stream;

import com.galaxy.lemon.common.log.LoggingCodec;
import com.galaxy.lemon.framework.stream.MultiOutput;
import com.galaxy.lemon.framework.stream.consumer.DefaultInputConsumer;
import com.galaxy.lemon.framework.stream.logging.InputLogger;
import com.galaxy.lemon.framework.stream.logging.SimpleInputLogger;
import com.galaxy.lemon.framework.stream.producer.ProducerAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(MultiOutput.class)
@Import({DefaultInputConsumer.class, MultiOutput.class, ProducerAspect.class})
public class StreamAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InputLogger inputLogger(LoggingCodec loggingCodec) {
        return new SimpleInputLogger(loggingCodec, DefaultInputConsumer.class);
    }
}
