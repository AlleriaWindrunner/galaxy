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

package com.galaxy.lemon.framework.stream.producer;

import com.galaxy.lemon.common.ConfigurableBeanName;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.extension.SpringExtensionLoader;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.instantiator.CommandDTOInstantiator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 生产者
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@Aspect
public class ProducerAspect implements Ordered, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ProducerAspect.class);

    private ApplicationContext applicationContext;
    private Map<String, MessageChannel> senders = new ConcurrentHashMap<>();
    private InternalDataHelper internalDataHelper;
    private CommandDTOInstantiator commandDTOInstantiator;

    public ProducerAspect(CommandDTOInstantiator commandDTOInstantiator,
                          InternalDataHelper internalDataHelper) {
        this.commandDTOInstantiator = commandDTOInstantiator;
        this.internalDataHelper = internalDataHelper;
    }

    @Around("@annotation(producers)")
    public void producer(ProceedingJoinPoint pjp, Producers producers) {
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable e1) {
            LemonException.throwLemonException(e1);
        }

        if (null == result) {
            if (logger.isWarnEnabled()) {
                logger.warn("Ignore sending data to channel {}, because of data is null.", Stream.of(producers.value()).map(Producer::channelName).collect(Collectors.joining(",")));
            }
            return;
        }

        try {
            final Object execRst = result;
            Producer[] producerArray = producers.value();
            Stream.of(producerArray).parallel().forEach(p -> {
                try {
                    sendMsg(execRst, p);
                } catch (Throwable e2) {
                    if (logger.isErrorEnabled()) {
                        logger.error("failed to sending msg to {} ==>> {}", p.channelName(), execRst);
                        logger.error("", e2);
                    }
                }
            });
        } catch (Throwable e) {         //防御性异常外抛
            if (logger.isErrorEnabled()) {
                logger.error("", e);
            }
        }

    }

    private void sendMsg(Object result, Producer producer) {
        MessageChannel sender = this.senders.get(producer.channelName());
        LemonException.throwLemonExceptionIfNecessary(JudgeUtils.isNull(sender), ErrorMsgCode.PRODUCER_RABBIT_EXCEPTION.getMsgCd(), "MessageChannel \"" + producer.channelName() + "\" does not exists.");
        Object dto = decorateCommandDTO(this.commandDTOInstantiator.newInstanceCommandDTO(), producer, result);
        sender.send(MessageBuilder.withPayload(dto).build());
        if (logger.isInfoEnabled()) {
            logger.info("Sending msg to {} : {}", producer.channelName(), dto);
        }
    }

    private Object decorateCommandDTO(BaseDTO commandDTO, Producer producer, Object result) {
        ((ConfigurableBeanName)commandDTO).setBeanName(producer.beanName());
        commandDTO.setBody(result);
        Optional.ofNullable(LemonDataHolder.getLemonData()).ifPresent(l -> internalDataHelper.copyLemonDataToDTO(l, commandDTO));
        return commandDTO;
    }


    @PostConstruct
    private void init() {
        SpringExtensionLoader.getSpringBeansOfType(applicationContext, MessageChannel.class).forEach((k, v) -> {
            this.senders.put(k, v);
        });
        if (logger.isInfoEnabled()) {
            logger.info("Finding message channel {}", this.senders);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 2;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
