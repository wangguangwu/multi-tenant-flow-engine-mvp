package com.wangguangwu.flowengine.tenant.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 租户配置属性
 * <p>
 * 定义多租户相关的配置项
 * </p>
 *
 * @author wangguangwu
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "flow.engine.tenant")
public class TenantProperties {

    /**
     * 是否启用多租户功能
     */
    private boolean enabled = true;

    /**
     * 系统租户ID
     */
    private String systemTenantId = "system";

    /**
     * 租户ID请求头名称
     */
    private String tenantIdHeader = "X-Tenant-ID";

}
