package com.wangguangwu.flowengine.tenant.exception;

/**
 * 租户未找到异常
 * <p>
 * 当需要租户上下文但未找到时抛出此异常
 * </p>
 *
 * @author wangguangwu
 */
public class TenantNotFoundException extends RuntimeException {

    /**
     * 构造函数
     */
    public TenantNotFoundException() {
        super("当前上下文中未找到租户信息");
    }

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public TenantNotFoundException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public TenantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
