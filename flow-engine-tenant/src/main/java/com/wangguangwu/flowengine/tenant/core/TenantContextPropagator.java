package com.wangguangwu.flowengine.tenant.core;

import com.wangguangwu.flowengine.tenant.api.TenantContext;

import java.util.concurrent.Callable;

/**
 * 租户上下文传播器
 * <p>
 * 用于在父子线程之间传递租户上下文
 * </p>
 *
 * @author wangguangwu
 */
public class TenantContextPropagator {

    /**
     * 包装Runnable，使子线程继承父线程的租户上下文
     *
     * @param runnable 原始Runnable
     * @return 包装后的Runnable
     */
    public static Runnable wrap(Runnable runnable) {
        // 获取当前线程的租户上下文
        TenantContext context = TenantContextHolder.getContext();
        
        return () -> {
            // 保存子线程原有的租户上下文（如果有）
            TenantContext originalContext = TenantContextHolder.getContext();
            
            try {
                // 设置从父线程继承的租户上下文
                if (context != null) {
                    TenantContextHolder.setContext(context);
                }
                
                // 执行原始任务
                runnable.run();
            } finally {
                // 清除租户上下文，不恢复原有上下文
                TenantContextHolder.clearContext();
            }
        };
    }
    
    /**
     * 包装Callable，使子线程继承父线程的租户上下文
     *
     * @param callable 原始Callable
     * @param <V> 返回值类型
     * @return 包装后的Callable
     */
    public static <V> Callable<V> wrap(Callable<V> callable) {
        // 获取当前线程的租户上下文
        TenantContext context = TenantContextHolder.getContext();
        
        return () -> {
            // 保存子线程原有的租户上下文（如果有）
            TenantContext originalContext = TenantContextHolder.getContext();
            
            try {
                // 设置从父线程继承的租户上下文
                if (context != null) {
                    TenantContextHolder.setContext(context);
                }
                
                // 执行原始任务并返回结果
                return callable.call();
            } finally {
                // 清除租户上下文，不恢复原有上下文
                TenantContextHolder.clearContext();
            }
        };
    }
}
