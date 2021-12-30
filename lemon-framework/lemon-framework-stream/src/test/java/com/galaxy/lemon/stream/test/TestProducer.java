package com.galaxy.lemon.stream.test;

import com.galaxy.lemon.stream.test.Hello;
import org.springframework.stereotype.Component;

import com.galaxy.lemon.framework.stream.MultiOutput;
import com.galaxy.lemon.framework.stream.producer.Producer;
import com.galaxy.lemon.framework.stream.producer.Producers;

@Component
public class TestProducer {
    @Producers({
        @Producer(beanName="helloMessageHandler", channelName=MultiOutput.OUTPUT_DEFAULT),
        @Producer(beanName="helloMessageHandler2", channelName=MultiOutput.OUTPUT_DEFAULT)
    })
    public Hello sendHello() {
        return new Hello("hello-->",40);
    }
}
