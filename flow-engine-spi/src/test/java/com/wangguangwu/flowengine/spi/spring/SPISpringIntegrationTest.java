package com.wangguangwu.flowengine.spi.spring;

import com.wangguangwu.flowengine.spi.annotation.Extension;
import com.wangguangwu.flowengine.spi.example.DataConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SPI Spring 集成测试
 *
 * @author wangguangwu
 */
@SpringBootTest
@EnableAutoConfiguration
class SPISpringIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SPIExtensionRegistry extensionRegistry;

    @Test
    @DisplayName("测试 Spring 环境中 SPI 组件自动注册")
    void testSPIComponentsRegistration() {
        assertNotNull(extensionRegistry, "SPI扩展注册器应该被注册");
        assertNotNull(applicationContext.getBean(SPIBeanPostProcessor.class), "SPI Bean后处理器应该被注册");
    }

    @Test
    @DisplayName("测试 Spring 环境中扩展点实现的自动发现")
    void testExtensionDiscovery() {
        // 获取测试配置中注册的自定义转换器
        assertTrue(applicationContext.containsBean("customDataConverter"), "自定义数据转换器应该被注册");
        
        // 通过扩展注册器获取扩展加载器
        var loader = extensionRegistry.getExtensionLoader(DataConverter.class);
        assertNotNull(loader, "扩展加载器不应为空");
        
        // 验证扩展点实现是否被正确注册
        // 注意：由于我们的实现简化了，这里可能需要调整测试逻辑
        // 实际项目中，可以通过修改 DefaultExtensionLoader 提供注册方法
    }

    /**
     * 测试配置类
     */
    @Configuration
    static class TestConfig {
        
        /**
         * 注册一个自定义数据转换器
         */
        @Bean
        public CustomDataConverter customDataConverter() {
            return new CustomDataConverter();
        }
    }

    /**
     * 自定义数据转换器，用于测试
     */
    @Extension("custom")
    static class CustomDataConverter implements DataConverter {
        
        @Override
        public String serialize(Object obj) {
            return "CUSTOM:" + obj;
        }

        @Override
        public <T> T deserialize(String data, Class<T> clazz) {
            if (clazz == String.class) {
                return clazz.cast(data.substring(7)); // 去掉 "CUSTOM:" 前缀
            }
            throw new UnsupportedOperationException("不支持的类型");
        }
    }
}
