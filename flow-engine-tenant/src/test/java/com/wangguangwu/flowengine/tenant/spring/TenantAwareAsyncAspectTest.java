package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import com.wangguangwu.flowengine.tenant.core.DefaultTenantContext;
import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TenantAwareAsyncAspect 单元测试
 *
 * @author wangguangwu
 */
class TenantAwareAsyncAspectTest {

    private TenantAwareAsyncAspect aspect;
    
    @Mock
    private ProceedingJoinPoint joinPoint;
    
    private AutoCloseable closeable;
    private final String testTenantId = "test-tenant";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        aspect = new TenantAwareAsyncAspect();
        TenantContextHolder.clearContext();
        
        // 设置测试租户上下文
        TenantContext testContext = DefaultTenantContext.create(testTenantId);
        TenantContextHolder.setContext(testContext);
    }

    @AfterEach
    void tearDown() throws Exception {
        TenantContextHolder.clearContext();
        closeable.close();
    }

    @Test
    @DisplayName("测试异步方法的租户上下文传播")
    void testAsyncMethodContextPropagation() throws Throwable {
        // 准备测试数据
        Method method = TestAsyncService.class.getMethod("asyncMethod");
        
        when(joinPoint.proceed()).thenReturn("success");
        when(joinPoint.getTarget()).thenReturn(new TestAsyncService());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod()).thenReturn(method);
        
        // 执行测试前验证上下文存在
        assertNotNull(TenantContextHolder.getContext());
        assertEquals(testTenantId, TenantContextHolder.getContext().tenantId());
        
        // 执行测试
        Object result = aspect.aroundAsyncMethod(joinPoint);
        
        // 验证结果
        assertEquals("success", result);
        
        // 验证租户上下文是否被清理
        assertNull(TenantContextHolder.getContext(), "异步方法执行后应清理租户上下文");
    }
    
    @Test
    @DisplayName("测试异步方法执行异常时租户上下文清理")
    void testAsyncMethodExceptionHandling() throws Throwable {
        // 准备测试数据
        Method method = TestAsyncService.class.getMethod("asyncMethodWithException");
        RuntimeException testException = new RuntimeException("测试异常");
        
        when(joinPoint.proceed()).thenThrow(testException);
        when(joinPoint.getTarget()).thenReturn(new TestAsyncService());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod()).thenReturn(method);
        
        // 执行测试前验证上下文存在
        assertNotNull(TenantContextHolder.getContext());
        
        // 执行测试并验证异常传播
        Exception exception = assertThrows(RuntimeException.class, () -> aspect.aroundAsyncMethod(joinPoint));
        assertEquals(testException, exception);
        
        // 验证租户上下文是否被清理
        assertNull(TenantContextHolder.getContext(), "异常情况下也应清理租户上下文");
    }
    
    @Test
    @DisplayName("测试无租户上下文时的异步方法执行")
    void testAsyncMethodWithoutContext() throws Throwable {
        // 清除租户上下文
        TenantContextHolder.clearContext();
        
        // 准备测试数据
        Method method = TestAsyncService.class.getMethod("asyncMethod");
        
        when(joinPoint.proceed()).thenReturn("success");
        when(joinPoint.getTarget()).thenReturn(new TestAsyncService());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod()).thenReturn(method);
        
        // 执行测试前验证上下文不存在
        assertNull(TenantContextHolder.getContext());
        
        // 执行测试
        Object result = aspect.aroundAsyncMethod(joinPoint);
        
        // 验证结果
        assertEquals("success", result);
        
        // 验证租户上下文仍然为空
        assertNull(TenantContextHolder.getContext());
    }
    
    // 测试用的异步服务类
    static class TestAsyncService {
        
        @Async
        public String asyncMethod() {
            return "async method";
        }
        
        @Async
        public String asyncMethodWithException() {
            throw new RuntimeException("测试异常");
        }
    }
}
