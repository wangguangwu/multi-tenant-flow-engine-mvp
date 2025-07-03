package com.wangguangwu.flowengine.tenant.spring;

import com.wangguangwu.flowengine.tenant.api.TenantContextInitializer;
import com.wangguangwu.flowengine.tenant.core.DefaultTenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

/**
 * 租户自动配置类
 * <p>
 * 自动配置多租户功能的Spring Bean
 * </p>
 *
 * @author wangguangwu
 */
@Configuration
@EnableConfigurationProperties(TenantProperties.class)
@ConditionalOnProperty(prefix = "flow.engine.tenant", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TenantAutoConfiguration {

    /**
     * 配置租户感知切面
     *
     * @return 租户感知切面
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantAwareAspect tenantAwareAspect() {
        return new TenantAwareAspect();
    }

    /**
     * 配置租户上下文过滤器
     *
     * @param properties 租户配置属性
     * @return 租户上下文过滤器
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean
    public TenantContextFilter tenantContextFilter(TenantProperties properties) {
        return new TenantContextFilter(properties.getTenantIdHeader(), properties.getSystemTenantId());
    }

    /**
     * 配置默认的租户上下文初始化器
     *
     * @return 默认的租户上下文初始化器
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantContextInitializer tenantContextInitializer() {
        return DefaultTenantContext::create;
    }
    
    /**
     * 配置租户感知的异步切面
     * <p>
     * 用于拦截@Async注解的方法，确保租户上下文在异步方法中可用
     * </p>
     *
     * @return 租户感知的异步切面
     */
    @Bean
    @ConditionalOnClass(Async.class)
    @ConditionalOnMissingBean
    public TenantAwareAsyncAspect tenantAwareAsyncAspect() {
        return new TenantAwareAsyncAspect();
    }
}
