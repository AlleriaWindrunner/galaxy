package com.galaxy.lemon.stream.test;

import com.galaxy.lemon.framework.data.DefaultCmdDTO;
import com.galaxy.lemon.framework.stream.MultiOutput.DefaultSender;
import com.galaxy.lemon.framework.stream.MultiOutput.OneSender;
import com.galaxy.lemon.stream.test.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = TestApplication.class)
public class HelloApplicationTest {
    private static final Logger logger = LoggerFactory.getLogger(HelloApplicationTest.class);
    
    //@Autowired
    private TestInOutChannel sender;
    
   // @Autowired
    private OneSender oneSender;
    
    //@Autowired
    private DefaultSender defaultSender;
    
    @Autowired
    private TestProducer testProducer;
    
    //@Test
    public void contextLoads(){
        Hello hello = new Hello("yuzhou",23);
        DefaultCmdDTO<Hello> dto = new DefaultCmdDTO<>("helloMessageHandler");
        dto.setBody(hello);
        for(int i= 0 ; i< 100; i++) {
            sender.output().send(MessageBuilder.withPayload(dto).build());
            logger.info("###sended msg {}", dto);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }
        
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
        }
    }
    //@Test
    public void outputTest(){
        System.out.println("oneSender==="+oneSender);
        Hello hello = new Hello("yuzhou",23);
        DefaultCmdDTO<Hello> dto = new DefaultCmdDTO<>("helloMessageHandler");
        dto.setBody(hello);
        for(int i= 0 ; i< 100; i++) {
            defaultSender.output().send(MessageBuilder.withPayload(dto).build());
            logger.info("###sended msg {}", dto);
            oneSender.output().send(MessageBuilder.withPayload(dto).build());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }
        
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
        }
    }
    
   // @Test
    public void testProducer() {
        for(int i = 0; i< 100; i++) {
            testProducer.sendHello();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
