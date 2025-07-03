package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantAware;
import com.wangguangwu.flowengine.tenant.api.TenantContext;
import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import com.wangguangwu.flowengine.tenant.exception.TenantNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 租户感知切面
 * <p>
 * 处理标记了@TenantAware注解的方法，检查租户上下文
 * </p>
 *
 * @author wangguangwu
 */
@Aspect
@Order(100)
public class TenantAwareAspect {

    /**
     * 环绕通知，拦截标记了@TenantAware注解的方法
     *
     * @param pjp 连接点
     * @return 方法执行结果
     * @throws Throwable 如果方法执行过程中发生异常
     */
    @Around("@annotation(com.wangguangwu.flowengine.tenant.api.TenantAware) || @within(com.wangguangwu.flowengine.tenant.api.TenantAware)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法或类上的TenantAware注解
        TenantAware tenantAware = AnnotationUtils.findAnnotation(method, TenantAware.class);

        // 获取当前租户上下文
        TenantContext tenantContext = TenantContextHolder.getContext();
        
        // 检查是否需要租户上下文
        if (tenantAware.required() && tenantContext == null) {
            throw new TenantNotFoundException("租户上下文不存在，但方法要求必须有租户上下文: " + method);
        }
        
        // 检查是否允许系统租户
        if (!tenantAware.allowSystemTenant() && tenantContext != null && tenantContext.systemTenant()) {
            throw new IllegalStateException("方法不允许系统租户访问: " + method);
        }
        
        // 执行原方法
        return pjp.proceed();
    }
}
