package com.koreait.store.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:my.properties")
public class ValueTest {
    @Value("${TEST}")
    private String test;
    @Value("${NUMBER}")
    private Integer number;

    public void test(){
        System.out.println(test);
        System.out.println(number);
    }
}
