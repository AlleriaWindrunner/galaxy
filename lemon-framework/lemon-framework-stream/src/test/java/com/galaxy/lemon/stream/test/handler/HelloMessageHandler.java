package com.galaxy.lemon.stream.test.handler;

import com.galaxy.lemon.framework.data.DefaultCmdDTO;
import com.galaxy.lemon.framework.stream.MessageHandler;
import com.galaxy.lemon.stream.test.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("helloMessageHandler")
public class HelloMessageHandler implements MessageHandler<Hello, DefaultCmdDTO<Hello>> {
    private static final Logger logger = LoggerFactory.getLogger(HelloMessageHandler.class);

    @Override
    public void onMessageReceive(DefaultCmdDTO<Hello> cmdDto) {
        logger.info("Receive msg hand {}", cmdDto.getBody());
    }
}
