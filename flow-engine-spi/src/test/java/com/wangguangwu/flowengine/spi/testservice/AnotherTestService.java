package com.wangguangwu.flowengine.spi.testservice;

public class AnotherTestService implements TestService {
    @Override
    public String sayHello() {
        return "Hello from AnotherTestService";
    }
}
