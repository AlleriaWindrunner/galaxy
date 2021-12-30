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

package com.galaxy.lemon.framework.stream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

/**
 * 多通道
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class MultiOutput {
    public static final String OUTPUT_DEFAULT = "output";
    public static final String OUTPUT_ONE = "output1";
    public static final String OUTPUT_TWO = "output2";
    public static final String OUTPUT_THREE = "output3";
    public static final String OUTPUT_FOUR = "output4";
    public static final String OUTPUT_FIVE = "output5";
    public static final String OUTPUT_SIX = "output6";
    public static final String OUTPUT_SEVEN = "output7";
    
    @ConditionalOnProperty(value=DefaultSender.ENABLE, matchIfMissing= false)
    @EnableBinding(DefaultSender.class)
    public static interface DefaultSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_DEFAULT+".enabled";
        
        @Output(MultiOutput.OUTPUT_DEFAULT)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=OneSender.ENABLE, matchIfMissing= false)
    @EnableBinding(OneSender.class)
    public static interface OneSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_ONE+".enabled";
        
        @Output(MultiOutput.OUTPUT_ONE)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=TwoSender.ENABLE, matchIfMissing= false)
    @EnableBinding(TwoSender.class)
    public static interface TwoSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_TWO+".enabled";
        
        @Output(MultiOutput.OUTPUT_TWO)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=ThreeSender.ENABLE, matchIfMissing= false)
    @EnableBinding(ThreeSender.class)
    public static interface ThreeSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_THREE+".enabled";
        
        @Output(MultiOutput.OUTPUT_THREE)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=FourSender.ENABLE, matchIfMissing= false)
    @EnableBinding(FourSender.class)
    public static interface FourSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_FOUR+".enabled";
        
        @Output(MultiOutput.OUTPUT_FOUR)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=FiveSender.ENABLE, matchIfMissing= false)
    @EnableBinding(FiveSender.class)
    public static interface FiveSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_FIVE+".enabled";
        
        @Output(MultiOutput.OUTPUT_FIVE)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=SixSender.ENABLE, matchIfMissing= false)
    @EnableBinding(SixSender.class)
    public static interface SixSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_SIX+".enabled";
        
        @Output(MultiOutput.OUTPUT_SIX)
        MessageChannel output();
    }
    
    @ConditionalOnProperty(value=SevenSender.ENABLE, matchIfMissing= false)
    @EnableBinding(SevenSender.class)
    public static interface SevenSender {
        String ENABLE = "spring.cloud.stream.bindings."+MultiOutput.OUTPUT_SEVEN+".enabled";
        
        @Output(MultiOutput.OUTPUT_SEVEN)
        MessageChannel output();
    }
}
