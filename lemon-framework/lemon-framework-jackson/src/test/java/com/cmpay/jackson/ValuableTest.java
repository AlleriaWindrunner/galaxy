package com.cmpay.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class ValuableTest {

    private static final String userJson ="{\n"+
            "    \"userId\": \"CON0001\",\n"+
            "    \"name\": \"eleven\",\n"+
            "    \"age\" : 30,\n"+
            "    \"birthday\" :\"1991-04-08\",\n"+
            "    \"sex\" : \"男\"\n"+
            "}";


    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            UserBO userBO = objectMapper.readValue(userJson, UserBO.class);
            System.out.println(userBO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
