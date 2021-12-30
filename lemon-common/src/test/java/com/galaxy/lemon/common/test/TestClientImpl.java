package com.galaxy.lemon.common.test;

public class TestClientImpl implements TestClient {
    @Override
    public Response hello(Request request) {
        System.out.println(request.getMsg());
        Response response = new Response();
        response.setAge(18);
        response.setName("yuzhou");
        return response;
    }
}
