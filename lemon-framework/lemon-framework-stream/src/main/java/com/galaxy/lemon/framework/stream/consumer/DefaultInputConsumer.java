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

package com.galaxy.lemon.framework.stream.consumer;

import com.galaxy.lemon.common.ExposeBeanName;
import com.galaxy.lemon.common.condition.OnlineCondition;
import com.galaxy.lemon.common.log.logback.MDCUtil;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.interceptor.InitialLemonData;
import com.galaxy.lemon.framework.stream.DefaultInput;
import com.galaxy.lemon.framework.stream.MessageHandler;
import com.galaxy.lemon.framework.stream.logging.InputLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

/**
 * input 通道消费者，可以配置多个主题
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnProperty(value = DefaultInputConsumer.ENABLE)
@Conditional(DefaultInputConsumer.ConsumerAvailableCondition.class)
@EnableBinding(value = {DefaultInput.class})
public class DefaultInputConsumer {
    private static final Logger logger = LoggerFactory.getLogger("DefaultInputConsumer");

    public static final String ENABLE = "spring.cloud.stream.bindings." + DefaultInput.DEFAULT_INPUT + ".consumer.enabled";
    public static final String BATCH_ENABLE = "spring.cloud.stream.bindings." + DefaultInput.DEFAULT_INPUT + ".consumer.batchEnabled";

    private Map<String, MessageHandler> messageHandlers;

    private InternalDataHelper internalDataHelper;

    private InputLogger inputLogger;

    public DefaultInputConsumer(InternalDataHelper internalDataHelper,
                                ObjectProvider<Map<String, MessageHandler>> messageHandlerMap,
                                InputLogger inputLogger) {
        this.internalDataHelper = internalDataHelper;
        this.messageHandlers = messageHandlerMap.getIfAvailable();
        this.inputLogger = inputLogger;
    }

    @PostConstruct
    public void afterPropertySet() {
        if (JudgeUtils.isEmpty(this.messageHandlers) && logger.isWarnEnabled()) {
            logger.warn("Cloud not found any MessageHandler in application context, ignore this warning if no consumer.");
        }
    }

    @InitialLemonData("onlyInstantiationContextLemonDataInitializer")
    @StreamListener(DefaultInput.DEFAULT_INPUT)
    public <T, D extends BaseDTO<T> & ExposeBeanName> void receive(D genericCmdDTO) {
        Optional.ofNullable(genericCmdDTO).map(BaseDTO::getRequestId).filter(JudgeUtils::isNotEmpty).ifPresent(MDCUtil::putMDCKey);
        if (logger.isDebugEnabled()) {
            logger.debug("Receive message from {} : {}", DefaultInput.DEFAULT_INPUT, genericCmdDTO);
        }
        if (JudgeUtils.isNull(genericCmdDTO)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Received a null object from default input channel by DefaultInputConsumer.");
            }
            return;
        }
        inputLogger.log(genericCmdDTO);
        if (JudgeUtils.isBlank(genericCmdDTO.getBeanName())) {
            if (logger.isErrorEnabled()) {
                logger.error("The message consumer handler bean name is blank, illegal message : {}", genericCmdDTO);
            }
            return;
        }
        MessageHandler<T, D> handler = this.getMessageHandler(genericCmdDTO.getBeanName());
        if (JudgeUtils.isNull(handler)) {
            if (logger.isErrorEnabled()) {
                logger.error("Message consumer handler {} could not be found, discard message : {},", genericCmdDTO.getBeanName(), genericCmdDTO);
            }
            return;
        }
        try {
            this.internalDataHelper.copyDTOToLemonData(genericCmdDTO, LemonDataHolder.getLemonData());
            handler.onMessageReceive(genericCmdDTO);
        } catch (Throwable t) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed during handing message {},", genericCmdDTO);
                logger.error("", t);
            }
        }
    }

    private <T, D extends BaseDTO<T> & ExposeBeanName> MessageHandler<T, D> getMessageHandler(String beanName) {
        return this.messageHandlers.get(beanName);
    }

    public static class ConsumerAvailableCondition extends AnyNestedCondition {
        public ConsumerAvailableCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @Conditional(OnlineCondition.class)
        static class Online {
        }

        @ConditionalOnProperty(name = BATCH_ENABLE)
        static class BatchConsumerAvailable {

        }

    }

}
