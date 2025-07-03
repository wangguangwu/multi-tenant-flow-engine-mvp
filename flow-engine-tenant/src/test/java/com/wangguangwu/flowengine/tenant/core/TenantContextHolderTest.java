package com.wangguangwu.flowengine.tenant.core;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TenantContextHolder 单元测试
 *
 * @author wangguangwu
 */
class TenantContextHolderTest {

    @AfterEach
    void tearDown() {
        TenantContextHolder.clearContext();
    }

    @Test
    @DisplayName("测试设置和获取上下文")
    void testSetAndGetContext() {
        // 准备测试数据
        TenantContext context = DefaultTenantContext.create("test-tenant");
        
        // 执行测试
        TenantContextHolder.setContext(context);
        TenantContext result = TenantContextHolder.getContext();
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test-tenant", result.tenantId());
    }
    
    @Test
    @DisplayName("测试清除上下文")
    void testClearContext() {
        // 准备测试数据
        TenantContext context = DefaultTenantContext.create("test-tenant");
        TenantContextHolder.setContext(context);
        
        // 执行测试
        TenantContextHolder.clearContext();
        
        // 验证结果
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试获取当前租户ID")
    void testGetCurrentTenantId() {
        // 准备测试数据
        TenantContext context = DefaultTenantContext.create("test-tenant");
        TenantContextHolder.setContext(context);
        
        // 执行测试
        String tenantId = TenantContextHolder.getCurrentTenantId();
        
        // 验证结果
        assertEquals("test-tenant", tenantId);
        
        // 清除上下文后，租户ID应为null
        TenantContextHolder.clearContext();
        assertNull(TenantContextHolder.getCurrentTenantId());
    }
    
    @Test
    @DisplayName("测试判断是否有上下文")
    void testHasContext() {
        // 初始状态应该没有上下文
        assertFalse(TenantContextHolder.hasContext());
        
        // 设置上下文后应该有上下文
        TenantContext context = DefaultTenantContext.create("test-tenant");
        TenantContextHolder.setContext(context);
        assertTrue(TenantContextHolder.hasContext());
        
        // 清除上下文后应该没有上下文
        TenantContextHolder.clearContext();
        assertFalse(TenantContextHolder.hasContext());
    }
    
    @Test
    @DisplayName("测试父子线程上下文传递")
    void testInheritableThreadLocal() throws InterruptedException {
        // 准备测试数据
        TenantContext parentContext = DefaultTenantContext.create("parent-tenant");
        TenantContextHolder.setContext(parentContext);
        
        // 创建子线程并获取其上下文
        AtomicReference<TenantContext> childContext = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        Thread childThread = new Thread(() -> {
            childContext.set(TenantContextHolder.getContext());
            latch.countDown();
        });
        
        // 启动子线程并等待其完成
        childThread.start();
        latch.await();
        
        // 验证子线程继承了父线程的上下文
        assertNotNull(childContext.get());
        assertEquals("parent-tenant", childContext.get().tenantId());
        assertEquals(parentContext.tenantId(), childContext.get().tenantId());
    }
}
