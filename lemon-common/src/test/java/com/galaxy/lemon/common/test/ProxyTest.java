package com.galaxy.lemon.common.test;

import com.galaxy.lemon.common.cglib.Proxy;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class ProxyTest {
    private static TestClient client = new TestClient() {
        @Override
        public Response hello(Request request) {
            System.out.println(request.getMsg());
            Response response = new Response();
            response.setAge(18);
            response.setName("yuzhou");
            return response;
        }
    };

    static String[] excludeProperties = new String[]{"class"};
    private static boolean isExcludeProperty(String propertyName) {
        return Stream.of(excludeProperties).anyMatch(s -> s.equals(propertyName));
    }

    public static void main(String[] args) {
//        TestClient client1 =  (TestClient) Proxy.getProxy(TestClient.class).newInstance(new InvocationHandler(){
//
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                System.out.println("starting exec method "+method.getName());
//                Object result = method.invoke(client, args);
//                System.out.println("finished exec method "+method.getName()+", result is "+result);
//                return result;
//            }
//        });
//
//        client1.hello(new TestClient.Request("testing...."));


        /////////////////////////////////

//        Stream.of(ReflectionUtils.getPropertyDescriptors(Object.class)).map(PropertyDescriptor::getName).forEach(
//                s -> System.out.println(s)
//        );
//
//        System.out.println(isExcludeProperty("class"));

        TestClientImpl testClientImpl = new TestClientImpl();

        TestClient client2 = (TestClient) Proxy.getProxy(TestClientImpl.class).newInstance(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("Cglib interceptor...");
                return methodProxy.invoke(testClientImpl, objects);
            }
        });
        client2.hello(new TestClient.Request("testing...."));
    }


}
