package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TenantContextFilter 单元测试
 *
 * @author wangguangwu
 */
class TenantContextFilterTest {

    private TenantContextFilter filter;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    private static final String SYSTEM_TENANT_ID = "system";
    
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        filter = new TenantContextFilter(TENANT_ID_HEADER, SYSTEM_TENANT_ID);
        TenantContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        TenantContextHolder.clearContext();
        closeable.close();
    }

    @Test
    @DisplayName("测试从请求头提取租户ID")
    void testExtractTenantIdFromHeader() throws ServletException, IOException {
        // 准备测试数据
        String tenantId = "test-tenant";
        when(request.getHeader(TENANT_ID_HEADER)).thenReturn(tenantId);
        
        // 执行测试
        filter.doFilterInternal(request, response, filterChain);
        
        // 验证结果
        verify(filterChain).doFilter(request, response);
        
        // 由于过滤器执行完会清除上下文，所以这里应该为null
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试使用系统租户")
    void testUseSystemTenant() throws ServletException, IOException {
        // 准备测试数据
        when(request.getHeader(TENANT_ID_HEADER)).thenReturn(SYSTEM_TENANT_ID);
        
        // 执行测试
        filter.doFilterInternal(request, response, filterChain);
        
        // 验证结果
        verify(filterChain).doFilter(request, response);
        
        // 由于过滤器执行完会清除上下文，所以这里应该为null
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试无租户ID请求头")
    void testNoTenantIdHeader() throws ServletException, IOException {
        // 准备测试数据
        when(request.getHeader(TENANT_ID_HEADER)).thenReturn(null);
        
        // 执行测试
        filter.doFilterInternal(request, response, filterChain);
        
        // 验证结果
        verify(filterChain).doFilter(request, response);
        
        // 由于过滤器执行完会清除上下文，所以这里应该为null
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试过滤器链异常时清理上下文")
    void testCleanupOnException() throws ServletException, IOException {
        // 准备测试数据
        String tenantId = "test-tenant";
        when(request.getHeader(TENANT_ID_HEADER)).thenReturn(tenantId);
        doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);
        
        // 执行测试并验证异常被传递
        assertThrows(ServletException.class, () -> filter.doFilterInternal(request, response, filterChain));
        
        // 验证即使发生异常，上下文也被清理
        assertNull(TenantContextHolder.getContext());
    }
    
    @Test
    @DisplayName("测试自定义请求头")
    void testCustomHeader() throws ServletException, IOException {
        // 准备测试数据
        String customHeader = "Custom-Tenant-Header";
        String tenantId = "custom-tenant";
        TenantContextFilter customFilter = new TenantContextFilter(customHeader, SYSTEM_TENANT_ID);
        
        when(request.getHeader(customHeader)).thenReturn(tenantId);
        
        // 执行测试
        customFilter.doFilterInternal(request, response, filterChain);
        
        // 验证结果
        verify(filterChain).doFilter(request, response);
        
        // 由于过滤器执行完会清除上下文，所以这里应该为null
        assertNull(TenantContextHolder.getContext());
    }
}
