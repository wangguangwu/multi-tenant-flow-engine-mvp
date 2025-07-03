package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 租户感知的异步切面
 * <p>
 * 拦截带有@Async注解的方法，确保租户上下文在异步方法中可用
 * </p>
 *
 * @author wangguangwu
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantAwareAsyncAspect {

    /**
     * 拦截带有@Async注解的方法
     * <p>
     * 在方法执行前捕获当前租户上下文，并在异步线程中设置相同的上下文
     * </p>
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 如果方法执行过程中抛出异常
     */
    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    public Object aroundAsyncMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前线程的租户上下文
        TenantContext tenantContext = TenantContextHolder.getContext();
        
        try {
            // 如果当前线程有租户上下文，则在异步线程中设置相同的上下文
            // 注意：这里不需要显式设置，因为我们使用了InheritableThreadLocal
            // 但为了确保在线程池重用线程时也能正确设置上下文，我们仍然显式设置一次
            if (tenantContext != null) {
                TenantContextHolder.setContext(tenantContext);
            }
            
            // 执行原始方法
            return joinPoint.proceed();
        } finally {
            // 清理租户上下文，避免线程池中的线程保留旧的上下文
            TenantContextHolder.clearContext();
        }
    }
}
