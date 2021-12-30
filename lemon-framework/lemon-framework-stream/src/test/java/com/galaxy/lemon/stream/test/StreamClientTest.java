package com.galaxy.lemon.stream.test;

import com.galaxy.lemon.stream.test.Hello;
import org.junit.Assert;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = TestApplication.class)
//@EnableStreamClients
public class StreamClientTest {

    //@Autowired
    private MyMessageClient myMessageClient;

    //@Test
    public void test() {
        Assert.assertNull(myMessageClient);
        this.myMessageClient.sendHello(new Hello("yuzhou", 33));
    }

}
