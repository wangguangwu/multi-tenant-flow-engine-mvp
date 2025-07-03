package com.wangguangwu.flowengine.sample.tenant;

import com.wangguangwu.flowengine.tenant.core.TenantContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户控制器示例
 * <p>
 * 展示如何在Web应用中使用多租户框架
 * </p>
 *
 * @author wangguangwu
 */
@RestController
@RequestMapping("/api/tenant")
public class TenantController {
    
    @Autowired
    private TenantServiceExample tenantService;
    
    /**
     * 获取当前租户信息
     *
     * @return 租户信息
     */
    @GetMapping("/info")
    public Map<String, Object> getTenantInfo() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取当前租户上下文
        String tenantId = TenantContextHolder.getCurrentTenantId();
        boolean isSystemTenant = TenantContextHolder.getContext().systemTenant();
        
        result.put("tenantId", tenantId);
        result.put("isSystemTenant", isSystemTenant);
        result.put("tenantName", TenantContextHolder.getContext().tenantName());
        
        return result;
    }
    
    /**
     * 获取租户感知数据
     *
     * @return 租户数据
     */
    @GetMapping("/data")
    public Map<String, Object> getTenantData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用租户感知方法
            String tenantAwareData = tenantService.getTenantAwareData();
            result.put("tenantAwareData", tenantAwareData);
        } catch (Exception e) {
            result.put("tenantAwareError", e.getMessage());
        }
        
        try {
            // 调用租户路由方法
            String tenantRoutingData = tenantService.getTenantRoutingData();
            result.put("tenantRoutingData", tenantRoutingData);
        } catch (Exception e) {
            result.put("tenantRoutingError", e.getMessage());
        }
        
        try {
            // 调用系统租户方法
            String systemInfo = tenantService.getSystemInfo();
            result.put("systemInfo", systemInfo);
        } catch (Exception e) {
            result.put("systemInfoError", e.getMessage());
        }
        
        return result;
    }
}
