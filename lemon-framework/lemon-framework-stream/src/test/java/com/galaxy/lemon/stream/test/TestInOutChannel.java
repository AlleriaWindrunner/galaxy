package com.galaxy.lemon.stream.test;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface TestInOutChannel {
    String HELLO = "hello";
    
    @Input(TestInOutChannel.HELLO)
    SubscribableChannel input();
    
    @Output(TestInOutChannel.HELLO)
    MessageChannel output();
}
