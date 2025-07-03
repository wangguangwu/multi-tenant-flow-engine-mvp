package com.wangguangwu.flowengine.tenant.core;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultTenantContext 单元测试
 *
 * @author wangguangwu
 */
class DefaultTenantContextTest {

    @Test
    @DisplayName("测试创建普通租户上下文")
    void testCreateWithTenantIdAndName() {
        // 准备测试数据
        String tenantId = "test-tenant";
        String tenantName = "测试租户";
        
        // 执行测试
        TenantContext context = DefaultTenantContext.create(tenantId, tenantName);
        
        // 验证结果
        assertEquals(tenantId, context.tenantId());
        assertEquals("Tenant-" + tenantName, context.tenantName());
        assertFalse(context.systemTenant());
    }
    
    @Test
    @DisplayName("测试仅使用租户ID创建上下文")
    void testCreateWithTenantIdOnly() {
        // 准备测试数据
        String tenantId = "test-tenant";
        
        // 执行测试
        TenantContext context = DefaultTenantContext.create(tenantId);
        
        // 验证结果
        assertEquals(tenantId, context.tenantId());
        assertEquals("Tenant-" + tenantId, context.tenantName());
        assertFalse(context.systemTenant());
    }
    
    @Test
    @DisplayName("测试创建系统租户上下文")
    void testCreateSystemTenant() {
        // 准备测试数据
        String tenantId = "system";
        
        // 执行测试
        TenantContext context = DefaultTenantContext.createSystemTenant(tenantId);
        
        // 验证结果
        assertEquals(tenantId, context.tenantId());
        assertEquals("SYSTEM", context.tenantName());
        assertTrue(context.systemTenant());
    }
    
    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        // 准备测试数据
        String tenantId = "test-tenant";
        
        // 执行测试
        TenantContext context = DefaultTenantContext.create(tenantId);
        String result = context.toString();
        
        // 验证结果
        assertTrue(result.contains(tenantId));
        assertTrue(result.contains("Tenant-" + tenantId));
        assertTrue(result.contains("systemTenant=false"));
    }
}
