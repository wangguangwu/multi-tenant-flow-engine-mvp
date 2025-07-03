package com.wangguangwu.flowengine.sample.tenant;

import com.wangguangwu.flowengine.tenant.core.annotation.TenantAware;
import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import com.wangguangwu.flowengine.tenant.core.isolation.TenantRouting;
import org.springframework.stereotype.Service;

/**
 * 租户服务示例
 * <p>
 * 展示如何使用多租户框架的核心功能
 * </p>
 *
 * @author wangguangwu
 */
@Service
public class TenantServiceExample {
    
    /**
     * 租户感知方法示例
     * <p>
     * 该方法要求必须在租户上下文中执行，且不允许系统租户
     * </p>
     *
     * @return 当前租户ID
     */
    @TenantAware(required = true, allowSystemTenant = false)
    public String getTenantAwareData() {
        // 获取当前租户ID
        String tenantId = TenantContextHolder.getCurrentTenantId();
        
        return "正在访问租户 " + tenantId + " 的数据";
    }
    
    /**
     * 租户路由方法示例
     * <p>
     * 该方法会自动根据当前租户上下文切换数据源
     * </p>
     *
     * @return 当前租户的数据
     */
    @TenantRouting(resourceType = TenantRouting.ResourceType.DATASOURCE)
    public String getTenantRoutingData() {
        // 获取当前租户ID
        String tenantId = TenantContextHolder.getCurrentTenantId();
        
        // 此时已自动切换到对应租户的数据源，可以直接访问数据
        return "已自动切换到租户 " + tenantId + " 的数据源";
    }
    
    /**
     * 系统租户方法示例
     * <p>
     * 该方法允许系统租户执行
     * </p>
     *
     * @return 系统信息
     */
    @TenantAware(allowSystemTenant = true)
    public String getSystemInfo() {
        // 获取当前租户上下文
        boolean isSystemTenant = TenantContextHolder.getContext().systemTenant();
        
        if (isSystemTenant) {
            return "当前是系统租户，可以访问所有租户的数据";
        } else {
            return "当前是普通租户，只能访问自己的数据";
        }
    }
}
