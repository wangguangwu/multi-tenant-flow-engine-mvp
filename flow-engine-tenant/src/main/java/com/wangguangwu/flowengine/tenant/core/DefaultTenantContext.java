package com.wangguangwu.flowengine.tenant.core;

import com.wangguangwu.flowengine.tenant.api.TenantContext;

/**
 * 默认租户上下文实现
 * <p>
 * 提供租户上下文的基本实现
 * </p>
 *
 * @param tenantId     租户ID
 * @param tenantName   租户名称
 * @param systemTenant 是否系统租户
 * @author wangguangwu
 */
public record DefaultTenantContext(String tenantId, String tenantName, boolean systemTenant) implements TenantContext {

    /**
     * 租户名称前缀
     */
    private static final String TENANT_NAME_PREFIX = "Tenant-";
    
    /**
     * 系统租户名称
     */
    private static final String SYSTEM_TENANT_NAME = "SYSTEM";

    /**
     * 构造函数
     *
     * @param tenantId     租户ID
     * @param tenantName   租户名称
     * @param systemTenant 是否系统租户
     */
    public DefaultTenantContext {
    }

    /**
     * 创建普通租户上下文
     *
     * @param tenantId   租户ID
     * @param tenantName 租户名称
     * @return 租户上下文
     */
    public static TenantContext create(String tenantId, String tenantName) {
        return new DefaultTenantContext(tenantId, TENANT_NAME_PREFIX + tenantName, false);
    }

    /**
     * 仅使用租户ID创建普通租户上下文
     *
     * @param tenantId 租户ID
     * @return 租户上下文
     */
    public static TenantContext create(String tenantId) {
        return new DefaultTenantContext(tenantId, TENANT_NAME_PREFIX + tenantId, false);
    }

    /**
     * 创建系统租户上下文
     *
     * @param tenantId 系统租户ID
     * @return 系统租户上下文
     */
    public static TenantContext createSystemTenant(String tenantId) {
        return new DefaultTenantContext(tenantId, SYSTEM_TENANT_NAME, true);
    }

    @Override
    public String toString() {
        return "TenantContext{" +
                "tenantId='" + tenantId + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", systemTenant=" + systemTenant +
                '}';
    }
}
