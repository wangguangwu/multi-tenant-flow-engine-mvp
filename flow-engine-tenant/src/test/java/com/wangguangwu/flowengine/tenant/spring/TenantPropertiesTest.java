package com.wangguangwu.flowengine.tenant.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TenantProperties 单元测试
 *
 * @author wangguangwu
 */
class TenantPropertiesTest {

    @Test
    @DisplayName("测试默认属性值")
    void testDefaultProperties() {
        // 执行测试
        TenantProperties properties = new TenantProperties();
        
        // 验证结果
        assertTrue(properties.isEnabled(), "默认应该启用多租户功能");
        assertEquals("X-Tenant-ID", properties.getTenantIdHeader(), "默认租户ID请求头应为X-Tenant-ID");
        assertEquals("system", properties.getSystemTenantId(), "默认系统租户ID应为system");
    }
    
    @Test
    @DisplayName("测试设置和获取enabled属性")
    void testEnabledProperty() {
        // 准备测试数据
        TenantProperties properties = new TenantProperties();
        
        // 执行测试
        properties.setEnabled(false);
        
        // 验证结果
        assertFalse(properties.isEnabled());
    }
    
    @Test
    @DisplayName("测试设置和获取tenantIdHeader属性")
    void testTenantIdHeaderProperty() {
        // 准备测试数据
        TenantProperties properties = new TenantProperties();
        String customHeader = "Custom-Tenant-Header";
        
        // 执行测试
        properties.setTenantIdHeader(customHeader);
        
        // 验证结果
        assertEquals(customHeader, properties.getTenantIdHeader());
    }
    
    @Test
    @DisplayName("测试设置和获取systemTenantId属性")
    void testSystemTenantIdProperty() {
        // 准备测试数据
        TenantProperties properties = new TenantProperties();
        String customSystemTenantId = "admin";
        
        // 执行测试
        properties.setSystemTenantId(customSystemTenantId);
        
        // 验证结果
        assertEquals(customSystemTenantId, properties.getSystemTenantId());
    }
}
