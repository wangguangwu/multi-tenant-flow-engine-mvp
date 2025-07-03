package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantAware;
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

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TenantAwareAspect 单元测试
 *
 * @author wangguangwu
 */
class TenantAwareAspectTest {

    private TenantAwareAspect aspect;
    
    @Mock
    private ProceedingJoinPoint joinPoint;
    
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        aspect = new TenantAwareAspect();
        TenantContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        TenantContextHolder.clearContext();
        closeable.close();
    }

    @Test
    @DisplayName("测试系统租户方法")
    void testSystemTenantMethod() throws Throwable {
        // 准备测试数据
        Method method = TestService.class.getMethod("systemTenantMethod");
        
        // 设置系统租户上下文
        TenantContext systemTenantContext = DefaultTenantContext.createSystemTenant("system");
        TenantContextHolder.setContext(systemTenantContext);
        
        when(joinPoint.proceed()).thenReturn("success");
        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod()).thenReturn(method);
        
        // 执行测试
        Object result = aspect.around(joinPoint);
        
        // 验证结果
        assertEquals("success", result);
        
        // 验证租户上下文是否被清理
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试指定租户ID参数的方法")
    void testMethodWithTenantIdParam() throws Throwable {
        // 准备测试数据
        Method method = TestService.class.getMethod("methodWithTenantIdParam", String.class, String.class);
        String tenantId = "test-tenant";
        
        when(joinPoint.proceed()).thenReturn("success");
        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[]{tenantId, "data"});
        
        // 执行测试
        Object result = aspect.around(joinPoint);
        
        // 验证结果
        assertEquals("success", result);
        
        // 验证租户上下文是否被清理
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试使用已存在的租户上下文")
    void testUseExistingTenantContext() throws Throwable {
        // 准备测试数据
        Method method = TestService.class.getMethod("normalMethod");
        TenantContext existingContext = DefaultTenantContext.create("existing-tenant");
        TenantContextHolder.setContext(existingContext);
        
        when(joinPoint.proceed()).thenReturn("success");
        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod()).thenReturn(method);
        
        // 执行测试
        Object result = aspect.around(joinPoint);
        
        // 验证结果
        assertEquals("success", result);
        
        // 验证租户上下文是否被清理
        assertNull(TenantContextHolder.getContext());
    }
    
    // 测试用的服务类
    static class TestService {
        
        @TenantAware(allowSystemTenant = true)
        public String systemTenantMethod() {
            return "system tenant method";
        }
        
        @TenantAware(required = false)
        public String methodWithTenantIdParam(String tenantId, String data) {
            return "method with tenant id param";
        }
        
        @TenantAware
        public String normalMethod() {
            return "normal method";
        }
    }
}
