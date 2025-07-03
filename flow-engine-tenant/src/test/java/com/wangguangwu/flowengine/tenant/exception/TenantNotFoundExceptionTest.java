package com.wangguangwu.flowengine.tenant.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TenantNotFoundException 单元测试
 *
 * @author wangguangwu
 */
class TenantNotFoundExceptionTest {

    @Test
    @DisplayName("测试创建无参异常")
    void testCreateWithNoArgs() {
        // 执行测试
        TenantNotFoundException exception = new TenantNotFoundException();
        
        // 验证结果
        assertNotNull(exception);
        assertEquals("当前上下文中未找到租户信息", exception.getMessage());
    }
    
    @Test
    @DisplayName("测试创建带消息的异常")
    void testCreateWithMessage() {
        // 准备测试数据
        String message = "自定义错误消息";
        
        // 执行测试
        TenantNotFoundException exception = new TenantNotFoundException(message);
        
        // 验证结果
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    @DisplayName("测试创建带消息和原因的异常")
    void testCreateWithMessageAndCause() {
        // 准备测试数据
        String message = "自定义错误消息";
        Throwable cause = new RuntimeException("原始异常");
        
        // 执行测试
        TenantNotFoundException exception = new TenantNotFoundException(message, cause);
        
        // 验证结果
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
