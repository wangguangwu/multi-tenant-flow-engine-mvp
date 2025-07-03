package com.wangguangwu.flowengine.tenant.api;

import java.lang.annotation.*;

/**
 * 租户感知注解
 * <p>
 * 标记需要租户上下文的方法或类
 * </p>
 *
 * @author wangguangwu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantAware {
    
    /**
     * 是否必须有租户上下文
     * <p>
     * 如果为true，则在没有租户上下文时抛出异常
     * </p>
     *
     * @return 是否必须有租户上下文
     */
    boolean required() default true;
    
    /**
     * 是否允许系统租户
     * <p>
     * 如果为false，则在当前租户为系统租户时抛出异常
     * </p>
     *
     * @return 是否允许系统租户
     */
    boolean allowSystemTenant() default true;
}
