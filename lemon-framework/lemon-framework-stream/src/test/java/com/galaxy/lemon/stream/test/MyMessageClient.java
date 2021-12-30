package com.galaxy.lemon.stream.test;

import com.galaxy.lemon.framework.stream.Source;
import com.galaxy.lemon.framework.stream.StreamClient;
import com.galaxy.lemon.stream.test.Hello;

@StreamClient("output")
public interface MyMessageClient {

    @Source(handlerBeanName = "helloHandler")
    void sendHello(Hello hello);
}
