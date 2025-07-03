package com.wangguangwu.flowengine.spi.loader;

import com.wangguangwu.flowengine.spi.exception.SPIException;
import com.wangguangwu.flowengine.spi.testservice.NoDefaultService;
import com.wangguangwu.flowengine.spi.testservice.TestService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultExtensionLoader 单元测试
 *
 * @author wangguangwu
 */
class DefaultExtensionLoaderTest {

    @Test
    void testGetDefaultExtension() {
        DefaultExtensionLoader<TestService> loader = DefaultExtensionLoader.getExtensionLoader(TestService.class);
        TestService service = loader.getDefaultExtension();
        assertNotNull(service);
        assertEquals("Hello from DefaultTestService", service.sayHello());
    }

    @Test
    void testGetNamedExtension() {
        DefaultExtensionLoader<TestService> loader = DefaultExtensionLoader.getExtensionLoader(TestService.class);
        TestService another = loader.getExtension("another");
        assertNotNull(another);
        assertEquals("Hello from AnotherTestService", another.sayHello());
    }

    @Test
    void testNoSuchExtension() {
        DefaultExtensionLoader<TestService> loader = DefaultExtensionLoader.getExtensionLoader(TestService.class);
        assertThrows(SPIException.class, () -> loader.getExtension("not-exist"));
    }

    @Test
    void testNoDefaultExtension() {
        // 使用没有默认实现的接口进行测试
        DefaultExtensionLoader<NoDefaultService> loader = DefaultExtensionLoader.getExtensionLoader(NoDefaultService.class);
        assertThrows(SPIException.class, loader::getDefaultExtension);
    }
}
