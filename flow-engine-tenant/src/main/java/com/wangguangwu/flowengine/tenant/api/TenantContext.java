package com.wangguangwu.flowengine.tenant.api;

/**
 * 租户上下文接口
 * <p>
 * 定义租户上下文的基本信息和操作
 * </p>
 *
 * @author wangguangwu
 */
public interface TenantContext {

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    String tenantId();
    
    /**
     * 获取租户名称
     *
     * @return 租户名称
     */
    String tenantName();
    
    /**
     * 判断是否为系统租户
     *
     * @return 如果是系统租户则返回true，否则返回false
     */
    boolean systemTenant();
}
