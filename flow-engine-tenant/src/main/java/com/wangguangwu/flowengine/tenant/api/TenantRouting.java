package com.wangguangwu.flowengine.tenant.api;

import java.lang.annotation.*;

/**
 * 租户数据源路由注解
 * <p>
 * 标记需要进行租户数据源路由的方法或类
 * </p>
 *
 * @author wangguangwu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantRouting {
    // 简化注解，移除不必要的属性
}
