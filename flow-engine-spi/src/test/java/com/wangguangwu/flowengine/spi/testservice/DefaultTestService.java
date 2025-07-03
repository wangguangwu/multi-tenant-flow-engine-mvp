package com.wangguangwu.flowengine.spi.testservice;

public class DefaultTestService implements TestService {
    @Override
    public String sayHello() {
        return "Hello from DefaultTestService";
    }
}
