package com.wangguangwu.flowengine.tenant.core;

import com.wangguangwu.flowengine.tenant.api.TenantContext;

/**
 * 租户上下文持有者
 * <p>
 * 使用InheritableThreadLocal存储当前线程的租户上下文，确保线程安全
 * </p>
 *
 * @author wangguangwu
 */
public class TenantContextHolder {

    /**
     * 使用InheritableThreadLocal存储租户上下文，确保线程安全
     */
    private static final InheritableThreadLocal<TenantContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();
    
    /**
     * 获取当前线程的租户上下文
     *
     * @return 当前租户上下文，如果不存在则返回null
     */
    public static TenantContext getContext() {
        return CONTEXT_HOLDER.get();
    }
    
    /**
     * 设置当前线程的租户上下文
     *
     * @param context 租户上下文
     */
    public static void setContext(TenantContext context) {
        CONTEXT_HOLDER.set(context);
    }
    
    /**
     * 清除当前线程的租户上下文
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
    
    /**
     * 获取当前租户ID
     *
     * @return 当前租户ID，如果不存在租户上下文则返回null
     */
    public static String getCurrentTenantId() {
        TenantContext context = getContext();
        return context != null ? context.tenantId() : null;
    }
    
    /**
     * 判断当前是否有租户上下文
     *
     * @return 如果存在租户上下文则返回true，否则返回false
     */
    public static boolean hasContext() {
        return getContext() != null;
    }
}
