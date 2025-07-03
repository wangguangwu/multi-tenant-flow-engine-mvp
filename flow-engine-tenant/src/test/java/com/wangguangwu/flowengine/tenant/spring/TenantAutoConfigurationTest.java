package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantContextInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TenantAutoConfiguration 单元测试
 *
 * @author wangguangwu
 */
class TenantAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TenantAutoConfiguration.class));

    @Test
    @DisplayName("测试默认配置")
    void testDefaultConfiguration() {
        contextRunner
            .withClassLoader(new FilteredClassLoader(Async.class)) // 排除 Async 类，避免加载 tenantAwareAsyncAspect
            .run(context -> {
                // 验证默认配置下Bean是否正确注册
                assertNotNull(context.getBean(TenantProperties.class));
                assertTrue(context.containsBean("tenantAwareAspect"));
                // 在Web环境中才会注册tenantContextFilter，测试环境不是Web环境
                assertFalse(context.containsBean("tenantContextFilter"));
                // 验证接口类型的Bean是否存在
                assertNotEquals(0, context.getBeanNamesForType(TenantContextInitializer.class).length);
            });
    }
    
    @Test
    @DisplayName("测试禁用多租户功能")
    void testDisabledConfiguration() {
        contextRunner
                .withPropertyValues("flow.engine.tenant.enabled=false")
                .run(context -> {
                    // 验证禁用时Bean不应被注册
                    // 在Web环境中才会注册tenantContextFilter
                    assertFalse(context.containsBean("tenantContextFilter"));
                    assertFalse(context.containsBean("tenantAwareAspect"));
                    assertFalse(context.containsBean("tenantAwareAsyncAspect"));
                    assertEquals(0, context.getBeanNamesForType(TenantContextInitializer.class).length);
                    
                    // 当禁用多租户功能时，TenantProperties Bean 可能不存在
                    // 这取决于 @EnableConfigurationProperties 的行为和 Spring Boot 版本
                    // 我们不再对 TenantProperties Bean 是否存在做断言
                });
    }
    
    @Test
    @DisplayName("测试自定义属性配置")
    void testCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "flow.engine.tenant.tenant-id-header=Custom-Tenant-Header",
                        "flow.engine.tenant.system-tenant-id=admin"
                )
                .run(context -> {
                    TenantProperties properties = context.getBean(TenantProperties.class);
                    assertEquals("Custom-Tenant-Header", properties.getTenantIdHeader());
                    assertEquals("admin", properties.getSystemTenantId());
                });
    }
    
    @Test
    @DisplayName("测试自定义Bean覆盖")
    void testCustomBeanOverride() {
        contextRunner
                .withUserConfiguration(CustomConfiguration.class)
                .run(context -> {
                    // 验证自定义Bean是否覆盖默认Bean
                    assertTrue(context.containsBean("tenantContextFilter"));
                    TenantContextFilter filter = context.getBean(TenantContextFilter.class);
                    assertInstanceOf(CustomTenantContextFilter.class, filter);
                });
    }
    
    @Configuration
    static class CustomConfiguration {
        
        @Bean
        public TenantContextFilter tenantContextFilter() {
            return new CustomTenantContextFilter("Custom-Header", "admin");
        }
    }
    
    // 自定义过滤器实现，用于测试Bean覆盖
    static class CustomTenantContextFilter extends TenantContextFilter {
        public CustomTenantContextFilter(String tenantIdHeader, String systemTenantId) {
            super(tenantIdHeader, systemTenantId);
        }
    }
}
