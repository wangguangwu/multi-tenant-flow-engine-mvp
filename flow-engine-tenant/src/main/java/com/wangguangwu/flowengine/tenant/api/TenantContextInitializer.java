package com.wangguangwu.flowengine.tenant.api;

/**
 * 租户上下文初始化器接口
 * <p>
 * 允许用户自定义租户上下文的初始化方式
 * </p>
 *
 * @author wangguangwu
 */
public interface TenantContextInitializer {

    /**
     * 初始化租户上下文
     *
     * @param tenantId 租户ID
     * @return 初始化的租户上下文
     */
    TenantContext initializeContext(String tenantId);
}
