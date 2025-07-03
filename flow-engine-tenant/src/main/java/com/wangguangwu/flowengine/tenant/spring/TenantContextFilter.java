package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import com.wangguangwu.flowengine.tenant.core.DefaultTenantContext;
import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 租户上下文过滤器
 * <p>
 * 从HTTP请求中提取租户信息，并设置到当前线程的租户上下文中
 * </p>
 *
 * @author wangguangwu
 */
public class TenantContextFilter extends OncePerRequestFilter implements Ordered {

    /**
     * 默认租户ID请求头名称
     */
    private static final String DEFAULT_TENANT_ID_HEADER = "X-Tenant-ID";
    
    /**
     * 租户ID请求头名称
     */
    private final String tenantIdHeader;
    
    /**
     * 系统租户ID
     */
    private final String systemTenantId;
    
    /**
     * 构造函数
     *
     * @param tenantIdHeader 租户ID请求头名称，如果为null则使用默认值
     * @param systemTenantId 系统租户ID
     */
    public TenantContextFilter(String tenantIdHeader, String systemTenantId) {
        this.tenantIdHeader = tenantIdHeader != null ? tenantIdHeader : DEFAULT_TENANT_ID_HEADER;
        this.systemTenantId = systemTenantId;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头中获取租户ID
            String tenantId = request.getHeader(tenantIdHeader);
            
            // 如果请求头中有租户ID，则设置租户上下文
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext tenantContext;
                
                // 判断是否为系统租户
                if (systemTenantId != null && systemTenantId.equals(tenantId)) {
                    tenantContext = DefaultTenantContext.createSystemTenant(tenantId);
                } else {
                    tenantContext = DefaultTenantContext.create(tenantId);
                }
                
                // 设置租户上下文
                TenantContextHolder.setContext(tenantContext);
            }
            
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 清除租户上下文，防止内存泄漏
            TenantContextHolder.clearContext();
        }
    }
    
    @Override
    public int getOrder() {
        // 确保在Spring Security过滤器之后执行
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
