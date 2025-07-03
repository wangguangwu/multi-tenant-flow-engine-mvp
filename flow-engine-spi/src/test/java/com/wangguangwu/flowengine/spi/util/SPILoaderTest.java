package com.wangguangwu.flowengine.spi.util;

import com.wangguangwu.flowengine.spi.testservice.TestService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SPILoaderTest {

    @Test
    void testGetDefaultExtension() {
        TestService service = SPILoader.getDefaultExtension(TestService.class);
        assertNotNull(service);
        assertEquals("Hello from DefaultTestService", service.sayHello());
    }

    @Test
    void testGetNamedExtension() {
        TestService another = SPILoader.getExtension(TestService.class, "another");
        assertNotNull(another);
        assertEquals("Hello from AnotherTestService", another.sayHello());
    }
}
